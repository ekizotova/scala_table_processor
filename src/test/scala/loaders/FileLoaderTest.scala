package loaders

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import loaders.FileLoader
import java.io.File
import java.nio.file.{Files, Paths}

class FileLoaderTest extends AnyFunSuite with Matchers {

  val loader = new FileLoader()

  test("load should correctly read a file with content") {
    val tempFile = Files.createTempFile("testFile", ".txt").toFile
    Files.write(tempFile.toPath, "Hello, world!".getBytes)
    try {
      val content = loader.load(tempFile.getAbsolutePath)
      content shouldEqual "Hello, world!"
    } finally {
      tempFile.delete()
    }
  }

  test("load should correctly read an empty file") {
    val tempFile = Files.createTempFile("emptyFile", ".txt").toFile
    try {
      val content = loader.load(tempFile.getAbsolutePath)
      content shouldEqual "" // Empty file should produce an empty string
    } finally {
      tempFile.delete()
    }
  }

  test("load should throw an exception for a non-existent file") {
    val nonExistentPath = "nonexistentfile.txt"
    val exception = intercept[RuntimeException] {
      loader.load(nonExistentPath)
    }
    exception.getMessage should include("Error loading file")
    exception.getMessage should include(nonExistentPath)
  }

  test("load should throw an exception when trying to read a directory") {
    val tempDir = Files.createTempDirectory("testDir").toFile
    try {
      val exception = intercept[RuntimeException] {
        loader.load(tempDir.getAbsolutePath)
      }
      exception.getMessage should include("Error loading file")
      exception.getMessage should include(tempDir.getAbsolutePath)
    } finally {
      tempDir.delete()
    }
  }

  test("load should handle file names with special characters") {
    val tempFile = new File("test @#$%^&()! file.txt")
    Files.write(tempFile.toPath, "Special characters test".getBytes)
    try {
      val content = loader.load(tempFile.getAbsolutePath)
      content shouldEqual "Special characters test"
    } finally {
      tempFile.delete()
    }
  }
}
