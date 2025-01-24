ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "3.4.2"


libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19"
libraryDependencies += "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0"


updateOptions := updateOptions.value.withLatestSnapshots(false)
