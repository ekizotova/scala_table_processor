package loaders.output

import org.scalatest.funsuite.AnyFunSuite
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class StdoutOutputLoaderTest extends AnyFunSuite {

  test("Print to stdout successfully") {
    val content = "Hello, World!"
    val loader = new StdoutLoader()

    val outputStream = new ByteArrayOutputStream()
    Console.withOut(outputStream) {
      loader.write(content)
    }

    val output = outputStream.toString
    assert(output.contains(content))
  }

  test("Handle long strings in output") {
    val longContent = "A" * 10000 // A long string
    val loader = new StdoutLoader()

    val outputStream = new ByteArrayOutputStream()
    Console.withOut(outputStream) {
      loader.write(longContent)
    }

    val output = outputStream.toString
    assert(output.contains(longContent))
  }
}
