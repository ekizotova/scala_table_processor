package handlers

import org.scalatest.funsuite.AnyFunSuite
import handlers._
import scala.collection.mutable
import filters.TableFilter
import scala.util.Try

class CliHandlerTest extends AnyFunSuite {
  // Dummy handler for testing
  class DummyCliHandler extends ChainableCliHandler {
    override def handle(args: Array[String], options: mutable.Map[String, Any], filters: mutable.Buffer[TableFilter]): Array[String] = {
      if (args.nonEmpty && args.head == "--test-option") {
        options("testOption") = args(1)
        args.drop(2)
      } else {
        next.fold(args)(_.handle(args, options, filters))
      }
    }
  }

  test("ChainableCliHandler should properly chain handlers together") {
    val handler1 = new DummyCliHandler
    val handler2 = new DummyCliHandler

    handler1.setNext(Option(handler2))

    val args = Array("--test-option", "value", "other", "args")
    val options = mutable.Map[String, Any]()
    val filters = mutable.Buffer[TableFilter]()

    handler1.handle(args, options, filters)

    assert(options("testOption") == "value")
  }

  test("ChainableCliHandler should terminate the chain if no handler exists") {
    val handler1 = new DummyCliHandler
    val args = Array("--test-option", "value", "other", "args")
    val options = mutable.Map[String, Any]()
    val filters = mutable.Buffer[TableFilter]()

    handler1.handle(args, options, filters)

    assert(args.sameElements(Array("--test-option", "value", "other", "args")))
  }

  test("ChainableCliHandler should process arguments and modify options") {
    val handler1 = new DummyCliHandler
    val args = Array("--test-option", "processedValue", "other", "args")
    val options = mutable.Map[String, Any]()
    val filters = mutable.Buffer[TableFilter]()

    handler1.handle(args, options, filters)

    assert(options("testOption") == "processedValue")
  }

  test("CliHandler should return description when describe is called") {
    val handler = new DummyCliHandler
    val description = handler.describe()

    assert(description.isEmpty)
  }

  test("CliHandler should return description when describe is overridden") {
    class CustomCliHandler extends ChainableCliHandler {
      override def describe(): Option[String] = Option("Custom CLI Handler description")
      override def handle(args: Array[String], options: mutable.Map[String, Any], filters: mutable.Buffer[TableFilter]): Array[String] = args
    }

    val handler = new CustomCliHandler
    val description = handler.describe()

    assert(description.contains("Custom CLI Handler description"))
  }
}
