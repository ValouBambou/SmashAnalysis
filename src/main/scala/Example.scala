import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors

object Example {
  def compute_win_rates(spark: SparkSession, sc: SparkContext, data_file_name: String): (Map[(String, String), Double], Map[(String, String), Int]) = {

    // Load local file data
    val df = spark.read.format("csv").option("header", "true")
      .load("resources/" + data_file_name)
      .select("Winner Character", "Loser Character")
    df.show()

    val characters = sc.textFile("resources/characters.csv").collect().toSet

    val wins_count = df.rdd
      .map(row => ((row(0).toString, row(1).toString), 1))
      .reduceByKey((acc, count) => acc + count)
      .filter(x => characters.contains(x._1._1) && characters.contains(x._1._2))
      .collect()
      .toMap

    println(wins_count)

    val win_rates = wins_count.map(it => {
      val winner = it._1._1
      val looser = it._1._2
      val num_wins = it._2.toDouble
      val total_match = num_wins + wins_count.getOrElse((looser, winner), 0).toDouble
      (it._1, num_wins / total_match)
    }).filter(x => characters.contains(x._1._1) && characters.contains(x._1._2))

    println(win_rates)

    sc.parallelize(wins_count.toSeq).saveAsTextFile("resources/win_count")
    sc.parallelize(win_rates.toSeq).saveAsTextFile("resources/win_rates")
    (win_rates, wins_count)
  }

  def array_of_winrate(characters: Array[String], i: Int, win_rates: Map[(String, String), Double]): Array[Double] = {
    Array.tabulate(characters.length)(j => {
      val winner_looser = (characters(i), characters(j))
      // in case the matchup was never played we put 50 %
      win_rates.getOrElse(winner_looser, 0.5)
    })
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
    var win_count = Map.empty[(String, String), Int]
    // get values from cached result if already computed or compute it
    try {
      win_rates = sc.textFile("resources/win_rates").map(s => {
        val tmp = s.replace("(", "").replace(")", "").split(",")
        ((tmp(0), tmp(1)), tmp(2).toDouble)
      }).collect().toMap

      win_count = sc.textFile("resources/win_count").map(s => {
        val tmp = s.replace("(", "").replace(")", "").split(",")
        ((tmp(0), tmp(1)), tmp(2).toInt)
      }).collect().toMap
    } catch {
      case e: Exception =>
        val tmp = compute_win_rates(spark, sc, "full_raw_data.csv")
        win_rates = tmp._1
        win_count = tmp._2
    }

    val characters = sc.textFile("resources/characters.csv").collect()
    val characters_to_int = characters.zipWithIndex.toMap
    val n_characters = characters.length

    val avg_win_rates = characters.map(character => {
      val n_total = win_count.filter(x => x._1._1 == character || x._1._2 == character).values.sum
      val n_wins = win_count.filter(x => x._1._1 == character).values.sum
      (character, n_wins, n_total, n_wins.toDouble / n_total)
    })

    avg_win_rates.sortBy(x => x._4).foreach(x => println(x))

    val matrix = List.tabulate(n_characters)(i => Vectors.dense(array_of_winrate(characters, i, win_rates)))
    val kmean_input = sc.parallelize(matrix)
    val clusters = KMeans.train(kmean_input, 3, 30)

    val classification = characters.map(character => {
      val kmean_data = matrix(characters_to_int(character))
      val cluster = clusters.predict(kmean_data)
      (character, cluster)
    })

    classification.groupBy(x => x._2).map(x => (x._1, x._2.map(y=> y._1).mkString(", "))).foreach(x => println(x))
  }
}