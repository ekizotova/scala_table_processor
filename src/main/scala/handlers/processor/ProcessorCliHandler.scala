package handlers.processor

import filters.TableFilter
import parsers.CliParser

class ProcessorCliHandler {
  def parseArguments(args: Array[String]): (Map[String, Any], Seq[TableFilter]) = {
    val cliParser = new CliParser(args)
    cliParser.parse()
  }

  def validateOptions(options: Map[String, Any]): Boolean = {
    options.contains("inputFile") && !options.contains("unknown")
  }
}
