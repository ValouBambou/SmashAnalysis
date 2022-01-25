import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

object Example {
  def compute_win_rates(spark: SparkSession, sc: SparkContext, data_file_name:String): Map[(String, String), Double] = {

    // Load local file data
    val df = spark.read.format("csv").option("header", "true")
      .load("resources/" + data_file_name)
      .select("Winner Character", "Loser Character")
    df.show()

    val wins_count = df.rdd
      .map(row => ((row(0).toString, row(1).toString), 1))
      .reduceByKey((acc, count) => acc + count)
      .collect()
      .toMap

    println(wins_count)

    val win_rates = wins_count.map(it => {
      val winner = it._1._1
      val looser = it._1._2
      val num_wins = it._2.toDouble
      val total_match = num_wins + wins_count.getOrElse((looser, winner), 0).toDouble
      (it._1, num_wins / total_match)
    })
    println(win_rates)

    sc.parallelize(win_rates.toSeq).saveAsTextFile("resources/win_rates")
    win_rates
  }

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Read CSV File").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val spark = SparkSession
      .builder()
      .appName("Spark SQL basic example")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    var win_rates = Map.empty[(String, String), Double]
    try {
      win_rates = sc.textFile("resources/win_rates").map(s => {
        val tmp = s.replace("(", "").replace(")", "").split(",")
        ((tmp(0), tmp(1)), tmp(2).toDouble)
      }).collect().toMap
    } catch {
      case e: Exception => win_rates = compute_win_rates(spark, sc, "sample_data.csv")
    }
    println(win_rates)
  }
}