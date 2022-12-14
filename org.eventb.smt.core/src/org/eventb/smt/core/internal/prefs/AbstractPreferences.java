/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.smt.core.internal.prefs;

import static org.eventb.smt.core.SMTPreferences.PREF_NODE_NAME;
import static org.eventb.smt.core.internal.provers.SMTProversCore.logError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eventb.smt.core.IDescriptor;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public abstract class AbstractPreferences<T extends IDescriptor> {

	public static boolean DEBUG = false;

	// Root of the preferences tree for this plug-in
	private static final IEclipsePreferences DEFAULT_ROOT = InstanceScope.INSTANCE
			.getNode(PREF_NODE_NAME);

	private final IEclipsePreferences root;
	private final String nodeName;
	protected final DescriptorList<T> bundled;
	protected final DescriptorList<T> known;

	private volatile IPreferencesChangeListener listener;

	protected AbstractPreferences(String nodeName) {
		this(DEFAULT_ROOT, nodeName);
	}

	protected AbstractPreferences(IEclipsePreferences root, String nodeName) {
		this.root = root;
		this.nodeName = nodeName;
		this.bundled = loadBundledDescriptors();
		this.known = newDescriptorList();
		doSetKnown(loadKnownDescriptors());
		if (DEBUG) {
			trace("Initialized preferences for ");
		}
	}

	protected abstract DescriptorList<T> loadBundledDescriptors();

	protected abstract DescriptorList<T> newDescriptorList();

	private List<T> loadKnownDescriptors() {
		final List<T> result = new ArrayList<T>();
		try {
			if (root.nodeExists(nodeName)) {
				loadFromNode(root.node(nodeName), result);
			}
		} catch (BackingStoreException e) {
			logError("loading preferences from " + root + "/" + nodeName, e);
		}
		return result;
	}

	private void loadFromNode(Preferences node, List<T> result)
			throws BackingStoreException {
		for (final String childName : sortedChildrenNames(node)) {
			try {
				final T desc = loadFromNode(node.node(childName));
				result.add(desc);
			} catch (IllegalArgumentException e) {
				logError("loading preference " + node.absolutePath(), e);
			}
		}
	}

	/**
	 * Returns the descriptor stored in the given preference node.
	 * 
	 * @param node
	 *            some preference node containing a serialized descriptor
	 * @return the descriptor in the given preference
	 */
	protected abstract T loadFromNode(Preferences node);
	
	private String[] sortedChildrenNames(final Preferences node)
			throws BackingStoreException {
		final String[] childrenNames = node.childrenNames();
		Arrays.sort(childrenNames, new Comparator<String>() {
			// Compare lengths first to implement numerical ordering
			@Override
			public int compare(String o1, String o2) {
				final int lengthDiff = o1.length() - o2.length();
				return lengthDiff == 0 ? o1.compareTo(o2) : lengthDiff;
			}
		});
		return childrenNames;
	}

	public void setKnown(T[] newDescs) {
		doSetKnown(Arrays.asList(newDescs));
		try {
			doSave();
		} catch (BackingStoreException e) {
			logError("saving preferences to " + root + "/" + nodeName, e);
		}
	}

	protected void doSetKnown(List<T> newDescs) {
		known.clear();
		for (T desc : newDescs) {
			final T real = getRealDescriptor(desc);
			known.add(real);
		}
		known.addIfNotPresent(bundled);
		final IPreferencesChangeListener local = listener;
		if (local != null) {
			local.preferencesChange();
		}
	}

	/**
	 * Returns the descriptor to store based on the given descriptor.
	 * Basically, this method should return a bundled descriptor with the same
	 * name, if there is any. Otherwise, it should return its parameter.
	 * 
	 * @param desc
	 *            some descriptor
	 * @return a bundled descriptor or the given descriptor
	 */
	protected abstract T getRealDescriptor(T desc);

	public T doGetBundled(String name) {
		return bundled.get(name);
	}

	public T[] doGetBundled() {
		return bundled.toArray();
	}

	public T[] doGetKnown() {
		return known.toArray();
	}

	protected T doGet(String name) {
		return known.get(name);
	}

	public void doSave() throws BackingStoreException {
		root.node(nodeName).removeNode();
		final Preferences node = root.node(nodeName);
		int count = 1;
		for (final T desc : known) {
			final Preferences childNode = node.node(Integer.toString(count++));
			((Descriptor) desc).serialize(childNode);
		}
		node.flush();
		if (DEBUG) {
			trace("Saved preferences for ");
		}
	}

	/**
	 * Registers a new listener for preferences change. There is currently
	 * support for at most one listener.
	 * 
	 * @param newListener
	 *            listener to set
	 */
	public void doAddChangeListener(IPreferencesChangeListener newListener) {
		this.listener = newListener;
	}

	private void trace(final String msg) {
		System.out.println(msg + nodeName + ":");
		for (final T desc : this.known) {
			System.out.println("\t" + desc);
		}
	}

	// TODO Listen to Eclipse preference changes

}