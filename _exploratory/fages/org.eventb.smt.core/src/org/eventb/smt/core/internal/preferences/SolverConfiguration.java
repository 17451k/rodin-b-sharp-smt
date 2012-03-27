/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.smt.core.internal.preferences;

import static java.lang.Boolean.parseBoolean;
import static java.util.regex.Pattern.quote;
import static org.eventb.smt.core.internal.preferences.SMTSolver.DEFAULT_SOLVER_ID;
import static org.eventb.smt.core.internal.preferences.Utils.decode;
import static org.eventb.smt.core.internal.preferences.Utils.encode;
import static org.eventb.smt.core.translation.SMTLIBVersion.LATEST;
import static org.eventb.smt.core.translation.SMTLIBVersion.parseVersion;

import org.eventb.smt.core.preferences.ISolverConfig;
import org.eventb.smt.core.translation.SMTLIBVersion;

/**
 * This class describes an SMT solverId configuration.
 * 
 */
public class SolverConfiguration implements ISolverConfig {
	public static final boolean ENABLED = true;
	public static final boolean EDITABLE = true;

	public static final int ID_COL = 0;
	public static final int ENABLED_COL = 1;
	public static final int NAME_COL = 2;
	public static final int SOLVER_ID_COL = 3;
	public static final int ARGS_COL = 4;
	public static final int SMTLIB_VERSION_COL = 5;
	public static final int EDITABLE_COL = 6;

	public static final String SEPARATOR = "|"; //$NON-NLS-1$

	private static final String DEFAULT_CONFIG_ID = ""; //$NON-NLS-1$
	private static final String DEFAULT_CONFIG_NAME = ""; //$NON-NLS-1$
	private static final String DEFAULT_SOLVER_ARGS = ""; //$NON-NLS-1$
	private static final SMTLIBVersion DEFAULT_SMTLIB_VERSION = LATEST;

	final private String id;

	final private String name;

	final private String solverId;

	final private String args;

	final private SMTLIBVersion smtlibVersion;

	final private boolean editable;

	private boolean enabled = true;

	/**
	 * Constructs a new SolverConfiguration
	 * 
	 * @param id
	 *            the id of the configuration
	 * @param solverId
	 *            the id of the solverId
	 * @param args
	 *            arguments that the solverId can use
	 * @param smtlibVersion
	 *            version of SMT-LIB to use with this solverId configuration
	 * @param editable
	 *            either the configuration is editable by the user or not
	 */
	public SolverConfiguration(final String id, final boolean enabled,
			final String name, final String solverId, final String args,
			final SMTLIBVersion smtlibVersion, final boolean editable) {
		this.id = id;
		this.enabled = enabled;
		this.name = name;
		this.solverId = solverId;
		this.args = args;
		this.smtlibVersion = smtlibVersion;
		this.editable = editable;
	}

	public SolverConfiguration(final String id, final String name,
			final String solverId, final String args,
			final SMTLIBVersion smtlibVersion, final boolean editable) {
		this(id, ENABLED, name, solverId, args, smtlibVersion, editable);
	}

	public SolverConfiguration(final String id, final String name,
			final String solverId, final String args,
			final SMTLIBVersion smtlibVersion) {
		this(id, ENABLED, name, solverId, args, smtlibVersion, EDITABLE);
	}

	public SolverConfiguration() {
		this(DEFAULT_CONFIG_ID, DEFAULT_CONFIG_NAME, DEFAULT_SOLVER_ID,
				DEFAULT_SOLVER_ARGS, DEFAULT_SMTLIB_VERSION);
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSolverId() {
		return solverId;
	}

	@Override
	public String getArgs() {
		return args;
	}

	@Override
	public SMTLIBVersion getSmtlibVersion() {
		return smtlibVersion;
	}

	@Override
	public boolean isEditable() {
		return editable;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Parses a preference string to build a solver configuration
	 * 
	 * @param configStr
	 *            the string to parse
	 * @return the solver configuration represented by the string
	 */
	public final static ISolverConfig parseConfig(final String configStr) {
		final String[] columns = configStr.split(quote(SEPARATOR));
		return new SolverConfiguration(decode(columns[ID_COL]),
				parseBoolean(columns[ENABLED_COL]), decode(columns[NAME_COL]),
				decode(columns[SOLVER_ID_COL]), decode(columns[ARGS_COL]),
				parseVersion(columns[SMTLIB_VERSION_COL]),
				parseBoolean(columns[EDITABLE_COL]));
	}

	@Override
	public void toString(final StringBuilder builder) {
		builder.append(encode(id)).append(SEPARATOR);
		builder.append(enabled).append(SEPARATOR);
		builder.append(encode(name)).append(SEPARATOR);
		builder.append(solverId).append(SEPARATOR);
		builder.append(encode(args)).append(SEPARATOR);
		builder.append(smtlibVersion).append(SEPARATOR);
		builder.append(editable);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (id == null ? 0 : id.hashCode());
		result = prime * result + (enabled ? 1 : 0);
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (solverId == null ? 0 : solverId.hashCode());
		result = prime * result + (args == null ? 0 : args.hashCode());
		result = prime * result
				+ (smtlibVersion == null ? 0 : smtlibVersion.hashCode());
		result = prime * result + (editable ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SolverConfiguration other = (SolverConfiguration) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (solverId == null) {
			if (other.solverId != null) {
				return false;
			}
		} else if (!solverId.equals(other.solverId)) {
			return false;
		}
		if (args == null) {
			if (other.args != null) {
				return false;
			}
		} else if (!args.equals(other.args)) {
			return false;
		}
		if (smtlibVersion == null) {
			if (other.smtlibVersion != null) {
				return false;
			}
		} else if (!smtlibVersion.equals(other.smtlibVersion)) {
			return false;
		}
		if (editable != other.editable) {
			return false;
		}
		return true;
	}
}
