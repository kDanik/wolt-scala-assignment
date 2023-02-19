ThisBuild / scalaVersion := "2.13.10"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """scala-wolt""",
    libraryDependencies ++= Seq(
      guice,
      "com.github.nscala-time" %% "nscala-time" % "2.32.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
    )
  )

import com.typesafe.sbt.packager.docker.DockerChmodType
import com.typesafe.sbt.packager.docker.DockerPermissionStrategy
dockerChmodType := DockerChmodType.UserGroupWriteExecute
dockerPermissionStrategy := DockerPermissionStrategy.CopyChown

packageName := "wolt-daniil-kurachkin"

dockerBaseImage := "eclipse-temurin:11-jre"
dockerExposedPorts := Seq(9000)
daemonUserUid  := None
daemonUser := "daemon"