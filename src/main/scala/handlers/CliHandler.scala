package handlers

import filters.{ColumnConditionFilter, FilterIsEmpty, FilterIsNotEmpty, TableFilter}
import formatters.MarkdownFormatter
import loaders.FileLoader
import parsers.{CliParser, CsvTableParser}
import evaluators.EnhancedFormulaEvaluator
import csvParts.*

import java.io.{File, PrintWriter}

// Chain of Responsibility for CLI Argument Parsing
trait CliHandler {
  def handle(args: Array[String], options: scala.collection.mutable.Map[String, Any],
             filters: scala.collection.mutable.Buffer[TableFilter]): Array[String]
  def describe(): Option[String] = None
}
