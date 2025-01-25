package handlers

import filters.{ColumnConditionFilter, FilterIsEmpty, FilterIsNotEmpty, TableFilter}
import formatters.MarkdownFormatter
import loaders.FileLoader
import parsers.CsvTableParser
import evaluators.EnhancedFormulaEvaluator
import csvParts.*

import java.io.{File, PrintWriter}

class OutputSeparatorHandler(var next: Option[CliHandler]) extends CliHandler {
  def setNext(nextHandler: Option[CliHandler]): Unit = {
    next = nextHandler
  }

  override def handle(args: Array[String], options: scala.collection.mutable.Map[String, Any],
                      filters: scala.collection.mutable.Buffer[TableFilter]): Array[String] = {
    if (args.nonEmpty && args.head == "--output-separator") {
      if (args.length < 2)
        throw new IllegalArgumentException("Invalid format for --output-separator: " +
          "expected --output-separator [SEPARATOR]")
      options("outputSeparator") = args(1).head
      args.drop(2)
    } else {
      next.map(_.handle(args, options, filters)).getOrElse(args)
    }
  }

  override def describe(): Option[String] = Some(
    "Output: --output-separator [STRING] : " +
      "For CSV output: the separator in the output file (optional, defaults to \",\")."
  )
}
