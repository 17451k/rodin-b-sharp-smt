package fr.systerel.smt.provers.ast;

import static fr.systerel.smt.provers.ast.SMTFactory.CPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.OPAR;
import static fr.systerel.smt.provers.ast.SMTFactory.SPACE;

public class SMTPairEnumMacro extends SMTMacro {

	SMTPairEnumMacro(String macroName, SMTVarSymbol var1, SMTVarSymbol var2,
			SMTMacroTerm[] terms) {
		super(macroName);
		this.var1 = var1;
		this.var2 = var2;
		this.terms = terms;
	}

	private SMTVarSymbol var1;
	private SMTVarSymbol var2;
	private SMTMacroTerm[] terms;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(OPAR);
		sb.append(super.getMacroName());
		sb.append(" (lambda ");
		sb.append(var1);
		sb.append(SPACE);
		sb.append(var2);
		sb.append(" . ");
		if (terms.length == 1) {
			sb.append(elemToString(var1.getNameWithQMark(),
					var2.getNameWithQMark(), terms[0].getArgTerms()[0],
					terms[0].getArgTerms()[1]));
			sb.append(CPAR);
			sb.append(CPAR);
		} else {
			sb.append("(or");
			for (SMTMacroTerm term : terms) {
				sb.append(elemToString(var1.getNameWithQMark(),
						var2.getNameWithQMark(), term.getArgTerms()[0],
						term.getArgTerms()[1]));
			}
			sb.append(CPAR);
			sb.append(CPAR);
			sb.append(CPAR);
		}
		return sb.toString();
	}

	private String elemToString(String var1, String var2, SMTTerm term1,
			SMTTerm term2) {
		StringBuffer sb = new StringBuffer();
		sb.append("(= (pair ");
		sb.append(var1);
		sb.append(SPACE);
		sb.append(var2);
		sb.append(CPAR);
		sb.append("(pair ");
		sb.append(term1);
		sb.append(SPACE);
		sb.append(term2);
		sb.append(CPAR);
		sb.append(CPAR);
		return sb.toString();
	}

	@Override
	public void toString(StringBuffer builder) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean equals(Object obj) {
		// TODO
		return true;
	}

}
