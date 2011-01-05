package fr.systerel.smt.provers.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;


public class SMTSignature {
	private final SMTLogic logic;

	private final HashSet<String> symbols = new HashSet<String>(); // TODO must
																	// implement
																	// SMT-LIB
																	// rules

	private final List<SMTSort> sorts = new ArrayList<SMTSort>();

	private final List<SMTPredicateSymbol> preds = new ArrayList<SMTPredicateSymbol>();

	private final List<SMTFunctionSymbol> funs = new ArrayList<SMTFunctionSymbol>();

	// TODO put this into a SMTSignature extending class that will be used by veriT
	// approach
	private final List<String> macros = new ArrayList<String>();

	public SMTSignature(final String logicName) {
		this.logic = new SMTLogic(logicName);
	}

	private static String sectionIndentation(final String sectionName) {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for (int i = 0; i < sectionName.length(); i++) {
			sb.append(" ");
		}
		sb.append("    ");
		return sb.toString();
	}

	private static <T> void extraSection(final StringBuilder sb,
			final List<T> elements, final String sectionName) {
		final String eltSep = sectionIndentation(sectionName);
		String separator = "";
		sb.append(" :");
		sb.append(sectionName);
		sb.append(" (");
		for (final T element : elements) {
			sb.append(separator);
			sb.append(element);
			separator = eltSep;
		}
		sb.append(")\n");
	}

	/**
	 * Gives a fresh identifier to a variable of which identifier contains the
	 * character '\''.
	 */
	public String giveFreshVar(final String name) {
		String freshVar = name;
		if (name.contains("\'")) {
			int discrNumber = name.length() - name.indexOf('\'');
			freshVar = name.replaceAll("'", "_" + discrNumber + "_");
			while (this.symbols.contains(freshVar)) {
				discrNumber = discrNumber + 1;
				freshVar = name.replaceAll("'", "_" + discrNumber + "_");
			}
		}
		return freshVar;
	}

	private void logicSection(final StringBuilder sb) {
		sb.append(" :logic ");
		sb.append(this.logic);
		sb.append("\n");
	}

	/**
	 * One sort per line. May add a comment beside.
	 */
	private void extrasortsSection(final StringBuilder sb) {
		if (!this.sorts.isEmpty()) {
			extraSection(sb, this.sorts, "extrasorts");
		}
	}

	private void extrapredsSection(final StringBuilder sb) {
		if (!preds.isEmpty()) {
			extraSection(sb, this.preds, "extrapreds");
		}
	}

	private void extrafunsSection(final StringBuilder sb) {
		if (!funs.isEmpty()) {
			extraSection(sb, this.funs, "extrafuns");
		}
	}

	// TODO put this into a SMTSignature extending class that will be used by veriT
	// approach
	private void extramacrosSection(final StringBuilder sb) {
		if (!macros.isEmpty()) {
			extraSection(sb, this.macros, "extramacros");
		}
	}

	public void addFunctionSymbol() {
		// TODO must verify the given argument, and give a fresh name if needed
	}

	public void addPredicateSymbol(final String name, final String type) {
		// TODO must verify the given argument, and give a fresh name if needed
	}

	public void addSort(final String sort) {
		// TODO must verify the given argument, and give a fresh name if needed
	}

	public void toString(StringBuilder sb) {
		this.logicSection(sb);
		this.extrasortsSection(sb);
		this.extrapredsSection(sb);
		this.extrafunsSection(sb);
		this.extramacrosSection(sb);
	}
}