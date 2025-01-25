package parsers

import filters.*
import formatters.*
import loaders.FileLoader
import parsers.CsvTableParser
import evaluators.EnhancedFormulaEvaluator
import csvParts.*
import handlers.*

import scala.collection.mutable
import scala.reflect.Selectable.reflectiveSelectable

// CliParser class to parse the command line arguments and process

class CliParser(args: Array[String]) {
  // Parse method to handle the command-line arguments

  def parse(): (Map[String, Any], Seq[TableFilter]) = {
    val options = scala.collection.mutable.Map[String, Any]()
    val filters = scala.collection.mutable.Buffer[TableFilter]()

    val helpHandler = new HelpHandler(None, Nil)
    val inputFileHandler = new InputFileHandler(None)
    val outputFileHandler = new OutputFileHandler(None)
    val stdoutHandler = new StdoutHandler(None)
    val filterHandler = new FilterHandler(None)
    val filterIsEmptyHandler = new FilterIsEmptyHandler(None)
    val filterIsNotEmptyHandler = new FilterIsNotEmptyHandler(None)
    val headersHandler = new HeadersHandler(None)
    val outputFormatHandler = new OutputFormatHandler(None)
    val outputSeparatorHandler = new OutputSeparatorHandler(None)
    val inputSeparatorHandler = new InputSeparatorHandler(None)
    val unknownArgumentHandler = new UnknownArgumentHandler(None)

    val handlers = Seq(
      helpHandler,
      inputFileHandler,
      outputFileHandler,
      stdoutHandler,
      filterHandler,
      filterIsEmptyHandler,
      filterIsNotEmptyHandler,
      headersHandler,
      outputFormatHandler,
      outputSeparatorHandler,
      inputSeparatorHandler,
      unknownArgumentHandler
    )

    // Chain of handlers so that they can process arguments in sequence

    val chain = handlers.reduceRight((handler, next) => {
      handler.asInstanceOf[ {def setNext(next: Option[CliHandler]): Unit}].setNext(Some(next))
      handler
    })

    helpHandler.setHandlers(handlers)

    var remainingArgs = args
    try {
      while (remainingArgs.nonEmpty) {
        remainingArgs = chain.handle(remainingArgs, options, filters)
      }
    } catch {
      case e: IllegalArgumentException =>
        println(s"Error: ${e.getMessage}")
        helpHandler.handle(Array("--help"), options, filters)
        return (Map.empty, Seq.empty)
    }

    if (!options.contains("help") && !options.contains("inputFile")) {
      println("Error: Missing required argument: --input-file\n")
      println("Use --help or -h for more information.")
      return (Map.empty, Seq.empty)
    }

    (options.toMap, filters.toSeq)
  }

  // Convert a column name (like A, B, C) to an index (0, 1, 2)
  private def columnNameToIndex(column: String): Int = {
    column.toUpperCase.foldLeft(0)((acc, char) => acc * 26 + (char - 'A' + 1)) - 1
  }

  // Parse conditions for filtering with operators like ">", "<".... and so on

  private def parseCondition(operator: String, value: Int): Int => Boolean = operator match {
    case ">" => _ > value
    case "<" => _ < value
    case ">=" => _ >= value
    case "<=" => _ <= value
    case "==" => _ == value
    case "!=" => _ != value
    case _ => throw new IllegalArgumentException(s"Unknown operator: $operator")
  }
}
