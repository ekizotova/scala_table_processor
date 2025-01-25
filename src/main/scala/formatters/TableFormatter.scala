package formatters

import csvParts.Table

// TableFormatter is a trait that defines the format method, which will format a table as a string.


trait TableFormatter {
  def format(table: Table): String
}
