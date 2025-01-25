package handlers

import filters.{ColumnConditionFilter, FilterIsEmpty, FilterIsNotEmpty, TableFilter}
import formatters.MarkdownFormatter
import loaders.FileLoader
import parsers.CsvTableParser
import evaluators.EnhancedFormulaEvaluator
import csvParts.*

import java.io.{File, PrintWriter}

class OutputFileHandler(var next: Option[CliHandler]) extends CliHandler {
  def setNext(nextHandler: Option[CliHandler]): Unit = {
    next = nextHandler
  }

  override def handle(args: Array[String], options: scala.collection.mutable.Map[String, Any],
                      filters: scala.collection.mutable.Buffer[TableFilter]): Array[String] = {
    if (args.nonEmpty && args.head == "--output-file") {

      if (args.length > 1) {
      options("outputFile") = args(1) // Set the output file if a file is provided
      args.drop(2) // Continue 
    } else {
      // If no file is provided, using stdout as the default output
      options("stdout") = true
      args.tail 
    }
  } else {
      next.map(_.handle(args, options, filters)).getOrElse(args)
    }
  }

  override def describe(): Option[String] = Some("Output: --output-file [FILE] : " +
    "The file to output the table to (optional).")
}
