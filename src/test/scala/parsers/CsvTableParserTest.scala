package parsers

import csvParts.*

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CsvTableParserTest extends AnyFunSuite with Matchers {
  val parser = new CsvTableParser()

  test("Parse simple CSV with integers") {
    val input =
      """1,2,3
        |4,5,6
        |7,8,9""".stripMargin
    val table = parser.parse(input)

    table.get(Coordinate(0, 0)) shouldEqual Option(Number(1))
    table.get(Coordinate(1, 1)) shouldEqual Option(Number(5))
    table.get(Coordinate(2, 2)) shouldEqual Option(Number(9))
    table.get(Coordinate(1, 0)) shouldEqual Option(Number(4))
  }

  test("Parse CSV with empty cells") {
    val input =
      """1,,3
        |,5,
        |7,8,""".stripMargin
    val table = parser.parse(input)

    table.get(Coordinate(0, 1)) shouldEqual Option(Empty)
    table.get(Coordinate(1, 0)) shouldEqual Option(Empty)
    table.get(Coordinate(2, 2)) shouldEqual None

    table.get(Coordinate(0, 0)) shouldEqual Option(Number(1))
    table.get(Coordinate(1, 1)) shouldEqual Option(Number(5))
    table.get(Coordinate(2, 0)) shouldEqual Option(Number(7))
    table.get(Coordinate(2, 1)) shouldEqual Option(Number(8))
  }

  test("Parse CSV with formulas") {
    val input =
      """=A1+B1,2,=C3*5
        |,=A2*2,""".stripMargin
    val table = parser.parse(input)

    table.get(Coordinate(0, 0)) shouldEqual Option(Formula("=A1+B1"))
    table.get(Coordinate(0, 2)) shouldEqual Option(Formula("=C3*5"))
    table.get(Coordinate(1, 1)) shouldEqual Option(Formula("=A2*2"))
  }

  test("Parse CSV with mixed content (numbers, empty, formulas)") {
    val input =
      """=A1+B1,,3
        |,5,=C3*2
        |7,,=A3+B3""".stripMargin
    val table = parser.parse(input)

    table.get(Coordinate(0, 0)) shouldEqual Option(Formula("=A1+B1"))
    table.get(Coordinate(0, 2)) shouldEqual Option(Number(3))
    table.get(Coordinate(1, 1)) shouldEqual Option(Number(5))
    table.get(Coordinate(1, 2)) shouldEqual Option(Formula("=C3*2"))
    table.get(Coordinate(2, 0)) shouldEqual Option(Number(7))
    table.get(Coordinate(2, 2)) shouldEqual Option(Formula("=A3+B3"))
  }

  test("Parse empty input string") {
    val input = ""
    val table = parser.parse(input)

    table.rows shouldEqual Vector(Vector(Empty))
  }

  test("Parse input with custom separator") {
    val input =
      """1;2;3
         4;5;6
         7;8;9""".stripMargin
    val customParser = new CsvTableParser(separator = ";")
    val table = customParser.parse(input)

    table.get(Coordinate(0, 0)) shouldEqual Option(Number(1))
    table.get(Coordinate(1, 1)) shouldEqual Option(Number(5))
    table.get(Coordinate(2, 2)) shouldEqual Option(Number(9))
  }

  test("Parse malformed CSV with inconsistent rows") {
    val input =
      """1,2
        |4,5,6
        |7""".stripMargin
    val table = parser.parse(input)

    table.get(Coordinate(0, 0)) shouldEqual Option(Number(1))
    table.get(Coordinate(0, 1)) shouldEqual Option(Number(2))
    table.get(Coordinate(1, 2)) shouldEqual Option(Number(6))
    table.get(Coordinate(2, 0)) shouldEqual Option(Number(7))
    table.get(Coordinate(2, 1)) shouldEqual None
    table.get(Coordinate(2, 2)) shouldEqual None
  }

  test("Parse CSV with trailing spaces") {
    val input =
      """ 1 ,  , =C3 * 5
        |   , 5 ,   """.stripMargin
    val table = parser.parse(input)

    table.get(Coordinate(0, 0)) shouldEqual Option(Number(1))
    table.get(Coordinate(0, 1)) shouldEqual Option(Empty)
    table.get(Coordinate(0, 2)) shouldEqual Option(Formula("=C3 * 5"))
    table.get(Coordinate(1, 1)) shouldEqual Option(Number(5))
    table.get(Coordinate(1, 2)) shouldEqual Option(Empty)
  }

  test("Handle invalid numeric input gracefully") {
    val input =
      """abc,123,=SUM(A1:A3)
        |2.5,,""".stripMargin
    val table = parser.parse(input)

    table.get(Coordinate(0, 0)) shouldEqual Option(Empty) // "abc" is not a valid numeric input, can be changed to parse as an error cell
    table.get(Coordinate(0, 1)) shouldEqual Option(Number(123))
    table.get(Coordinate(0, 2)) shouldEqual Option(Formula("=SUM(A1:A3)"))
    table.get(Coordinate(1, 0)) shouldEqual Option(Empty) // "2.5" is not an integer, can be changed to parse as an error cell
  }

  test("Parse single-row CSV") {
    val input = "1,2,3"
    val table = parser.parse(input)

    table.get(Coordinate(0, 0)) shouldEqual Option(Number(1))
    table.get(Coordinate(0, 1)) shouldEqual Option(Number(2))
    table.get(Coordinate(0, 2)) shouldEqual Option(Number(3))
  }

  test("Parse single-column CSV") {
    val input =
      """1
        |2
        |3""".stripMargin
    val table = parser.parse(input)

    table.get(Coordinate(0, 0)) shouldEqual Option(Number(1))
    table.get(Coordinate(1, 0)) shouldEqual Option(Number(2))
    table.get(Coordinate(2, 0)) shouldEqual Option(Number(3))
  }

  test("Parse CSV with completely empty rows") {
    val input =
      """1,2,3
        |
        |7,8,9""".stripMargin
    val table = parser.parse(input)

    table.get(Coordinate(1, 0)) shouldEqual Option(Empty)
    table.get(Coordinate(2, 0)) shouldEqual Option(Number(7))
  }
}
