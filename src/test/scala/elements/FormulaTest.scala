package elements

import csvParts._
import evaluators._

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class FormulaTest extends AnyFunSuite with Matchers {
  test("Evaluate simple valid formula") {
    val formula = Formula("5+3")
    val table = Table(Map.empty)  // Empty table 
    val evaluator = new EnhancedFormulaEvaluator(Map(
      "+" -> (_ + _)
    ))

    formula.evaluate(table, evaluator) shouldEqual Right(8)
  }

  test("Evaluate formula with cell reference") {
    val formula = Formula("A1+10")
    val table = Table(Map(Coordinate(0, 0) -> Number(5))) // A1 is 5
    val evaluator = new EnhancedFormulaEvaluator(Map(
      "+" -> (_ + _)
    ))

    formula.evaluate(table, evaluator) shouldEqual Right(15)
  }

  test("Evaluate formula with invalid cell reference") {
    val formula = Formula("A1")
    val table = Table(Map.empty) // A1 is not present in the table
    val evaluator = new EnhancedFormulaEvaluator(Map.empty)

    formula.evaluate(table, evaluator) shouldEqual Left("Cell not found")
  }
  
  test("Evaluate malformed formula") {
    val formula = Formula("5++3") // Malformed expression with multiple operators
    val table = Table(Map.empty)
    val evaluator = new EnhancedFormulaEvaluator(Map(
      "+" -> (_ + _)
    ))

    formula.evaluate(table, evaluator) shouldEqual Left("Invalid formula structure")
  }

  test("Evaluate formula with parentheses") {
    val formula = Formula("(2+3)*4")
    val table = Table(Map.empty)
    val evaluator = new EnhancedFormulaEvaluator(Map(
      "+" -> (_ + _),
      "*" -> (_ * _)
    ))

    formula.evaluate(table, evaluator) shouldEqual Right(20)
  }

  test("Evaluate formula with division by zero") {
    val formula = Formula("10/0")
    val table = Table(Map.empty)
    val evaluator = new EnhancedFormulaEvaluator(Map(
      "/" -> ((a, b) => if (b == 0) throw new ArithmeticException("Division by zero") else a / b)
    ))

    formula.evaluate(table, evaluator) shouldEqual Left("Division by zero")
  }

  test("Evaluate formula with missing cell reference") {
    val formula = Formula("A2+B1") // A2 and B1 do not exist
    val table = Table(Map.empty)
    val evaluator = new EnhancedFormulaEvaluator(Map(
      "+" -> (_ + _)
    ))

    formula.evaluate(table, evaluator) shouldEqual Left("Cell not found")
  }

  test("Evaluate formula with cyclic dependency") {
    val formulaA = Formula("A1")
    val formulaB = Formula("B1")
    val table = Table(Map(
      Coordinate(0, 0) -> formulaB, // A1 references B1
      Coordinate(0, 1) -> formulaA // B1 references A1
    ))
    val evaluator = new EnhancedFormulaEvaluator(Map.empty)

    formulaA.evaluate(table, evaluator) shouldEqual Left("Invalid cell reference type")
  }

  test("Evaluate formula with empty cell reference") {
    val formula = Formula("A1")
    val table = Table(Map(Coordinate(0, 0) -> Empty)) // A1 is empty
    val evaluator = new EnhancedFormulaEvaluator(Map.empty)

    formula.evaluate(table, evaluator) shouldEqual Left("Empty cell reference")
  }

  test("Evaluate formula with multiple operations") {
    val formula = Formula("A1+B1*2")
    val table = Table(Map(
      Coordinate(0, 0) -> Number(3),
      Coordinate(0, 1) -> Number(4)
    )) // A1 is 3, B1 is 4
    val evaluator = new EnhancedFormulaEvaluator(Map(
      "+" -> (_ + _),
      "*" -> (_ * _)
    ))

    formula.evaluate(table, evaluator) shouldEqual Right(14)
  }

  test("Evaluate empty formula") {
    val formula = Formula("")
    val table = Table(Map.empty)
    val evaluator = new EnhancedFormulaEvaluator(Map.empty)

    formula.evaluate(table, evaluator) shouldEqual Left("Empty formula")
  }
}
