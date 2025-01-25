package handlers

import filters.{ColumnConditionFilter, FilterIsEmpty, FilterIsNotEmpty, TableFilter}
import formatters.MarkdownFormatter
import loaders.FileLoader
import parsers.CsvTableParser
import evaluators.EnhancedFormulaEvaluator
import csvParts.*

import java.io.{File, PrintWriter}

class FilterIsEmptyHandler(var next: Option[CliHandler]) extends CliHandler {
  def setNext(nextHandler: Option[CliHandler]): Unit = {
    next = nextHandler
  }

  override def handle(args: Array[String], options: scala.collection.mutable.Map[String, Any], filters: scala.collection.mutable.Buffer[TableFilter]): Array[String] = {
    if (args.nonEmpty && args.head == "--filter-is-empty") {
      if (args.length < 2) throw new IllegalArgumentException("Invalid format for --filter-is-empty: expected --filter-is-empty COLUMN")
      val column = args(1)
      filters += FilterIsEmpty(columnNameToIndex(column))
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
    "Transformation: --filter-is-empty [COLUMN] : Filter out lines with non-empty cells in the specified column (optional, can be repeated)."
  )
}
