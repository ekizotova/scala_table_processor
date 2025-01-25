package filters

import csvParts.{Coordinate, Table, Empty, Cell}

// FilterIsNotEmpty is a filter that keeps only rows where the specified column contains non-empty cells

case class FilterIsNotEmpty(column: Int) extends TableFilter {
  override def apply(table: Table): Table = {
    val rowIndices = table.cells.collect {
      case (Coordinate(row, col), cell) if col == column && cell != Empty => row
    }.toSet

    val filteredCells = table.cells.filter {
      case (Coordinate(row, _), _) => rowIndices.contains(row)
    }

    Table(filteredCells)
  }
}
