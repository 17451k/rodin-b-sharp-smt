/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.internal.preferences.ui;

import static org.eventb.smt.internal.preferences.SMTPreferences.DEFAULT_SOLVER_INDEX;
import static org.eventb.smt.internal.preferences.SMTPreferences.DEFAULT_SOLVER_PREFERENCES;
import static org.eventb.smt.internal.preferences.SMTPreferences.DEFAULT_TRANSLATION_PATH;
import static org.eventb.smt.internal.preferences.SMTPreferences.DEFAULT_VERIT_PATH;
import static org.eventb.smt.internal.preferences.SMTPreferences.SOLVER_INDEX_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.SOLVER_PREFERENCES_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.TRANSLATION_PATH_ID;
import static org.eventb.smt.internal.preferences.SMTPreferences.VERIT_PATH_ID;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eventb.smt.internal.provers.ui.SmtProversUIPlugin;


/**
 * Class used to initialize default preference values.
 */
public class SMTPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = SmtProversUIPlugin.getDefault()
				.getPreferenceStore();
		store.setDefault(SOLVER_PREFERENCES_ID, DEFAULT_SOLVER_PREFERENCES);
		store.setDefault(SOLVER_INDEX_ID, DEFAULT_SOLVER_INDEX);
		store.setDefault(VERIT_PATH_ID, DEFAULT_VERIT_PATH);
		store.setDefault(TRANSLATION_PATH_ID, DEFAULT_TRANSLATION_PATH);
	}
}