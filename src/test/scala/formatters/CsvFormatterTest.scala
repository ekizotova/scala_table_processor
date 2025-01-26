package formatters

import org.scalatest.funsuite.AnyFunSuite

import csvParts.{Coordinate, Cell, Table, Number, Empty, Formula}

class CsvFormatterTest extends AnyFunSuite {
  def createTable(cells: Map[Coordinate, Cell]): Table = Table(cells)

  test("format should correctly format a table without headers") {
    val cells = Map(
      Coordinate(0, 0) -> Number(10),
      Coordinate(0, 1) -> Number(20),
      Coordinate(1, 0) -> Number(30),
      Coordinate(1, 1) -> Empty
    )
    val table = createTable(cells)
    val formatter = new CsvFormatter(separator = ",", includeHeaders = false)

    val result = formatter.format(table)

    assert(result == "10, 20\n30,   ")
  }

  test("format should correctly format a table with headers") {
    val cells = Map(
      Coordinate(0, 0) -> Number(10),
      Coordinate(0, 1) -> Number(20),
      Coordinate(1, 0) -> Formula("A1 + 1"),
      Coordinate(1, 1) -> Empty
    )
    val table = createTable(cells)
    val formatter = new CsvFormatter(separator = ",", includeHeaders = true)

    val result = formatter.format(table)

    assert(result == " ,      A,  B\n1,     10, 20\n2, A1 + 1,   ")
  }

  // Test case 4: Table with an empty row
  test("format should handle an empty row") {
    val cells = Map(
      Coordinate(0, 0) -> Empty,
      Coordinate(0, 1) -> Empty
    )
    val table = createTable(cells)
    val formatter = new CsvFormatter(separator = ",", includeHeaders = false)

    val result = formatter.format(table)

    assert(result == "")
  }

  test("format should handle an empty table gracefully") {
    val table = createTable(Map(Coordinate(0, 0) -> Empty)) 
    val formatter = new CsvFormatter(separator = ",", includeHeaders = true)

    val result = formatter.format(table)

    assert(result == " , A\n") // Expect only the header row, graceful return of empty table is not implemented 
  }

  test("format should correctly handle tables with multiple rows and columns") {
    val cells = Map(
      Coordinate(0, 0) -> Number(1),
      Coordinate(0, 1) -> Number(2),
      Coordinate(1, 0) -> Number(3),
      Coordinate(1, 1) -> Number(4),
      Coordinate(2, 0) -> Formula("A1 + A2"),
      Coordinate(2, 1) -> Empty
    )
    val table = createTable(cells)
    val formatter = new CsvFormatter(separator = ",", includeHeaders = true)

    val result = formatter.format(table)

    assert(result == " ,       A, B\n1,       1, 2\n2,       3, 4\n3, A1 + A2,  ")
  }

  test("format should handle tables with more rows than columns") {
    val cells = Map(
      Coordinate(0, 0) -> Number(10),
      Coordinate(1, 0) -> Formula("A1 + 1"),
      Coordinate(2, 0) -> Empty
    )
    val table = createTable(cells)
    val formatter = new CsvFormatter(separator = ",", includeHeaders = false)

    val result = formatter.format(table)

    assert(result == "    10\nA1 + 1")
  }

  test("format should handle tables with some empty columns") {
    val cells = Map(
      Coordinate(0, 0) -> Number(5),
      Coordinate(1, 0) -> Empty,
      Coordinate(1, 1) -> Number(10),
      Coordinate(2, 1) -> Formula("B1 + 5")
    )
    val table = createTable(cells)
    val formatter = new CsvFormatter(separator = ",", includeHeaders = true)

    val result = formatter.format(table)

    assert(result == " , A,      B\n1, 5,       \n2,  ,     10\n3,  , B1 + 5")
  }

  test("format should support different separators like tab and semicolon") {
    val cells = Map(
      Coordinate(0, 0) -> Number(10),
      Coordinate(0, 1) -> Number(20)
    )
    val table = createTable(cells)
    val formatterComma = new CsvFormatter(separator = ",", includeHeaders = true)
    val formatterSemicolon = new CsvFormatter(separator = ";", includeHeaders = true)
    val formatterTab = new CsvFormatter(separator = "\t", includeHeaders = true)

    val resultComma = formatterComma.format(table)
    val resultSemicolon = formatterSemicolon.format(table)
    val resultTab = formatterTab.format(table)

    assert(resultComma == " ,  A,  B\n1, 10, 20")
    assert(resultSemicolon == " ;  A;  B\n1; 10; 20")
    assert(resultTab == " \t A\t B\n1\t10\t20")
  }
}
