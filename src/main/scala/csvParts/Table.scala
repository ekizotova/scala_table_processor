package csvParts

case class Table(cells: Map[Coordinate, Cell]) {
  def get(coordinate: Coordinate): Option[Cell] = cells.get(coordinate)

  // Insert a new cell value at a given coordinate
  def insert(coordinate: Coordinate, cell: Cell): Table =
    copy(cells = cells.updated(coordinate, cell))

  def map(f: Cell => Cell): Table =
    copy(cells = cells.map { case (coordinate, cell) => (coordinate, f(cell)) })

  // Convert the cells into a row-major structure (list of lists)
  def rows: Vector[Vector[Cell]] = {
    if (cells.isEmpty) Vector.empty
    else {
      val maxRow = cells.keys.map(_.row).max
      val maxCol = cells.keys.map(_.col).max

      (0 to maxRow).map { row =>
        (0 to maxCol).map { col =>
          cells.getOrElse(Coordinate(row, col), Empty)
        }.toVector
      }.toVector
    }
  }
}
