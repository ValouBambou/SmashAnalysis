name := "SmashAnalysis"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies++=Seq(
  "org.scala-lang"%"scala-library"%"2.13.8",
  "org.apache.spark"%"spark-core_2.13"%"3.2.0",
  "org.apache.spark"%"spark-sql_2.13"%"3.2.0",
  "org.apache.spark"%"spark-mllib_2.13"%"3.2.0",
  "org.plotly-scala"%"plotly-core_2.13"%"0.8.2"
)
