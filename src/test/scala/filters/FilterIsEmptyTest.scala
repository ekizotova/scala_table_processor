package filters

import csvParts._
import filters.FilterIsEmpty

import org.scalatest.funsuite.AnyFunSuite

class FilterIsEmptyTest extends AnyFunSuite {

  test("Apply filter to a table with empty cells in the specified column") {
    val filter = FilterIsEmpty(column = 1)
    val table = Table(Map(
      Coordinate(0, 0) -> Number(3),  // Not empty
      Coordinate(0, 1) -> Empty,      // Empty
      Coordinate(1, 1) -> Empty,      // Empty
      Coordinate(2, 1) -> Number(7)   // Not empty
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map(
      Coordinate(0, 0) -> Number(3),
      Coordinate(0, 1) -> Empty,     
      Coordinate(1, 1) -> Empty      
    ))

    assert(resultTable == expectedTable)
  }

  test("Apply filter to a table with no empty cells in the specified column") {
    val filter = FilterIsEmpty(column = 1)
    val table = Table(Map(
      Coordinate(0, 0) -> Number(3), 
      Coordinate(0, 1) -> Number(6), 
      Coordinate(1, 1) -> Number(7),  
      Coordinate(2, 1) -> Number(4)   
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map())  

    assert(resultTable == expectedTable)
  }

  test("Apply filter to a table with all empty cells in the specified column") {
    val filter = FilterIsEmpty(column = 1)
    val table = Table(Map(
      Coordinate(0, 1) -> Empty,  
      Coordinate(1, 1) -> Empty,  
      Coordinate(2, 1) -> Empty   
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map(
      Coordinate(0, 1) -> Empty, //retained 
      Coordinate(1, 1) -> Empty,  
      Coordinate(2, 1) -> Empty   
    ))

    assert(resultTable == expectedTable)
  }

  test("Apply filter to a table with an empty table") {
    val filter = FilterIsEmpty(column = 1)
    val table = Table(Map())  // empty table, no rows

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map())  // no rows to filter

    assert(resultTable == expectedTable)
  }

  test("Apply filter with a column that has no matching coordinates") {
    val filter = FilterIsEmpty(column = 10)  //outbound
    val table = Table(Map(
      Coordinate(0, 0) -> Number(3),  
      Coordinate(1, 0) -> Number(6)   
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map())  
    
    assert(resultTable == expectedTable)
  }

  test("Apply filter where all cells in the column are non-empty") {
    val filter = FilterIsEmpty(column = 0)
    val table = Table(Map(
      Coordinate(0, 0) -> Number(3),  
      Coordinate(1, 0) -> Number(6),  
      Coordinate(2, 0) -> Number(7)   
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map())  

    assert(resultTable == expectedTable)
  }

  test("Apply filter with a column that contains both empty and non-empty cells") {
    val filter = FilterIsEmpty(column = 0)
    val table = Table(Map(
      Coordinate(0, 0) -> Empty,     
      Coordinate(1, 0) -> Number(3),  
      Coordinate(2, 0) -> Empty       
    ))

    val resultTable = filter.apply(table)
    val expectedTable = Table(Map(
      Coordinate(0, 0) -> Empty,     
      Coordinate(2, 0) -> Empty       
    ))

    assert(resultTable == expectedTable)
  }

}
