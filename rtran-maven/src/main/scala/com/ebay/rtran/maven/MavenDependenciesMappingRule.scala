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

import com.ebay.rtran.maven.util.MavenModelUtil
import MavenModelUtil._
import com.typesafe.scalalogging.LazyLogging
import com.ebay.rtran.api.{IProjectCtx, IRule, IRuleConfig}

import scala.collection.JavaConversions._


class MavenDependenciesMappingRule(ruleConfig: MavenDependenciesMappingRuleConfig)
  extends IRule[MultiModuleMavenModel] with LazyLogging {

  override def transform(model: MultiModuleMavenModel): MultiModuleMavenModel = {
    model.modules filter { module =>
      ruleConfig.packageTypes match {
        case Some(set) => set contains module.pomModel.getPackaging
        case None => true
      }
    } foreach { module =>
      val resolvedDeps = module.resolvedDependencies
      val matches = for {
        dep <- resolvedDeps
        from <- ruleConfig.from
        if from matches dep
      } yield dep
      if (matches.size == ruleConfig.from.size) {
        // remove `from`
        (for {
          toBeRemoved <- ruleConfig.from
          dep <- module.pomModel.getDependencies
          if toBeRemoved.key == dep.key
        } yield dep) foreach module.pomModel.removeDependency
        // add `to`
        ruleConfig.to filterNot {dep =>
          module.resolvedDependencies.exists(_.key == dep.key)
        } foreach (module.pomModel.addDependency(_))
        logger.info("{} mapped {} to {} in {}", id, ruleConfig.from, ruleConfig.to, module.pomFile)
        logger.info("Rule {} was applied to 1 files", id)
      }
    }
    model
  }

  override def isEligibleFor(projectCtx: IProjectCtx) = projectCtx.isInstanceOf[MavenProjectCtx]
}

case class MavenDependenciesMappingRuleConfig(from: Set[SimpleDependency],
                                              to: Set[SimpleDependency],
                                              packageTypes: Option[Set[String]] = None) extends IRuleConfig