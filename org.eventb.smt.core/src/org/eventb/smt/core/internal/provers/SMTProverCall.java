/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	Systerel - initial API and implementation
 * 	UFRN - code refactoring
 *******************************************************************************/
package org.eventb.smt.core.internal.provers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;
import static org.eventb.smt.core.SMTLIBVersion.V1_2;
import static org.eventb.smt.core.SMTLIBVersion.V2_0;
import static org.eventb.smt.core.SolverKind.ALT_ERGO;
import static org.eventb.smt.core.SolverKind.MATHSAT5;
import static org.eventb.smt.core.SolverKind.OPENSMT;
import static org.eventb.smt.core.SolverKind.Z3;
import static org.eventb.smt.core.internal.translation.Translator.DEBUG;
import static org.eventb.smt.core.internal.translation.Translator.DEBUG_DETAILS;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.core.seqprover.xprover.ProcessMonitor;
import org.eventb.core.seqprover.xprover.XProverCall2;
import org.eventb.smt.core.SolverKind;
import org.eventb.smt.core.TranslationApproach;
import org.eventb.smt.core.internal.ast.SMTBenchmark;
import org.eventb.smt.core.internal.prefs.SimplePreferences;
import org.eventb.smt.core.internal.translation.TranslationResult;
import org.eventb.smt.core.internal.translation.Translator;

/**
 * 
 * Each instance of this class represents a call to an external SMT solver.
 * 
 */
public abstract class SMTProverCall extends XProverCall2 {

	protected static final String RES_FILE_EXTENSION = ".res";
	protected static final String SMT_LIB_FILE_EXTENSION = ".smt";
	protected static final String NON_STANDARD_SMT_LIB2_FILE_EXTENSION = ".smt2";
	private static final String RODIN_SEQUENT = "rodin_sequent";
	/**
	 * FOR DEBUG ONLY
	 */
	protected final StringBuilder debugBuilder;

	/**
	 * The benchmark produced by the translator if the sequent was not
	 * simplified to a trivial predicate
	 */
	protected SMTBenchmark benchmark;
	// FIXME cannot this field be removed ? (used to check veriT pre-processing)
	protected boolean translationPerformed = false;

	protected File translationFolder = null;

	/**
	 * FOR PERFORMANCE TESTS ONLY
	 */
	protected boolean exceptionRaised = false;

	/**
	 * Solver output at the end of the call
	 */
	protected String solverResult;

	/**
	 * Tells whether the given sequent was discharged (valid = true) or not
	 * (valid = false)
	 */
	private volatile boolean valid;

	volatile Set<Predicate> neededHypotheses = null;

	volatile boolean goalNeeded = true;

	protected final Translator translator;

	final List<Process> activeProcesses = new ArrayList<Process>();

	SMTConfiguration config;

	IPath translationPath = null;

	/**
	 * Name of the lemma to prove
	 */
	String lemmaName;

	/**
	 * Access to these files must be synchronized. smtBenchmarkFile contains the
	 * sequent to discharge translated to SMT-LIB language, smtResultFile
	 * contains the result of the solver
	 */
	File smtTranslationDir = null;
	File smtBenchmarkFile;
	File smtResultFile;

	/**
	 * Creates an instance of this class.
	 * 
	 * @param sequent
	 *            the sequent to discharge
	 * @param pm
	 *            proof monitor used for cancellation
	 */
	protected SMTProverCall(final ISimpleSequent sequent,
			final IProofMonitor pm, final SMTConfiguration config,
			final Translator translator) {
		this(sequent, pm, new StringBuilder(), config, translator);
	}

	protected SMTProverCall(final ISimpleSequent sequent,
			final IProofMonitor pm, final StringBuilder debugBuilder,
			final SMTConfiguration config, final Translator translator) {
		super(sequent, pm);
		this.debugBuilder = debugBuilder;
		this.config = config;
		this.translationPath = SimplePreferences.getTranslationPath();
		this.translator = translator;
		this.lemmaName = RODIN_SEQUENT;
	}

	/**
	 * FOR TESTS ONLY
	 */
	public SMTProverCall(final ISimpleSequent sequent, final IProofMonitor pm,
			final StringBuilder debugBuilder, final SMTConfiguration config,
			final String poName, final Translator translator) {
		super(sequent, pm);
		this.debugBuilder = debugBuilder;
		this.config = config;
		this.lemmaName = poName;
		this.translationPath = SimplePreferences.getTranslationPath();
		this.translator = translator;
	}

	/**
	 * FOR DEBUG ONLY
	 */
	private static void showProcessOutput(final StringBuilder debugBuilder,
			ProcessMonitor monitor, boolean error) {
		final String kind = error ? "error" : "output";
		debugBuilder.append("-- Begin dump of process ").append(kind)
				.append(" --\n");
		final byte[] bytes = error ? monitor.error() : monitor.output();
		if (bytes.length != 0) {
			final String output = new String(bytes);
			if (output.endsWith("\n")) {
				debugBuilder.append(error);
			} else {
				debugBuilder.append(error).append("\n");
			}
		}
		debugBuilder.append("-- End dump of process ").append(kind)
				.append(" --\n");
	}

	/**
	 * FOR DEBUG ONLY
	 */
	private synchronized void showSMTBenchmarkFile() {
		showFile(debugBuilder, smtBenchmarkFile);
	}

	/**
	 * FOR DEBUG ONLY
	 */
	private synchronized void showSMTResultFile() {
		showFile(debugBuilder, smtResultFile);
	}

	/**
	 * FOR DEBUG ONLY: print the SMT solver result into a file
	 * 
	 * @throws IOException
	 */
	private synchronized void printSMTResultFile() throws IOException {
		final FileWriter fileWriter = new FileWriter(smtResultFile);
		fileWriter.write(solverResult);
		fileWriter.close();
	}

	/**
	 * Sets up input arguments for solver.
	 */
	private synchronized List<String> solverCommandLine() {
		final List<String> commandLine = new ArrayList<String>();

		// Selected solver binary path
		commandLine.add(config.getSolverPath().toOSString());

		// Add special arguments specifying benchmark language version
		commandLine.addAll(SMTVersionArgs.getArgs(config));

		// Patch to deactivate the z3 MBQI module which is buggy.
		if (config.getSmtlibVersion() == V1_2 && config.getKind() == Z3) {
			commandLine.add("AUTO_CONFIG=false");
			commandLine.add("MBQI=false");
		}

		// Selected solver arguments
		final String args = config.getArgs();
		if (!args.isEmpty()) {
			commandLine.addAll(asList(args.split(" ")));
		}

		// Benchmark file produced by translating the Event-B sequent
		if (config.getKind() == MATHSAT5) {
			commandLine.add("< " + smtBenchmarkFile.getAbsolutePath());
		} else {
			commandLine.add(smtBenchmarkFile.getAbsolutePath());
		}

		return commandLine;
	}

	/**
	 * Calls an SMT solver and checks its result.
	 * 
	 * @param commandLine
	 *            Command-line which executes the solver on the produced
	 *            benchmark
	 * @throws IOException
	 */
	private void callProver(final List<String> commandLine) throws IOException,
			IllegalArgumentException {

		if (DEBUG_DETAILS) {
			debugBuilder.append("About to launch solver command:\n   ");
			for (String arg : commandLine) {
				debugBuilder.append(" ");
				debugBuilder.append(arg);
			}
			debugBuilder.append("\n");
		}

		try {
			final ProcessBuilder builder = new ProcessBuilder(commandLine);
			builder.redirectErrorStream(true);
			final Process process = builder.start();
			activeProcesses.add(process);
			final ProcessMonitor monitor = new ProcessMonitor(null, process,
					this);

			if (DEBUG_DETAILS)
				showProcessOutcome(debugBuilder, monitor);

			solverResult = new String(monitor.output());
			if (DEBUG) {
				printSMTResultFile();
			}
			if (DEBUG_DETAILS) {
				debugBuilder.append("Result file contains:\n");
				showSMTResultFile();
			}

			valid = checkResult();
			if (DEBUG_DETAILS) {
				debugBuilder.append("Prover ").append(
						valid ? "succeeded" : "failed");
				debugBuilder.append("\n");
			}

		} finally {
			if (DEBUG_DETAILS)
				debugBuilder.append("Solver command finished.\n");
		}
	}

	/**
	 * Checks if the result provided by the solver contains the "unsat" string.
	 * "A formula is valid in a theory exactly when its negation is not satisfiable in this theory"
	 * So is set and returned "valid" attribut.
	 */
	private boolean checkResult() {
		if (compile("^unsat$", MULTILINE).matcher(solverResult).find()) {
			return true;
		} else if (compile("^sat$", MULTILINE).matcher(solverResult).find()
				|| compile("^unknown$", MULTILINE).matcher(solverResult).find()) {
			return false;
		} else {
			throw new IllegalArgumentException("Unexpected response of "
					+ config.getSolverName() + ". See "
					+ lemmaName + ".res for more details.");
		}
	}

	/**
	 * Creates a new PrintWriter given the file.
	 * 
	 * @param smtFile
	 *            the SMT file which will be the output of the translation
	 * @return the PrintWriter linked to the SMT file.
	 */
	protected static PrintWriter openSMTFileWriter(final File smtFile) {
		try {
			final PrintWriter smtFileWriter = new PrintWriter(
					new BufferedWriter(new FileWriter(smtFile)));

			return smtFileWriter;

		} catch (final IOException ioe) {
			ioe.printStackTrace();
			ioe.getMessage();
			return null;
		} catch (final SecurityException se) {
			se.printStackTrace();
			se.getMessage();
			return null;
		}
	}

	/**
	 * FOR DEBUG ONLY
	 * 
	 * @param file
	 *            the file to show
	 */
	protected static void showFile(final StringBuilder builder, File file) {
		if (file == null) {
			builder.append("***File has been cleaned up***\n");
			return;
		}
		try {
			final BufferedReader rdr = new BufferedReader(new FileReader(file));
			String line;
			while ((line = rdr.readLine()) != null) {
				builder.append(line).append("\n");
			}
		} catch (IOException e) {
			builder.append("***Exception when reading file: ");
			builder.append(e.getMessage()).append("***\n");
		}
	}

	/**
	 * FOR DEBUG ONLY
	 */
	protected static void showProcessOutcome(final StringBuilder builder,
			ProcessMonitor monitor) {
		showProcessOutput(builder, monitor, false);
		showProcessOutput(builder, monitor, true);
		builder.append("Exit code is: ").append(monitor.exitCode())
				.append("\n");
	}

	protected void setTranslationPath(final TranslationApproach approach) {
		this.translationPath = this.translationPath.append(approach.toString());
	}

	protected void setTranslationDirectories(
			final TranslationApproach approach, final StringBuilder debugBuilder) {
		translationFolder = new File(translationPath.toOSString());
		if (!translationFolder.mkdirs()) {
			// TODO handle the error
		} else {
			if (DEBUG) {
				if (DEBUG_DETAILS) {
					debugBuilder.append("Created temporary ");
					debugBuilder.append(approach.toString());
					debugBuilder.append(" translation folder '");
					debugBuilder.append(translationFolder).append("'\n");
				}
			} else {
				/**
				 * The deletion will be done when exiting Rodin.
				 */
				translationFolder.deleteOnExit();
			}
		}
	}

	/**
	 * Makes temporary files in the given path
	 */
	protected synchronized void makeTempFileNames() throws IOException {
		final IPath benchmarkTargetPath = translationPath.append(lemmaName);

		if (smtTranslationDir == null) {
			smtTranslationDir = new File(benchmarkTargetPath.toOSString());
			if (!smtTranslationDir.mkdirs()) {
				if (smtTranslationDir.exists()) {
					if (DEBUG) {
						if (DEBUG_DETAILS) {
							debugBuilder
									.append("The directory already exists.");
						}
					}
				} else {
					throw new IOException(
							"An error occured while trying to make the temporary SMT translation directory.");
				}
			} else {
				if (DEBUG) {
					if (DEBUG_DETAILS) {
						debugBuilder
								.append("Made temporary SMT translation directory '");
						debugBuilder.append(smtTranslationDir).append("'\n");
					}
				} else {
					/**
					 * The deletion will be done when exiting Rodin.
					 */
					smtTranslationDir.deleteOnExit();
				}
			}
		}

		final SolverKind solverKind = config.getKind();
		if (config.getSmtlibVersion() == V2_0
				&& (solverKind == ALT_ERGO || solverKind == OPENSMT)) {
			smtBenchmarkFile = File.createTempFile(lemmaName,
					NON_STANDARD_SMT_LIB2_FILE_EXTENSION, smtTranslationDir);
		} else {
			smtBenchmarkFile = File.createTempFile(lemmaName,
					SMT_LIB_FILE_EXTENSION, smtTranslationDir);
		}
		if (DEBUG) {
			if (DEBUG_DETAILS) {
				debugBuilder.append("Created temporary SMT benchmark file '");
				debugBuilder.append(smtBenchmarkFile).append("'\n");
			}
		} else {
			/**
			 * The deletion will be done when exiting Rodin.
			 */
			smtBenchmarkFile.deleteOnExit();
		}

		if (DEBUG) {
			smtResultFile = File.createTempFile(
					lemmaName + "_" + solverKind.toString(),
					RES_FILE_EXTENSION, smtTranslationDir);
			if (DEBUG_DETAILS) {
				debugBuilder.append("Created temporary SMT result file '");
				debugBuilder.append(smtResultFile).append("'\n");
			}

			final PrintStream stream = new PrintStream(smtResultFile);
			stream.println("FAILED");
			stream.close();
		}
	}

	/**
	 * Translates the sequent to SMT-LIB language and sets the benchmark file
	 * with the result.
	 * 
	 * @throws IOException
	 */
	abstract protected void makeSMTBenchmarkFile() throws IOException;

	abstract protected void extractUnsatCore();

	/**
	 * Runs the external SMT solver on the sequent given at instance creation.
	 */
	@Override
	public void run() {
		/**
		 * This statement was put inside a try...catch() block to handle a null
		 * pointer exception when launching some tests
		 */
		if (proofMonitor != null) {
			proofMonitor.setTask("Translating Event-B proof obligation");
		}

		try {

			/**
			 * Translation of the event-b sequent
			 */
			final TranslationResult result = translator.translate(lemmaName,
					sequent);

			/**
			 * If it was simplified to a trivial predicate, the sequent is set
			 * valid and the predicate used as an unsat-core.
			 */
			if (result.isTrivial()) {
				final ITrackedPredicate pred = result.getTrivialPredicate();
				if (pred.isHypothesis()) {
					valid = true;
					neededHypotheses = singleton(pred.getPredicate());
					goalNeeded = false;
				} else {
					valid = true;
					neededHypotheses = emptySet();
					goalNeeded = true;
				}
			} else {
				/**
				 * !result.isTrivial(), an SMT-LIB benchmark was produced
				 */
				benchmark = result.getSMTBenchmark();

				translationPerformed = false;
				try {
					makeSMTBenchmarkFile();
					translationPerformed = true;
				} catch (IllegalArgumentException e) {
					if (DEBUG) {
						debugBuilder.append("Due to translation failure, ");
						debugBuilder.append("the solver won't be launched.\n");
						e.printStackTrace();
					}
				}

				if (translationPerformed) {
					final String solverName = config.getSolverName();
					if (DEBUG_DETAILS) {
						debugBuilder.append("Launching ").append(solverName);
						debugBuilder.append(" with input:\n\n");
						showSMTBenchmarkFile();
					}

					setMonitorMessage("Running SMT solver : " + solverName
							+ ".");

					try {
						callProver(solverCommandLine());
					} catch (IllegalArgumentException e) {
						if (DEBUG) {
							exceptionRaised = true;
							debugBuilder
									.append("Exception raised during prover call : ");
							debugBuilder.append(e.getMessage()).append("\n");
						}
					}
				}

				if (isValid()) {
					if (canExtractUnsatCore()) {
						// FIXME it is not possible to check z3 version, so make
						// errors be catched if not a version capable of manage
						// unsat-cores.
						extractUnsatCore();
					}
				}
			}

		} catch (final IOException e) {
			if (DEBUG) {
				debugBuilder.append(e.getMessage()).append("\n");
			}
			throw new IllegalArgumentException(e);
		} finally {
			if (DEBUG) {
				debugBuilder.append("End of prover call.\n");
				System.out.print(debugBuilder);
			}
		}
	}

	private boolean canExtractUnsatCore() {
		switch (config.getKind()) {
		case VERIT:
			return config.getArgs().contains("--proof=");
		case Z3:
			return config.getSmtlibVersion() == V2_0;
		default:
			return false;
		}
	}

	/**
	 * PUBLIC FOR ACCEPTANCE TESTS ONLY
	 */
	public boolean benchmarkIsNull() {
		return benchmark == null;
	}

	/**
	 * FOR PERFORMANCE TESTS ONLY
	 */
	public boolean isTranslationPerformed() {
		return translationPerformed;
	}

	/**
	 * FOR PERFORMANCE TESTS ONLY
	 */
	public boolean isExceptionRaised() {
		return exceptionRaised;
	}

	/**
	 * Tells whether the sequent has been proved valid by the external SMT
	 * solver.
	 **/
	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public Set<Predicate> neededHypotheses() {
		return neededHypotheses;
	}

	@Override
	public boolean isGoalNeeded() {
		return goalNeeded;
	}

	/**
	 * Human-readable message to be displayed for this proof.
	 */
	@Override
	public String displayMessage() {
		/**
		 * Currently, when no benchmark was produced, it means that PP found a
		 * trivial predicate in the sequent.
		 */
		if (benchmarkIsNull()) {
			return "PP (trivial)";
		} else {
			return config.getName();
		}
	}

	/**
	 * Cleans up this prover call: destroys processes.
	 */
	@Override
	public synchronized void cleanup() {
		for (final Process p : activeProcesses) {
			p.destroy();
		}
	}
}
