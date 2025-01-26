package elements

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import csvParts._

class TableTest extends AnyFunSuite with Matchers {
  // Helper to create a simple table for tests
  private def createTestTable(): Table = {
    Table(Map(
      Coordinate(0, 0) -> Number(1),
      Coordinate(0, 1) -> Number(2),
      Coordinate(1, 0) -> Empty,
      Coordinate(1, 1) -> Formula("A1 + B1") 
    ))
  }

  test("get should return the correct cell at a given coordinate") {
    val table = createTestTable()
    table.get(Coordinate(0, 0)) shouldEqual Option(Number(1))
    table.get(Coordinate(1, 1)) shouldEqual Option(Formula("A1 + B1"))
  }

  test("get should return None for coordinates that do not exist") {
    val table = createTestTable()
    table.get(Coordinate(2, 2)) shouldEqual None
  }

  test("insert should add a new cell or update an existing cell") {
    val table = createTestTable()
    val updatedTable = table.insert(Coordinate(0, 2), Number(5))
    updatedTable.get(Coordinate(0, 2)) shouldEqual Option(Number(5))
  }

  test("insert should overwrite an existing cell value") {
    val table = createTestTable()
    val updatedTable = table.insert(Coordinate(0, 1), Number(10))
    updatedTable.get(Coordinate(0, 1)) shouldEqual Option(Number(10))
  }

  test("map should apply a transformation function to all cells") {
    val table = createTestTable()
    val transformedTable = table.map {
      case Number(value) => Number(value * 2) 
      case cell => cell 
    }

    transformedTable.get(Coordinate(0, 0)) shouldEqual Option(Number(2))
    transformedTable.get(Coordinate(0, 1)) shouldEqual Option(Number(4))
    transformedTable.get(Coordinate(1, 0)) shouldEqual Option(Empty)
    transformedTable.get(Coordinate(1, 1)) shouldEqual Option(Formula("A1 + B1"))
  }

  test("rows should convert the table to a row-major Vector of Vectors") {
    val table = createTestTable()
    val expectedRows = Vector(
      Vector(Number(1), Number(2)),
      Vector(Empty, Formula("A1 + B1"))
    )

    table.rows shouldEqual expectedRows
  }

  test("rows should handle an empty table") {
    val emptyTable = Table(Map.empty)
    emptyTable.rows shouldEqual Vector.empty
  }

  test("rows should fill in missing cells with Empty") {
    val sparseTable = Table(Map(
      Coordinate(0, 0) -> Number(1),
      Coordinate(2, 2) -> Number(9)
    ))

    val expectedRows = Vector(
      Vector(Number(1), Empty, Empty),
      Vector(Empty, Empty, Empty),
      Vector(Empty, Empty, Number(9))
    )

    sparseTable.rows shouldEqual expectedRows
  }

  test("map should not modify Empty or Formula cells when applying transformations") {
    val table = createTestTable()
    val transformedTable = table.map {
      case Number(value) => Number(value * 3)
      case other => other 
    }

    transformedTable.get(Coordinate(0, 0)) shouldEqual Option(Number(3))
    transformedTable.get(Coordinate(1, 0)) shouldEqual Option(Empty)
    transformedTable.get(Coordinate(1, 1)) shouldEqual Option(Formula("A1 + B1"))
  }

  test("insert should handle overwriting Empty cells with Number cells") {
    val table = createTestTable()
    val updatedTable = table.insert(Coordinate(1, 0), Number(42))
    updatedTable.get(Coordinate(1, 0)) shouldEqual Option(Number(42))
  }

  test("insert should handle overwriting Formula cells with Number cells") {
    val table = createTestTable()
    val updatedTable = table.insert(Coordinate(1, 1), Number(99))
    updatedTable.get(Coordinate(1, 1)) shouldEqual Option(Number(99))
  }

  test("rows should return a single row if all cells are in one row") {
    val singleRowTable = Table(Map(
      Coordinate(0, 0) -> Number(5),
      Coordinate(0, 1) -> Number(6)
    ))

    singleRowTable.rows shouldEqual Vector(
      Vector(Number(5), Number(6))
    )
  }
}
