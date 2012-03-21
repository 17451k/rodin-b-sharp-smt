/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.translation;

/**
 * Enumeration used to describe what approach is being used *
 */
public enum SMTTranslationApproach {
	USING_PP("PP"), USING_VERIT("veriT");

	/**
	 * The name of the approach
	 */
	private String name;

	/**
	 * The constructor of the enum
	 * 
	 * @param name
	 *            the name used to the approach
	 */
	private SMTTranslationApproach(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}