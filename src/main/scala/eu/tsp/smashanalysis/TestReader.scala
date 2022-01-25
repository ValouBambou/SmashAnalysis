package eu.tsp.smashanalysis

object TestReader {
  def main(args: Array[String]): Unit =  {
    val matches = new MatchCSVReader("resources/sample_data.csv").readMatches()
    println(matches.size)
    println(matches(283))
    println(matches(284))
    println(matches.count( B => B.Stage.equals("Battlefield") && B.Winner.equals("Donkey Kong")))
    println(matches.filter(B => B.Stage.equals("Battlefield") && B.Winner.equals("Donkey Kong")))
    println(matches.map(_.Winner).distinct)
    println(matches.map(_.Stage).distinct)
  }
}
