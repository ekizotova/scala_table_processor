package loaders

trait TableLoader {
  def load(source: String): String
}
