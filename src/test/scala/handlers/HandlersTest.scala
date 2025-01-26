package handlers

import scala.collection.mutable
import org.scalatest.funsuite.AnyFunSuite
import scala.util.Try
import filters.TableFilter


class HandlersTest extends AnyFunSuite {
  test("InputSeparatorHandler should correctly handle --input-separator option") {
    val args = Array("--input-separator", "|", "some", "other", "args")
    val options = mutable.Map[String, Any]()
    val filters = mutable.Buffer[TableFilter]()

    val inputSeparatorHandler = new InputSeparatorHandler(None)
    inputSeparatorHandler.handle(args, options, filters)

    assert(options("inputSeparator") == '|')
  }

  test("InputSeparatorHandler should throw exception for missing input separator") {
    val args = Array("--input-separator")
    val options = mutable.Map[String, Any]()
    val filters = mutable.Buffer[TableFilter]()

    val inputSeparatorHandler = new InputSeparatorHandler(None)

    val exception = intercept[IllegalArgumentException] {
      inputSeparatorHandler.handle(args, options, filters)
    }

    assert(exception.getMessage.contains("Invalid format for --input-separator"))
  }

  test("OutputFormatHandler should correctly handle --output-format option") {
    val args = Array("--output-format", "md", "some", "other", "args")
    val options = mutable.Map[String, Any]()
    val filters = mutable.Buffer[TableFilter]()

    val outputFormatHandler = new OutputFormatHandler(None)
    outputFormatHandler.handle(args, options, filters)

    assert(options("outputFormat") == "md")
  }

  test("OutputSeparatorHandler should correctly handle --output-separator option") {
    val args = Array("--output-separator", ";", "some", "other", "args")
    val options = mutable.Map[String, Any]()
    val filters = mutable.Buffer[TableFilter]()

    val outputSeparatorHandler = new OutputSeparatorHandler(None)
    outputSeparatorHandler.handle(args, options, filters)

    assert(options("outputSeparator") == ';')
  }

  test("OutputSeparatorHandler should throw exception for missing output separator") {
    val args = Array("--output-separator")
    val options = mutable.Map[String, Any]()
    val filters = mutable.Buffer[TableFilter]()

    val outputSeparatorHandler = new OutputSeparatorHandler(None)

    val exception = intercept[IllegalArgumentException] {
      outputSeparatorHandler.handle(args, options, filters)
    }

    assert(exception.getMessage.contains("Invalid format for --output-separator"))
  }
}
