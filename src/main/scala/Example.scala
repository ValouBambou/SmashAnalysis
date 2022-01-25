import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}


object Example {
  def main(args: Array[String]): Unit = {
    var conf = new SparkConf().setAppName("Read CSV File").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val spark = SparkSession
      .builder()
      .appName("Spark SQL basic example")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    // Load local file data
    val df = spark.read.format("csv").option("header", "true").load("resources/sample_data.csv")
    df.printSchema()
    df.show()
  }
}