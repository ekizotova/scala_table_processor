package handlers

import filters.*
import formatters.*
import loaders.FileLoader
import parsers.CsvTableParser
import evaluators.EnhancedFormulaEvaluator
import csvParts.*
import handlers.ChainableCliHandler

import java.io.{File, PrintWriter}

class HelpHandler(var next: Option[CliHandler], initialHandlers: Seq[CliHandler]) extends CliHandler {
  private var allHandlers: Seq[CliHandler] = initialHandlers

  def setHandlers(handlers: Seq[CliHandler]): Unit = {
    allHandlers = handlers
  }

  def setNext(nextHandler: Option[CliHandler]): Unit = {
    next = nextHandler
  }

  override def handle(args: Array[String], options: scala.collection.mutable.Map[String, Any],
                      filters: scala.collection.mutable.Buffer[TableFilter]): Array[String] = {
    if (args.nonEmpty && (args.head == "--help" || args.head == "-h")) {
      options("help") = true
      println("\nAvailable CLI Options.\n")

      println("Input options:\n")
      printDescriptions(filterDescriptionsByCategory("Input"))

      println("\nOutput options:\n")
      printDescriptions(filterDescriptionsByCategory("Output"))

      println("\nTransformation options:\n")
      printDescriptions(filterDescriptionsByCategory("Transformation"))

      Array.empty[String]
    } else {
      next.map(_.handle(args, options, filters)).getOrElse(args)
    }
  }

  private def filterDescriptionsByCategory(category: String): Seq[String] = {
    allHandlers
      .flatMap(_.describe())
      .filter(_.startsWith(s"$category:"))
      .map(_.stripPrefix(s"$category:"))
  }

  private def printDescriptions(descriptions: Seq[String]): Unit = {
    descriptions.foreach(desc => println(s"  $desc"))
  }

  override def describe(): Option[String] = Some("--help, -h : Display this help message.")
}
