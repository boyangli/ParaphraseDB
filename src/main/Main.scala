package main
import java.util.zip._
import java.io._
import java.util.Scanner

object Main {

  case class Record(lhs: String, f: String, e: String, properties: String, suffix: String) {
    override def toString() =
      lhs + " ||| " + f + " ||| " + e + " ||| " + properties + " ||| " + suffix
  }

  def main(args: Array[String]) {
    import com.sleepycat.db._

    val db = new BerkeleyDB()
    val loader = new Loader("""../../ppdb-1.0-xxxl-all.gz""")

    var list: List[Rule] = Nil

    do {
      val time = System.currentTimeMillis()
      list = loader.loadBatch(50000)

      list foreach
        {
          rule =>
            db.putBothEnd(rule)
        }

      println(list.head)

      db.sync
      loader.printStats
      val nextTime = System.currentTimeMillis()
      println("time = " + (nextTime - time) / 1000.0)
    } while (list != Nil)

    db.close

    //    for (r <- rules) {
    //      println(r.toFullString)

    //            println("**")
    //      db.put(r)
    //    }

    //    val results = db.getAll("webdgspn")
    //
    //    println("retrieved " + results.size)
    //
    //    println("from db: " + results.map(_.toFullString).mkString("\n"))
    //    }
    //    val secs = (System.currentTimeMillis() - time).toDouble / 10000 / 1000
    //    println("seconds = " + secs)
    //    for (r <- rules) {
    //      
    //      val rule = db.get(r.source)
    //      println("from db: " + rule.get.toFullString)
    //    }
  }

  def abbreviate(scanner: Scanner, pw: PrintWriter) {

    var count = 0
    var lineCount = 1

    var line1 = scanner.nextLine()
    var data1 = decode(line1)

    while (scanner.hasNextLine()) {

      if (lineCount % 10000 == 0) {
        println("processed " + lineCount + " lines.")
      }

      val line2 = scanner.nextLine()
      val data2 = decode(line2)

      if (data1.lhs == data2.lhs && data1.e == data2.f && data1.f == data2.e) {
        pw.println(data1)
        count += 1

        if (scanner.hasNextLine()) {
          line1 = scanner.nextLine()
          data1 = decode(line1)
        } else {
          println("# duplicates = " + count)
          println("# lines = " + lineCount)
          System.exit(0)
        }

      } else {
        // outputs one
        pw.println(data1)

        data1 = data2
      }
      lineCount += 1
    }

    pw.println(data1)

    println(lineCount)
    println("identical pairs = " + count)
    pw.close
  }

  def decode(text: String): Record =
    {
      val split = text.split("\\s\\|\\|\\|\\s")
      val propText = split(3).split(" ")
      val props = propText.filterNot(x =>
        x.startsWith("GlueRule") || x.startsWith("PhrasePenalty") || x.startsWith("RarityPenalty") || x.startsWith("Char") || x.startsWith("Word")).mkString(" ")

      Record(split(0), split(1), split(2), props, split(4))
    }
}