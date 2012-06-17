/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.prefs;

import static org.eventb.smt.core.internal.provers.SMTProversCore.logError;

import org.eventb.smt.core.internal.preferences.solvers.BundledSolverList;
import org.eventb.smt.core.prefs.ISolverDescriptor;
import org.osgi.service.prefs.Preferences;

/**
 * Implements solver preferences by providing a memory cache from the preference
 * store associated to this plug-in. The solver descriptions are stored in a
 * preference tree rooted at the <code>solver</code> node.
 *
 * This class is also responsible for checking that no two solvers bear the same
 * name.
 *
 * @author Laurent Voisin
 */
public class SolverPreferences extends AbstractPreferences<ISolverDescriptor> {

	private static final SolverPreferences INSTANCE = new SolverPreferences();

	private class SolverList extends DescriptorList<ISolverDescriptor> {

		public SolverList() {
			super();
		}

		@Override
		public boolean isValid(ISolverDescriptor desc) {
			final String name = desc.getName();
			if (doGet(name) != null) {
				logError("Duplicate solver name " + name + " ignored", null);
				return false;
			}
			return true;
		}

		@Override
		public ISolverDescriptor[] newArray(int length) {
			return new ISolverDescriptor[length];
		}

	}

	// Singleton class.
	private SolverPreferences() {
		super("solver");
	}

	@Override
	protected DescriptorList<ISolverDescriptor> loadBundledDescriptors() {
		return new BundledSolverList();
	}

	@Override
	protected DescriptorList<ISolverDescriptor> newDescriptorList() {
		return new SolverList();
	}

	@Override
	protected SolverDescriptor loadFromNode(Preferences node) {
		return new SolverDescriptor(node);
	}

	public static ISolverDescriptor[] getBundledSolvers() {
		return INSTANCE.doGetBundled();
	}

	public static ISolverDescriptor[] getUserSolvers() {
		return INSTANCE.doGetUser();
	}

	public static void setUserSolvers(ISolverDescriptor[] newSolvers) {
		INSTANCE.setUser(newSolvers);
	}

}
