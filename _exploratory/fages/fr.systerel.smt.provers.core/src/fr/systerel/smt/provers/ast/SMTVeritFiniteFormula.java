/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vitor Alcantara de Almeida - Implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;
import fr.systerel.smt.provers.ast.macros.SMTMacroSymbol;

//TODO: Comment this class
public class SMTVeritFiniteFormula extends SMTFormula {

	private final SMTMacroSymbol finitePred;
	private final SMTPredicateSymbol pArgument;
	private final SMTFunctionSymbol kArgument;
	private final SMTFunctionSymbol fArgument;
	private final SMTTerm[] terms;

	public SMTMacroSymbol getFinitePred() {
		return finitePred;
	}

	public SMTPredicateSymbol getpArgument() {
		return pArgument;
	}

	public SMTFunctionSymbol getkArgument() {
		return kArgument;
	}

	public SMTFunctionSymbol getfArgument() {
		return fArgument;
	}

	public SMTTerm[] getTerms() {
		return terms;
	}

	public SMTVeritFiniteFormula(final SMTMacroSymbol finitePredSymbol,
			final SMTPredicateSymbol pArgument,
			final SMTFunctionSymbol fArgument,
			final SMTFunctionSymbol kArgument, final SMTTerm[] terms) {
		finitePred = finitePredSymbol;
		this.terms = terms;
		this.pArgument = pArgument;
		this.kArgument = kArgument;
		this.fArgument = fArgument;
	}

	@Override
	public void toString(final StringBuilder builder, final boolean printPoint) {
		builder.append(OPAR);
		builder.append(finitePred.name);
		builder.append(SPACE);
		builder.append(pArgument.name);
		for (final SMTTerm term : terms) {
			builder.append(SPACE);
			term.toString(builder);
		}
		builder.append(SPACE);
		builder.append(fArgument.name);
		builder.append(SPACE);
		builder.append(kArgument.name);
		builder.append(CPAR);

	}

}
