ThisBuild / scalaVersion := "2.12.10"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "hw2"
ThisBuild / organizationName := "hw2"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x                             => MergeStrategy.first
}

lazy val root = (project in file("."))
  .settings(
    name := "hw2",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "3.1.1",
      "com.datastax.spark" %% "spark-cassandra-connector" % "3.0.1",
      "org.scalatest" %% "scalatest" % "3.2.2" % Test,
      "com.holdenkarau" %% "spark-testing-base" % "3.0.0_1.0.0" % Test
    )
  )
