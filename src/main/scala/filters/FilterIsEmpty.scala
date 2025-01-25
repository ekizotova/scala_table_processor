package filters

import csvParts.{Coordinate, Table, Empty, Cell}

// FilterIsEmpty is a filter that keeps only rows where the specified column contains empty cells

case class FilterIsEmpty(column: Int) extends TableFilter {
  override def apply(table: Table): Table = {
    val rowIndices = table.cells.collect {
      case (Coordinate(row, col), Empty) if col == column => row
    }.toSet

    val filteredCells = table.cells.filter {
      case (Coordinate(row, _), _) => rowIndices.contains(row)
    }

    Table(filteredCells)
  }
}
