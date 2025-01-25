package parsers
import csvParts.{Cell, Table, Coordinate, Empty, Number, Formula}

trait TableParser {
  def parse(input: String): Table
}

//def parse(input: String, separator: Char = ','): Table
