package elements

import csvParts._

import org.scalatest.flatspec.AnyFlatSpec

class CellTest extends AnyFlatSpec {
  "transform" should "apply the function to Number values" in {
    val number = Number(10)
    val result = number.transform(_ * 2)
    assert(result == Number(20))
  }

  it should "not change Empty" in {
    val empty = Empty
    val result = empty.transform(_ * 2)
    assert(result == Empty)
  }

  it should "not change Formula" in {
    val formula = Formula("A1 + B2")
    val result = formula.transform(_ * 2)
    assert(result == formula)  // assuming Formula doesn't transform
  }
}
