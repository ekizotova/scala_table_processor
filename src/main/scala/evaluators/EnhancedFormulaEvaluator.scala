package evaluators

import evaluators.FormulaEvaluator
import parsers.FormulaParser
import csvParts.{BinaryOperationNode, Cell, CellReferenceNode, Coordinate, Empty,
  Formula, FormulaAST, Number, NumberNode, ParenthesisNode, Table}


// Class to evaluate formulas in an enhanced way
class EnhancedFormulaEvaluator(operators: Map[String, (Int, Int) => Int]) {
  private val evaluator = new FormulaEvaluator(operators)

  // Method to evaluate a formula expression
  def evaluateFormula(formula: String, table: Table): Either[String, Int] = {
    evaluator.evaluateFormula(formula, table)
  }
}
