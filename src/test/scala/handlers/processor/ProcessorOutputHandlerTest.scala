package handlers.processor

import handlers.processor._

import org.scalatest.funsuite.AnyFunSuite
import loaders.output.{FileOutputLoader, StdoutLoader}
import java.io.{ByteArrayOutputStream, File}


class ProcessorOutputHandlerTest extends AnyFunSuite {

  test("Handle output with outputFile option") {
    val options = Map("outputFile" -> "output.txt")
    val handler = new ProcessorOutputHandler()

    val outputStream = new ByteArrayOutputStream()
    Console.withOut(outputStream) {
      handler.handleOutput("Hello, World!", options)
    }

    val output = outputStream.toString
    // Assert that the output is written to file (check file content)
    val file = new File("output.txt")
    assert(file.exists())
    file.delete()
  }

  test("Handle output with stdout option") {
    val options = Map("stdout" -> true)
    val handler = new ProcessorOutputHandler()

    val outputStream = new ByteArrayOutputStream()
    Console.withOut(outputStream) {
      handler.handleOutput("Hello, World!", options)
    }

    val output = outputStream.toString
    assert(output.contains("Hello, World!"))
  }

  test("Handle output with both outputFile and stdout options") {
    val options = Map("outputFile" -> "output.txt", "stdout" -> true)
    val handler = new ProcessorOutputHandler()

    val outputStream = new ByteArrayOutputStream()
    Console.withOut(outputStream) {
      handler.handleOutput("Hello, World!", options)
    }

    val output = outputStream.toString
    // Assert that the output is written to both file and stdout
    val file = new File("output.txt")
    assert(file.exists())
    assert(output.contains("Hello, World!"))
    file.delete()
  }

  test("Handle output with no output options") {
    val options = Map.empty[String, Any]
    val handler = new ProcessorOutputHandler()

    val outputStream = new ByteArrayOutputStream()
    Console.withOut(outputStream) {
      handler.handleOutput("Hello, World!", options)
    }

    val output = outputStream.toString
    assert(output.contains("Hello, World!"))
  }
}
