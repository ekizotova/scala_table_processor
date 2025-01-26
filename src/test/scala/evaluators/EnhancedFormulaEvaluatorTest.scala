package evaluators

import csvParts._
import evaluators._

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class EnhancedFormulaEvaluatorTest extends AnyFunSuite with Matchers {
  test("Evaluate simple numeric formula") {
    val operators: Map[String, (Int, Int) => Int] = Map(
      "+" -> (_ + _)
    )
    val evaluator = new EnhancedFormulaEvaluator(operators)
    val formula = "5+3"
    val table = Table(Map.empty)

    evaluator.evaluateFormula(formula, table) shouldEqual Right(8)
  }

    test("Evaluate formula with cell reference") {
      val operators: Map[String, (Int, Int) => Int] = Map(
        "+" -> (_ + _)
      )
      val evaluator = new EnhancedFormulaEvaluator(operators)
      val formula = "A1+10"
      val table = Table(Map(Coordinate(0, 0) -> Number(5)))  // A1 is 5

      evaluator.evaluateFormula(formula, table) shouldEqual Right(15)  // 5 (A1) + 10
    }

    test("Evaluate formula with invalid cell reference") {
      val operators: Map[String, (Int, Int) => Int] = Map(
        "+" -> (_ + _)
      )
      val evaluator = new EnhancedFormulaEvaluator(operators)
      val formula = "A1+10"
      val table = Table(Map.empty)  // A1 is not present in the table

      evaluator.evaluateFormula(formula, table) shouldEqual Left("Cell not found")
    }

    test("Detect cyclic dependency in formulas") {
      val operators: Map[String, (Int, Int) => Int] = Map(
        "+" -> (_ + _)
      )
      val evaluator = new EnhancedFormulaEvaluator(operators)
      val formulaA = Formula("B1+1")
      val formulaB = Formula("A1+1")
      val table = Table(Map(
        Coordinate(0, 0) -> formulaB,  // A1 references B1
        Coordinate(0, 1) -> formulaA   // B1 references A1
      ))

      evaluator.evaluateFormula("A1", table) shouldEqual Left("Invalid cell reference type")
    }

    test("Evaluate formula with division by zero") {
      val operators: Map[String, (Int, Int) => Int] = Map(
        "/" -> ((a, b) => if (b == 0) throw new ArithmeticException("Division by zero") else a / b)
      )
      val evaluator = new EnhancedFormulaEvaluator(operators)
      val formula = "10/0"
      val table = Table(Map.empty)

      evaluator.evaluateFormula(formula, table) shouldEqual Left("Division by zero")
    }

    test("Evaluate formula with parentheses") {
      val operators: Map[String, (Int, Int) => Int] = Map(
        "+" -> (_ + _),
        "*" -> (_ * _)
      )
      val evaluator = new EnhancedFormulaEvaluator(operators)
      val formula = "(2+3)*4"
      val table = Table(Map.empty)

      evaluator.evaluateFormula(formula, table) shouldEqual Right(20)
    }

    test("Evaluate formula with multiple operations") {
      val operators: Map[String, (Int, Int) => Int] = Map(
        "+" -> (_ + _),
        "*" -> (_ * _)
      )
      val evaluator = new EnhancedFormulaEvaluator(operators)
      val formula = "2+3*4"
      val table = Table(Map.empty)

      evaluator.evaluateFormula(formula, table) shouldEqual Right(20)
    }

    test("Evaluate empty formula") {
      val operators: Map[String, (Int, Int) => Int] = Map.empty
      val evaluator = new EnhancedFormulaEvaluator(operators)
      val formula = ""
      val table = Table(Map.empty)

      evaluator.evaluateFormula(formula, table) shouldEqual Left("Empty formula")
    }

    test("Evaluate formula with missing cell") {
      val operators: Map[String, (Int, Int) => Int] = Map(
        "+" -> (_ + _)
      )
      val evaluator = new EnhancedFormulaEvaluator(operators)
      val formula = "A1+B1"  // A1 and B1 are missing
      val table = Table(Map(Coordinate(0, 0) -> Number(5)))  // A1 exists but B1 is missing

      evaluator.evaluateFormula(formula, table) shouldEqual Left("Cell not found")
    }
}
