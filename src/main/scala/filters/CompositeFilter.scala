package filters

import csvParts.Table

// CompositeFilter is a filter that applies multiple filters in sequence
class CompositeFilter(filters: Seq[TableFilter]) extends TableFilter {
  override def apply(table: Table): Table =
    filters.foldLeft(table)((t, filter) => filter(t))
}
