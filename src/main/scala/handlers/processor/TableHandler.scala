package handlers.processor

import csvParts.*
import evaluators.EnhancedFormulaEvaluator
import filters.*
import formatters.{CsvFormatter, MarkdownFormatter, TableFormatter}
import loaders.FileLoader
import parsers.*

import java.io.{File, PrintWriter}

class TableHandler {
  def loadAndParse(options: Map[String, Any]): Table = {
    val inputFile = options("inputFile").asInstanceOf[String]
    val inputSeparator = options.getOrElse("inputSeparator", ",").toString

    val loader = new FileLoader
    val parser = new CsvTableParser(inputSeparator)

    val rawInput = loader.load(inputFile)
    parser.parse(rawInput)
  }

  private val evaluator = new EnhancedFormulaEvaluator(Map(
    "+" -> (_ + _),
    "-" -> (_ - _),
    "*" -> (_ * _),
    "/" -> ((a, b) => if (b == 0) throw new ArithmeticException("Division by zero") else a / b)
  ))

  def evaluate(table: Table): Table = {
    table.map {
      case Formula(expression) =>
        evaluator.evaluateFormula(expression, table) match {
          case Right(value) => Number(value)
          case Left(error) => Formula(s"<ERROR: $error>")
        }
      case cell => cell
    }
  }

  def applyFilters(table: Table, filters: Seq[TableFilter]): Table = {
    filters.foldLeft(table)((t, filter) => filter(t))
  }

  def getFormatter(options: Map[String, Any]): TableFormatter = {
    val outputFormat = options.getOrElse("outputFormat", "csv").asInstanceOf[String]
    val outputSeparator = options.getOrElse("outputSeparator", ",").toString
    val includeHeaders = options.getOrElse("headers", false).asInstanceOf[Boolean]

    outputFormat match {
      case "csv" => new CsvFormatter(outputSeparator, includeHeaders)
      case "md" => new MarkdownFormatter(includeHeaders)
      case _ => throw new IllegalArgumentException(s"Unknown output format: $outputFormat")
    }
  }
}
