package fr.systerel.smt.provers.internal.core;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics.AbsractLazilyConstrTactic;

import fr.systerel.smt.provers.core.SmtProversCore;

/**
 * This class file contains static classes that extend the autoTactics extension point in the sequent prover
 * 
 * 
 * @author YFT
 *
 */
public class AutoTactics {

	
	/**
	 * This class is not meant to be instantiated
	 */
	private AutoTactics(){
	//	
	}
		
	public static class SMT extends AbsractLazilyConstrTactic{

		@Override
		protected ITactic getSingInstance() {
			return SmtProversCore.externalSMT(true);
		}
	}
	
		
}