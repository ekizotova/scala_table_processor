package formatters

import csvParts._

import org.scalatest.funsuite.AnyFunSuite

class MarkdownFormatterTest extends AnyFunSuite {

  private def createTable(cells: Map[Coordinate, Cell]): Table = Table(cells)

  test("format should handle an empty table gracefully without headers") {
    val table = createTable(Map(Coordinate(0, 0) -> Empty))
    val formatter = new MarkdownFormatter(includeHeaders = false)

    val result = formatter.format(table)

    assert(result == "|   |\n|---|\n")
  }
  

  test("MarkdownFormatter should throw an exception for invalid table input") {
    val table = null //  an invalid input
    val formatter = new MarkdownFormatter()

    assertThrows[NullPointerException] {
      formatter.format(table)
    }
  }
}
