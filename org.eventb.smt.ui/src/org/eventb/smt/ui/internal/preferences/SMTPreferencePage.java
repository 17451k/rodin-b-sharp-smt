/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.ui.internal.preferences;

import static org.eclipse.jface.resource.JFaceResources.getString;
import static org.eventb.smt.core.preferences.PreferenceManager.TRANSLATION_PATH_ID;
import static org.eventb.smt.core.preferences.PreferenceManager.VERIT_PATH_ID;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eventb.smt.core.SMTCore;

/**
 * @author yguyot
 */
public class SMTPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private static final String SMT_TRANSLATION_SETTINGS_LABEL = "SMT translation settings:";
	private static final String VERIT_PATH_LABEL = "veriT path";
	private static final String TRANSLATION_PATH_LABEL = "Temporary translation files path";

	@Override
	public void init(final IWorkbench workbench) {
		setDescription(SMT_TRANSLATION_SETTINGS_LABEL);
	}

	/*
	 * The preferences are actually stored in the SMT core plug-in.
	 */
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return new ScopedPreferenceStore(InstanceScope.INSTANCE,
				SMTCore.PLUGIN_ID);
	}

	/*
	 * It is on purpose that getFieldEditorParent() is called for each field, it
	 * is mandated by the FieldEditor API. Do not share this code.
	 */
	@Override
	protected void createFieldEditors() {
		final StringFieldEditor translationDirBrowser = new TranslationDirectoryEditor(
				TRANSLATION_PATH_ID, TRANSLATION_PATH_LABEL,
				getFieldEditorParent());
		translationDirBrowser.setEmptyStringAllowed(false);
		addField(translationDirBrowser);

		final FieldEditor veriTBinaryBrowser = new FileFieldEditor(
				VERIT_PATH_ID, VERIT_PATH_LABEL, true, getFieldEditorParent());
		addField(veriTBinaryBrowser);
	}

	/*
	 * We want a directory field editor that validates the user input on every
	 * keystroke, because, otherwise, the "Apply" and "OK" button are not
	 * disabled when the field is edited directly by the user and is incorrect.
	 *
	 * However, the DirectoryFieldEditor does not allow this, unless we call the
	 * default constructor and provide all information afterwards. We therefore
	 * sub-class DirectoryFieldEditor, copy its constructor, just changing one
	 * line to set the validation strategy we want. This is a pity.
	 */
	private static class TranslationDirectoryEditor extends
			DirectoryFieldEditor {
		public TranslationDirectoryEditor(String name, String label,
				Composite parent) {
			init(name, label);
			setErrorMessage(getString("DirectoryFieldEditor.errorMessage"));
			setChangeButtonText(getString("openBrowse"));
			setValidateStrategy(VALIDATE_ON_KEY_STROKE);
			createControl(parent);
		}
	}

}