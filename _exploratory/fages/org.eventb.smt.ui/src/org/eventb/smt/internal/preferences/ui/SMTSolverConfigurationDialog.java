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

import static org.eclipse.jface.window.Window.CANCEL;
import static org.eclipse.jface.window.Window.OK;
import static org.eclipse.swt.SWT.APPLICATION_MODAL;
import static org.eclipse.swt.SWT.DIALOG_TRIM;
import static org.eclipse.swt.SWT.DROP_DOWN;
import static org.eclipse.swt.SWT.READ_ONLY;
import static org.eclipse.swt.SWT.RESIZE;
import static org.eventb.smt.internal.provers.internal.core.SMTSolver.getSolver;
import static org.eventb.smt.internal.translation.SMTLIBVersion.getVersion;

import java.io.File;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.smt.internal.preferences.SMTSolverConfiguration;
import org.eventb.smt.internal.provers.internal.core.SMTSolver;
import org.eventb.smt.internal.translation.SMTLIBVersion;

/**
 * @author guyot
 * 
 */
public class SMTSolverConfigurationDialog extends Dialog {
	private static final String CONFIG_ID_LABEL = "Config ID";
	private static final String SOLVER_LABEL = "Solver";
	private static final String SOLVER_PATH_LABEL = "Solver path";
	private static final String SOLVER_ARGS_LABEL = "Solver arguments";
	private static final String SMT_LIB_LABEL = "SMT-LIB";

	int returnCode = 0;

	final Set<String> usedIds;
	final SMTSolverConfiguration solverConfig;

	public SMTSolverConfigurationDialog(final Shell parentShell,
			final SMTSolverConfiguration solverConfig, final Set<String> usedIds) {
		super(parentShell, APPLICATION_MODAL | DIALOG_TRIM | RESIZE);
		if (solverConfig != null) {
			this.solverConfig = solverConfig;
			usedIds.remove(solverConfig.getId());
		} else {
			this.solverConfig = new SMTSolverConfiguration();
		}
		this.usedIds = usedIds;
		setText("Solver configuration");
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(4, true));
		GridData data;

		/**
		 * Solver ID
		 */
		final Label idLabel = new Label(shell, SWT.NONE);
		idLabel.setText(CONFIG_ID_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		idLabel.setLayoutData(data);

		final Text idText = new Text(shell, SWT.BORDER);
		idText.setText(solverConfig.getId());
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		idText.setLayoutData(data);

		/**
		 * Solver
		 */
		final Label solverLabel = new Label(shell, SWT.NONE);
		solverLabel.setText(SOLVER_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		solverLabel.setLayoutData(data);

		final Combo solverCombo = new Combo(shell, getStyle() | DROP_DOWN
				| READ_ONLY);
		for (final SMTSolver solver : SMTSolver.values()) {
			solverCombo.add(solver.toString());
		}
		solverCombo.setText(solverConfig.getSolver().toString());
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		solverCombo.setLayoutData(data);

		/**
		 * Solver path
		 */
		final Label solverPathLabel = new Label(shell, SWT.NONE);
		solverPathLabel.setText(SOLVER_PATH_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		solverPathLabel.setLayoutData(data);

		final Text solverPathText = new Text(shell, SWT.BORDER);
		solverPathText.setText(solverConfig.getPath());
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		solverPathText.setLayoutData(data);

		final Button browseButton = new Button(shell, SWT.PUSH);
		browseButton.setText("Browse");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final FileDialog dialog = new FileDialog(shell, getStyle());
				String path = dialog.open();
				if (validPath(path)) {
					solverPathText.setText(path);
				}
			}
		});
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		browseButton.setLayoutData(data);

		/**
		 * Solver arguments
		 */
		final Label argsLabel = new Label(shell, SWT.NONE);
		argsLabel.setText(SOLVER_ARGS_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		argsLabel.setLayoutData(data);

		final Text argsText = new Text(shell, SWT.BORDER);
		argsText.setText(solverConfig.getArgs());
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		argsText.setLayoutData(data);

		/**
		 * SMT-LIB version
		 */
		final Label smtlibLabel = new Label(shell, SWT.NONE);
		smtlibLabel.setText(SMT_LIB_LABEL);
		data = new GridData();
		data.horizontalSpan = 1;
		smtlibLabel.setLayoutData(data);

		final Combo smtlibCombo = new Combo(shell, getStyle() | DROP_DOWN
				| READ_ONLY);
		for (final SMTLIBVersion smtlibVersion : SMTLIBVersion.values()) {
			smtlibCombo.add(smtlibVersion.toString());
		}
		smtlibCombo.setText(solverConfig.getSmtlibVersion().toString());
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		smtlibCombo.setLayoutData(data);

		/**
		 * OK Button
		 */
		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("OK");
		data = new GridData(GridData.FILL_HORIZONTAL);
		okButton.setLayoutData(data);
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final String id = idText.getText();
				final String path = solverPathText.getText();
				if (validId(id)) {
					if (validPath(path)) {
						solverConfig.setId(id);
						solverConfig.setSolver(getSolver(solverCombo.getText()));
						solverConfig.setPath(path);
						solverConfig.setArgs(argsText.getText());
						solverConfig.setSmtlibVersion(getVersion(smtlibCombo
								.getText()));
						returnCode = OK;
						shell.close();
					}
				} else {
					UIUtils.showError("A solver ID and the solver path are required.\nThe solver ID must be unique.");
				}
			}
		});

		/**
		 * Cancel Button
		 */
		final Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText("Cancel");
		data = new GridData(GridData.FILL_HORIZONTAL);
		cancelButton.setLayoutData(data);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				returnCode = CANCEL;
				shell.close();
			}
		});

		shell.setDefaultButton(okButton);
	}

	boolean validId(final String id) {
		return (!id.isEmpty()) && (!usedIds.contains(id));
	}

	static boolean validPath(final String path) {
		if (path != null) {
			if (!path.isEmpty()) {
				final File file = new File(path);
				try {
					if (file.exists()) {
						if (file.isFile()) {
							if (file.canExecute()) {
								return true;
							} else {
								UIUtils.showError("Rodin cannot execute the indicated file.");
								return false;
							}
						} else {
							UIUtils.showError("The indicated file is not a valid file.");
							return false;
						}
					} else {
						UIUtils.showError("The indicated file does not exist.");
						return false;
					}
				} catch (SecurityException se) {
					UIUtils.showError("Rodin cannot read or execute the indicated file.");
					return false;
				}
			} else {
				UIUtils.showError("The solver path is required.");
				return false;
			}
		} else {
			return false;
		}
	}

	public SMTSolverConfiguration getSolverConfig() {
		return solverConfig;
	}

	public int open() {
		final Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.pack();
		shell.open();
		final Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return returnCode;
	}
}