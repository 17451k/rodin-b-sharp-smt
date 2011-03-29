package fr.systerel.smt.provers.ast;

import java.util.HashSet;
import java.util.Set;

public class SMTMacroSymbol extends SMTPredicateSymbol {

	// VeriT Extended SMT-LIB Symbols
	public static final String BUNION = "union";
	public static final String BINTER = "inter";
	public static final String EMPTY = "emptyset";
	public static final String INTER = "inter";
	public static final String SETMINUS = "setminus";
	public static final String IN = "in";
	public static final String SUBSETEQ = "subseteq";
	public static final String SUBSET = "subset";
	public static final String RANGE_INTEGER = "range";
	public static final String PROD = "prod";
	public static final String DOM = "dom";
	public static final String RAN = "ran";
	public static final String IMG = "img";
	public static final String DOMR = "domr";
	public static final String DOMS = "doms";
	public static final String RANR = "ranr";
	public static final String RANS = "rans";
	public static final String INV = "inv";
	public static final String COMP = "comp";
	public static final String OVR = "ovr";
	public static final String ID = "id";
	public static final String FCOMP = "comp";
	public static final String EMPTY_PAIR = "emptyset2";

	public static final String DIV = "/";

	public static final String ENUM = "enum";
	public static final String MOD = "mod";
	public static final String RANGE_SUBSTRACION = "rans";
	public static final String RANGE_RESTRICTION = "ranr";
	public static final String RELATION = "rel";
	public static final String TOTAL_RELATION = "totp";
	public static final String SURJECTIVE_RELATION = "surp";
	public static final String TOTAL_SURJECTIVE_RELATION = "totsurp";
	public static final String PARTIAL_FUNCTION = "pfun";
	public static final String TOTAL_FUNCTION = "tfun";
	public static final String MAPSTO = "pair";
	public static final String NAT = "Nat";
	public static final String NAT1 = "Nat1";
	public static final String PARTIAL_INJECTION = "pinj";
	public static final String TOTAL_INJECTION = "tinj";
	public static final String PARTIAL_SURJECTION = "psur";
	public static final String TOTAL_SURJECTION = "tsur";
	public static final String TOTAL_BIJECTION = "bij";
	public static final String CARTESIAN_PRODUCT = "prod";
	public static final String DOMAIN_RESTRICTION = "domr";
	public static final String DOMAIN_SUBSTRACTION = "doms";
	public static final String RELATIONAL_IMAGE = "img";
	public static final String LAMBDA = "lambda";
	public static final String CSET = "cset";
	public static final String ELEM = "elem";
	public static final String ISMIN = "ismin";
	public static final String ISMAX = "ismax";
	public static final String FINITE = "finite";
	public static final String CARD = "card";
	public static final String PAIR = "Pair";

	public static final String FUNP = "funp";
	public static final String INJP = "injp";

	private static String[] VERIT_SYMBOLS = { BUNION, BINTER, EMPTY, INTER,
			SETMINUS, IN, SUBSETEQ, SUBSET, RANGE_INTEGER, PROD, DOM, RAN, IMG,
			DOMR, DOMS, RANR, RANS, INV, COMP, OVR, ID, FCOMP, EMPTY_PAIR, DIV,
			ENUM, MOD, RANGE_SUBSTRACION, RANGE_RESTRICTION, RELATION,
			TOTAL_RELATION, SURJECTIVE_RELATION, TOTAL_SURJECTIVE_RELATION,
			PARTIAL_FUNCTION, TOTAL_FUNCTION, MAPSTO, NAT, NAT1,
			PARTIAL_INJECTION, TOTAL_INJECTION, PARTIAL_SURJECTION,
			TOTAL_SURJECTION, TOTAL_BIJECTION, CARTESIAN_PRODUCT,
			DOMAIN_RESTRICTION, DOMAIN_SUBSTRACTION, RELATIONAL_IMAGE, LAMBDA,
			CSET, ELEM, ISMIN, ISMAX, FINITE, CARD, PAIR, FUNP, INJP };

	public static Set<String> getVeritSymbols() {
		Set<String> set = new HashSet<String>();
		for (String symbol : VERIT_SYMBOLS) {
			set.add(symbol);
		}
		return set;
	}

	SMTMacroSymbol(String symbolName, SMTSortSymbol[] args, boolean predefined) {
		super(symbolName, args, predefined);
	}

	SMTMacroSymbol(String symbolName, SMTSortSymbol[] args) {
		super(symbolName, args, false);
	}
}
