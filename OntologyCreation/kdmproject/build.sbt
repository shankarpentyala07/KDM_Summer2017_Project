name := "kdmproject"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.6.1" % "provided",
  "org.apache.spark" %% "spark-streaming" % "1.6.1",
  "org.apache.spark" %% "spark-mllib" % "1.6.1",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models",
  "edu.stanford.nlp" % "stanford-parser" % "3.6.0",
  "com.google.protobuf" % "protobuf-java" % "2.6.1",
  "net.sourceforge.owlapi" % "owlapi-distribution" % "5.1.0",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.2",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.2",
  "net.sourceforge.owlapi" % "owlapi-distribution" % "3.4.3",
  "com.hermit-reasoner" % "org.semanticweb.hermit" % "1.3.8.4"


)
        