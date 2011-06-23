package fr.systerel.smt.provers.core.tests;

import br.ufrn.smt.solver.translation.SMTSolver;
import fr.systerel.smt.provers.core.tests.utils.LemmaData;

public class XMLtoSMTTestsAltErgo extends XMLtoSMTTests {

	public XMLtoSMTTestsAltErgo(final LemmaData data) {
		super(data, SMTSolver.ALT_ERGO);
	}

}
