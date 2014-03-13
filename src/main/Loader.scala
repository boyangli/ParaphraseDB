package main

import java.io._
import java.util.zip._
import java.util.Scanner
import scala.collection.mutable.ListBuffer
import java.text.DecimalFormat

class Loader(filename: String) {

  private var inputStream = new GZIPInputStream(new FileInputStream(filename))
  private var scanner = new Scanner(inputStream)

  private var count = 0
  private var duplicates = 0
  private var lineCount = 1
  private var tenK = 1
  private var line1: String = null
  private var rule1: Rule = null
  /**
   * load a number of rule from the gz file
   * if the number is less than zero, load every rule
   */
  def load(filename: String, limit: Int): List[Rule] =
    {
      val list = ListBuffer[Rule]()
      def func(rule: Rule) {
        list += rule
      }

      loadAndExec(filename, limit, func)
      list.toList
    }

  def printStats() {
    println("# duplicates = " + duplicates)
    println("# lines = " + lineCount)
    println("# valid rules = " + count)
  }

  /**
   * load a number of rule from the gz file
   * if the number is less than zero, load every rule
   */
  def loadBatch(limit: Int): List[Rule] = {

    var list = List[Rule]()
    var i = 0

    if (line1 == null) {
      if (scanner.hasNextLine()) {
        line1 = scanner.nextLine()
        rule1 = Rule(line1)
      } else {
        return list
      }
    }

    while (scanner.hasNextLine()) {

      val line2 = scanner.nextLine()
      val rule2 = Rule(line2)
      lineCount += 1

      if (rule1.lhs == rule2.lhs && rule1.source == rule2.target && rule1.source == rule2.target) {
        // duplicates detected
        duplicates += 1

        if (isValid(rule1)) {
          list = rule1 :: list
          count += 1
          i += 1
        }

        if (scanner.hasNextLine()) {
          line1 = scanner.nextLine()
          rule1 = Rule(line1)
          lineCount += 1
        } else {

          return list
        }

      } else {
        // outputs one
        if (isValid(rule1)) {
          list = rule1 :: list
          count += 1
          i += 1
        }
        rule1 = rule2
      }

      if (i >= limit) {
        return list
      }
    }

    list
  }

  /**
   * load a number of rule from the gz file
   * if the number is less than zero, load every rule
   */
  def loadAndExec(filename: String, limit: Int, func: Rule => Unit) {

    var count = 0
    var duplicates = 0
    var lineCount = 1
    var tenK = 1
    var line1 = scanner.nextLine()
    var rule1 = Rule(line1)

    while (scanner.hasNextLine()) {

      if (lineCount >= tenK * 10000) {
        println("processed " + lineCount + " lines.")
        tenK += 1
      }

      val line2 = scanner.nextLine()
      val rule2 = Rule(line2)
      lineCount += 1

      if (rule1.lhs == rule2.lhs && rule1.source == rule2.target && rule1.source == rule2.target) {
        // duplicates detected
        duplicates += 1

        if (isValid(rule1)) {
          func(rule1)
          count += 1
        }

        if (scanner.hasNextLine()) {
          line1 = scanner.nextLine()
          rule1 = Rule(line1)
          lineCount += 1
        } else {
          println("# duplicates = " + duplicates)
          println("# lines = " + lineCount)
          println("# valid rules = " + count)
          return
        }

      } else {
        // outputs one
        if (isValid(rule1)) {
          func(rule1)
          count += 1
        }
        rule1 = rule2
      }

      if (limit > 0 && count >= limit) {
        println("# duplicates = " + duplicates)
        println("# lines = " + lineCount)
        println("# valid rules = " + count)
        return
      }
    }

    println("# duplicates = " + duplicates)
    println("# lines = " + lineCount)
    println("# valid rules = " + count)
  }

  /**
   * a valid rule must contain characters other than numbers and punctuation symbols
   *
   */
  def isValid(rule: Rule): Boolean =
    {
      def isLetter(char: Char) = (char >= 'A' && char <= 'Z') || (char >= 'a' && char <= 'z');

      {
        // either the source or the target must contain letters
        rule.source.exists(isLetter) || rule.target.exists(isLetter)
      } && {
        // must not end with ".html"
        (!rule.source.endsWith(".html")) && (!rule.target.endsWith(".html"))
      } && {
        // must not start with "www" and ends with ".com"
        (!((rule.source.endsWith(".com") || rule.source.endsWith(".org") || rule.source.endsWith(".gov")) && rule.source.startsWith("www."))) && 
        (!((rule.target.endsWith(".com") || rule.target.endsWith(".org") || rule.target.endsWith(".gov")) && rule.target.startsWith("www.")))
      } && {
        // not email addresses
        ! (rule.source.contains("@") || rule.target.contains("@")) 
      }
    }

}

class Rule(val lhs: String, val source: String, val target: String, val Abstract: Boolean, val Adjacent: Boolean,
  val ContainsX: Boolean, val Lex_ef: Double, val Lex_fe: Double,
  val Lexical: Boolean, val Monotonic: Boolean, val UnalignedSource: Int, val UnalignedTarget: Int,
  val p_LHS1e: Double, val p_LHS1f: Double, val p_e1LHS: Double,
  val p_e1f: Double, val p_e1f_LHS: Double, val p_f1LHS: Double, val p_f1e: Double,
  val p_f1e_LHS: Double, val AGigaSim: Double, val GoogleNgramSim: Double, val alignment: String) {

  override def toString() = lhs + " => " + source + " | " + target

  override def equals(any: Any) = any match {
    case that: Rule =>
      this.lhs == that.lhs && this.source == that.source && this.target == that.target
    case _ => false
  }

  def toFullString() =
    {
      val df = new DecimalFormat("##0.00####");
      val builder = new StringBuilder()
      builder.append(lhs + " ||| ")
      builder.append(source + " ||| ")
      builder.append(target + " ||| ")
      builder.append("Abstract=" + Rule.toZeroOne(Abstract))
      builder.append(" Adjacent=" + Rule.toZeroOne(Adjacent))
      builder.append(" ContainsX=" + Rule.toZeroOne(ContainsX))
      builder.append(" Lex(e|f)=" + df.format(Lex_ef))
      builder.append(" Lex(f|e)=" + df.format(Lex_fe))
      builder.append(" Lexical=" + Rule.toZeroOne(Lexical))
      builder.append(" Monotonic=" + Rule.toZeroOne(Monotonic))
      builder.append(" UnalignedSource=" + UnalignedSource)
      builder.append(" UnalignedTarget=" + UnalignedTarget)
      builder.append(" p(LHS|e)=" + df.format(p_LHS1e))
      builder.append(" p(LHS|f)=" + df.format(p_LHS1f))
      builder.append(" p(e|LHS)=" + df.format(p_e1LHS))
      builder.append(" p(e|f)=" + df.format(p_e1f))
      builder.append(" p(e|f,LHS)=" + df.format(p_e1f_LHS))
      builder.append(" p(f|LHS)=" + df.format(p_f1LHS))
      builder.append(" p(f|e)=" + df.format(p_f1e))
      builder.append(" p(f|e,LHS)=" + df.format(p_f1e_LHS))
      builder.append(" AGigaSim=" + df.format(AGigaSim))
      builder.append(" GoogleNgramSim=" + df.format(GoogleNgramSim))
      builder.append(" ||| " + alignment)

      builder.toString

    }
}

object Rule {

  private def toBool(text: String) = if (text == "0") false else true

  private def toZeroOne(bool: Boolean) = if (bool) "1" else "0"

  def apply(line: String): Rule =
    {
      val split = line.split("\\s\\|\\|\\|\\s")
      val propText = split(3).split(" ")

      val map = propText.map { p =>
        val two = p.split("=")
        val left = two(0)
        val right = two(1)
        (left, right)
      }
        .toMap

      val lhs = split(0)
      val source: String = split(1)
      val target: String = split(2)
      val Abstract: Boolean = toBool(map("Abstract"))
      val Adjacent: Boolean = toBool(map("Adjacent"))
      val ContainsX: Boolean = toBool(map("ContainsX"))
      val Lex_ef: Double = map("Lex(e|f)").toDouble
      val Lex_fe: Double = map("Lex(f|e)").toDouble
      val Lexical: Boolean = toBool(map("Lexical"))
      val Monotonic: Boolean = toBool(map("Monotonic"))
      val UnalignedSource: Int = map("UnalignedSource").toInt
      val UnalignedTarget: Int = map("UnalignedTarget").toInt
      val p_LHS1e: Double = map("p(LHS|e)").toDouble
      val p_LHS1f: Double = map("p(LHS|f)").toDouble
      val p_e1LHS: Double = map("p(e|LHS)").toDouble
      val p_e1f: Double = map("p(e|f)").toDouble
      val p_e1f_LHS: Double = map("p(e|f,LHS)").toDouble
      val p_f1LHS: Double = map("p(f|LHS)").toDouble
      val p_f1e: Double = map("p(f|e)").toDouble
      val p_f1e_LHS: Double = map("p(f|e,LHS)").toDouble
      val AGigaSim: Double = map("AGigaSim").toDouble
      val GoogleNgramSim: Double = map("GoogleNgramSim").toDouble
      val alignment: String = split(4)

      new Rule(lhs, source, target, Abstract, Adjacent, ContainsX,
        Lex_ef, Lex_fe, Lexical, Monotonic, UnalignedSource,
        UnalignedTarget, p_LHS1e, p_LHS1f, p_e1LHS, p_e1f, p_e1f_LHS, p_f1LHS, p_f1e,
        p_f1e_LHS, AGigaSim, GoogleNgramSim, alignment)

    }

}