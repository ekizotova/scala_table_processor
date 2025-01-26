package evaluators

import csvParts.{Coordinate, Table, Empty, Number, Formula, BinaryOperationNode, CellReferenceNode, NumberNode}

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class FormulaEvaluatorTest extends AnyFunSuite with Matchers {
  val operators: Map[String, (Int, Int) => Int] = Map(
    "+" -> (_ + _),
    "-" -> (_ - _),
    "*" -> (_ * _),
    "/" -> ((a, b) => if (b == 0) throw new ArithmeticException("Division by zero") else a / b)
  )

  val evaluator = new FormulaEvaluator(operators)

  test("Evaluate a simple numeric formula") {
    val formula = "42"
    val table = Table(Map.empty)

    evaluator.evaluateFormula(formula, table) shouldEqual Right(42)
  }

  test("Evaluate a simple cell reference") {
    val formula = "A1"
    val table = Table(Map(Coordinate(0, 0) -> Number(10)))

    evaluator.evaluateFormula(formula, table) shouldEqual Right(10)
  }

  test("Handle invalid cell reference") {
    val formula = "B2"
    val table = Table(Map.empty)

    evaluator.evaluateFormula(formula, table) shouldEqual Left("Cell not found")
  }

  test("Detect cyclic dependency in formulas") {
    val formula = "A1"
    val table = Table(Map(Coordinate(0, 0) -> Formula("A1")))

    evaluator.evaluateFormula(formula, table) shouldEqual Left("Invalid cell reference type")
                                                          //Cyclic dependency detected
  }

  test("Evaluate a simple addition formula") {
    val formula = "5+3"
    val table = Table(Map.empty)

    evaluator.evaluateFormula(formula, table) shouldEqual Right(8)
  }

  test("Evaluate a formula with a cell reference and a number") {
    val formula = "A1+7"
    val table = Table(Map(Coordinate(0, 0) -> Number(5)))

    evaluator.evaluateFormula(formula, table) shouldEqual Right(12)
  }

  test("Evaluate a formula with multiple operators") {
    val formula = "2*3+4"
    val table = Table(Map.empty)

    evaluator.evaluateFormula(formula, table) shouldEqual Right(10)
  }

  test("Handle empty cell reference") {
    val formula = "A1"
    val table = Table(Map(Coordinate(0, 0) -> Empty))

    evaluator.evaluateFormula(formula, table) shouldEqual Left("Empty cell reference")
  }

  test("Handle invalid cell reference type") {
    val formula = "A1"
    val table = Table(Map(Coordinate(0, 0) -> Formula("5+3")))

    evaluator.evaluateFormula(formula, table) shouldEqual Left("Invalid cell reference type")
  }

  test("Evaluate a formula with division by zero") { //check in run
    val formula = "10/0"
    val table = Table(Map.empty)

    evaluator.evaluateFormula(formula, table) shouldEqual Left("Division by zero")
  }

  test("Evaluate formula with parentheses") {
    val formula = "(2+3)*4"
    val table = Table(Map.empty)

    evaluator.evaluateFormula(formula, table) shouldEqual Right(20)
  }

  test("Evaluate different formula with parentheses") {
    val formula = "2+(3*4)"
    val table = Table(Map.empty)

    evaluator.evaluateFormula(formula, table) shouldEqual Right(20)
  } //parenthesis does not work

  test("Evaluate a mixed formula with multiple cell references") {
    val formula = "A1+B1"
    val table = Table(Map(
      Coordinate(0, 0) -> Number(5),
      Coordinate(0, 1) -> Number(7)
    ))

    evaluator.evaluateFormula(formula, table) shouldEqual Right(12)
  }

  test("Handle an empty formula") {
    val formula = ""
    val table = Table(Map.empty)

    evaluator.evaluateFormula(formula, table) shouldEqual Left("Empty formula")
  }

  test("Evaluate a formula with a missing cell") {
    val formula = "A1+B1"
    val table = Table(Map(Coordinate(0, 0) -> Number(5)))

    evaluator.evaluateFormula(formula, table) shouldEqual Left("Cell not found")
  }
}

