package filters

import org.scalatest.funsuite.AnyFunSuite
import csvParts._
import filters._

class CompositeFilterTest extends AnyFunSuite {
  def createSampleTable: Table = {
    Table(Map(
      Coordinate(0, 0) -> Number(10),
      Coordinate(0, 1) -> Number(20),
      Coordinate(1, 0) -> Number(30),
      Coordinate(1, 1) -> Empty,
      Coordinate(2, 0) -> Number(50),
      Coordinate(2, 1) -> Number(60)
    ))
  }

  test("CompositeFilter should apply multiple filters sequentially, keeping rows with non-empty values") {
    val table = createSampleTable

    val compositeFilter = new CompositeFilter(Seq(
      FilterIsNotEmpty(1),
      FilterIsEmpty(0)
    ))

    val filteredTable = compositeFilter.apply(table)

    assert(!filteredTable.cells.contains(Coordinate(0, 1)))
    assert(!filteredTable.cells.contains(Coordinate(2, 1)))
    assert(!filteredTable.cells.contains(Coordinate(1, 1)))  // Should be filtered out
  }
}
