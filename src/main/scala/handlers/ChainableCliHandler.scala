package handlers

// Chain of Responsibility for CLI handler

trait ChainableCliHandler extends CliHandler {
  protected var next: Option[CliHandler] = None

  def setNext(nextHandler: Option[CliHandler]): Unit = {
    next = nextHandler
  }
}
