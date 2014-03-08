package main

import com.sleepycat.je._
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
    val home = new File("./data/PPDB")
    
    val envConfig = new EnvironmentConfig()
    val dbConfig = new DatabaseConfig()
    envConfig.setAllowCreate(true)
    dbConfig.setAllowCreate(true)
    
    envConfig.setTransactional(true)
    dbConfig.setTransactional(true)   
    
    env = new Environment(home, envConfig)
    
    
    ppdb = env.openDatabase(null, "sampleDatabase.db",
      dbConfig);
  }

  def put(rule: Rule) {
    var theKey = new DatabaseEntry(rule.source.getBytes(java.nio.charset.Charset.forName("UTF-8")))
    val theData = new DatabaseEntry()
    val binding = new RuleBinding()
    binding.objectToEntry(rule, theData);
    ppdb.put(null, theKey, theData);
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

