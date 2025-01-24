package csvParts

import evaluators.EnhancedFormulaEvaluator

case class Coordinate(row: Int, col: Int) // Position in the table

// A sealed trait representing different types of cells in the table.
sealed trait Cell {
  def transform(f: Int => Int): Cell = this match {
    case Number(value) => Number(f(value))
    case other => other
  }
}

case object Empty extends Cell
case class Number(value: Int) extends Cell

// Case class representing a cell that contains a formula
case class Formula(expression: String) extends Cell {
  def evaluate(table: Table, evaluator: EnhancedFormulaEvaluator): Either[String, Int] = {
    evaluator.evaluateFormula(expression, table)
  }
}
