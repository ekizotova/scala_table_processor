package loaders.output

import loaders.output.OutputLoader

class StdoutLoader extends OutputLoader {
  override def write(output: String): Unit = {
    println(output)
  }
}
