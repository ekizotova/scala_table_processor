package handlers.processor

import org.scalatest.funsuite.AnyFunSuite

import handlers.processor.ProcessorCliHandler
import filters.TableFilter
import parsers.CliParser

class ProcessorCliHandlerTest extends AnyFunSuite {

  test("parseArguments should parse valid arguments correctly") {
    val args = Array("--input-file", "data.csv", "--filter-is-empty", "B")
    val handler = new ProcessorCliHandler()

    val (options, filters) = handler.parseArguments(args)

    assert(options.contains("inputFile"))
    assert(options("inputFile") == "data.csv")
    assert(filters.nonEmpty)
  }

  test("parseArguments should handle missing required arguments") {
    val args = Array("--filter-is-empty")
    val handler = new ProcessorCliHandler()

    val (options, filters) = handler.parseArguments(args)

    assert(options.isEmpty)
    assert(filters.isEmpty)
  }

  test("parseArguments should handle no arguments gracefully") {
    val args: Array[String] = Array()  // Specify the type of the array explicitly
    val handler = new ProcessorCliHandler()

    val (options, filters) = handler.parseArguments(args)

    assert(options.isEmpty)
    assert(filters.isEmpty)
  }


  test("parseArguments should handle --help argument correctly") {
    val args = Array("--help")
    val handler = new ProcessorCliHandler()

    val (options, filters) = handler.parseArguments(args)

    assert(options.contains("help"))
    assert(filters.isEmpty)
  }

  test("validateOptions should return true for valid options") {
    val handler = new ProcessorCliHandler()

    val validOptions = Map("inputFile" -> "data.csv")
    assert(handler.validateOptions(validOptions))
  }

  test("validateOptions should return false for missing inputFile") {
    val handler = new ProcessorCliHandler()

    val invalidOptions = Map("unknown" -> "value")
    assert(!handler.validateOptions(invalidOptions))
  }

  test("validateOptions should return false for unknown options") {
    val handler = new ProcessorCliHandler()

    val invalidOptions = Map("inputFile" -> "data.csv", "unknown" -> "value")
    assert(!handler.validateOptions(invalidOptions))
  }

  test("validateOptions should return false for empty options") {
    val handler = new ProcessorCliHandler()

    val emptyOptions = Map[String, Any]()
    assert(!handler.validateOptions(emptyOptions))
  }

  test("parseArguments should handle multiple filters correctly") {
    val args = Array("--input-file", "data.csv", "--filter-is-empty", "B", "--filter-is-not-empty", "C")
    val handler = new ProcessorCliHandler()

    val (options, filters) = handler.parseArguments(args)

    assert(options.contains("inputFile"))
    assert(filters.size == 2)  // Expecting two filters: isEmpty and isNotEmpty
  }

  test("parseArguments should return empty options for invalid filter") {
    val args = Array("--input-file", "data.csv", "--filterInvalid")
    val handler = new ProcessorCliHandler()

    val (options, filters) = handler.parseArguments(args)

    assert(options.contains("inputFile"))
    assert(filters.isEmpty)
  }
}
