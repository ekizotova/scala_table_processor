package handlers

import filters.{ColumnConditionFilter, FilterIsEmpty, FilterIsNotEmpty, TableFilter}
import formatters.MarkdownFormatter
import loaders.FileLoader
import parsers.CsvTableParser
import evaluators.EnhancedFormulaEvaluator
import csvParts.*

import java.io.{File, PrintWriter}

class FilterIsNotEmptyHandler(var next: Option[CliHandler]) extends CliHandler {
  def setNext(nextHandler: Option[CliHandler]): Unit = {
    next = nextHandler
  }
  
  override def handle(args: Array[String], options: scala.collection.mutable.Map[String, Any], filters: scala.collection.mutable.Buffer[TableFilter]): Array[String] = {
    if (args.nonEmpty && args.head == "--filter-is-not-empty") {
      if (args.length < 2) throw new IllegalArgumentException("Invalid format for --filter-is-not-empty: expected --filter-is-not-empty COLUMN")
      val column = args(1)
      filters += FilterIsNotEmpty(columnNameToIndex(column))
      args.drop(2)
    } else {
      next.map(_.handle(args, options, filters)).getOrElse(args)
    }
  }

  // Helper to convert column names to indices 
  private def columnNameToIndex(column: String): Int = {
    column.toUpperCase.foldLeft(0)((acc, char) => acc * 26 + (char - 'A' + 1)) - 1
  }

  override def describe(): Option[String] = Some(
    "Transformation: --filter-is-not-empty [COLUMN] : Filter out lines with empty cells in the specified column (optional, can be repeated)."
  )
}
