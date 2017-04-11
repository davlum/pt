name := """pivot-table"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  javaCore,
  filters,
  "org.postgresql" % "postgresql" % "9.4.1212.jre7",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.jooq" % "jooq" % "3.9.0",
  "org.jooq" % "jooq-meta" % "3.9.0",
  "org.apache.commons" % "commons-math3" % "3.6.1",
  "org.apache.poi" % "poi" % "3.14",
  "org.apache.poi" % "poi-ooxml" % "3.14",
  "com.typesafe.play" %% "play-mailer" % "5.0.0",
  "com.itextpdf" % "itextpdf" % "5.5.11",
  "com.itextpdf.tool" % "xmlworker" % "5.5.11"
)
