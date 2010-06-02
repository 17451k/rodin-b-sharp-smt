package fr.systerel.smt.provers.internal.core;

import org.eventb.core.seqprover.xprover.XProverInput;

public class SmtInput extends XProverInput {


	// Forces to use in the mono-lemma prover
	final String forces;
	
	protected SmtInput(String forces, long timeOutDelay, boolean restricted) {
		super(restricted, timeOutDelay);
		if (forces.length() == 0){
			this.forces = null;
			return;
		}
		this.forces = forces;
	}
	
	protected SmtInput(String forces, long timeOutDelay) {
		this(forces, timeOutDelay, false);
	}
	
	public SmtInput(int forces, long timeOutDelay) {
		this(forcesToString(forces), timeOutDelay, false);
	}
	
	public SmtInput(int forces, long timeOutDelay, boolean restricted) {
		this(forcesToString(forces), timeOutDelay, restricted);
	}

	public static String forcesToString(int forces) {
		return null;

	}

	private static void addForce(StringBuilder builder, int force, char image, int forces) {
		if ((forces & force) != 0) {
			if (builder.length() != 0) builder.append(';');
			builder.append(image);
		}			
	}
	
	@Override
	public boolean hasError() {
		return forces == null || super.hasError();
	}

	@Override
	public String getError() {
		if (forces == null)
			return "Invalid forces";
		return super.getError();
	}
	
}
