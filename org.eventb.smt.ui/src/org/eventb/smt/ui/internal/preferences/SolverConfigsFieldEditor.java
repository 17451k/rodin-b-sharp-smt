/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.ui.internal.preferences;

import static org.eclipse.swt.SWT.FULL_SELECTION;
import static org.eventb.smt.core.preferences.PreferenceManager.FORCE_RELOAD;
import static org.eventb.smt.core.preferences.PreferenceManager.FORCE_REPLACE;
import static org.eventb.smt.core.preferences.PreferenceManager.getPreferenceManager;
import static org.eventb.smt.core.preferences.SolverConfigFactory.ARGS_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.EDITABLE_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.ENABLED_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.ID_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.NAME_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.SMTLIB_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.SOLVER_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.TIME_OUT_COL;
import static org.eventb.smt.core.preferences.SolverConfigFactory.newConfig;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eventb.smt.core.preferences.ISolverConfig;
import org.eventb.smt.core.preferences.ISolverConfigsPreferences;

/**
 * This class is used to build the solver configurations table printed in the
 * preferences page. This table contains all the information set by the user
 * when he added a new SMT solver configuration. This class also defines four
 * buttons which interact with the table:
 * <ul>
 * <li>The 'Add' button to add a new SMT solver configuration into the table.</li>
 * <li>The 'Edit' button to modify a previously added SMT solver configuration.</li>
 * <li>The 'Remove' button to remove an existing SMT solver configuration.</li>
 * </ul>
 * The table is represented by a <code>Table</code>, contained in a
 * <code>TableViewer</code>. The data are contained in a
 * <code>SMTPreferences</code> instance, of which the
 * <code>SolverConfiguration</code> list is given as input to the
 * <code>TableViewer</code>. As a consequence, it is necessary to update the
 * <code>tableViewer</code> each time the list <code>solverConfigs</code> is
 * modified, by calling the <code>refresh</code> method.
 * 
 * @author guyot
 */
class SolverConfigsFieldEditor extends
		AbstractTableFieldEditor<ISolverConfigsPreferences> {
	/**
	 * Labels
	 */
	private static final String DUPLICATE_LABEL = "Duplicate...";
	private static final String ID_LABEL = "ID";
	private static final String EXECUTION_LABEL = "Execution";
	private static final String NAME_LABEL = "Name";
	private static final String SOLVER_LABEL = "Solver";
	private static final String ARGS_LABEL = "Arguments";
	private static final String SMTLIB_LABEL = "SMT-LIB";
	private static final String TIMEOUT_LABEL = "Time Out";
	private static final String EDITABLE_LABEL = "Editable";

	private static final int ID_COL_BOUND = 0;
	private static final int EXECUTION_COL_BOUND = 80;
	private static final int NAME_COL_BOUND = 100;
	private static final int SOLVER_COL_BOUND = 100;
	private static final int ARGS_COL_BOUND = 200;
	private static final int SMTLIB_COL_BOUND = 70;
	private static final int TIMEOUT_COL_BOUND = 70;
	private static final int EDITABLE_COL_BOUND = 0;

	/**
	 * The button for duplicating an object of the table.
	 */
	protected Button duplicateButton;

	/**
	 * Column labels and bounds
	 */
	private static final String[] COLUMNS_LABELS;
	static {
		ArrayList<String> columnsLabels = new ArrayList<String>();
		columnsLabels.add(ID_COL, ID_LABEL);
		columnsLabels.add(ENABLED_COL, EXECUTION_LABEL);
		columnsLabels.add(NAME_COL, NAME_LABEL);
		columnsLabels.add(SOLVER_COL, SOLVER_LABEL);
		columnsLabels.add(ARGS_COL, ARGS_LABEL);
		columnsLabels.add(SMTLIB_COL, SMTLIB_LABEL);
		columnsLabels.add(TIME_OUT_COL, TIMEOUT_LABEL);
		columnsLabels.add(EDITABLE_COL, EDITABLE_LABEL);
		COLUMNS_LABELS = new String[columnsLabels.size()];
		columnsLabels.toArray(COLUMNS_LABELS);
	}
	private static final Integer[] COLUMNS_BOUNDS;
	static {
		ArrayList<Integer> columnsBounds = new ArrayList<Integer>();
		columnsBounds.add(ID_COL, ID_COL_BOUND);
		columnsBounds.add(ENABLED_COL, EXECUTION_COL_BOUND);
		columnsBounds.add(NAME_COL, NAME_COL_BOUND);
		columnsBounds.add(SOLVER_COL, SOLVER_COL_BOUND);
		columnsBounds.add(ARGS_COL, ARGS_COL_BOUND);
		columnsBounds.add(SMTLIB_COL, SMTLIB_COL_BOUND);
		columnsBounds.add(TIME_OUT_COL, TIMEOUT_COL_BOUND);
		columnsBounds.add(EDITABLE_COL, EDITABLE_COL_BOUND);
		COLUMNS_BOUNDS = new Integer[columnsBounds.size()];
		columnsBounds.toArray(COLUMNS_BOUNDS);
	}

	static final int ENABLED_INDEX = 0;
	static final int DISABLED_INDEX = 1;
	static final String ENABLED = "enabled";
	static final String DISABLED = "disabled";
	static final String[] ENABLED_COMBO_VALUES = new String[2];
	static {
		ENABLED_COMBO_VALUES[ENABLED_INDEX] = ENABLED;
		ENABLED_COMBO_VALUES[DISABLED_INDEX] = DISABLED;
	}

	/**
	 * Creates a new solver configurations field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public SolverConfigsFieldEditor(final String name, final String labelText,
			final Composite parent) {
		super(name, labelText, parent, getPreferenceManager()
				.getSolverConfigsPrefs());
	}

	@Override
	protected String[] getColumnsLabel() {
		return COLUMNS_LABELS;
	}

	@Override
	protected Integer[] getColumnsBounds() {
		return COLUMNS_BOUNDS;
	}

	@Override
	protected void createTableViewer(Composite parent) {
		tableViewer = new TableViewer(parent, FULL_SELECTION);

		createColumns(tableViewer);
		tableViewer.setColumnProperties(getColumnsLabel());
		tableViewer.setContentProvider(new ContentProvider<ISolverConfig>());
		tableViewer.setLabelProvider(new SolverConfigsLabelProvider());

		CellEditor[] editors = new CellEditor[getColumnsLabel().length];

		editors[ENABLED_COL] = new ComboBoxCellEditor(tableViewer.getTable(),
				ENABLED_COMBO_VALUES, SWT.READ_ONLY);

		tableViewer.setCellEditors(editors);

		tableViewer.setCellModifier(new ICellModifier() {

			@Override
			public void modify(Object element, String property, Object value) {
				if (element instanceof Item)
					element = ((Item) element).getData();

				final ISolverConfig config = (ISolverConfig) element;
				if (property.equals(EXECUTION_LABEL)) {
					final int comboIndex = (Integer) value;
					final boolean enable = ENABLED_COMBO_VALUES[comboIndex]
							.equals(ENABLED);
					smtPrefs.getSolverConfig(config.getID()).setEnabled(enable);
					tableViewer.refresh();
				}
			}

			@Override
			public Object getValue(Object element, String property) {
				if (property.equals(EXECUTION_LABEL)) {
					final ISolverConfig config = (ISolverConfig) element;
					return config.isEnabled() ? ENABLED_INDEX : DISABLED_INDEX;
				}

				return null;
			}

			@Override
			public boolean canModify(Object element, String property) {
				return property.equals(EXECUTION_LABEL);
			}
		});
	}

	/**
	 * Remove the currently selected configuration from the list of solvers
	 * configurations, refresh the table viewer, updates the index of the
	 * selected configuration and refresh the button states.
	 * 
	 * @param solversTable
	 *            the solvers table
	 */
	@Override
	void removeCurrentSelection(final Table solversTable) {
		final String configToRemove = solversTable.getSelection()[0].getText();
		smtPrefs.removeSolverConfig(configToRemove);
		tableViewer.refresh();
		selectionChanged();
	}

	/**
	 * Sets the buttons statuses depending on the selection in the table.
	 */
	@Override
	void selectionChanged() {
		final Table configsTable = tableViewer.getTable();
		final TableItem[] selection = configsTable.getSelection();
		if (selection.length == 0) {
			removeButton.setEnabled(false);
			editButton.setEnabled(false);
			duplicateButton.setEnabled(false);
		} else {
			final TableItem firstItem = configsTable.getSelection()[0];
			final String selectedConfigID = firstItem.getText();
			final Map<String, ISolverConfig> solverConfigs = smtPrefs
					.getSolverConfigs();
			final boolean validSelection = solverConfigs
					.containsKey(selectedConfigID);
			final boolean validEditableSelection = validSelection ? solverConfigs
					.get(selectedConfigID).isEditable() : false;
			removeButton.setEnabled(validEditableSelection);
			editButton.setEnabled(validEditableSelection);
			duplicateButton.setEnabled(validSelection);
		}
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns);

		final Table configsTable = tableViewer.getTable();

		/**
		 * 'Add...' button
		 */
		addButton = new Button(buttonsGroup, SWT.PUSH);
		addButton.setText(ADD_LABEL);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				/**
				 * When pushed, opens the solver configuration shell
				 */
				final ISolverConfig newConfig = newConfig();

				if (newConfig == null) {
					return;
				}

				final SolverConfigDialog solverConfigDialog = new SolverConfigDialog(
						buttonsGroup.getShell(), newConfig);
				if (solverConfigDialog.open() == Window.OK) {
					/**
					 * Creates a new <code>SolverConfiguration</code> object,
					 * and adds it to the list.
					 */
					smtPrefs.add(solverConfigDialog.getSolverConfig());

					/**
					 * Refreshes the table viewer.
					 */
					tableViewer.refresh();
					selectionChanged();
				}
			}
		});
		final GridData addButtonData = new GridData(GridData.FILL_HORIZONTAL);
		addButtonData.widthHint = convertHorizontalDLUsToPixels(addButton,
				IDialogConstants.BUTTON_WIDTH);
		addButton.setLayoutData(addButtonData);

		/**
		 * 'Remove' button
		 */
		removeButton = new Button(buttonsGroup, SWT.PUSH);
		removeButton.setEnabled(false);
		removeButton.setText(REMOVE_LABEL);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				/**
				 * When pushed, remove the current selection
				 */
				removeCurrentSelection(configsTable);
			}
		});
		final GridData removeButtonData = new GridData(GridData.FILL_HORIZONTAL);
		removeButtonData.widthHint = convertHorizontalDLUsToPixels(
				removeButton, IDialogConstants.BUTTON_WIDTH);
		removeButton.setLayoutData(removeButtonData);

		/**
		 * 'Edit' button
		 */
		editButton = new Button(buttonsGroup, SWT.PUSH);
		editButton.setEnabled(false);
		editButton.setText(EDIT_LABEL);
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				/**
				 * When pushed, opens the configuration shell of the solver
				 * currently selected in the table.
				 */
				final String selectionID = configsTable.getSelection()[0]
						.getText();
				if (smtPrefs.getSolverConfigs().containsKey(selectionID)) {
					final ISolverConfig configToEdit = smtPrefs
							.getSolverConfigs().get(selectionID);
					if (configToEdit != null) {
						final SolverConfigDialog solverConfigDialog = new SolverConfigDialog(
								buttonsGroup.getShell(), configToEdit);
						if (solverConfigDialog.open() == Window.OK) {
							smtPrefs.add(solverConfigDialog.getSolverConfig(),
									FORCE_REPLACE);
							/**
							 * Refreshes the table viewer.
							 */
							tableViewer.refresh();
							selectionChanged();
						}
					}
				}
			}
		});
		final GridData editButtonData = new GridData(GridData.FILL_HORIZONTAL);
		editButtonData.widthHint = convertHorizontalDLUsToPixels(editButton,
				IDialogConstants.BUTTON_WIDTH);
		editButton.setLayoutData(editButtonData);

		/**
		 * 'Duplicate...' button
		 */
		duplicateButton = new Button(buttonsGroup, SWT.PUSH);
		duplicateButton.setEnabled(false);
		duplicateButton.setText(DUPLICATE_LABEL);
		duplicateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				/**
				 * When pushed, opens the solver configuration shell
				 */
				final String selectionID = configsTable.getSelection()[0]
						.getText();
				if (smtPrefs.getSolverConfigs().containsKey(selectionID)) {
					final ISolverConfig configToDuplicate = smtPrefs
							.getSolverConfigs().get(selectionID);
					if (configToDuplicate != null) {
						final ISolverConfig duplicatedConfig = newConfig(configToDuplicate);
						if (duplicatedConfig == null) {
							return;
						}

						final SolverConfigDialog solverConfigDialog = new SolverConfigDialog(
								buttonsGroup.getShell(), duplicatedConfig);
						if (solverConfigDialog.open() == Window.OK) {
							smtPrefs.add(solverConfigDialog.getSolverConfig());
							/**
							 * Refreshes the table viewer.
							 */
							tableViewer.refresh();
							selectionChanged();
						}
					}
				}
			}
		});
		final GridData duplicateButtonData = new GridData(
				GridData.FILL_HORIZONTAL);
		duplicateButtonData.widthHint = convertHorizontalDLUsToPixels(
				duplicateButton, IDialogConstants.BUTTON_WIDTH);
		duplicateButton.setLayoutData(duplicateButtonData);

		/**
		 * Packs everything.
		 */
		configsTable.pack();
		parent.pack();
	}

	@Override
	protected void doLoad() {
		smtPrefs = getPreferenceManager().getSolverConfigsPrefs(FORCE_RELOAD);
		tableViewer.setInput(smtPrefs.getSolverConfigs());
		tableViewer.refresh();
	}

	@Override
	protected void doLoadDefault() {
		smtPrefs.loadDefault();
		tableViewer.setInput(smtPrefs.getSolverConfigs());
		tableViewer.refresh();
		selectionChanged();
	}
}