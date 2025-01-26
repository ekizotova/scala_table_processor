package filters

import filters.FilterIsNotEmpty
import csvParts._

import org.scalatest.funsuite.AnyFunSuite

class FilterIsNotEmptyTest extends AnyFunSuite {

  test("Apply filter to a table with non-empty cells in the specified column") {
    val filter = FilterIsNotEmpty(column = 1)
    val table = Table(Map(
      Coordinate(0, 0) -> Number(3),
      Coordinate(0, 1) -> Number(6),  
      Coordinate(1, 1) -> Empty,      // Empty, will be excluded
      Coordinate(2, 1) -> Number(7)   
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map(
      Coordinate(0, 0) -> Number(3),
      Coordinate(0, 1) -> Number(6),  
      Coordinate(2, 1) -> Number(7)  
    ))

    assert(resultTable == expectedTable)
  }

  test("Apply filter to a table with no non-empty cells in the specified column") {
    val filter = FilterIsNotEmpty(column = 1)
    val table = Table(Map(
      Coordinate(0, 1) -> Empty,
      Coordinate(1, 1) -> Empty,  
      Coordinate(2, 1) -> Empty   
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map()) 

    assert(resultTable == expectedTable)
  }

  test("Apply filter to a table with all non-empty cells in the specified column") {
    val filter = FilterIsNotEmpty(column = 1)
    val table = Table(Map(
      Coordinate(0, 1) -> Number(6),  
      Coordinate(1, 1) -> Number(7),  
      Coordinate(2, 1) -> Number(4)   
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map(
      Coordinate(0, 1) -> Number(6),  
      Coordinate(1, 1) -> Number(7),  
      Coordinate(2, 1) -> Number(4)   
    ))

    assert(resultTable == expectedTable)
  }

  test("Apply filter to a table with an empty table") {
    val filter = FilterIsNotEmpty(column = 1)
    val table = Table(Map())  // empty table, no rows

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map())  // no rows to filter

    assert(resultTable == expectedTable)
  }

  test("Apply filter with a column that has no matching coordinates") {
    val filter = FilterIsNotEmpty(column = 10)  
    val table = Table(Map(
      Coordinate(0, 0) -> Number(3), 
      Coordinate(1, 0) -> Number(6)  
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map())  

    assert(resultTable == expectedTable)
  }

  test("Apply filter where all cells in the column are empty") {
    val filter = FilterIsNotEmpty(column = 0)
    val table = Table(Map(
      Coordinate(0, 0) -> Empty,  
      Coordinate(1, 0) -> Empty,  
      Coordinate(2, 0) -> Empty  
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map())  // nothing retained

    assert(resultTable == expectedTable)
  }

  test("Apply filter where some cells in the column are non-empty") {
    val filter = FilterIsNotEmpty(column = 0)
    val table = Table(Map(
      Coordinate(0, 0) -> Number(3),  
      Coordinate(1, 0) -> Empty,     
      Coordinate(2, 0) -> Number(7)   
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map(
      Coordinate(0, 0) -> Number(3), 
      Coordinate(2, 0) -> Number(7)   
    ))

    assert(resultTable == expectedTable)
  }

  test("Apply filter with a column containing both non-empty and empty cells") {
    val filter = FilterIsNotEmpty(column = 1)
    val table = Table(Map(
      Coordinate(0, 1) -> Empty,      
      Coordinate(1, 1) -> Number(5),  
      Coordinate(2, 1) -> Empty,      
      Coordinate(3, 1) -> Number(7)   
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map(
      Coordinate(1, 1) -> Number(5),  
      Coordinate(3, 1) -> Number(7)   
    ))

    assert(resultTable == expectedTable)
  }
}

