package handlers

import filters.{ColumnConditionFilter, FilterIsEmpty, FilterIsNotEmpty, TableFilter}
import formatters.MarkdownFormatter
import loaders.FileLoader
import parsers.CsvTableParser
import evaluators.EnhancedFormulaEvaluator
import csvParts.*

import java.io.{File, PrintWriter}

class OutputFormatHandler(var next: Option[CliHandler]) extends CliHandler {
  def setNext(nextHandler: Option[CliHandler]): Unit = {
    next = nextHandler
  }

  override def handle(args: Array[String], options: scala.collection.mutable.Map[String, Any],
                      filters: scala.collection.mutable.Buffer[TableFilter]): Array[String] = {
    if (args.nonEmpty && args.head == "--output-format") {
      if (args.length < 2)
        throw new IllegalArgumentException("Invalid format for --output-format: expected --output-format [csv|md]")
      options("outputFormat") = args(1).toLowerCase
      args.drop(2)
    } else {
      next.map(_.handle(args, options, filters)).getOrElse(args)
    }
  }

  override def describe(): Option[String] = Some(
    "Output: --output-format [csv|md] : The format of the output (optional, default: csv)."
  )
}
