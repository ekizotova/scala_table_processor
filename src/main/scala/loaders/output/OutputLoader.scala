package loaders.output

trait OutputLoader {
  def write(output: String): Unit
}
