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

import java.io.File

import com.ebay.rtran.maven.util.MavenModelUtil
import MavenModelUtil._
import com.typesafe.scalalogging.LazyLogging
import com.ebay.rtran.api.{IProjectCtx, IRule, IRuleConfig}

import scala.collection.JavaConversions._


class MavenRemoveDependenciesRule(ruleConfig: MavenRemoveDependenciesRuleConfig)
  extends IRule[MultiModuleMavenModel] with LazyLogging {

  override def transform(model: MultiModuleMavenModel): MultiModuleMavenModel = {
    var changes = Set.empty[File]
    model.modules filter { module =>
      ruleConfig.packageTypes match {
        case Some(set) => set contains module.pomModel.getPackaging
        case None => true
      }
    } foreach { module =>
      val resolvedDeps = module.resolvedDependencies
      (for {
        toBeRemoved <- ruleConfig.dependencies
        resolvedDep <- resolvedDeps
        dep <- module.pomModel.getDependencies
        if (toBeRemoved matches resolvedDep) && (resolvedDep.key == dep.key)
      } yield dep) foreach { dep =>
        logger.info("{} removed dependency {} from {}", id, dep, module.pomFile)
        module.pomModel.removeDependency(dep)
        changes += module.pomFile
      }
      module
    }
    logger.info("Rule {} was applied to {} files", id, changes.size.toString)
    model
  }

  override def isEligibleFor(projectCtx: IProjectCtx) = projectCtx.isInstanceOf[MavenProjectCtx]
}

case class MavenRemoveDependenciesRuleConfig(dependencies: Set[SimpleDependency],
                                             packageTypes: Option[Set[String]] = None) extends IRuleConfig