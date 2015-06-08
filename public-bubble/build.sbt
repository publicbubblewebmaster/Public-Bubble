name := """public-bubble"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  "com.typesafe.play" %% "anorm" % "2.4.0",
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc41",
  "com.cloudinary" % "cloudinary-http42" % "1.1.3",
  "com.google.api-client" % "google-api-client" % "1.20.0"
)
