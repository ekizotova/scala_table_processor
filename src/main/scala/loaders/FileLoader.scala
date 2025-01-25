package loaders

import java.io.File
import scala.io.Source

class FileLoader extends TableLoader {  
  def load(filename: String): String = {
    try {
      val source = Source.fromFile(new File(filename))
      try {
        source.mkString
      } finally {
        source.close()
      }
    } catch {
      case e: Exception => throw new RuntimeException(s"Error loading file $filename: ${e.getMessage}", e)
    }
  }
}
