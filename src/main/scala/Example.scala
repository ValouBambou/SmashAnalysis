import org.apache.spark.{SparkConf, SparkContext}

object Example {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Spark Job for Loading Data").setMaster("local[*]") // local[*] will access all core of your machine
    val sc = new SparkContext(conf) // Create Spark Context
    // Load local file data
    val emp_data = sc.textFile("ressources/example_data.csv") // It will return a RDD
    // Read the records
    println(emp_data.foreach(println))
  }
}