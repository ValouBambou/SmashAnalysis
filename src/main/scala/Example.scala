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
    val df = spark.read.format("csv").option("header", "true")
      .load("resources/sample_data.csv")
      .select("Stage Name", "Winner Character", "Loser Character")
    df.show()


    val character_to_int = sc.textFile("resources/characters.csv").collect()
    val n_characters = character_to_int.length

    val wins_matrix = Array.ofDim[Int](n_characters, n_characters)
    wins_matrix.foreach(it => println(it.mkString("[", ", ", "]")))

  }
}