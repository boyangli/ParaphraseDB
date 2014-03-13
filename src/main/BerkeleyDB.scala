package main

import com.sleepycat.db._
import com.sleepycat.bind.tuple.TupleBinding
import com.sleepycat.bind.tuple.TupleInput
import com.sleepycat.bind.tuple.TupleOutput
import java.io.File

class BerkeleyDB {

  var ppdb: Database = null
  var env: Environment = null

  setup()

  /**
   * setting up the data base
   *  The Java Edition does not support HASH.
   *  See http://stackoverflow.com/questions/4674674/cannot-change-berkeley-db-database-type-in-java-edition
   */
  def setup() {
    val home = new File("""H:\Albert\PPDB-BerkeleyDB\""")
    //val home = new File("""./data/PPDB""")
    val envConfig = new EnvironmentConfig()
    val dbConfig = new DatabaseConfig()
    envConfig.setAllowCreate(true)
    dbConfig.setAllowCreate(true)

    dbConfig.setType(DatabaseType.HASH)

    envConfig.setTransactional(false)
    dbConfig.setTransactional(false)

    dbConfig.setUnsortedDuplicates(true)

    envConfig.setInitializeCache(true);
    envConfig.setInitializeLocking(true);
    //envConfig.setCacheSize(1048576)
    env = new Environment(home, envConfig)

    //println("cache size =" + env.getConfig().getCacheSize())
    ppdb = env.openDatabase(null, "paraphraseDB.db", "paraphraseDB", dbConfig)
    //ppdb = new Database("./data/PPDB/paraphraseDB.db", "paraphraseDB", dbConfig);

  }

  def sync() {
    ppdb.sync()
  }

  def putBothEnd(rule: Rule) {
    var theKey = new DatabaseEntry(rule.source.getBytes(java.nio.charset.Charset.forName("UTF-8")))
    val theData = new DatabaseEntry()
    val binding = new RuleBinding()
    binding.objectToEntry(rule, theData)
    ppdb.put(null, theKey, theData)

    theKey = new DatabaseEntry(rule.target.getBytes(java.nio.charset.Charset.forName("UTF-8")))
    ppdb.put(null, theKey, theData)
  }

  def putSingle(key: String, rule: Rule) {
    var theKey = new DatabaseEntry(key.getBytes(java.nio.charset.Charset.forName("UTF-8")))
    val theData = new DatabaseEntry()
    val binding = new RuleBinding()
    binding.objectToEntry(rule, theData)
    ppdb.put(null, theKey, theData)
  }

  def get(key: String): Option[Rule] =
    {
      var theKey = new DatabaseEntry(key.getBytes(java.nio.charset.Charset.forName("UTF-8")))
      val theData = new DatabaseEntry()
      var rule: Rule = null
      if (ppdb.get(null, theKey, theData, LockMode.DEFAULT) ==
        OperationStatus.SUCCESS) {

        // Recreate the data String.
        val binding = new RuleBinding()
        rule = binding.entryToObject(theData)
        Some(rule)
      } else {
        None
      }

    }

  def getAll(key: String): List[Rule] =
    {

      val keyString = key.trim
      val cursorConfig = new CursorConfig()
      cursorConfig.setReadUncommitted(true)
      val cursor = ppdb.openCursor(null, cursorConfig)
      val results = scala.collection.mutable.ListBuffer[Rule]()

      var theKey = new DatabaseEntry(keyString.getBytes(java.nio.charset.Charset.forName("UTF-8")))
      val theData = new DatabaseEntry()
      var rule: Rule = null

      var retVal = cursor.getSearchKey(theKey, theData,
        LockMode.DEFAULT);
      // Count the number of duplicates. If the count is greater than 1,
      // print the duplicates.
      while (retVal == OperationStatus.SUCCESS) {
        val binding = new RuleBinding()
        rule = binding.entryToObject(theData)
        results += rule
        retVal = cursor.getNextDup(theKey, theData, LockMode.DEFAULT);
      }

      results.toList
    }

  def close() {
    if (ppdb != null) {
      ppdb.close
    }

    if (env != null) {
      env.close
    }

  }
}

class RuleBinding extends TupleBinding[Rule] {

  override def entryToObject(ti: TupleInput) = {

    val lhs = ti.readString()
    val source: String = ti.readString()
    val target: String = ti.readString()
    val Abstract: Boolean = ti.readBoolean()
    val Adjacent: Boolean = ti.readBoolean()
    val ContainsX: Boolean = ti.readBoolean()
    val Lex_ef: Double = ti.readDouble()
    val Lex_fe: Double = ti.readDouble()
    val Lexical: Boolean = ti.readBoolean()
    val Monotonic: Boolean = ti.readBoolean()
    val UnalignedSource: Int = ti.readInt()
    val UnalignedTarget: Int = ti.readInt()
    val p_LHS1e: Double = ti.readDouble()
    val p_LHS1f: Double = ti.readDouble()
    val p_e1LHS: Double = ti.readDouble()
    val p_e1f: Double = ti.readDouble()
    val p_e1f_LHS: Double = ti.readDouble()
    val p_f1LHS: Double = ti.readDouble()
    val p_f1e: Double = ti.readDouble()
    val p_f1e_LHS: Double = ti.readDouble()
    val AGigaSim: Double = ti.readDouble()
    val GoogleNgramSim: Double = ti.readDouble()
    val alignment: String = ti.readString()

    new Rule(lhs, source, target, Abstract, Adjacent, ContainsX,
      Lex_ef, Lex_fe, Lexical, Monotonic, UnalignedSource,
      UnalignedTarget, p_LHS1e, p_LHS1f, p_e1LHS, p_e1f, p_e1f_LHS, p_f1LHS, p_f1e,
      p_f1e_LHS, AGigaSim, GoogleNgramSim, alignment)
  }

  override def objectToEntry(rule: Rule, to: TupleOutput) {
    to.writeString(rule.lhs)
    to.writeString(rule.source)
    to.writeString(rule.target)
    to.writeBoolean(rule.Abstract)
    to.writeBoolean(rule.Adjacent)
    to.writeBoolean(rule.ContainsX)
    to.writeDouble(rule.Lex_ef)
    to.writeDouble(rule.Lex_fe)
    to.writeBoolean(rule.Lexical)
    to.writeBoolean(rule.Monotonic)
    to.writeInt(rule.UnalignedSource)
    to.writeInt(rule.UnalignedTarget)
    to.writeDouble(rule.p_LHS1e)
    to.writeDouble(rule.p_LHS1f)
    to.writeDouble(rule.p_e1LHS)
    to.writeDouble(rule.p_e1f)
    to.writeDouble(rule.p_e1f_LHS)
    to.writeDouble(rule.p_f1LHS)
    to.writeDouble(rule.p_f1e)
    to.writeDouble(rule.p_f1e_LHS)
    to.writeDouble(rule.AGigaSim)
    to.writeDouble(rule.GoogleNgramSim)
    to.writeString(rule.alignment)
  }
}

