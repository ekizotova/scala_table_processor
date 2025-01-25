package formatters

import csvParts.{Coordinate, Empty, Formula, Number, Table}

// CsvFormatter formats a table into a CSV (Comma-Separated default) representation

class CsvFormatter(separator: String = ",", includeHeaders: Boolean = false) extends TableFormatter {

  // Format the table as a CSV string

  override def format(table: Table): String = {
    val numRows = table.cells.keys.map(_.row).max + 1
    val numCols = table.cells.keys.map(_.col).max + 1

    // Compute max lengths for each column
    val maxLengths = (0 until numCols).map { col =>
      val columnValues = (0 until numRows).map { row =>
        table.get(Coordinate(row, col)) match {
          case Some(Number(value)) => value.toString
          case Some(Formula(expression)) => expression
          case Some(Empty) => ""
          case None => "" // Missing cells are treated as empty
        }
      }
      val header = ('A' + col).toChar.toString
      (columnValues :+ header).map(_.length).max // Include the header in max length computation
    }

    // Add extra column for row numbers if headers are included
    val rowNumberColumnWidth = if (includeHeaders) (1 to table.cells.size).map(_.toString.length).max else 0
    val totalLengths = if (includeHeaders) rowNumberColumnWidth +: maxLengths else maxLengths

    // Generate column headers if stated
    val headers = if (includeHeaders) {
      val headerRow = ("" +: (0 until numCols).map(col => ('A' + col).toChar.toString))
      formatRow(headerRow, totalLengths, separator)
    } else ""

    // only include rows that are not empty
    val filteredRows = table.cells.collect {
      case (Coordinate(row, _), cell) if cell != Empty => row
    }.toSeq.sorted  // Convert to Seq and sort to maintain order

    //  filtered rows without duplication
    val rows = filteredRows.distinct.zipWithIndex.map { case (row, index) =>  // Re-index the rows here
      val cells = (if (includeHeaders) Seq((index + 1).toString) else Seq()) ++  // Use the new index for row numbering
        (0 until numCols).map { col =>
          table.get(Coordinate(row, col)) match {
            case Some(Number(value)) => value.toString
            case Some(Formula(expression)) => expression
            case Some(Empty) => ""
            case None => ""
          }
        }
      formatRow(cells, totalLengths, separator)
    }

    if (headers.nonEmpty) headers + "\n" + rows.mkString("\n")
    else rows.mkString("\n")
  }

  private def formatRow(cells: Seq[String], maxLengths: Seq[Int], separator: String): String = {
    val alignedCells = cells.zip(maxLengths).map { case (cell, maxLength) =>
      val escapedCell = escapeCell(cell, separator)
      escapedCell.reverse.padTo(maxLength, ' ').reverse // Align to the right
    }
    
    // Add space after the separator if it's a comma or semicolon
    val sep = separator match {
      case "," | ";" => s"$separator "
      case "\t" => "\t" //  handle tabs
      case _ => separator.toString
    }
    alignedCells.mkString(sep)
  }

  // Escaping quotes
  private def escapeCell(cell: String, separator: String): String = {
    if (cell.contains(separator) || cell.contains('"')) {
      "\"" + cell.replace("\"", "\"\"") + "\""
    } else {
      cell
    }
  }
}


/* more refactored version
class CsvFormatter(separator: String = ",", includeHeaders: Boolean = false) extends TableFormatter {
  override def format(table: Table): String = {
    val (numRows, numCols) = computeTableDimensions(table)
    val maxLengths = computeMaxLengths(table, numRows, numCols)

    val rowNumberColumnWidth = if (includeHeaders) computeRowNumberWidth(table) else 0
    val totalLengths = if (includeHeaders) rowNumberColumnWidth +: maxLengths else maxLengths

    val headers = generateHeaders(numCols, totalLengths)
    val rows = generateRows(table, numCols, totalLengths)

    Seq(headers, rows).filter(_.nonEmpty).mkString("\n")
  }

  private def computeTableDimensions(table: Table): (Int, Int) = {
    val numRows = table.cells.keys.map(_.row).max + 1
    val numCols = table.cells.keys.map(_.col).max + 1
    (numRows, numCols)
  }

  private def computeMaxLengths(table: Table, numRows: Int, numCols: Int): Seq[Int] = {
    (0 until numCols).map { col =>
      val columnValues = (0 until numRows).map { row =>
        table.get(Coordinate(row, col)) match {
          case Some(Number(value)) => value.toString
          case Some(Formula(expression)) => expression
          case Some(Empty) => ""
          case None => ""
        }
      }
      val header = ('A' + col).toChar.toString
      (columnValues :+ header).map(_.length).max
    }
  }

  private def computeRowNumberWidth(table: Table): Int = {
    (1 to table.cells.size).map(_.toString.length).max
  }

  private def generateHeaders(numCols: Int, totalLengths: Seq[Int]): String = {
    if (includeHeaders) {
      val headerRow = "" +: (0 until numCols).map(col => ('A' + col).toChar.toString)
      formatRow(headerRow, totalLengths, separator)
    } else ""
  }

  private def generateRows(table: Table, numCols: Int, totalLengths: Seq[Int]): String = {
    val filteredRows = table.cells.collect {
      case (Coordinate(row, _), cell) if cell != Empty => row
    }.toSeq.sorted.distinct

    filteredRows.zipWithIndex.map { case (row, index) =>
      val cells = (if (includeHeaders) Seq((index + 1).toString) else Seq()) ++
        (0 until numCols).map { col =>
          table.get(Coordinate(row, col)) match {
            case Some(Number(value)) => value.toString
            case Some(Formula(expression)) => expression
            case Some(Empty) => ""
            case None => ""
          }
        }
      formatRow(cells, totalLengths, separator)
    }.mkString("\n")
  }

  private def formatRow(cells: Seq[String], maxLengths: Seq[Int], separator: String): String = {
    val alignedCells = cells.zip(maxLengths).map { case (cell, maxLength) =>
      val escapedCell = escapeCell(cell, separator)
      escapedCell.reverse.padTo(maxLength, ' ').reverse
    }
    val sep = separator match {
      case "," | ";" | "|" => s"$separator "
      case "\t" => "\t"
      case _ => separator
    }
    alignedCells.mkString(sep)
  }

  private def escapeCell(cell: String, separator: String): String = {
    if (cell.contains(separator) || cell.contains('"')) {
      "\"" + cell.replace("\"", "\"\"") + "\""
    } else {
      cell
    }
  }
}

 */
