/*******************************************************************************
 * Copyright (c) 2011, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.performance.xml;

import static org.eventb.smt.tests.ConfigProvider.BUNDLED_VERIT;
import static org.eventb.smt.utils.Theory.TheoryLevel.L1;

import java.util.Arrays;
import java.util.List;

import org.eventb.smt.core.performance.xml.utils.LemmaData;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class XMLtoSMTTestsL1VeritV2_0 extends XMLtoSMTTests {
	public XMLtoSMTTestsL1VeritV2_0(final LemmaData data) {
		super(data, BUNDLED_VERIT, GET_UNSAT_CORE);
	}

	@Parameters
	public static List<LemmaData[]> getDocumentDatas() {
		return getDocumentDatas(Arrays.asList(L1));
	}
}
