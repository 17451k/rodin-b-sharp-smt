/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     YGU (Systerel) - initial API and implementation
 *******************************************************************************/
package br.ufrn.smt.solver.translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultInspector;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.pp.IPPMonitor;
import org.eventb.pp.PPProof;

import fr.systerel.smt.provers.ast.SMTBenchmark;
import fr.systerel.smt.provers.ast.SMTFactory;
import fr.systerel.smt.provers.ast.SMTFormula;
import fr.systerel.smt.provers.ast.SMTFunctionSymbol;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTLogic.SMTOperator;
import fr.systerel.smt.provers.ast.SMTPredicateSymbol;
import fr.systerel.smt.provers.ast.SMTSignaturePP;
import fr.systerel.smt.provers.ast.SMTSortSymbol;
import fr.systerel.smt.provers.ast.SMTSymbol;
import fr.systerel.smt.provers.ast.SMTTerm;
import fr.systerel.smt.provers.ast.SMTVar;
import fr.systerel.smt.provers.internal.core.IllegalTagException;

/**
 * This class does the SMT translation through ppTrans. ppTrans is called first,
 * to reduce an Event-B sequent to Predicate Calculus. Then the SMT translation
 * is done.
 */
public class SMTThroughPP extends TranslatorV1_2 {
	public SMTThroughPP(final String solver) {
		super(solver);
	}

	/**
	 * An instance of <code>SMTThroughPP</code> is associated to a signature
	 * that is completed during the translation process.
	 */
	protected SMTSignaturePP signature;
	/**
	 * In order to translate memberships, the approach implemented in this class
	 * defines some new predicates. <code>msTypeMap</code> is a map between each
	 * Event-B type used in a membership and the corresponding SMT-LIB predicate
	 * symbols.
	 */
	protected final Map<Type, SMTPredicateSymbol> msTypeMap = new HashMap<Type, SMTPredicateSymbol>();

	/**
	 * This list contains the terms of the current membership being translated
	 * in the translation process.
	 */
	// FIXME Seems to be unsafe, to be deleted if possible
	protected List<SMTTerm> membershipPredicateTerms = new ArrayList<SMTTerm>();

	/**
	 * This is the public translation method
	 * 
	 * @param lemmaName
	 *            the name to be used in the SMT-LIB benchmark
	 * @param hypotheses
	 *            the hypotheses of the Event-B sequent
	 * @param goal
	 *            the goal of the Event-B sequent
	 * @return the SMT-LIB benchmark built over the translation of the given
	 *         Event-B sequent
	 * @throws TranslationException
	 */
	public static SMTBenchmark translateToSmtLibBenchmark(
			final String lemmaName, final List<Predicate> hypotheses,
			final Predicate goal, final String solver)
			throws TranslationException {
		final SMTBenchmark smtB = new SMTThroughPP(solver).translate(lemmaName,
				hypotheses, goal);
		return smtB;
	}

	class MemberShipPredicateInspector extends
			DefaultInspector<RelationalPredicate> {
		@Override
		public void inspect(final RelationalPredicate relPredicate,
				final IAccumulator<RelationalPredicate> accumulator) {
			if (relPredicate.getTag() == Formula.IN) {
				accumulator.add(relPredicate);
			}
		}
	}

	private Map<String, Type> storeMonadicMSPIdentifiers(
			final List<Predicate> hypotheses, final Predicate goal) {
		final MemberShipPredicateInspector msp = new MemberShipPredicateInspector();
		final List<RelationalPredicate> msRelPred = new ArrayList<RelationalPredicate>();
		for (final Predicate p : hypotheses) {
			msRelPred.addAll(p.inspect(msp));
		}
		msRelPred.addAll(goal.inspect(msp));

		final Set<FreeIdentifier> monadicSetElements = new HashSet<FreeIdentifier>();
		final Set<Expression> notMSPSets = new HashSet<Expression>();

		Expression left;
		Expression right;

		for (final RelationalPredicate predicate : msRelPred) {
			left = predicate.getLeft();
			right = predicate.getRight();

			assert right instanceof FreeIdentifier
					|| right instanceof BoundIdentifier;

			notMSPSets.add(left);
			if (notMSPSets.contains(right)) {
				continue;
			} else if (right instanceof BoundIdentifier) {
				notMSPSets.add(right);
				continue;
			} else if (right.getType().getSource() != null) {
				notMSPSets.add(right);
				continue;
			} else {
				monadicSetElements.add((FreeIdentifier) right);
			}
		}
		monadicSetElements.removeAll(notMSPSets);

		final Map<String, Type> monadicSetEnv = new HashMap<String, Type>();

		for (final FreeIdentifier set : monadicSetElements) {
			monadicSetEnv.put(set.getName(), set.getType());
		}

		return monadicSetEnv;
	}

	/**
	 * This is the translation method for the ppTrans approach of SMT
	 * translation.
	 * 
	 * @throws TranslationException
	 */
	@Override
	protected SMTBenchmark translate(final String lemmaName,
			final List<Predicate> hypotheses, final Predicate goal)
			throws TranslationException {

		/**
		 * PP translation
		 */
		final PPProof ppProof = ppTranslation(hypotheses, goal);
		final List<Predicate> ppTranslatedHypotheses = ppProof
				.getTranslatedHypotheses();
		final Predicate ppTranslatedGoal = ppProof.getTranslatedGoal();

		final SMTLogic logic = determineLogic();

		/**
		 * SMT translation
		 */
		// translates the signature
		translateSignature(logic, ppTranslatedHypotheses, ppTranslatedGoal);

		// translates each hypothesis
		final List<SMTFormula> translatedAssumptions = new ArrayList<SMTFormula>();
		for (final Predicate hypothesis : ppTranslatedHypotheses) {
			clearFormula();
			translatedAssumptions.add(translate(hypothesis));
		}

		// translates the goal
		clearFormula();
		final SMTFormula smtFormula = translate(ppTranslatedGoal);

		return new SMTBenchmark(lemmaName, signature, translatedAssumptions,
				smtFormula);
	}

	/**
	 * This is the pp translation method.
	 */
	private static PPProof ppTranslation(final List<Predicate> hypotheses,
			final Predicate goal) {
		final PPProof ppProof = new PPProof(hypotheses, goal, new IPPMonitor() {

			@Override
			public boolean isCanceled() {
				// TODO Auto-generated method stub
				return false;
			}
		});

		/**
		 * Translates the original hypotheses and goal to predicate calculus
		 */
		ppProof.translate();

		return ppProof;
	}

	/**
	 * This method is used only to test the SMT translation
	 */
	public static SMTFormula translate(final SMTLogic logic,
			final Predicate predicate, final String solver) {
		final SMTThroughPP translator = new SMTThroughPP(solver);
		translator.translateSignature(logic, new ArrayList<Predicate>(0),
				predicate);
		predicate.accept(translator);
		return translator.getSMTFormula();
	}

	/**
	 * This method translates the given predicate into an SMT Formula.
	 * 
	 * @throws TranslationException
	 */
	private SMTFormula translate(final Predicate predicate)
			throws TranslationException {
		predicate.accept(this);
		return getSMTFormula();
	}

	Map<String, Type> monadicSets = new HashMap<String, Type>();

	/**
	 * This method builds the SMT-LIB signature of a sequent given as its set of
	 * hypotheses and its goal. The method also fill the variables
	 * <code>typeMap</code>, <code>varMap</code> and <code>msTypeMap</code>.
	 * 
	 * @param logic
	 *            the SMT-LIB logic
	 * @param hypotheses
	 *            the set of hypotheses of the sequent
	 * @param goal
	 *            the goal of the sequent
	 */
	@Override
	public void translateSignature(final SMTLogic logic,
			final List<Predicate> hypotheses, final Predicate goal) {
		signature = new SMTSignaturePP(logic);

		linkLogicSymbols();

		final ITypeEnvironment typeEnvironment = extractTypeEnvironment(
				hypotheses, goal);

		monadicSets.putAll(storeMonadicMSPIdentifiers(hypotheses, goal));

		/**
		 * For each membership of the type environment,
		 */
		final IIterator iter = typeEnvironment.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			final String varName = iter.getName();
			Type varType = iter.getType();
			boolean parseConstant = true;

			/**
			 * check if the the variable is a monadic set. If so, translate the
			 * base type of it
			 */
			if (monadicSets.containsKey(varName)) {
				varType = monadicSets.get(varName).getBaseType();
				parseConstant = false;
			}

			/**
			 * translates the type into an SMT-LIB sort, adds it to the types
			 * mapping (and adds it to the signature sort symbols set)
			 */
			final SMTSortSymbol smtSortSymbol;
			if (!typeMap.containsKey(varType)) {
				smtSortSymbol = translateTypeName(varType);
				typeMap.put(varType, smtSortSymbol);
			} else {
				smtSortSymbol = typeMap.get(varType);
			}

			/**
			 * gets a fresh name for the type of the variable to be typed, adds
			 * it to the type names mapping (and adds it to the signature
			 * symbols set)
			 * 
			 */
			final Set<Type> baseTypes = getBaseTypes(new HashSet<Type>(),
					varType);
			for (final Type baseType : baseTypes) {
				if (!typeMap.containsKey(baseType)) {
					final SMTSortSymbol baseSort = translateTypeName(baseType);
					typeMap.put(baseType, baseSort);
				}
			}

			if (parseConstant) {
				/**
				 * gets a fresh name for the variable to be typed, adds it to
				 * the variable names mapping (and adds it to the signature
				 * symbols set)
				 */
				final SMTFunctionSymbol smtConstant;
				if (!varMap.containsKey(varName)) {
					smtConstant = signature.freshConstant(varName,
							smtSortSymbol);
					varMap.put(varName, smtConstant);
				} else {
					smtConstant = (SMTFunctionSymbol) varMap.get(varName);
				}

				/**
				 * adds the typing item (<code>x ⦂ S</code>) to the signature as
				 * a constant (<code>extrafuns</code> SMT-LIB section, with a
				 * sort but no argument: <code>(x S)</code>).
				 */
				signature.addConstant(smtConstant);
			}
		}

		final List<Type> biTypes = getBoundIDentDeclTypes(hypotheses, goal);

		final Iterator<Type> bIterator = biTypes.iterator();

		while (bIterator.hasNext()) {

			final Type varType = bIterator.next();

			/**
			 * translates the type into an SMT-LIB sort, adds it to the types
			 * mapping (and adds it to the signature sort symbols set)
			 */
			final SMTSortSymbol smtSortSymbol;
			if (!typeMap.containsKey(varType)) {
				smtSortSymbol = translateTypeName(varType);
				typeMap.put(varType, smtSortSymbol);
			} else {
				smtSortSymbol = typeMap.get(varType);
			}

			/**
			 * do the same for each base type used to define this type into an
			 * SMT-LIB sort
			 */
			final Set<Type> baseTypes = getBaseTypes(new HashSet<Type>(),
					varType);
			for (final Type baseType : baseTypes) {
				if (!typeMap.containsKey(baseType)) {
					final SMTSortSymbol baseSort = translateTypeName(baseType);
					typeMap.put(baseType, baseSort);
				}
			}
		}

	}

	/**
	 * This method links some symbols of the logic to the main Event-B symbols.
	 */
	private void linkLogicSymbols() {
		final SMTLogic logic = signature.getLogic();
		final FormulaFactory ff = FormulaFactory.getDefault();

		typeMap.put(ff.makeIntegerType(), logic.getIntegerSort());
		typeMap.put(ff.makeBooleanType(), logic.getBooleanSort());
	}

	/**
	 * This method recursively traverses the type tree to list all base types
	 * used to define it. That is, those which can be extracted from powerset
	 * types and product types.
	 * 
	 * @param baseTypes
	 *            the actual list of extracted base types
	 * @param type
	 *            the type from which base types must be extracted
	 * @return the list of base types extracted from this type
	 */
	private static Set<Type> getBaseTypes(final Set<Type> baseTypes,
			final Type type) {
		final boolean isAProductType = type instanceof ProductType;
		/**
		 * Base case: the type is a base type. Adds it to the list and returns
		 * the list.
		 */
		if (type.getSource() == null && type.getTarget() == null
				&& type.getBaseType() == null && !isAProductType) {
			baseTypes.add(type);
			return baseTypes;
		}

		/**
		 * The type looks like <code>alpha × beta</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha and beta.
		 */
		else if (isAProductType) {
			final ProductType product = (ProductType) type;
			return getBaseTypes(getBaseTypes(baseTypes, product.getLeft()),
					product.getRight());
		}

		/**
		 * The type looks like <code>ℙ(alpha × beta)</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha and beta.
		 */
		else if (type.getSource() != null) {
			return getBaseTypes(getBaseTypes(baseTypes, type.getSource()),
					type.getTarget());
		}

		/**
		 * The type looks like <code>ℙ(alpha)</code>. Calls recursively
		 * <code>getBaseTypes</code> on alpha.
		 */
		else if (type.getBaseType() != null) {
			return getBaseTypes(baseTypes, type.getBaseType());
		}

		/**
		 * This case should not be reached because Event-B types given as
		 * arguments are well-formed.
		 */
		else {
			// FIXME Check that this case really should not be reached, if it is
			// right, delete this case, otherwise, add an exception or implement
			// this case.
			return null;
		}
	}

	/**
	 * This method translates an Event-B type into an SMT-LIB sort. It gives the
	 * sort a fresh name if necessary, to avoid any name collision.
	 */
	@Override
	protected SMTSortSymbol translateTypeName(final Type type) {
		if (type.getSource() == null && type.getTarget() == null
				&& type.getBaseType() == null) {
			return signature.freshSort(type.toString());
		}
		return signature.freshSort();
	}

	/**
	 * This method translates an Event-B associative expression into an SMT
	 * node.
	 */
	@Override
	public void visitAssociativeExpression(
			final AssociativeExpression expression) {
		final SMTTerm[] children = smtTerms(expression.getChildren());
		final int tag = expression.getTag();
		switch (tag) {
		case Formula.PLUS:
			smtNode = sf.makePlus((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.PLUS), children, signature);
			break;
		case Formula.MUL:
			smtNode = sf.makeMul((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.MUL), children, signature);
			break;
		default:
			/**
			 * BUNION, BINTER, BCOMP, FCOMP and OVR tags cannot be produced by
			 * ppTrans.
			 */
			throw new IllegalTagException(tag);
		}
	}

	/**
	 * This method translates an Event-B atomic expression into an SMT node.
	 */
	@Override
	public void visitAtomicExpression(final AtomicExpression expression) {
		switch (expression.getTag()) {
		case Formula.INTEGER:
			// FIXME this uses a set theory
			// smtNode = sf.makeInteger(signature.getLogic().getIntegerCste());
			break;
		case Formula.BOOL:
			// FIXME this uses a set theory
			// smtNode = sf.makeBool(signature.getLogic().getBooleanCste());
			break;
		case Formula.TRUE:
			// FIXME this is the boolean value true
			// smtNode = sf.makeTrue(signature.getLogic().getTrue());
			break;
		case Formula.FALSE:
			// FIXME this is the boolean value false
			// smtNode = sf.makeFalse(signature.getLogic().getFalse());
			break;
		default:
			// TODO check that it's true for KPRED, KSUCC, KPRJ1_GEN, KPRJ2_GEN,
			// KID_GEN tags
			/**
			 * NATURAL, NATURAL1, EMPTYSET, KPRED, KSUCC, KPRJ1_GEN, KPRJ2_GEN,
			 * KID_GEN tags cannot be produced by ppTrans.
			 */
			throw new IllegalTagException(expression.getTag());
		}
	}

	/**
	 * This method translates an Event-B binary expression into an SMT node.
	 */
	@Override
	public void visitBinaryExpression(final BinaryExpression expression) {
		final Expression left = expression.getLeft();
		final Expression right = expression.getRight();
		final SMTTerm[] children = smtTerms(left, right);
		switch (expression.getTag()) {
		case Formula.MINUS:
			smtNode = sf.makeMinus((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.MINUS), children, signature);
			break;
		case Formula.MAPSTO:
			if (left.getTag() != Formula.MAPSTO) {
				membershipPredicateTerms.add(0, children[0]);
			}

			if (right.getTag() != Formula.MAPSTO) {
				membershipPredicateTerms.add(children[1]);
			}
			break;
		case Formula.DIV:
			smtNode = sf.makeDiv((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.DIV), children, signature);
			break;
		case Formula.MOD:
			throw new IllegalArgumentException(
					"The operation \'modulo\' is not supported yet");
		case Formula.EXPN:
			throw new IllegalArgumentException(
					"The operation \'exponential\' is not supported yet");
		default:
			/**
			 * REL, TREL, SREL, STREL, PFUN, TFUN, PINJ, TINJ, PSUR, TSUR, TBIJ,
			 * SETMINUS, CPROD, DPROD, PPROD, DOMRES, DOMSUB, RANRES, RANSUB,
			 * UPTO, FUNIMAGE, RELIMAGE
			 */
			throw new IllegalTagException(expression.getTag());
		}
	}

	/**
	 * This method translates an Event-B bool expression into an SMT node.
	 */
	@Override
	public void visitBoolExpression(final BoolExpression expression) {
		final SMTFormula predicate = smtFormula(expression.getPredicate());
		switch (expression.getTag()) {
		case Formula.KBOOL:
			smtNode = predicate; // FIXME Is that right?
			break;
		default:
			throw new IllegalTagException(expression.getTag());
		}
	}

	/**
	 * This method translates an Event-B literal predicate into an SMT node.
	 */
	@Override
	public void visitLiteralPredicate(final LiteralPredicate predicate) {
		switch (predicate.getTag()) {
		case Formula.BTRUE:
			smtNode = sf.makePTrue(signature);
			break;
		case Formula.BFALSE:
			smtNode = sf.makePFalse(signature);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}
	}

	/**
	 * This method translates an Event-B relational predicate into an SMT node.
	 */
	@Override
	public void visitRelationalPredicate(final RelationalPredicate predicate) {
		final int tag = predicate.getTag();
		switch (tag) {
		case Formula.EQUAL: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = SMTFactory.makeEqual(children);
			break;
		}
		case Formula.NOTEQUAL: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeNotEqual(children);
		}
			break;
		case Formula.LT: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeLessThan((SMTPredicateSymbol) signature.getLogic()
					.getOperator(SMTOperator.LT), children, signature);
		}
			break;
		case Formula.LE: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeLessEqual((SMTPredicateSymbol) signature
					.getLogic().getOperator(SMTOperator.LE), children,
					signature);
		}
			break;
		case Formula.GT: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeGreaterThan((SMTPredicateSymbol) signature
					.getLogic().getOperator(SMTOperator.GT), children,
					signature);
		}
			break;
		case Formula.GE: {
			final SMTTerm[] children = smtTerms(predicate.getLeft(),
					predicate.getRight());
			smtNode = sf.makeGreaterEqual((SMTPredicateSymbol) signature
					.getLogic().getOperator(SMTOperator.GE), children,
					signature);
		}
			break;
		case Formula.IN:
			/**
			 * The predicate is <code>a ∈ s</code> with <code>a ⦂ S</code> and
			 * <code>s ⦂ ℙ(S)</code>. Then we just need to map the type of
			 * <code>a</code> to an SMT-LIB predicate : <code>(p S PS)</code>.
			 */
			final Expression left = predicate.getLeft();
			final Expression right = predicate.getRight();

			// Translate monadic sets (special case)
			if (right instanceof FreeIdentifier) {
				final FreeIdentifier rightSet = (FreeIdentifier) right;
				if (monadicSets.containsKey(rightSet.getName())) {
					final Type leftType = left.getType();
					final SMTTerm leftTerm = smtTerm(left);
					final SMTTerm[] argTerms = { leftTerm };
					// SMTPredicateSymbol predSymbol = msTypeMap.get(leftType);
					final SMTSortSymbol[] argSorts = { leftTerm.getSort() };

					// FIXME Check if there is already defined a predicate
					// before creating a new one.
					final SMTPredicateSymbol predSymbol = signature
							.addPredicateSymbol(rightSet.getName(), argSorts);

					msTypeMap.put(leftType, predSymbol);

					smtNode = SMTFactory.makeAtom(predSymbol, argTerms,
							signature);
					membershipPredicateTerms.clear();
					break;
				}
			}

			final SMTTerm[] children = smtTerms(left, right);

			if (left.getTag() != Formula.MAPSTO) {
				membershipPredicateTerms.add(0, children[0]);
			}
			membershipPredicateTerms.add(children[1]);

			final int numberOfArguments = membershipPredicateTerms.size();
			final SMTSortSymbol[] argSorts = new SMTSortSymbol[numberOfArguments];
			for (int i = 0; i < numberOfArguments; i++) {
				argSorts[i] = membershipPredicateTerms.get(i).getSort();
			}

			final Type leftType = left.getType();

			SMTPredicateSymbol predSymbol = msTypeMap.get(leftType);
			if (predSymbol == null) {
				predSymbol = signature.addNewPredicateSymbol(
						signature.freshPredName(), argSorts);
				msTypeMap.put(leftType, predSymbol);
			}

			if (predSymbol == null) {
				// TODO throw new exception
			}

			final SMTTerm[] args = membershipPredicateTerms
					.toArray(new SMTTerm[numberOfArguments]);

			smtNode = SMTFactory.makeAtom(predSymbol, args, signature);
			membershipPredicateTerms.clear();
			break;
		case Formula.NOTIN:
			break;
		default:
			/**
			 * SUBSET, SUBSETEQ, NOTSUBSET and NOTSUBSETEQ cannot be produced by
			 * ppTrans.
			 */
			throw new IllegalTagException(tag);
		}
	}

	/**
	 * This method translates an Event-B unary expression into an SMT node.
	 */
	@Override
	public void visitUnaryExpression(final UnaryExpression expression) {
		final SMTTerm[] children = new SMTTerm[] { smtTerm(expression
				.getChild()) };
		switch (expression.getTag()) {
		case Formula.UNMINUS:
			smtNode = sf.makeUMinus((SMTFunctionSymbol) signature.getLogic()
					.getOperator(SMTOperator.UMINUS), children, signature);
			break;
		default:
			/**
			 * KCARD, POW, POW1, KUNION, KINTER, KDOM, KRAN, KPRJ1, KPRJ2, KID,
			 * KMIN, KMAX, CONVERSE
			 */
			throw new IllegalTagException(expression.getTag());
		}
	}

	@Override
	public void visitSetExtension(final SetExtension expression) {
		// TODO
	}

	/**
	 * This method translates an Event-B bound identifier declaration into an
	 * SMT node.
	 */
	@Override
	public void visitBoundIdentDecl(final BoundIdentDecl boundIdentDecl) {
		final String varName = boundIdentDecl.getName();
		final SMTVar smtVar;

		final Set<String> symbolNames = new HashSet<String>();
		for (final SMTSymbol function : varMap.values()) {
			symbolNames.add(function.getName());
		}
		for (final SMTVar var : qVarMap.values()) {
			symbolNames.add(var.getSymbol().getName());
		}
		final String smtVarName = signature.freshSymbolName(symbolNames,
				varName);
		final SMTSortSymbol sort = typeMap.get(boundIdentDecl.getType());
		smtVar = (SMTVar) sf.makeVar(smtVarName, sort);
		if (!qVarMap.containsKey(varName)) {
			qVarMap.put(varName, smtVar);
			boundIdentifiers.add(varName);
		} else {
			qVarMap.put(smtVarName, smtVar);
			boundIdentifiers.add(smtVarName);
		}
		smtNode = smtVar;
	}

	/**
	 * This method translates an Event-B bound identifier into an SMT node.
	 */
	@Override
	public void visitBoundIdentifier(final BoundIdentifier expression) {
		final String bidName = boundIdentifiers.get(boundIdentifiers.size()
				- expression.getBoundIndex() - 1);
		smtNode = qVarMap.get(bidName);
	}

	/**
	 * This method translates an Event-B free identifier into an SMT node.
	 */
	@Override
	public void visitFreeIdentifier(final FreeIdentifier expression) {
		smtNode = sf
				.makeConstant(
						(SMTFunctionSymbol) varMap.get(expression.getName()),
						signature);
	}

	/**
	 * This method translates an Event-B quantified predicate into an SMT node
	 */
	@Override
	public void visitQuantifiedPredicate(final QuantifiedPredicate predicate) {
		boundIdentifiersMarker.push(boundIdentifiers.size());

		final SMTTerm[] termChildren = smtTerms(predicate.getBoundIdentDecls());
		final SMTFormula formulaChild = smtFormula(predicate.getPredicate());

		switch (predicate.getTag()) {
		case Formula.FORALL:
			smtNode = sf.makeForAll(termChildren, formulaChild);
			break;
		case Formula.EXISTS:
			smtNode = sf.makeExists(termChildren, formulaChild);
			break;
		default:
			throw new IllegalTagException(predicate.getTag());
		}

		final int top = boundIdentifiersMarker.pop();

		boundIdentifiers.subList(top, boundIdentifiers.size()).clear();
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitBecomesEqualTo(final BecomesEqualTo assignment) {
		throw new IllegalArgumentException(
				"'becomes equal to' assignments are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitBecomesMemberOf(final BecomesMemberOf assignment) {
		throw new IllegalArgumentException(
				"'becomes member of' assignments are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitBecomesSuchThat(final BecomesSuchThat assignment) {
		throw new IllegalArgumentException(
				"'becomes such that' assignments are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitQuantifiedExpression(final QuantifiedExpression expression) {
		throw new IllegalArgumentException(
				"'Quantified expressions' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitSimplePredicate(final SimplePredicate predicate) {
		throw new IllegalArgumentException(
				"'Simple predicates' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitMultiplePredicate(final MultiplePredicate predicate) {
		throw new IllegalArgumentException(
				"'Multiple predicates' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitExtendedExpression(final ExtendedExpression expression) {
		throw new IllegalArgumentException(
				"'Extended expressions' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}

	/**
	 * This method should not be called in the PP approach of SMT translation
	 */
	@Override
	public void visitExtendedPredicate(final ExtendedPredicate predicate) {
		throw new IllegalArgumentException(
				"'Extended predicates' are not compatible with the underlying logic used in this version of SMT-LIB.");
	}
}
