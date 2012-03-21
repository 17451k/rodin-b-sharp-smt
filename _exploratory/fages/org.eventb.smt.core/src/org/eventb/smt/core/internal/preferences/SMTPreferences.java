/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.preferences;

import static org.eventb.smt.core.preferences.AbstractSolverConfiguration.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eventb.smt.core.SMTCore;
import org.eventb.smt.core.preferences.AbstractPreferences;
import org.eventb.smt.core.preferences.AbstractSolverConfiguration;

/**
 * The SMT preferences class
 */
public class SMTPreferences extends AbstractPreferences {
	public static final boolean USE_DEFAULT_SCOPE = true;

	public static final String SEPARATOR = ";"; //$NON-NLS-1$
	public static final String DEFAULT_SOLVER_PREFERENCES = ""; //$NON-NLS-1$
	public static final String DEFAULT_VERIT_PATH = ""; //$NON-NLS-1$

	public static final IEclipsePreferences SMT_PREFS = ConfigurationScope.INSTANCE
			.getNode(SMTCore.PLUGIN_ID);
	public static final IEclipsePreferences DEFAULT_SMT_PREFS = DefaultScope.INSTANCE
			.getNode(SMTCore.PLUGIN_ID);

	private final IEclipsePreferences prefsNode;
	private List<AbstractSolverConfiguration> solverConfigs;
	private int selectedConfigIndex;
	private String translationPath;
	private String veriTPath;

	private List<AbstractSolverConfiguration> defaultSolverConfigs;
	private String defaultVeriTPath;

	public SMTPreferences(boolean useDefaultScope) {
		if (useDefaultScope) {
			prefsNode = DEFAULT_SMT_PREFS;
		} else {
			prefsNode = SMT_PREFS;
		}
		defaultSolverConfigs = new ArrayList<AbstractSolverConfiguration>(0);
		defaultVeriTPath = DEFAULT_VERIT_PATH;
	}

	public SMTPreferences(final boolean useDefaultScope,
			final List<AbstractSolverConfiguration> solverConfigs,
			final int selectedConfigIndex, final String translationPath,
			final String veriTPath) {
		this(useDefaultScope);
		this.solverConfigs = solverConfigs;
		this.selectedConfigIndex = selectedConfigIndex;
		this.translationPath = translationPath;
		this.veriTPath = veriTPath;
	}

	/**
	 * Creates a list with all solverConfig detail elements from the preferences
	 * String
	 * 
	 * @param preferences
	 *            The String that contains the details of the solverConfig
	 * @return The list of solvers and its details parsed from the preferences
	 *         String
	 */
	private static List<AbstractSolverConfiguration> parsePrefs(
			final String preferences) throws PatternSyntaxException {
		final List<AbstractSolverConfiguration> solverConfigs = new ArrayList<AbstractSolverConfiguration>();

		final String[] rows = preferences.split(SEPARATOR);
		for (final String row : rows) {
			if (row.length() > 0) {
				final AbstractSolverConfiguration solverConfig = parse(row);
				final String path = solverConfig.getPath();
				if (path == null) {
					continue;
				}
				/**
				 * Checks if the configuration was added automatically by the
				 * plug-in, then if its path is not correct, it is not added to
				 * the list.
				 */
				if (!isABundlePath(path) || isPathValid(path)) {
					solverConfigs.add(solverConfig);
				}
			}
		}
		return solverConfigs;
	}

	public static final String toString(
			final List<AbstractSolverConfiguration> solverConfigs) {
		final StringBuilder sb = new StringBuilder();

		String separator = "";
		for (final AbstractSolverConfiguration solverConfig : solverConfigs) {
			sb.append(separator);
			solverConfig.toString(sb);
			separator = SEPARATOR;
		}

		return sb.toString();
	}

	public void load() {
		solverConfigs = parsePrefs(prefsNode.get(SOLVER_PREFERENCES_ID,
				DEFAULT_SOLVER_PREFERENCES));
		selectedConfigIndex = prefsNode.getInt(CONFIG_INDEX_ID,
				DEFAULT_CONFIG_INDEX);
		translationPath = prefsNode.get(TRANSLATION_PATH_ID,
				DEFAULT_TRANSLATION_PATH);
		veriTPath = prefsNode.get(VERIT_PATH_ID, DEFAULT_VERIT_PATH);
	}

	@Override
	public void save() {
		prefsNode.put(SOLVER_PREFERENCES_ID, toString(solverConfigs));
		prefsNode.putInt(CONFIG_INDEX_ID, selectedConfigIndex);
		prefsNode.put(TRANSLATION_PATH_ID, translationPath);
		prefsNode.put(VERIT_PATH_ID, veriTPath);
	}

	@Override
	public List<AbstractSolverConfiguration> getSolverConfigs() {
		return solverConfigs;
	}

	@Override
	public AbstractSolverConfiguration getSelectedSolverConfiguration() {
		try {
			return solverConfigs.get(selectedConfigIndex);
		} catch (final IndexOutOfBoundsException ioobe) {
			if (solverConfigs.size() > 0) {
				throw NoSMTSolverSelectedException;
			} else {
				throw NoSMTSolverSetException;
			}
		}
	}

	@Override
	public AbstractSolverConfiguration getSolverConfiguration(
			final String configId) {
		for (final AbstractSolverConfiguration solverConfig : solverConfigs) {
			if (solverConfig.getID().equals(configId)) {
				return solverConfig;
			}
		}
		return null;
	}

	@Override
	public int getSelectedConfigIndex() {
		return selectedConfigIndex;
	}

	@Override
	public String getTranslationPath() {
		return translationPath;
	}

	@Override
	public String getVeriTPath() {
		return veriTPath;
	}

	private static final String getValidPath(final String currentPath,
			final String newPath, final String defaultPath) {
		if (isPathValid(newPath)) {
			return newPath;
		} else if (!isPathValid(currentPath)) {
			return defaultPath;
		} else {
			return currentPath;
		}
	}

	public static boolean isPathValid(final String path) {
		return isPathValid(path, new StringBuilder(0));
	}

	@Override
	public boolean validId(final String id) {
		final Set<String> usedIds = AbstractSolverConfiguration
				.getIDs(solverConfigs);
		return !id.isEmpty() && !usedIds.contains(id);
	}

	private static void addSolverConfig(
			List<AbstractSolverConfiguration> solverConfigs,
			final AbstractSolverConfiguration solverConfig)
			throws IllegalArgumentException {
		if (isPathValid(solverConfig.getPath())) {
			if (!solverConfigs.contains(solverConfig)) {
				solverConfigs.add(solverConfig);
			}
		} else {
			throw new IllegalArgumentException(
					"Could not add the SMT-solver configuration: invalid path."); //$NON-NLS-1$
		}
	}

	@Override
	public void addSolverConfig(final AbstractSolverConfiguration solverConfig)
			throws IllegalArgumentException {
		addSolverConfig(solverConfigs, solverConfig);
	}

	@Override
	public void addSolverConfigToDefault(
			final AbstractSolverConfiguration solverConfig)
			throws IllegalArgumentException {
		addSolverConfig(defaultSolverConfigs, solverConfig);
	}

	@Override
	public void removeSolverConfig(final int configIndex) {
		solverConfigs.remove(configIndex);
		if (selectedConfigIndex > configIndex) {
			selectedConfigIndex--;
		} else if (selectedConfigIndex == configIndex) {
			if (solverConfigs.size() > 0) {
				selectedConfigIndex = 0;
			} else {
				selectedConfigIndex = -1;
			}
		}
	}

	/**
	 * Tells whether the current selection index is valid or not
	 * 
	 * @return whether the current selection index is valid or not
	 */
	@Override
	public boolean selectedConfigIndexValid() {
		return selectedConfigIndex >= 0
				&& selectedConfigIndex < solverConfigs.size();
	}

	@Override
	public void setSelectedConfigIndex(final boolean selectionRequested,
			final int selectionIndex) {
		/**
		 * If there is only one solver set in the table, it is selected for SMT
		 * proofs
		 */
		if (solverConfigs.size() == 1) {
			selectedConfigIndex = 0;
		} else {
			/**
			 * Else, if a selection was requested, the corresponding
			 * configuration is selected for SMT proofs.
			 */
			if (selectionRequested) {
				selectedConfigIndex = selectionIndex;
			} else {
				/**
				 * Else if the current selected solver is not valid...
				 */
				if (!selectedConfigIndexValid()) {
					/**
					 * if there is some configurations set in the list, the
					 * first one is selected for SMT proofs, else the selected
					 * configuration index is set to -1.
					 */
					if (solverConfigs.size() > 1) {
						selectedConfigIndex = 0;
					} else {
						selectedConfigIndex = -1;
					}
				}
			}
		}
	}

	/**
	 * @param translationPath
	 *            the translationPath to set
	 */
	@Override
	public void setTranslationPath(String translationPath) {
		this.translationPath = getValidPath(this.translationPath,
				translationPath, DEFAULT_TRANSLATION_PATH);
	}

	/**
	 * Sets veriT path to the path of the integrated veriT solver.
	 */
	@Override
	public void setVeriTPath(final String veriTPath) {
		this.veriTPath = getValidPath(this.veriTPath, veriTPath,
				DEFAULT_VERIT_PATH);
	}

	@Override
	public void setDefaultVeriTPath(final String veriTPath) {
		this.defaultVeriTPath = getValidPath(this.defaultVeriTPath, veriTPath,
				DEFAULT_VERIT_PATH);
	}

	private static boolean isABundlePath(final String path) {
		final IPath bundlesPath = new Path(
				"configuration/org.eclipse.osgi/bundles");
		final String devBundlesFix = "org.eventb.smt.verit";
		return path.contains(bundlesPath.toOSString())
				|| path.contains(devBundlesFix);
	}
}