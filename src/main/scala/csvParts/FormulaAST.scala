package csvParts

import csvParts.FormulaAST

sealed trait FormulaAST // Abstract Syntax Tree

case class NumberNode(value: Int) extends FormulaAST
case class CellReferenceNode(reference: String) extends FormulaAST
case class BinaryOperationNode(left: FormulaAST, operator: String, right: FormulaAST) extends FormulaAST
case class ParenthesisNode(inner: FormulaAST) extends FormulaAST
