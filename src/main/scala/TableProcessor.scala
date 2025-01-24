import filters.*
import formatters.{CsvFormatter, MarkdownFormatter, TableFormatter}
import loaders.FileLoader
import parsers.*
import evaluators.EnhancedFormulaEvaluator
import csvParts.*
import handlers.processor.{ProcessorCliHandler, ProcessorOutputHandler, TableHandler}
import handlers.CliHandler

import java.io.{File, PrintWriter}

// The entry point
object TableProcessor {
  def main(args: Array[String]): Unit = {
    val cliHandler = new ProcessorCliHandler
    val (options, filters) = cliHandler.parseArguments(args)
    
    if (!cliHandler.validateOptions(options)) return

    val tableHandler = new TableHandler

    val table = tableHandler.loadAndParse(options)
    val evaluatedTable = tableHandler.evaluate(table)
    val filteredTable = tableHandler.applyFilters(evaluatedTable, filters)
    val formatter = tableHandler.getFormatter(options)
    val formattedOutput = formatter.format(filteredTable)
    
    val processorOutputHandler = new ProcessorOutputHandler
    
    processorOutputHandler.handleOutput(formattedOutput, options)
  }
}


// left commented old code there just for debug purposes 
/*
  def main(args: Array[String]): Unit = {
    val cliParser = new CliParser(args)
    val (options, filters) = cliParser.parse()

    if (options.contains("unknown") || !options.contains("inputFile")) {
      return
    }

    val inputFile = options("inputFile").asInstanceOf[String] //think of another way to implement, custom structure

    val inputSeparator = options.getOrElse("inputSeparator", ',').asInstanceOf[Char]

    val outputFile = options.get("outputFile").map(_.asInstanceOf[String])
    val useStdout = options.getOrElse("stdout", false).asInstanceOf[Boolean]

    val loader = new FileLoader
    val parser = new CsvTableParser

    val evaluator = new EnhancedFormulaEvaluator(Map(
      "+" -> (_ + _),
      "-" -> (_ - _),
      "*" -> (_ * _),
      "/" -> ((a, b) => if (b == 0) throw new ArithmeticException("Division by zero") else a / b)
    ))

    val rawInput = loader.load(inputFile)
    val table = parser.parse(rawInput, inputSeparator)

    val evaluatedTable = table.map {
      case Formula(expression) =>
        evaluator.evaluateFormula(expression, table) match {
          case Right(value) => Number(value)
          case Left(error) => Formula(s"<ERROR: $error>")
        }
      case cell => cell
    }

    val filteredTable = filters.foldLeft(evaluatedTable)((t, filter) => filter(t))

    val outputFormat = options.getOrElse("outputFormat", "csv").asInstanceOf[String]
    val outputSeparator = options.getOrElse("outputSeparator", ',').asInstanceOf[Char]
    val includeHeaders = options.getOrElse("headers", false).asInstanceOf[Boolean]

    val formatter: TableFormatter = outputFormat match {
      case "csv" => new CsvFormatter(outputSeparator, includeHeaders)
      case "md" => new MarkdownFormatter(includeHeaders)
      case _ => throw new IllegalArgumentException(s"Unknown output format: $outputFormat")
    }

    val output = formatter.format(filteredTable)

    (outputFile, useStdout) match {  
      case (Some(file), true) =>
        val pw = new PrintWriter(new File(file))
        pw.write(output)
        pw.close()
        println(output)

      case (Some(file), false) =>
        val pw = new PrintWriter(new File(file))
        pw.write(output)
        pw.close()

      case (None, true) =>
        println(output)

      case (None, false) =>
        println(output)
    }
  }

   */
