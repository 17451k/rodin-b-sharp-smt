/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 *******************************************************************************/

package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;
import static fr.systerel.smt.provers.ast.SMTSymbol.BENCHMARK;

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
	 * Adds the opening format of a benchmark command to the given string
	 * builder.
	 */
	private void benchmarkCmdOpening(final StringBuilder sb) {
		sb.append(OPAR);
		sb.append(BENCHMARK);
		sb.append(SPACE);
		sb.append(name);
		sb.append("\n");
	}

	private void assumptionsSection(final StringBuilder sb) {
		for (final SMTFormula assumption : assumptions) {
			sb.append(" :assumption ");
			assumption.toString(sb);
			sb.append("\n");
		}
	}

	private void formulaSection(StringBuilder sb) {
		sb.append(" :formula (not ");
		goal.toString(sb);
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
		return name;
	}

	/**
	 * Prints the benchmark into the given print writer.
	 */
	public void print(final PrintWriter pw) {
		final StringBuilder sb = new StringBuilder();
		benchmarkCmdOpening(sb);
		signature.toString(sb);
		sb.append("\n");
		assumptionsSection(sb);
		formulaSection(sb);
		sb.append(CPAR);
		pw.println(sb.toString());
	}
}
