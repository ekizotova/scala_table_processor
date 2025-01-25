package parsers

import csvParts.{FormulaAST, NumberNode, CellReferenceNode, BinaryOperationNode, ParenthesisNode}

// FormulaParser class to parse formulas and build an Abstract Syntax Tree (AST)


class FormulaParser(operators: Map[String, (Int, Int) => Int]) {
  def parseFormula(formula: String): Either[String, FormulaAST] = {
    val tokens = tokenize(formula)
    if (tokens.isEmpty) return Left("Empty formula")

    var currentNode: FormulaAST = null
    var currentOperator: String = null

    // Process each token in the formula

    for (token <- tokens) {
      token match {
        case num if num.forall(_.isDigit) =>
          val numberNode = NumberNode(num.toInt)
          currentNode = appendToAST(currentNode, currentOperator, numberNode)
          currentOperator = null
        case ref if ref.matches("[A-Z]+[1-9]\\d*") =>
          val cellRefNode = CellReferenceNode(ref)
          currentNode = appendToAST(currentNode, currentOperator, cellRefNode)
          currentOperator = null
        case op if operators.contains(op) =>
          if (currentOperator != null) {
            return Left("Invalid formula structure") // Detect consecutive operators
          }
          currentOperator = op
        case invalidToken =>
          Left(s"Invalid token: $invalidToken") //  return error for invalid token
      }
    }

    if (currentOperator != null) {
      Left("Invalid formula structure") // Detect dangling operator at the end
    } else if (currentNode == null) {
      Left("Invalid formula structure") // Ensure there's at least one valid token
    } else {
      Right(currentNode)
    }
  }

  // Helper method to append nodes to the AST

  private def appendToAST(leftNode: FormulaAST, operator: String, rightNode: FormulaAST): FormulaAST = {
    if (operator == null) rightNode
    else BinaryOperationNode(leftNode, operator, rightNode)
  }

  // Helper method to tokenize the formula string into a list of tokens

  private def tokenize(formula: String): List[String] = {
    val pattern = """[A-Z]+[1-9]\d*|\d+|[+*/()-]""".r
    pattern.findAllIn(formula).toList
  }
}
