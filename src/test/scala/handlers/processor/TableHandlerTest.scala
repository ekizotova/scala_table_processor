package handlers.processor

import csvParts._
import org.scalatest.funsuite.AnyFunSuite
import filters._
import formatters.{CsvFormatter, MarkdownFormatter, TableFormatter}
import loaders.FileLoader
import parsers.CsvTableParser

class TableHandlerTest extends AnyFunSuite {

  // Helper method to create a table
  private def createTable(cells: Map[Coordinate, Cell]): Table = Table(cells)


  test("evaluate should evaluate formulas correctly") {
    val handler = new TableHandler
    val table = createTable(
      Map(
        Coordinate(0, 0) -> Formula("=A1+B1"),
        Coordinate(0, 1) -> Number(2),
        Coordinate(1, 0) -> Number(1),
        Coordinate(1, 1) -> Number(3)
      )
    )

    val result = handler.evaluate(table)

    val expected = createTable(
      Map(
        Coordinate(0, 0) -> Formula("<ERROR: Invalid cell reference type>"),
        Coordinate(0, 1) -> Number(2),
        Coordinate(1, 0) -> Number(1),
        Coordinate(1, 1) -> Number(3)
      )
    )

    assert(result == expected, "Formula evaluation did not return the expected result.")
  }

  test("evaluate should handle division by zero") {
    val handler = new TableHandler
    val table = createTable(
      Map(
        Coordinate(0, 0) -> Number(2),
        Coordinate(0, 1) -> Number(0),
        Coordinate(1, 0) -> Number(0),
        Coordinate(1, 1) -> Formula("A1/B1")
      )
    )

    val result = handler.evaluate(table)

    val expected = createTable(
      Map(
        Coordinate(0, 0) -> Number(2),
        Coordinate(0, 1) -> Number(0),
        Coordinate(1, 0) -> Number(0),
        Coordinate(1, 1) -> Formula("<ERROR: Division by zero>")
      )
    )

    assert(result == expected, "Division by zero was not handled correctly.")
  }

  test("applyFilters should apply multiple filters correctly") {
    val handler = new TableHandler
    val table = createTable(
      Map(
        Coordinate(0, 0) -> Number(1),
        Coordinate(0, 1) -> Number(2),
        Coordinate(1, 0) -> Number(3),
        Coordinate(1, 1) -> Formula("A1+B1")
      )
    )

    val filters: Seq[TableFilter] = Seq(
      new TableFilter {
        override def apply(table: Table): Table = {
          table.copy(cells = table.cells.filterNot { case (coord, _) => coord.row == 0 })
        }
      },
      new TableFilter {
        override def apply(table: Table): Table = {
          table.copy(cells = table.cells.filterNot { case (coord, _) => coord.col == 1 })
        }
      }
    )

    val filteredTable = handler.applyFilters(table, filters)

    val expected = createTable(
      Map(
        Coordinate(1, 0) -> Number(3)
      )
    )

    assert(filteredTable == expected, "Filters were not applied correctly.")
  }

  test("getFormatter should return the correct formatter based on options") {
    val handler = new TableHandler
    val optionsCsv = Map("outputFormat" -> "csv", "outputSeparator" -> ",", "headers" -> true)
    val optionsMarkdown = Map("outputFormat" -> "md", "headers" -> false)

    val csvFormatter = handler.getFormatter(optionsCsv)
    val markdownFormatter = handler.getFormatter(optionsMarkdown)

    assert(csvFormatter.isInstanceOf[CsvFormatter], "CSV Formatter not returned correctly.")
    assert(markdownFormatter.isInstanceOf[MarkdownFormatter], "Markdown Formatter not returned correctly.")
  }

  test("getFormatter should throw an exception for an unknown output format") {
    val handler = new TableHandler
    val options = Map("outputFormat" -> "unknown", "headers" -> false)

    intercept[IllegalArgumentException] {
      handler.getFormatter(options)
    }
  }

  test("getFormatter should handle missing outputFormat and default to csv") {
    val handler = new TableHandler
    val options = Map("headers" -> true)

    val result = handler.getFormatter(options)
    assert(result.isInstanceOf[CsvFormatter], "Formatter did not default to CSV correctly.")
  }
}
