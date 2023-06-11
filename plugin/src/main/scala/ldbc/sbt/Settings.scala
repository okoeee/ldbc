/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package ldbc.sbt

import sbt._
import sbt.Keys._

import CustomKeys._
import Dependencies._

object Settings {

  lazy val projectSettings = Def.settings(
    resolvers += "Lepus Maven" at "s3://com.github.takapi327.s3-ap-northeast-1.amazonaws.com/lepus/",
    libraryDependencies ++= Seq(ldbcGenerator),
    baseClassloader := Commands.baseClassloaderTask.value,
    (Compile / sourceGenerators) += Generator.generate.taskValue
  )
}
