package handlers

import filters.{ColumnConditionFilter, FilterIsEmpty, FilterIsNotEmpty, TableFilter}
import formatters.MarkdownFormatter
import loaders.FileLoader
import parsers.CsvTableParser
import evaluators.EnhancedFormulaEvaluator
import csvParts.*

import java.io.{File, PrintWriter}

class FilterHandler(var next: Option[CliHandler]) extends CliHandler {
  def setNext(nextHandler: Option[CliHandler]): Unit = {
    next = nextHandler
  }

  override def handle(args: Array[String], options: scala.collection.mutable.Map[String, Any], 
                      filters: scala.collection.mutable.Buffer[TableFilter]): Array[String] = {
    if (args.nonEmpty && args.head == "--filter") {
      if (args.length < 4) throw new IllegalArgumentException("Invalid format for --filter: expected --filter COLUMN OPERATOR VALUE")
      val column = args(1)
      val operator = args(2)
      val value = args(3).toInt
      filters += ColumnConditionFilter(columnNameToIndex(column), parseCondition(operator, value))
      args.drop(4)
    } else {
      next.map(_.handle(args, options, filters)).getOrElse(args)
    }
  }

  // Helper to convert column names to indices 
  private def columnNameToIndex(column: String): Int = {
    column.toUpperCase.foldLeft(0)((acc, char) => acc * 26 + (char - 'A' + 1)) - 1
  }

  // Helper method to parse conditions 
  def parseCondition(operator: String, value: Int): Int => Boolean = operator match {
    case ">" => _ > value
    case "<" => _ < value
    case ">=" => _ >= value
    case "<=" => _ <= value
    case "==" => _ == value
    case "!=" => _ != value
    case _ => throw new IllegalArgumentException(s"Unknown operator: $operator")
  }

  override def describe(): Option[String] = Some(
    "Transformation: --filter [COLUMN] ( < | > | <= | >= | == | != ) [NUMBER] " +
      ": Filter on the column (optional, can be repeated)."
  )
}
