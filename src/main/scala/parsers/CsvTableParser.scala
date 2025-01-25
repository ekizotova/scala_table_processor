package parsers

import csvParts.*

import scala.util.Try

class CsvTableParser(separator: String = ",") extends TableParser {
  override def parse(input: String): Table = {
    // Split input into rows and parse each cell
    val rows = input.split("\n").zipWithIndex.flatMap {
      case (line, rowIndex) =>
        line.split(separator).zipWithIndex.map {
          case (cellValue, colIndex) =>
            val coordinate = Coordinate(rowIndex, colIndex)
            val cell = cellValue.trim match {
              case "" => Empty // Empty cell
              case num if num.matches("\\d+") => Number(num.toInt) // Positive integers
              case formula if formula.startsWith("=") => Formula(formula) // Simple formula (starts with '=')
              case _ => Empty
            }
            coordinate -> cell
        }
    }
    Table(rows.toMap)
  }
}
