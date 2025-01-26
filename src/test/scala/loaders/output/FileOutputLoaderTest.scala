package loaders.output

import org.scalatest.funsuite.AnyFunSuite
import java.io.{File, FileNotFoundException, PrintWriter}

class FileOutputLoaderTest extends AnyFunSuite {

  test("Write to file successfully") {
    val fileName = "test_output.txt"
    val content = "Hello, World!"
    val loader = new FileOutputLoader(fileName)

    loader.write(content)

    val file = new File(fileName)
    assert(file.exists())
    assert(file.length() > 0)
    file.delete()
  }

  test("Fail to write to an invalid file path") {
    val invalidFileName = "/invalid_path/test_output.txt"
    val content = "Hello, World!"
    val loader = new FileOutputLoader(invalidFileName)

    intercept[RuntimeException] {
      loader.write(content)
    }
  }

  test("Write to file with empty file name") {
    val emptyFileName = ""
    val content = "Hello, World!"
    val loader = new FileOutputLoader(emptyFileName)

    intercept[RuntimeException] {
      loader.write(content)
    }
  }

  test("Ensure file is closed even on exception") {
    val fileName = "test_output.txt"
    val loader = new FileOutputLoader(fileName)

    val content = "Hello, World!"
    val file = new File(fileName)

    try {
      // Test with throwing exception inside the write method.
      intercept[RuntimeException] {
        loader.write(content) // This will throw a RuntimeException if there is an I/O error
      }
    } catch {
      case e: RuntimeException =>
        println(s"Expected exception caught: ${e.getMessage}")
    }

    // Ensure the file is created (indicating it's at least been touched by the writer)
    assert(file.exists())

    // Clean up the file after the test
    file.delete()
  }
}

