package main

import java.io._
import java.util.Scanner
import java.util.zip._

object LineCounter {

  def main(args: Array[String]) {
    val inputStream = new GZIPInputStream(new FileInputStream("""../../ppdb-1.0-xxxl-all.gz"""))
    val scanner = new Scanner(inputStream)
    var lineCount = 0

    while (scanner.hasNextLine()) {
      scanner.nextLine
      lineCount += 1

      if (lineCount % 100000 == 0) {
        println(lineCount + " lines")
      }
    }
    
    println(lineCount + " lines in total")
      
  }
}