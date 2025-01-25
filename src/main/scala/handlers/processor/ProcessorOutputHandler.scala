package handlers.processor

import csvParts.*
import evaluators.EnhancedFormulaEvaluator
import filters.*
import formatters.{CsvFormatter, MarkdownFormatter, TableFormatter}
import loaders.FileLoader
import loaders.output.{FileOutputLoader, OutputLoader, StdoutLoader}
import parsers.*

import java.io.{File, PrintWriter}

class ProcessorOutputHandler {
  def handleOutput(output: String, options: Map[String, Any]): Unit = {

    val outputLoader = determineOutputStrategy(options)
    outputLoader.foreach(_.write(output))
  }

  private def determineOutputStrategy(options: Map[String, Any]): List[OutputLoader] = {

    val outputFile = options.get("outputFile").map(_.asInstanceOf[String])
    val useStdout = options.getOrElse("stdout", false).asInstanceOf[Boolean]

    (outputFile, useStdout) match {
      case (Some(file), true) =>
        List(new FileOutputLoader(file), new StdoutLoader) // File output strategy
      case (Some(file), false) =>
        List(new FileOutputLoader(file))// File output strategy
      case (None, true) =>
        List(new StdoutLoader) // Stdout output strategy
      case (None, false) =>
        List(new StdoutLoader) // Stdout output strategy
    }
  }
}

