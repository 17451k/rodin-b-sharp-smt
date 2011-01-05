/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Creation
 *******************************************************************************/

package fr.systerel.smt.provers.ast;

import java.io.PrintWriter;
import java.util.List;

/**
 * This class builds an SMT-LIB SMTBenchmark
 * 
 * @author guyot
 * 
 */
public class SMTBenchmark {
	private final String name;
	private final SMTSignature signature;
	private final List<SMTFormula> assumptions;
	private final SMTFormula goal;

	/**
	 * Adds the closing format of a benchmark command to the given string
	 * builder.
	 */
	private static void benchmarkCmdClosing(final StringBuilder sb) {
		sb.append(")");
	}

	/**
	 * Adds the opening format of a benchmark command to the given string
	 * builder.
	 */
	private void benchmarkCmdOpening(final StringBuilder sb) {
		sb.append("(benchmark ");
		sb.append(name);
		sb.append("\n");
	}

	private void assumptionsSection(final StringBuilder sb) {
		for (final SMTFormula assumption : this.assumptions) {
			sb.append(" :assumption ");
			assumption.toString(sb);
			sb.append("\n");
		}
	}

	private void formulaSection(StringBuilder sb) {
		sb.append(" :formula (not ");
		this.goal.toString(sb);
		sb.append(")\n");
	}

	public SMTBenchmark(final String lemmaName, final SMTSignature signature,
			final List<SMTFormula> assumptions, final SMTFormula goal) {
		this.name = lemmaName;
		this.signature = signature;
		this.assumptions = assumptions;
		this.goal = goal;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Prints the benchmark into the given print writer.
	 */
	public void print(final PrintWriter pw) {
		final StringBuilder sb = new StringBuilder();
		this.benchmarkCmdOpening(sb);
		this.signature.toString(sb);
		sb.append("\n");
		this.assumptionsSection(sb);
		this.formulaSection(sb);
		benchmarkCmdClosing(sb);
		pw.println(sb.toString());
	}
}