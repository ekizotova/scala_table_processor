package formatters

import csvParts.{Empty, Formula, Number, Table, Coordinate}


// MarkdownFormatter formats a table as a Markdown table representation


class MarkdownFormatter(includeHeaders: Boolean = false) extends TableFormatter {
  override def format(table: Table): String = {
    val numCols = table.cells.keys.map(_.col).max + 1

    // Filter rows where column B is empty
    val filteredRows = table.cells.collect {
      case (Coordinate(row, _), cell) if cell != Empty => row
    }.toSeq.sorted.distinct  //  no duplicates in the filtered rows

    // Compute max widths for each column
    val maxWidths = (0 until numCols).map { col =>
      val columnValues = filteredRows.map { row =>
        table.get(Coordinate(row, col)) match {
          case Some(Number(value)) => value.toString
          case Some(Formula(expression)) => expression
          case Some(Empty) => ""
          case None => ""
        }
      }
      val header = ('A' + col).toChar.toString
      (columnValues :+ header).map(_.length).max // Include headers 
    }

    // Add extra column for row numbers if headers are true
    val rowNumberColumnWidth = if (includeHeaders) (1 to filteredRows.size).map(_.toString.length).max else 0
    val totalWidths = if (includeHeaders) rowNumberColumnWidth +: maxWidths else maxWidths

    // Format headers (if true)
    val headers = if (includeHeaders) {
      val headerRow = ("" +: (0 until numCols).map(col => ('A' + col).toChar.toString))
      formatRow(headerRow, totalWidths, isHeader = true)
    } else ""

    // Header separator row
    val headerSeparator = if (includeHeaders) formatSeparator(totalWidths) else ""

    // Format rows with new indexing from 1
    val rows = filteredRows.zipWithIndex.map { case (row, index) =>
      val cells = (if (includeHeaders) Seq((index + 1).toString) else Seq()) ++
        (0 until numCols).map { col =>
          table.get(Coordinate(row, col)) match {
            case Some(Number(value)) => value.toString
            case Some(Formula(expression)) => expression
            case Some(Empty) => ""
            case None => ""
          }
        }
      formatRow(cells, totalWidths)
    }

    // Add first empty row for "headers false" case
    val rowsWithFirstEmptyRow = if (!includeHeaders) {
      val emptyRow = Seq.fill(numCols)("")
      val formattedEmptyRow = formatRow(emptyRow, totalWidths)

      // Add separator row after the first empty row
      val separatorRow = formatSeparator(totalWidths)

      val rowsWithSeparator = formattedEmptyRow + "\n" + separatorRow + "\n" + rows.mkString("\n")
      rowsWithSeparator
    } else {
      rows.mkString("\n")
    }

    // Combine 
    Seq(
      if (includeHeaders) s"$headers\n$headerSeparator" else "",
      rowsWithFirstEmptyRow
    ).filter(_.nonEmpty).mkString("\n")
  }

  private def formatRow(cells: Seq[String], maxWidths: Seq[Int], isHeader: Boolean = false): String = {
    val alignedCells = cells.zip(maxWidths).map { case (cell, maxWidth) =>
      val padding = maxWidth - cell.length
      " " * padding + cell 
    }
    alignedCells.mkString("| ", " | ", " |")
  }

  private def formatSeparator(maxWidths: Seq[Int]): String = {
    maxWidths.map(width => "-" * width).mkString("|-", "-|-", "-|")
  }
}

