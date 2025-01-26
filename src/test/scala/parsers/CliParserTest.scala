package parsers

import filters.*
import handlers.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CliParserTest extends AnyFunSuite with Matchers {

  test("Parse --help argument") {
    val args = Array("--help")
    val cliParser = new CliParser(args)
    val (options, filters) = cliParser.parse()

    options should contain key "help"
    options("help") shouldEqual true
    filters shouldBe empty
  }

  test("Parse --input-file with a valid file name") {
    val args = Array("--input-file", "data.csv")
    val cliParser = new CliParser(args)
    val (options, filters) = cliParser.parse()

    options should contain key "inputFile"
    options("inputFile") shouldEqual "data.csv"
    filters shouldBe empty
  }

  test("Parse --output-file with a valid file name") {
    val args = Array("--input-file", "data.csv", "--output-file", "output.csv")
    val cliParser = new CliParser(args)
    val (options, filters) = cliParser.parse()

    options should contain key "outputFile"
    options("outputFile") shouldEqual "output.csv"
    filters shouldBe empty
  }

  test("Parse --filter arguments with conditions") {
    val args = Array("--input-file", "data.csv", "--filter", "A", ">", "5")
    val cliParser = new CliParser(args)
    val (options, filters) = cliParser.parse()

    options should contain key "inputFile"
    options("inputFile") shouldEqual "data.csv"
    filters should have size 1
    filters.head shouldBe a[ColumnConditionFilter]

    filters.head match {
      case filter: ColumnConditionFilter =>
        filter.column shouldEqual 0
        filter.condition(6) shouldBe true
        filter.condition(4) shouldBe false
      case _ => fail("Expected ColumnConditionFilter")
    }
  }


  test("Parse multiple arguments") {
    val args = Array("--input-file", "data.csv", "--output-file", "output.csv", "--filter",  "A", ">", "5")
    val cliParser = new CliParser(args)
    val (options, filters) = cliParser.parse()

    options should contain key "inputFile"
    options should contain key "outputFile"
    options("inputFile") shouldEqual "data.csv"
    options("outputFile") shouldEqual "output.csv"
    filters should have size 1
    filters.head shouldBe a[ColumnConditionFilter]
  }

  test("Handle missing --input-file argument") {
    val args = Array("--output-file", "output.csv")
    val cliParser = new CliParser(args)
    val (options, filters) = cliParser.parse()

    options shouldBe empty
    filters shouldBe empty
  }

  test("Handle unknown arguments") {
    val args = Array("--unknown", "value")
    val cliParser = new CliParser(args)
    val (options, filters) = cliParser.parse()

    options shouldBe empty
    filters shouldBe empty
  }

  test("Parse empty arguments") {
    val args = Array.empty[String]
    val cliParser = new CliParser(args)
    val (options, filters) = cliParser.parse()

    options shouldBe empty
    filters shouldBe empty
  }

  test("Parse multiple filters with mixed conditions") {
    val args = Array("--input-file", "data.csv", "--filter", "A", ">", "5", "--filter", "B", "<", "10", "--filter", "C", "==", "15")
    val cliParser = new CliParser(args)
    val (options, filters) = cliParser.parse()

    options should contain key "inputFile"
    options("inputFile") shouldEqual "data.csv"

    filters should have size 3
    filters(0) shouldBe a[ColumnConditionFilter]
    filters(1) shouldBe a[ColumnConditionFilter]
    filters(2) shouldBe a[ColumnConditionFilter]
  }

  test("Parse --help alongside other arguments") {
    val args = Array("--help", "--input-file", "data.csv")
    val cliParser = new CliParser(args)
    val (options, filters) = cliParser.parse()

    options should contain key "help"
    options("help") shouldEqual true
    filters shouldBe empty
  }

  test("Handle invalid filter condition") {
    val args = Array("--filter", "InvalidCondition")
    val cliParser = new CliParser(args)
    val (options, filters) = cliParser.parse()

    options shouldBe empty
    filters shouldBe empty

    val outputStream = new java.io.ByteArrayOutputStream()
    Console.withOut(outputStream) {
      cliParser.parse()
    }

    val output = outputStream.toString
    output should include("Error: Invalid format for --filter")
  }

  test("Handle invalid operator in filter condition") {
    val args = Array("--filter", "A", "<>", "5")
    val cliParser = new CliParser(args)

    val (options, filters) = cliParser.parse()

    options shouldBe empty
    filters shouldBe empty

    val outputStream = new java.io.ByteArrayOutputStream()
    Console.withOut(outputStream) {
      cliParser.parse()
    }

    val output = outputStream.toString
    output should include("Error: Unknown operator: <>")
  }

  test("Convert column name to index") {
    val cliParser = new CliParser(Array.empty)
    val columnIndex = cliParser.getClass.getDeclaredMethod("columnNameToIndex", classOf[String])
    columnIndex.setAccessible(true)

    columnIndex.invoke(cliParser, "A") shouldEqual 0
    columnIndex.invoke(cliParser, "B") shouldEqual 1
    columnIndex.invoke(cliParser, "Z") shouldEqual 25
    columnIndex.invoke(cliParser, "AA") shouldEqual 26
    columnIndex.invoke(cliParser, "AB") shouldEqual 27
  }

  test("Parse filter conditions with valid operators") {
    val cliParser = new CliParser(Array.empty)
    val parseCondition = cliParser.getClass.getDeclaredMethod("parseCondition", classOf[String], classOf[Int])
    parseCondition.setAccessible(true)

    val gtCondition = parseCondition.invoke(cliParser, ">", 5).asInstanceOf[Int => Boolean]
    gtCondition(6) shouldEqual true
    gtCondition(4) shouldEqual false

    val eqCondition = parseCondition.invoke(cliParser, "==", 10).asInstanceOf[Int => Boolean]
    eqCondition(10) shouldEqual true
    eqCondition(11) shouldEqual false
  }
}
