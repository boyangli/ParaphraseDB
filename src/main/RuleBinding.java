//package main;
//
//import com.sleepycat.bind.tuple.TupleBinding;
//import com.sleepycat.bind.tuple.TupleInput;
//import com.sleepycat.bind.tuple.TupleOutput;
//
//class RuleBinding extends TupleBinding {
//	@Override
//	public Object entryToObject(TupleInput ti) {
//
//		String lhs = ti.readString();
//		String source = ti.readString();
//		String target = ti.readString();
//		Boolean Abstract = ti.readBoolean();
//		Boolean Adjacent = ti.readBoolean();
//		Boolean ContainsX = ti.readBoolean();
//		double Lex_ef = ti.readDouble();
//		double Lex_fe = ti.readDouble();
//		boolean Lexical = ti.readBoolean();
//		boolean Monotonic = ti.readBoolean();
//		int UnalignedSource = ti.readInt();
//		int UnalignedTarget = ti.readInt();
//		double p_LHS1e = ti.readDouble();
//		double p_LHS1f = ti.readDouble();
//		double p_e1LHS = ti.readDouble();
//		double p_e1f = ti.readDouble();
//		double p_e1f_LHS = ti.readDouble();
//		double p_f1LHS = ti.readDouble();
//		double p_f1e = ti.readDouble();
//		double p_f1e_LHS = ti.readDouble();
//		double AGigaSim = ti.readDouble();
//		double GoogleNgramSim = ti.readDouble();
//		String alignment = ti.readString();
//
//		return new Rule(lhs, source, target, Abstract, Adjacent, ContainsX,
//				Lex_ef, Lex_fe, Lexical, Monotonic, UnalignedSource,
//				UnalignedTarget, p_LHS1e, p_LHS1f, p_e1LHS, p_e1f, p_e1f_LHS,
//				p_f1LHS, p_f1e, p_f1e_LHS, AGigaSim, GoogleNgramSim, alignment);
//	}
//
//	@Override
//	public void objectToEntry(Object obj, TupleOutput to) {
//		Rule rule = (Rule) obj;
//
//		to.writeString(rule.lhs());
//		to.writeString(rule.source());
//		to.writeString(rule.target());
//		to.writeBoolean(rule.Abstract());
//		to.writeBoolean(rule.Adjacent());
//		to.writeBoolean(rule.ContainsX());
//		to.writeDouble(rule.Lex_ef());
//		to.writeDouble(rule.Lex_fe());
//		to.writeBoolean(rule.Lexical());
//		to.writeBoolean(rule.Monotonic());
//		to.writeInt(rule.UnalignedSource());
//		to.writeInt(rule.UnalignedTarget());
//		to.writeDouble(rule.p_LHS1e());
//		to.writeDouble(rule.p_LHS1f());
//		to.writeDouble(rule.p_e1LHS());
//		to.writeDouble(rule.p_e1f());
//		to.writeDouble(rule.p_e1f_LHS());
//		to.writeDouble(rule.p_f1LHS());
//		to.writeDouble(rule.p_f1e());
//		to.writeDouble(rule.p_f1e_LHS());
//		to.writeDouble(rule.AGigaSim());
//		to.writeDouble(rule.GoogleNgramSim());
//		to.writeString(rule.alignment());
//	}
//}