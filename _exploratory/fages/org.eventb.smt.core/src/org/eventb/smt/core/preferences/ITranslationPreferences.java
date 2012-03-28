/*******************************************************************************
 * Copyright (c) 2012 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.preferences;

/**
 * @author Systerel (yguyot)
 *
 */
public interface ITranslationPreferences extends IPreferences {

	public abstract String getTranslationPath();

	public abstract void setTranslationPath(final String path);

	public abstract String getVeriTPath();

	public abstract void setVeriTPath(final String path);

}
