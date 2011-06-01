/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Vitor - Implementation
 *******************************************************************************/
package fr.systerel.smt.provers.ast;

/**
 * This interface is meant to be implemented by SMTLogic or SMTTheory instances
 * which define arithmetic SMTFunctionSymbol which string representations are
 * the SMT-LIB symbols "÷", "expn".
 */
public interface ISMTArithmeticFunsExtended extends ISMTArithmeticFuns {
	

	/**
	 * returns the division function symbol
	 * 
	 * @return the division function symbol
	 */
	public SMTSymbol getDiv();

	/**
	 * returns the exponentiation function symbol
	 * 
	 * @return the exponentiation function symbol
	 */
	public SMTSymbol getExpn();

	public SMTSymbol getMod();
}
