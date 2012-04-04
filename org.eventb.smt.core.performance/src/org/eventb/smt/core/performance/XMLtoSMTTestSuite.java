/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.performance;

import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL1AltErgoV1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL1AltErgoV2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL1CVC3V1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL1CVC3V2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL1CVC4V1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL1CVC4V2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL1MathSat5V1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL1MathSat5V2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL1OpenSMTV1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL1OpenSMTV2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL1VeritV1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL1VeritV2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL1Z3V1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL1Z3V2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL2AltErgoV1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL2AltErgoV2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL2CVC3V1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL2CVC3V2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL2CVC4V1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL2CVC4V2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL2MathSat5V1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL2MathSat5V2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL2OpenSMTV1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL2OpenSMTV2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL2VeritV1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL2VeritV2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL2Z3V1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL2Z3V2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL3AltErgoV1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL3AltErgoV2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL3CVC3V1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL3CVC3V2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL3CVC4V1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL3CVC4V2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL3MathSat5V1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL3MathSat5V2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL3OpenSMTV1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL3OpenSMTV2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL3VeritV1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL3VeritV2_0;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL3Z3V1_2;
import org.eventb.smt.core.performance.xml.XMLtoSMTTestsL3Z3V2_0;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { // SMT 1.2 level 1
		XMLtoSMTTestsL1AltErgoV1_2.class, //
		XMLtoSMTTestsL1CVC3V1_2.class, //
		XMLtoSMTTestsL1CVC4V1_2.class, //
		XMLtoSMTTestsL1MathSat5V1_2.class, //
		XMLtoSMTTestsL1OpenSMTV1_2.class, //
		XMLtoSMTTestsL1VeritV1_2.class, //
		XMLtoSMTTestsL1Z3V1_2.class,

		// SMT 1.2 level 2
		XMLtoSMTTestsL2AltErgoV1_2.class, //
		XMLtoSMTTestsL2CVC3V1_2.class, //
		XMLtoSMTTestsL2CVC4V1_2.class, //
		XMLtoSMTTestsL2MathSat5V1_2.class, //
		XMLtoSMTTestsL2OpenSMTV1_2.class, //
		XMLtoSMTTestsL2VeritV1_2.class, //
		XMLtoSMTTestsL2Z3V1_2.class,

		// SMT 1.2 level 3
		XMLtoSMTTestsL3AltErgoV1_2.class, //
		XMLtoSMTTestsL3CVC3V1_2.class, //
		XMLtoSMTTestsL3CVC4V1_2.class, //
		XMLtoSMTTestsL3MathSat5V1_2.class, //
		XMLtoSMTTestsL3OpenSMTV1_2.class, //
		XMLtoSMTTestsL3VeritV1_2.class, //
		XMLtoSMTTestsL3Z3V1_2.class,

		// SMT 2.0 level 1
		XMLtoSMTTestsL1AltErgoV2_0.class, //
		XMLtoSMTTestsL1CVC3V2_0.class, //
		XMLtoSMTTestsL1CVC4V2_0.class, //
		XMLtoSMTTestsL1MathSat5V2_0.class, //
		XMLtoSMTTestsL1OpenSMTV2_0.class, //
		XMLtoSMTTestsL1VeritV2_0.class, //
		XMLtoSMTTestsL1Z3V2_0.class,

		// SMT 2.0 level 2
		XMLtoSMTTestsL2AltErgoV2_0.class, //
		XMLtoSMTTestsL2CVC3V2_0.class, //
		XMLtoSMTTestsL2CVC4V2_0.class, //
		XMLtoSMTTestsL2MathSat5V2_0.class, //
		XMLtoSMTTestsL2OpenSMTV2_0.class, //
		XMLtoSMTTestsL2VeritV2_0.class, //
		XMLtoSMTTestsL2Z3V2_0.class,

		// SMT 2.0 level 3
		XMLtoSMTTestsL3AltErgoV2_0.class, //
		XMLtoSMTTestsL3CVC3V2_0.class, //
		XMLtoSMTTestsL3CVC4V2_0.class, //
		XMLtoSMTTestsL3MathSat5V2_0.class, //
		XMLtoSMTTestsL3OpenSMTV2_0.class, //
		XMLtoSMTTestsL3VeritV2_0.class, //
		XMLtoSMTTestsL3Z3V2_0.class })
public class XMLtoSMTTestSuite {
	// Just for tests
}