/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.translation;

import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.smt.core.internal.ast.SMTBenchmark;
import org.eventb.smt.core.internal.translation.TranslationResult;

/**
 * Encapsulates the SMT-LIB benchmark produced during the translation of an
 * Event-B sequent.
 * 
 * @author Yoann Guyot
 */
public class BenchmarkResult extends TranslationResult {
	/**
	 * The produced SMT-LIB benchmark
	 */
	private final SMTBenchmark benchmark;

	public BenchmarkResult(final SMTBenchmark benchmark) {
		this.benchmark = benchmark;
	}

	@Override
	public boolean isTrivial() {
		return false;
	}

	@Override
	public SMTBenchmark getSMTBenchmark() {
		return benchmark;
	}

	@Override
	public ITrackedPredicate getTrivialPredicate() {
		return null;
	}
}
