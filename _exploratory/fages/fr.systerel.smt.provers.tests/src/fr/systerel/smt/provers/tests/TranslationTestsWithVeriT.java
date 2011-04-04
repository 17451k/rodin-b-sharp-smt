/*******************************************************************************
 * Copyright (c)
 *     
 *******************************************************************************/
package fr.systerel.smt.provers.tests;

import static org.eventb.core.ast.Formula.FORALL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.junit.Test;

import br.ufrn.smt.solver.translation.SMTThroughVeriT;
import fr.systerel.smt.provers.ast.SMTFunctionSymbol;
import fr.systerel.smt.provers.ast.SMTLogic;
import fr.systerel.smt.provers.ast.SMTPredicateSymbol;
import fr.systerel.smt.provers.ast.SMTSignature;
import fr.systerel.smt.provers.ast.SMTSortSymbol;

/**
 * Ensure that translation to veriT extended version of SMT-LIB is correct
 * 
 * @author Vitor Alcantara de Almeida
 * 
 */
public class TranslationTestsWithVeriT extends AbstractTests {
	protected static final ITypeEnvironment defaultTe, simpleTe, errorTe;
	protected static final SMTLogic defaultLogic;
	protected static final String defaultFailMessage = "SMT-LIB translation failed: ";
	private SMTSignature signature;

	static {
		simpleTe = mTypeEnvironment("e", "ℙ(S)", "f", "ℙ(S)", "g", "S", "AB",
				"ℤ ↔ ℤ", "C", "ℤ ↔ (ℤ ↔ ℤ)");

		defaultTe = mTypeEnvironment("S", "ℙ(S)", "p", "S", "q", "S", "r",
				"ℙ(R)", "s", "ℙ(R)", "a", "ℤ", "A", "ℙ(ℤ)", "AB", "ℤ ↔ ℤ", "b",
				"ℤ", "c", "ℤ", "u", "BOOL", "v", "BOOL");

		errorTe = mTypeEnvironment("AZ", "ℤ ↔ ℙ(ℤ)");

		defaultLogic = SMTLogic.VeriTSMTLIBUnderlyingLogic.getInstance();
	}

	private static void testTranslationV1_2Default(final String predStr,
			final String expectedSMTNode) {
		testTranslationV1_2(defaultTe, predStr, expectedSMTNode,
				defaultFailMessage);
	}

	/**
	 * Parses a Predicate Calculus formula, (builds hypotheses and goal) and
	 * tests its SMT-LIB translation
	 * 
	 * @param iTypeEnv
	 *            Input type environment
	 * @param predStr
	 *            String representation of the input predicate
	 * @param expectedSMTNode
	 *            String representation of the expected node
	 * @param failMessage
	 *            Human readable error message
	 */
	private static void testTranslationV1_2(final ITypeEnvironment iTypeEnv,
			final String predStr, final String expectedSMTNode,
			final String failMessage) throws AssertionError {
		final Predicate pred = parse(predStr, iTypeEnv);

		final List<Predicate> hypothesis = new ArrayList<Predicate>();
		hypothesis.add(pred);

		testTranslationV1_2(pred, expectedSMTNode, failMessage);
	}

	/**
	 * Tests the SMT-LIB translation with the given Predicate Calculus formula
	 * 
	 * @param ppred
	 *            Input Predicate Calculus formula
	 * @param expectedSMTNode
	 *            String representation of the expected node
	 * @param failMessage
	 *            Human readable error message
	 */
	private static void testTranslationV1_2(final Predicate ppred,
			final String expectedSMTNode, final String failMessage) {
		final String actualSMTNode = SMTThroughVeriT.translate(defaultLogic,
				ppred).toString();

		System.out.println(translationMessage(ppred, actualSMTNode));
		assertEquals(failMessage, expectedSMTNode, actualSMTNode);
	}

	public void setSignatureForTests(ITypeEnvironment typeEnvironment) {
		this.signature = SMTThroughVeriT.translateSMTSignature(typeEnvironment);
	}

	private StringBuilder typeEnvironmentFunctionsFail(
			Set<String> expectedFunctions, Set<SMTFunctionSymbol> functions) {
		StringBuilder sb = new StringBuilder();
		sb.append("The translated functions wasn't the expected ones. The expected functions are:");
		for (String expectedFunction : expectedFunctions) {
			sb.append("\n");
			sb.append(expectedFunction);
		}
		sb.append("\nBut the translated functions were:");
		for (SMTFunctionSymbol fSymbol : functions) {
			sb.append("\n");
			sb.append(fSymbol.toString());
		}
		sb.append("\n");
		return sb;
	}

	private StringBuilder typeEnvironmentPredicatesFail(
			Set<String> expectedPredicates, Set<SMTPredicateSymbol> predicates) {
		StringBuilder sb = new StringBuilder();
		sb.append("The translated predicates wasn't the expected ones. The expected predicates are:");
		for (String expectedPredicate : expectedPredicates) {
			sb.append("\n");
			sb.append(expectedPredicate);
		}
		sb.append("\nBut the translated predicates were:");
		for (SMTPredicateSymbol predicateSymbol : predicates) {
			sb.append("\n");
			sb.append(predicateSymbol.toString());
		}
		sb.append("\n");
		return sb;
	}

	private StringBuilder typeEnvironmentSortsFail(Set<String> expectedSorts,
			Set<SMTSortSymbol> sorts) {
		StringBuilder sb = new StringBuilder();
		sb.append("The translated sorts wasn't the expected ones. The expected sorts are:");
		for (String expectedSort : expectedSorts) {
			sb.append("\n");
			sb.append(expectedSort);
		}
		sb.append("\nBut the translated sorts were:");
		for (SMTSortSymbol sortSymbol : sorts) {
			sb.append("\n");
			sb.append(sortSymbol.toString());
		}
		sb.append("\n");
		return sb;
	}

	public void testTypeEnvironmentFunctions(Set<String> expectedFunctions) {
		expectedFunctions.add("(~ Int Int)");
		expectedFunctions.add("(- Int Int Int)");
		expectedFunctions.add("(* Int Int)");
		expectedFunctions.add("(+ Int Int)");
		expectedFunctions.add("(pair 's 't (Pair 's 't))");
		Set<SMTFunctionSymbol> functionSymbols = signature.getFuns();
		Iterator<SMTFunctionSymbol> iterator = functionSymbols.iterator();
		StringBuilder sb = typeEnvironmentFunctionsFail(expectedFunctions,
				functionSymbols);
		assertEquals(sb.toString(), expectedFunctions.size(),
				functionSymbols.size());

		while (iterator.hasNext()) {
			assertTrue(sb.toString(),
					expectedFunctions.contains(iterator.next().toString()));
		}
	}

	public void testTypeEnvironmentSorts(Set<String> expectedSorts) {
		expectedSorts.add("(Pair 's 't)");
		expectedSorts.add("Int");

		Set<SMTSortSymbol> sortSymbols = signature.getSorts();
		Iterator<SMTSortSymbol> iterator = sortSymbols.iterator();
		StringBuilder sb = typeEnvironmentSortsFail(expectedSorts, sortSymbols);
		assertEquals(sb.toString(), expectedSorts.size(), sortSymbols.size());

		while (iterator.hasNext()) {
			assertTrue(sb.toString(),
					expectedSorts.contains(iterator.next().toString()));
		}
	}

	public void testTypeEnvironmentPredicates(Set<String> expectedPredicates) {
		expectedPredicates.add("(>= Int Int)");
		expectedPredicates.add("(> Int Int)");
		expectedPredicates.add("(= Int Int)");
		expectedPredicates.add("(< Int Int)");
		expectedPredicates.add("(<= Int Int)");

		Set<SMTPredicateSymbol> predicateSymbols = signature.getPreds();
		Iterator<SMTPredicateSymbol> iterator = predicateSymbols.iterator();
		StringBuilder sb = typeEnvironmentPredicatesFail(expectedPredicates,
				predicateSymbols);
		assertEquals(sb.toString(), expectedPredicates.size(),
				predicateSymbols.size());

		while (iterator.hasNext()) {
			assertTrue(sb.toString(),
					expectedPredicates.contains(iterator.next().toString()));
		}
	}

	private static final String translationMessage(final Predicate ppPred,
			final String smtNode) {
		final StringBuilder sb = new StringBuilder();
		sb.append("\'");
		sb.append(ppPred.toString());
		sb.append("\' was translated in \'");
		sb.append(smtNode);
		sb.append("\'");
		return sb.toString();
	}

	@Test
	public void testTypeEnvironmentFunctionSimpleTe() {
		setSignatureForTests(simpleTe);
		Set<String> expectedFunctions = new HashSet<String>();

		expectedFunctions.add("(g S)");

		testTypeEnvironmentFunctions(expectedFunctions);
	}

	/**
	 * Testing rule 7
	 */
	@Test
	public void testTypeEnvironmenSortSimpleTe() {
		setSignatureForTests(simpleTe);
		Set<String> expectedSorts = new HashSet<String>();

		expectedSorts.add("S");

		testTypeEnvironmentSorts(expectedSorts);
	}

	/*
	 * The purpose of this test is to show that sets of sets are not supported.
	 */
	@Test
	public void testTypeEnvironmenSortErrorTe() {
		setSignatureForTests(errorTe);
		Set<String> expectedSorts = new HashSet<String>();

		expectedSorts
				.add("AZ (Something. The purpose of this test is to show that sets of sets are not supported.");

		testTypeEnvironmentSorts(expectedSorts);
	}

	/**
	 * Testing rules 4, 5 and 6
	 */
	@Test
	public void testTypeEnvironmentPredicateSimpleTe() {
		setSignatureForTests(simpleTe);
		Set<String> expectedPredicates = new HashSet<String>();

		expectedPredicates.add("(e S)");
		expectedPredicates.add("(f S)");
		expectedPredicates.add("(AB (Pair Int Int))");
		expectedPredicates.add("(C (Pair Int (Pair Int Int)))");

		testTypeEnvironmentPredicates(expectedPredicates);
	}

	/**
	 * Sets of sets are not supported yet.
	 */
	@Test
	public void testTypeEnvironmentPredicateDefaultTe() {
		setSignatureForTests(defaultTe);
		Set<String> expectedPredicates = new HashSet<String>();

		expectedPredicates.add("(r R)");
		expectedPredicates.add("(s R)");
		expectedPredicates.add("(A Int)");
		expectedPredicates.add("(AB (Pair Int Int))");

		testTypeEnvironmentPredicates(expectedPredicates);
	}

	/**
	 * Testing rule 8
	 */
	@Test
	public void testTypeEnvironmentFunctionDefaultTe() {
		setSignatureForTests(defaultTe);
		Set<String> expectedFunctions = new HashSet<String>();

		expectedFunctions.add("(p S)");
		expectedFunctions.add("(q S)");
		expectedFunctions.add("(a Int)");
		expectedFunctions.add("(b Int)");
		expectedFunctions.add("(c Int)");
		expectedFunctions.add("(u Bool)");
		expectedFunctions.add("(v Bool)");
		expectedFunctions.add("(pair 's 't (Pair 's 't))");

		testTypeEnvironmentFunctions(expectedFunctions);
	}

	/**
	 * "pred-ass"
	 */
	@Test
	public void testPredAssop() {

		testTranslationV1_2Default("(u = v)", "(iff u v)");

		/**
		 * land
		 */
		testTranslationV1_2Default("(a = b) ∧ (u = v)",
				"(and (= a b) (iff u v))");
		/**
		 * land (multiple predicates)
		 */
		testTranslationV1_2Default("(a = b) ∧ (u = v) ∧ (r = s)",
				"(and (= a b) (iff u v) (= r s))");
		/**
		 * lor
		 */
		testTranslationV1_2Default("(a = b) ∨ (u = v)",
				"(or (= a b) (iff u v))");
		/**
		 * lor (multiple predicates)
		 */
		testTranslationV1_2Default("(a = b) ∨ (u = v) ∨ (r = s)",
				"(or (= a b) (iff u v) (= r s))");
	}

	/**
	 * "pred-boolequ with constants only"
	 */
	@Test
	public void testPredBoolEquCnst() {
		testTranslationV1_2Default("u = v", "(iff u v)");

	}

	/**
	 * "pred-boolequ"
	 */
	@Test
	public void testPredBoolEqu() {
		testTranslationV1_2Default("u = TRUE", "(= u TRUE)");
		testTranslationV1_2Default("TRUE = u", "(= TRUE u)");
	}

	/**
	 * "pred-bin" in ppTrans abstract syntax
	 */
	@Test
	public void testPredBinop() {
		/**
		 * limp
		 */
		testTranslationV1_2Default("(a < b ∧ b < c) ⇒ a < c",
				"(implies (and (< a b) (< b c)) (< a c))");
		/**
		 * leqv
		 */
		testTranslationV1_2Default("(a ≤ b ∧ b ≤ a) ⇔ a = b",
				"(iff (and (<= a b) (<= b a)) (= a b))");
	}

	/**
	 * "pred-una" Testing rule 14:
	 */
	@Test
	public void testPredUna() {
		testTranslationV1_2Default("¬ ((a ≤ b ∧ b ≤ c) ⇒ a < c)",
				"(not (implies (and (<= a b) (<= b c)) (< a c)))");
	}

	/**
	 * "pred-lit"
	 */
	@Test
	public void testPredLit() {
		/**
		 * btrue
		 */
		testTranslationV1_2Default("⊤", "true");
		/**
		 * bfalse
		 */
		testTranslationV1_2Default("⊥", "false");
	}

	/**
	 * "pred-rel"
	 */
	@Test
	public void testPredRelop() {
		/**
		 * equal (identifiers of type ℤ)
		 */
		testTranslationV1_2Default("a = b", "(= a b)");
		/**
		 * equal (integer numbers)
		 */
		testTranslationV1_2Default("42 = 42", "(= 42 42)");
		/**
		 * notequal
		 */
		testTranslationV1_2Default("a ≠ b", "(neq a b))");
		/**
		 * lt
		 */
		testTranslationV1_2Default("a < b", "(< a b)");
		/**
		 * le
		 */
		testTranslationV1_2Default("a ≤ b", "(<= a b)");
		/**
		 * gt
		 */
		testTranslationV1_2Default("a > b", "(> a b)");
		/**
		 * ge
		 */
		testTranslationV1_2Default("a ≥ b", "(>= a b)");
	}

	/**
	 * Arithmetic expressions binary operations: cf. "a-expr-bin"
	 */
	@Test
	public void testArithExprBinop() {
		/**
		 * minus
		 */
		testTranslationV1_2Default("a − b = c", "(= (- a b) c)");
		/**
		 * equal (a-expr-bin)
		 */
		testTranslationV1_2Default("a − b = a − c", "(= (- a b) (- a c))");
	}

	@Test
	public void testArithExprBinopExponentialUnsupported() { // TODO Add
																// exponential
																// binop
		/**
		 * expn
		 */
		testTranslationV1_2Default("a ^ b = c", "(= (^ a b) c)");
		/**
		 * div
		 */
		testTranslationV1_2Default("a ÷ b = c", "(= (/ a b) c)");
		/**
		 * mod
		 */
		testTranslationV1_2Default("a mod b = c", "(= (% a b) c)");
	}

	@Test
	public void testArithExprBinopUnsupported() {
		/**
		 * div
		 */
		testTranslationV1_2Default("a ÷ b = c", "(= (/ a b) c)");
		/**
		 * mod
		 */
		testTranslationV1_2Default("a mod b = c", "(= (mod a b) c)");
	}

	/**
	 * Arithmetic expressions associative operations: cf. "a-expr-ass"
	 */
	@Test
	public void testArithExprAssnop() {
		/**
		 * plus
		 */
		testTranslationV1_2Default("a + c + b = a + b + c",
				"(= (+ a c b) (+ a b c))");
		/**
		 * mul
		 */
		testTranslationV1_2Default("a ∗ b ∗ c = a ∗ c ∗ b",
				"(= (* a b c) (* a c b))");
	}

	@Test
	public void testAssociativeExpression() {

		testTranslationV1_2Default("s = {}", "(= s emptyset)");
		// testTranslationV1_2Default("r = \u2205\u2982ℙ(R)", "(= r emptyset");

	}

	/**
	 * Arithmetic expressions unary operations: cf. "a-expr-una"
	 */
	@Test
	public void testArithExprUnop() {
		/**
		 * uminus (right child)
		 */
		testTranslationV1_2Default("a = −b", "(= a (~ b))");
		/**
		 * uminus (left child)
		 */
		testTranslationV1_2Default("−a = b", "(= (~ a) b)");
	}

	/**
	 * "pred-in" This test should not happen with ppTrans; The
	 */

	@Test
	public void testPredIn() {
		testTranslationV1_2Default("a ∈ A", "(in a A)");
		testTranslationV1_2Default("a↦b ∈ AB", "(in (pair a b) AB)");
	}

	/**
	 * "pred-setequ"
	 */
	@Test
	public void testPredSetEqu() {
		testTranslationV1_2Default("r = s", "(= r s)");
	}

	/**
	 * "pred-identequ"
	 */
	@Test
	public void testPredIdentEqu() {
		testTranslationV1_2Default("p = q", "(= p q)");
	}

	@Test
	public void testRule14() {

		/**
		 * inverse(SMT)/converse(EventB)
		 */
		testTranslationV1_2Default("AB = (AB)∼", "(= AB (inv AB))");
		/**
		 * not
		 */
		testTranslationV1_2Default("\u00ac(p = q)", "(not (= p q))");

		/**
		 * uminus
		 */
		testTranslationV1_2Default("a = (−b)", "(= a (~ b))");

		/**
		 * id
		 */
		testTranslationV1_2Default("AB = id", "(= AB id)");

		/**
		 * dom
		 */
		testTranslationV1_2Default("a ∈ dom(AB)", "(in a (dom AB))");

		/**
		 * ran
		 */
		testTranslationV1_2Default("b ∈ ran(AB)", "(in b (ran AB))");

	}

	@Test
	public void testExistsRule17() {
		/**
		 * exists
		 */
		testTranslationV1_2Default("∃x·x∈s", "(exists (?x R) (in ?x s))");
		/**
		 * exists (multiple identifiers)
		 */
		testTranslationV1_2Default("∃x,y·x∈s∧y∈s",
				"(exists (?x R) (?y R) (and (in ?x s) (in ?y s)))");
	}

	@Test
	public void testForallRule17() {
		/**
		 * forall
		 */
		testTranslationV1_2Default("∀x·x∈s", "(forall (?x R) (in ?x s))");
		/**
		 * forall (multiple identifiers)
		 */
		testTranslationV1_2Default("∀x,y·x∈s∧y∈s",
				"(forall (?x R) (?y R) (and (in ?x s) (in ?y s)))");
		/**
		 * forall (multiple identifiers)
		 */
		final QuantifiedPredicate base = (QuantifiedPredicate) parse(
				"∀x,y·x∈s ∧ y∈s", defaultTe);
		final BoundIdentDecl[] bids = base.getBoundIdentDecls();
		bids[1] = bids[0];
		final Predicate p = ff.makeQuantifiedPredicate(FORALL, bids,
				base.getPredicate(), null);
		// System.out.println("Predicate " + p);
		testTranslationV1_2(p,
				"(forall (?x R) (?x_0 R) (and (in ?x s) (in ?x_0 s)))",
				"twice same decl");
	}

	@Test
	public void testRule16() {
		testTranslationV1_2Default(
				"((A ∩ A) ⊂ (A ∪ A)) ∧ (a + b + c = b) ∧  (a ∗ b ∗ c = 0)",
				"(and (subset (inter A A) (union A A)) (= (+ a b c) b) (= (* a b c) 0))");

		testTranslationV1_2Default(
				"((A ∩ A) ⊂ (A ∪ A)) ∨ (a + b + c = b) ∨  (a ∗ b ∗ c = 0)",
				"(or (subset (inter A A) (union A A)) (= (+ a b c) b) (= (* a b c) 0))");
	}

	@Test
	public void testRule15SetMinusUnionInter() {

		testTranslationV1_2Default("(A ∖ A) ⊂ (A ∪ A)",
				"(subset (setminus A A) (union A A))");

		testTranslationV1_2Default("(A ∩ A) ⊂ (A ∪ A)",
				"(subset (inter A A) (union A A))");

	}

	@Test
	public void testRule15() {

		/**
		 * ∈ , ⊆ , ⊂
		 */
		testTranslationV1_2Default("(a ∈ A) ∧ (A ⊆ A)",
				"(and (in a A) (subseteq A A))");

		/**
		 * < , > , ⇒ , =
		 */
		testTranslationV1_2Default("(a < b ∧ b > c) ⇒ a = c",
				"(implies (and (< a b) (> b c)) (= a c))");

		/**
		 * ≤, ∧ , ≥ , ⇔
		 */
		testTranslationV1_2Default("(a ≤ b ∧ b ≥ c) ⇔ (a ÷ b) < (c mod b)",
				"(iff (and (<= a b) (>= b c)) (< (/ a b) (mod c b)))");

	}

	@Test
	public void testRule15Functions() {

		testTranslationV1_2Default("AB ∈ (A↔A)", "(in AB (rel A A))");

		testTranslationV1_2Default("AB ∈ (A→A)", "(in AB (tfun A A))");

		testTranslationV1_2Default("AB ∈ (A⇸A)", "(in AB (pfun A A))");

		testTranslationV1_2Default("AB ∈ (A↣A)", "(in AB (tinj A A))");

		testTranslationV1_2Default("AB ∈ (A⤔A)", "(in AB (pinj A A))");

		testTranslationV1_2Default("AB ∈ (A↠A)", "(in AB (tsur A A))");

		testTranslationV1_2Default("AB ∈ (A⤀A)", "(in AB (psur A A))");

		testTranslationV1_2Default("AB ∈ (A⤖A)", "(in AB (bij A A))");
	}

	@Test
	public void testRule15RelationOverridingCompANdCP() {
		/**
		 * relation overriding
		 */
		testTranslationV1_2Default("(AB \ue103 AB) = (AB \ue103 AB)",
				"(= (ovr AB AB) (ovr AB AB))");

		testTranslationV1_2Default("(AB \u003b AB) = (AB \u003b AB)",
				"(= (fcomp AB AB) (fcomp AB AB))");
	}

	@Test
	public void testRule15CartesianProductAndIntegerRange() {

		testTranslationV1_2Default("(A × A) = (A × A)",
				"(= (prod A A) (prod A A))");

		testTranslationV1_2Default("(a ‥ a) = (a ‥ a)",
				"(= (range a a) (range a a))");
	}

	@Test
	public void testRule15RestrictionsAndSubstractions() {

		testTranslationV1_2Default("(A ◁ AB) = (A ◁ AB)",
				"(= (domr A AB) (domr A AB))");

		testTranslationV1_2Default("(A ⩤ AB) = (A ⩤ AB)",
				"(= (doms A AB) (doms A AB))");

		testTranslationV1_2Default("(AB ▷ A) = (AB ▷ A)",
				"(= (ranr AB A) (ranr AB A))");

		testTranslationV1_2Default("(AB ⩥ A) = (AB ⩥ A)",
				"(= (rans AB A) (rans AB A))");
	}

	@Test
	public void testRule18() {

		testTranslationV1_2Default("{a∗b∣a+b ≥ 0} = {a∗a∣a ≥ 0}",
				"(= cset_0 cset_1)");

		testTranslationV1_2Default("{a∣a ≥ 0} = A", "(= cset_0 A)");
	}

	@Test
	public void testRule19() {
		testTranslationV1_2Default("{0 ↦ 1,1 ↦ 2} = {0 ↦ 1,1 ↦ 2}",
				"(= enum_0 enum_1)");

		testTranslationV1_2Default("{0,1,2,3,4} = A", "(= enum_0 A)");
	}

	@Test
	public void testRule20() {

		testTranslationV1_2Default("(λx·x>0 ∣ x+x) = ∅", "(= cset_0 emptyset)");
	}

	@Test
	public void testRule21() {
		// FIXME
		testTranslationV1_2Default("bool(⊤) ∈ BOOL", "(= cset_0 emptyset)");
	}

	@Test
	public void testRule22and23() {

		testTranslationV1_2Default("min({2,3}) = min({2,3})",
				"(= ismin_var ismin_var_0)");

		testTranslationV1_2Default("max({2,3}) = max({2,3})",
				"(= ismax_var ismax_var_0)");
	}

	@Test
	public void testRule24() {
		testTranslationV1_2Default("finite({1,2,3})", "finite_p");
	}

	@Test
	public void testRule25() {
		testTranslationV1_2Default("card({1,2,3}) = card({1,2,3})",
				"(= card_k card_k_0)");
	}

}
