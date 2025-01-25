package handlers

import filters.TableFilter

import scala.collection.mutable

class UnknownArgumentHandler(var next: Option[CliHandler]) extends CliHandler {
  def setNext(nextHandler: Option[CliHandler]): Unit = {
    next = nextHandler
  }

  override def handle(args: Array[String], options: mutable.Map[String, Any],
                      filters: mutable.Buffer[TableFilter]): Array[String] = {
    if (args.nonEmpty) {
      options("unknown") = true
      println(s"Error: Unknown argument '${args.head}'.")
      println("Use --help or -h for more information.")
      Array.empty[String] 
    } else {
      next.map(_.handle(args, options, filters)).getOrElse(args)
    }
  }

  override def describe(): Option[String] = None 
}
