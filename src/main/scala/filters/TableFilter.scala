package filters

import csvParts.Table

// TableFilter is a trait that defines the apply method, which filters a table

trait TableFilter {
  def apply(table: Table): Table
}
