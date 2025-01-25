package evaluators

import csvParts.{BinaryOperationNode, Cell, CellReferenceNode, Coordinate, Empty,
  Formula, FormulaAST, Number, NumberNode, ParenthesisNode, Table}
import parsers.FormulaParser

import scala.util.{Failure, Success, Try}

//  class responsible for evaluating formulas
class FormulaEvaluator(operators: Map[String, (Int, Int) => Int]) {
  private val parser = new FormulaParser(operators)

  def evaluateFormula(formula: String, table: Table): Either[String, Int] = {
    parser.parseFormula(formula).flatMap(ast => evaluateAST(ast, table, Set()))
  }

  private def evaluateAST(node: FormulaAST, table: Table, visited: Set[String]): Either[String, Int] = {
    node match {
      case NumberNode(value) => Right(value)
      case CellReferenceNode(ref) => evaluateCellReference(ref, table, visited)
      case BinaryOperationNode(left, op, right) =>
        for {
          leftValue <- evaluateAST(left, table, visited)
          rightValue <- evaluateAST(right, table, visited)
          operation <- operators.get(op).toRight(s"Unknown operator: $op")
          result <- Try(operation(leftValue, rightValue))
            .toEither
            .left.map {
              case _: ArithmeticException if op == "/" => "Division by zero"
              case ex => s"Error during evaluation: ${ex.getMessage}"
            }
        } yield result
      case ParenthesisNode(inner) => evaluateAST(inner, table, visited)
    }
  }

  private def evaluateCellReference(ref: String, table: Table, visited: Set[String]): Either[String, Int] = {
    if (visited.contains(ref)) Left("Cyclic dependency detected")
    else {
      referenceToCoordinate(ref) match {
        case Some(coord) => lookupCellValue(coord, table)
        case None => Left(s"Invalid cell reference: $ref")
      }
    }
  }

  private def lookupCellValue(coord: Coordinate, table: Table): Either[String, Int] = {
    table.get(coord) match {
      case Some(Number(value)) => Right(value)
      case Some(Empty) => Left("Empty cell reference")
      case Some(_) => Left("Invalid cell reference type")
      case None => Left("Cell not found")
    }
  }

  private def referenceToCoordinate(reference: String): Option[Coordinate] = {
    val colRegex = """([A-Z]+)(\d+)""".r
    reference match {
      case colRegex(colStr, rowStr) =>
        val col = colStr.foldLeft(0)((acc, c) => acc * 26 + (c - 'A' + 1)) - 1 // Convert column letters to 0-indexed number
        val row = rowStr.toInt - 1 // Convert row number to 0-indexed
        Some(Coordinate(row, col))
      case _ => None
    }
  }
}
