package parsers

import csvParts.{NumberNode, CellReferenceNode, BinaryOperationNode, FormulaAST, ParenthesisNode}

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class FormulaParserTest extends AnyFunSuite with Matchers {
  val operators: Map[String, (Int, Int) => Int] = Map(
    "+" -> (_ + _),
    "-" -> (_ - _),
    "*" -> (_ * _),
    "/" -> ((a, b) => if (b == 0) throw new ArithmeticException("Division by zero") else a / b)
  )
  val parser = new FormulaParser(operators)

  test("Parse a simple number formula") {
    val formula = "42"
    val result = parser.parseFormula(formula)

    result shouldEqual Right(NumberNode(42))
  }

  test("Parse a simple cell reference formula") {
    val formula = "A1"
    val result = parser.parseFormula(formula)

    result shouldEqual Right(CellReferenceNode("A1"))
  }

  test("Parse a simple addition formula") {
    val formula = "5+3"
    val result = parser.parseFormula(formula)

    result shouldEqual Right(BinaryOperationNode(NumberNode(5), "+", NumberNode(3)))
  }

  test("Parse a formula with mixed cell references and numbers") {
    val formula = "B2+7"
    val result = parser.parseFormula(formula)

    result shouldEqual Right(BinaryOperationNode(CellReferenceNode("B2"), "+", NumberNode(7)))
  }

  test("Parse a formula with multiple operators") {
    val formula = "10*3-4"
    val result = parser.parseFormula(formula)

    result shouldEqual Right(
      BinaryOperationNode(
        BinaryOperationNode(NumberNode(10), "*", NumberNode(3)),
        "-",
        NumberNode(4)
      )
    )
  }

  test("Handle parentheses in a formula") {  // not implemented but parsing works
    val formula = "(3+2)*5"
    val result = parser.parseFormula(formula)

    result shouldEqual Right(
      BinaryOperationNode(
        BinaryOperationNode(NumberNode(3), "+", NumberNode(2)), "*", NumberNode(5)))
  }

  test("Parse an empty formula") {
    val formula = ""
    val result = parser.parseFormula(formula)

    result shouldEqual Left("Empty formula")
  }

  /*
  //todo should be corrected 
  test("Parse a formula with unsupported characters") {
    val formula = "5+3@"
    val result = parser.parseFormula(formula)

    result shouldEqual Left("Invalid token: @")
  }*/

  test("Parse a formula with only operators") {
    val formula = "+-*/"
    val result = parser.parseFormula(formula)

    result shouldEqual Left("Invalid formula structure")
  }

  test("Handle invalid structure with dangling operator") {
    val formula = "5+"
    val result = parser.parseFormula(formula)

    result shouldEqual Left("Invalid formula structure")
  }
}
