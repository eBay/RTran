/*
 * Copyright (c) 2016 eBay Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ebay.rtran.maven

import java.io._

import com.ebay.rtran.api.{IModel, IModelProvider}
import com.ebay.rtran.maven.util.MavenModelUtil._
import com.ebay.rtran.maven.util.MavenUtil
import org.apache.commons.io.{FileUtils, IOUtils}
import org.apache.commons.lang3.StringUtils
import org.apache.maven.model.io.jdom.MavenJDOMWriter2
import org.apache.maven.model.io.xpp3.{MavenXpp3Reader, MavenXpp3Writer}
import org.apache.maven.model.{Dependency, Model, Plugin}
import org.eclipse.aether.artifact.DefaultArtifact
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.Format.TextMode

import scala.collection.JavaConversions._
import scala.collection.immutable.ListMap
import scala.language.postfixOps
import scala.reflect.ClassTag
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}


case class MultiModuleMavenModel(rootPom: File, modules: List[MavenModel]) extends IModel {
  lazy val (parents, subModules) = modules.partition(_.localParent.isEmpty)
}

case class MavenModel(pomFile: File, pomModel: Model) {

  def localParent = Option(pomModel.getParent) flatMap { p =>
    Try {
      val relativePomPath = {
        if (p.getRelativePath.endsWith("pom.xml")) p.getRelativePath
        else p.getRelativePath.stripPrefix("/") + "/pom.xml"
      }
      val localParentFile = new File(pomFile.getParent, relativePomPath).getCanonicalFile
      val model = new MavenXpp3Reader().read(new FileReader(localParentFile))
      MavenModel(localParentFile, model)
    } match {
      case Success(m) if m.pomModel.getArtifactId == p.getArtifactId => Some(m)
      case _ => None
    }
  }

  def parent: Option[MavenModel] = localParent orElse {
    Option(pomModel.getParent) map {p =>
      val artifact = MavenUtil.resolveArtifact(
        new DefaultArtifact(s"${p.getGroupId}:${p.getArtifactId}:pom:${p.getVersion}")
      )
      val model = new MavenXpp3Reader().read(new FileReader(artifact.getFile))
      MavenModel(artifact.getFile, model)
    }
  }


  def resolvedDependencies: List[Dependency] = {
    val managed = managedDependencies
    pomModel.getDependencies.map(resolve) map { dep =>
      managed get dep.getManagementKey match {
        case None => dep
        case Some(md) => merge(md, dep)
      }
    } toList
  }

  def managedDependencies: Map[String, Dependency] = {
    implicit val properties = this.properties

    var result = parent.map(_.managedDependencies).getOrElse(ListMap.empty) ++
      ListMap(Option(pomModel.getDependencyManagement)
        .map(_.getDependencies.map(resolve).map(dep => dep.getManagementKey -> dep))
        .getOrElse(List.empty):_*)

    val imports = result.values.toList.filter(dep => dep.getScope == "import" && dep.getType == "pom")
    imports foreach { dep =>
      val artifact = MavenUtil.resolveArtifact(
        new DefaultArtifact(s"${dep.getGroupId}:${dep.getArtifactId}:pom:${dep.getVersion}")
      )
      if (artifact.getFile.exists()) {
        val model = MavenModel(artifact.getFile, new MavenXpp3Reader().read(new FileReader(artifact.getFile)))
        
        // the dependencies declared in imported pom has lower priority than dependencies in current pom's dependency management
        result = model.managedDependencies ++ result
      }
    }

    result
  }


  def managedPlugins: Map[String, Plugin] = {
    val parentPlugins = parent.map(_.managedPlugins).getOrElse(Map.empty)
    val modelPlugins =
      Try(pomModel.getBuild.getPluginManagement)
        .map(_.getPlugins.map(resolve).map(plugin => plugin.getKey -> plugin).toMap)
        .getOrElse(Map.empty)

    //plugin management can inherit version from parent
    modelPlugins.foreach(p =>
      if (Option(p._2.getVersion).isEmpty) {
        val pluginDefinedInParent = parentPlugins.get(p._1)
        pluginDefinedInParent match {
          case Some(plugin) => p._2.setVersion(plugin.getVersion)
          case _ => //nothing
        }

      }
    )

    parentPlugins ++ modelPlugins
  }

  def resolvedPlugins: List[Plugin] = {
    Option(pomModel.getBuild).map(_.getPlugins.map(resolve)).getOrElse(List.empty) map {plugin =>
      managedPlugins get plugin.getKey match {
        case None => plugin
        case Some(mp) => merge(mp, plugin)
      }
    } toList
  }

  implicit def properties: Map[String, String] = {
    val modelParent = parent //avoid emthod call
    var props = modelParent.map(_.properties).getOrElse(Map.empty) ++ pomModel.getProperties

    //should have below properties as pom may have below variables
    val parentGroupId = modelParent match {
      case Some(model) => model.pomModel.getGroupId
      case _ => ""
    }

    val parentVersion = modelParent match {
      case Some(model) => model.pomModel.getVersion
      case _ => ""
    }

    val projectV = Option(pomModel.getVersion).getOrElse(parentVersion)
    if (!Option(projectV).isEmpty) {
      //if null, use parent's project.version, dont overwrite
      props += ("project.version" -> projectV)
    }

    props += ("project.groupId" -> Option(pomModel.getGroupId).getOrElse(parentGroupId))
    props += ("project.artifactId" -> Option(pomModel.getArtifactId).getOrElse(""))

    //solve case like spring-framework.version=${spring.version} & spring.version=1.1.1
    val propsToSolveReference = props.filter((p) => (p._2 != null && p._2.startsWith("${") && p._2.endsWith("}")))
    propsToSolveReference foreach { (p) =>
      val value = StringUtils.substringBetween(p._2, "${", "}")
      props += (p._1 -> props.get(value).getOrElse(p._2))
    }
    props

  }

}

class MultiModuleMavenModelProvider extends IModelProvider[MultiModuleMavenModel, MavenProjectCtx] {

  private val CompilerArgumentsPattern = """(?s)<configuration.*?>(.*?)</configuration>""".r

  private val ArgumentsPattern = """(</?.*?)(:)(.*?/?>)""".r

  private val ArgumentsBackPattern = """(</?.*?)(__colon__)(.*?/?>)""".r

  private val defaultEncoding = "UTF-8"

  override def id(): String = getClass.getName

  override def save(model: MultiModuleMavenModel): Unit = {
    model.modules foreach { module =>
      val encoding = Option(module.pomModel.getModelEncoding) getOrElse defaultEncoding
      val content = fixContent(FileUtils.readFileToString(module.pomFile, encoding))
      val builder = new SAXBuilder
      builder.setIgnoringBoundaryWhitespace(false)
      builder.setIgnoringElementContentWhitespace(false)
      val doc = builder.build(new StringReader(content))

      // guess the line separator
      val separator = if (content.contains(IOUtils.LINE_SEPARATOR_WINDOWS)) IOUtils.LINE_SEPARATOR_WINDOWS else IOUtils.LINE_SEPARATOR_UNIX

      val format = Format.getRawFormat.setEncoding(encoding).setTextMode(TextMode.PRESERVE).setLineSeparator(separator)
      val outWriter = new StringWriter()
      new MavenJDOMWriter2().setExpandEmptyElements(true).write(module.pomModel, doc, outWriter, format)
      //new MavenXpp3Writer().write(new FileWriter("aa"), module.pomModel)

      val updatedContent = outWriter.toString

      FileUtils.write(module.pomFile, fixBack(updatedContent), encoding)
    }
  }

  // replace element like Xlint:-path to Xlint__colon__-path, to pass the validation of xml
  private def fixContent(content: String) = {
    CompilerArgumentsPattern.replaceAllIn(content, {matcher =>
      Regex.quoteReplacement(ArgumentsPattern.replaceAllIn(matcher.matched, "$1__colon__$3"))
    })
  }

  // replace __colon__ back to :
  private def fixBack(content: String) = {
    CompilerArgumentsPattern.replaceAllIn(content, {matcher =>
      Regex.quoteReplacement(ArgumentsBackPattern.replaceAllIn(matcher.matched, "$1:$3"))
    })
  }

  override def create(projectCtx: MavenProjectCtx): MultiModuleMavenModel = {

    def findModules(root: MavenModel): List[MavenModel] =  {
      val modules = root.pomModel.getModules ++ root.pomModel.getProfiles.flatMap(profile => profile.getModules) toSet

      modules.foldLeft(List.empty[MavenModel]) {(list, module) =>
        list ++ (createMavenModel(new File(root.pomFile.getParent, s"$module/pom.xml")) map { m =>
          m :: findModules(m)
        } getOrElse List.empty)
      }
    }

    def createMavenModel(pomFile: File) = Try {
      val pomModel = new MavenXpp3Reader().read(new StringReader(fixContent(FileUtils.readFileToString(pomFile, defaultEncoding))))
      MavenModel(pomFile.getCanonicalFile, pomModel)
    }

    createMavenModel(projectCtx.rootPomFile) match {
      case Success(root) =>
        val modules = root :: findModules(root)
        val keys = modules.map(_.pomFile).toSet
        val unprocessedParents = modules.map(_.localParent) collect {
          case Some(m) => m
        } filter {m =>
          !keys.contains(m.pomFile)
        }
        MultiModuleMavenModel(projectCtx.rootPomFile, unprocessedParents ++ modules)
      case Failure(e) => throw e
    }
  }
}
