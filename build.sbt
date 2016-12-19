name := "playback-scala" 

version := "1.0.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

// using Alpine Linux image with OracleJDK 8
dockerBaseImage := "frolvlad/alpine-oraclejdk8"

// install bash is right after the FROM command 
import com.typesafe.sbt.packager.docker._
dockerCommands := dockerCommands.value.flatMap{
  case cmd@Cmd("FROM",_) => List(cmd, Cmd("RUN", "apk update && apk add bash"))
  case other => List(other)
}