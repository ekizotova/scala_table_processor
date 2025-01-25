package filters

import csvParts.{Coordinate, Empty, Number, Table}

// ColumnConditionFilter is a filter that applies a condition on a specific column of the table
case class ColumnConditionFilter(column: Int, condition: Int => Boolean) extends TableFilter {
  override def apply(table: Table): Table = {
    val filteredCells = table.cells.filter { case (coordinate, cell) =>
      coordinate.col == column && (cell match {
        case Number(value) => condition(value)
        case _ => false // Ignore non-numeric cells
      })
    }

    val rowIndices = filteredCells.keys.map(_.row).toSet
    val retainedCells = table.cells.filterKeys(coordinate => rowIndices.contains(coordinate.row)).toMap
    Table(retainedCells)
  }
}
