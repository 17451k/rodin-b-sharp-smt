/*******************************************************************************
 * Copyright (c) 2012, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences.configurations;

import static org.eventb.smt.core.SMTCore.newConfigDescriptor;

import org.eventb.smt.core.IConfigDescriptor;
import org.eventb.smt.ui.internal.preferences.AbstractElement;

/**
 * Model element describing a configuration element in the SMT configurations
 * table.
 *
 * @see ConfigModel
 * @author Laurent Voisin
 */
public class ConfigElement extends AbstractElement<IConfigDescriptor> {

	String solverName;
	String args;
	boolean enabled;

	public ConfigElement() {
		super(true, "");
		this.solverName = "";
		this.args = "";
		this.enabled = true;
	}

	public ConfigElement(IConfigDescriptor origin) {
		super(!origin.isBundled(), origin.getName());
		this.solverName = origin.getSolverName();
		this.args = origin.getArgs();
		this.enabled = origin.isEnabled();
	}

	// Editable copy constructor
	private  ConfigElement(ConfigElement configElement) {
		super(true, configElement.name);
		this.solverName = configElement.solverName;
		this.args = configElement.args;
		this.enabled = configElement.enabled;
	}
	
	@Override
	public IConfigDescriptor toCore() {
		return newConfigDescriptor(name, solverName, args, enabled);
	}

	@Override
	public AbstractElement<IConfigDescriptor> duplicate() {
		return new ConfigElement(this);
	}

}
