package eu.tsp.smashanalysis

import scala.io.Source

class MatchCSVReader(val fileName: String) extends MatchReader {
  def readMatches(): Seq[Match] = {

    for {
      line <- Source.fromFile(fileName).getLines().drop(1).toVector
      values = line.split(",").map(_.trim)

    } yield Match(values(7), values(values.length - 2), values(values.length - 1))
  }
}
