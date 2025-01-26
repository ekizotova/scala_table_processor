package filters

import org.scalatest.funsuite.AnyFunSuite
import csvParts.{Table, Coordinate, Number, Empty}
import filters.ColumnConditionFilter

class ColumnConditionFilterTest extends AnyFunSuite {
  test("Apply filter to a table with matching numeric values") {
    val filter = ColumnConditionFilter(column = 1, condition = (x: Int) => x > 5)
    val table = Table(Map(
      Coordinate(0, 0) -> Number(3),
      Coordinate(0, 1) -> Number(6),
      Coordinate(1, 1) -> Number(7),
      Coordinate(2, 1) -> Number(4)
    ))

    val resultTable = filter.apply(table)

    val expectedTable = Table(Map(
      Coordinate(0, 0) -> Number(3),
      Coordinate(0, 1) -> Number(6),
      Coordinate(1, 0) -> Empty,
      Coordinate(1, 1) -> Number(7),
    ))

    val resultRows = resultTable.rows
    val expectedRows = expectedTable.rows

    assert(resultRows == expectedRows)
  }


  test("Apply filter to a table with mixed non-numeric and numeric cells") {
    val filter = ColumnConditionFilter(column = 1, condition = (x: Int) => x > 5)
    val table = Table(Map(
      Coordinate(0, 0) -> Empty,
      Coordinate(0, 1) -> Number(6),
      Coordinate(1, 1) -> Number(7),
      Coordinate(2, 1) -> Empty,
      Coordinate(3, 1) -> Number(4)
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map(
      Coordinate(0, 1) -> Number(6),
      Coordinate(1, 1) -> Number(7)
    ))

    val resultRows = resultTable.rows
    val expectedRows = expectedTable.rows

    assert(resultRows == expectedRows)
  }

  test("Apply filter to an empty table") {
    val filter = ColumnConditionFilter(column = 1, condition = (x: Int) => x > 5)
    val table = Table(Map.empty)  // Empty table

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map.empty)  // Should still be empty

    assert(resultTable == expectedTable)
  }

  test("Apply filter to a table with no matching rows") {
    val filter = ColumnConditionFilter(column = 1, condition = (x: Int) => x == 0)
    val table = Table(Map(
      Coordinate(0, 1) -> Number(5),
      Coordinate(1, 1) -> Number(10),
      Coordinate(2, 1) -> Number(15)
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map.empty)  // No rows match

    assert(resultTable == expectedTable)
  }

  test("Apply filter with a condition that always returns false") {
    val filter = ColumnConditionFilter(column = 1, condition = (x: Int) => false)
    val table = Table(Map(
      Coordinate(0, 1) -> Number(5),
      Coordinate(1, 1) -> Number(10),
      Coordinate(2, 1) -> Number(15)
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map.empty)  // No rows match

    assert(resultTable == expectedTable)
  }

  test("Apply filter with a condition that always returns true") {
    val filter = ColumnConditionFilter(column = 1, condition = (x: Int) => true)
    val table = Table(Map(
      Coordinate(0, 1) -> Number(5),
      Coordinate(1, 1) -> Number(10),
      Coordinate(2, 1) -> Number(15)
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map(
      Coordinate(0, 1) -> Number(5),
      Coordinate(1, 1) -> Number(10),
      Coordinate(2, 1) -> Number(15)
    ))

    assert(resultTable == expectedTable)
  }

  test("Apply filter to a table with no cells in the target column") {
    val filter = ColumnConditionFilter(column = 2, condition = (x: Int) => x > 5)
    val table = Table(Map(
      Coordinate(0, 0) -> Number(3),
      Coordinate(1, 1) -> Number(6),
      Coordinate(2, 0) -> Number(4)
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map.empty)  // No cells in column 2

    assert(resultTable == expectedTable)
  }

  test("Apply filter with column index out of bounds") {
    val filter = ColumnConditionFilter(column = 10, condition = (x: Int) => x > 5)
    val table = Table(Map(
      Coordinate(0, 0) -> Number(3),
      Coordinate(1, 1) -> Number(6),
      Coordinate(2, 0) -> Number(4)
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map.empty)  // No cells in the out-of-bounds column

    assert(resultTable == expectedTable)
  }
}
