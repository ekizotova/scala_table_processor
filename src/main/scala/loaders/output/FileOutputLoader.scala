package loaders.output

import loaders.output.OutputLoader

import java.io.{File, PrintWriter, IOException}

class FileOutputLoader (file: String) extends OutputLoader {
  override def write(output: String): Unit = {
    try {
      val pw = new PrintWriter(new File(file))
      try {
        pw.write(output)
      } finally {
        try {
          pw.close()
        } catch {
          case e: IOException =>
            throw new IOException("Error closing the file", e)
        }
      }
    } catch {
      case e: IOException =>
        throw new RuntimeException(s"Failed to write to the file: ${e.getMessage}", e)
    }
  }
}
