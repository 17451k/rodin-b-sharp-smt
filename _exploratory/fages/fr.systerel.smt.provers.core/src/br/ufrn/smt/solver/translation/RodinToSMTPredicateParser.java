package br.ufrn.smt.solver.translation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

public class RodinToSMTPredicateParser {

	private long minimalFiniteValue = 0;
	private long minimalEnumValue = 0;
	private long minimalElemvalue = 0;
	private ArrayList<Predicate> hypotheses;
	private Predicate goal;
	private String translatedPath;
	private String nameOfThisLemma;
	private SimpleSMTVisitor visitor;
	Hashtable<String, String> singleQuotVars = new Hashtable<String, String>();
	private Hashtable<String, String> funs = new Hashtable<String, String>();
	private Hashtable<String, String> preds = new Hashtable<String, String>();
	private ArrayList<String> macros = new ArrayList<String>();
	private ArrayList<String> sorts = new ArrayList<String>();
	private ArrayList<String> assumptions = new ArrayList<String>();
	private String smtGoal = "";
	
	private String errorMessageString = "";
	
	File translatedFile;
	
	
	public String getErrorMessageString() {
		return errorMessageString;
	}

	public void setErrorMessageString(String errorMessageString) {
		this.errorMessageString = errorMessageString;
	}

	public File getTranslatedFile() {
		return translatedFile;
	}

	public void setTranslatedFile(File translatedFile) {
		this.translatedFile = translatedFile;
	}

	public ArrayList<String> getNotImplementedOperation() {
		return notImplementedOperation;
	}

	public void setNotImplementedOperation(ArrayList<String> notImplementedOperation) {
		this.notImplementedOperation = notImplementedOperation;
	}

	public boolean isNecessaryAllMacros() {
		return isNecessaryAllMacros;
	}

	public void setNecessaryAllMacros(boolean isNecessaryAllMacros) {
		this.isNecessaryAllMacros = isNecessaryAllMacros;
	}

	public ArrayList<Predicate> getHypotheses() {
		return hypotheses;
	}

	public void setHypotheses(ArrayList<Predicate> hypotheses) {
		this.hypotheses = hypotheses;
	}

	ArrayList<String> notImplementedOperation = new ArrayList<String>();
	private boolean isNecessaryAllMacros = false;
	
	private void getDataFromVisitor(SimpleSMTVisitor smv)
	{
		//funs.putAll(smv.getFuns());
		funs = smv.getFuns();
		//sorts.addAll(smv.getSorts());
		sorts = smv.getSorts();
		preds = smv.getPreds();
		//assumptions.addAll(smv.getAssumptions());
		assumptions = smv.getAssumptions();
		//macros.addAll(smv.getMacros());
		macros = smv.getMacros();
		minimalElemvalue = smv.getMinimalElemvalue();
		minimalEnumValue = smv.getMinimalEnumValue();
		minimalFiniteValue = smv.getMinimalFiniteValue();
		if(smv.isNecessaryAllMacros() == true)
		{
			isNecessaryAllMacros = true;
		}		
	}
	
	public long getMinimalFiniteValue() {
		return minimalFiniteValue;
	}

	public void setMinimalFiniteValue(long minimalFiniteValue) {
		this.minimalFiniteValue = minimalFiniteValue;
	}

	public long getMinimalEnumValue() {
		return minimalEnumValue;
	}

	public void setMinimalEnumValue(long minimalEnumValue) {
		this.minimalEnumValue = minimalEnumValue;
	}

	public long getMinimalElemvalue() {
		return minimalElemvalue;
	}

	public void setMinimalElemvalue(long minimalElemvalue) {
		this.minimalElemvalue = minimalElemvalue;
	}

	public Hashtable<String, String> getSingleQuotVars() {
		return singleQuotVars;
	}

	public void setSingleQuotVars(Hashtable<String, String> singleQuotVars) {
		this.singleQuotVars = singleQuotVars;
	}
	
	
	public ArrayList<Predicate> getHypothesis() {
		return hypotheses;
	}

	public void setHypothesis(ArrayList<Predicate> hypothesis) {
		this.hypotheses = hypothesis;
	}

	public Predicate getGoal() {
		return goal;
	}

	public void setGoal(Predicate goal) {
		this.goal = goal;
	}

	public String getTranslatedPath() {
		return translatedPath;
	}

	public void setTranslatedPath(String translatedPath) {
		this.translatedPath = translatedPath;
	}

	public String getNameOfThisLemma() {
		return nameOfThisLemma;
	}

	public void setNameOfThisLemma(String nameOfThisLemma) {
		this.nameOfThisLemma = nameOfThisLemma;
	}

	public SimpleSMTVisitor getVisitor() {
		return visitor;
	}

	public void setVisitor(SimpleSMTVisitor visitor) {
		this.visitor = visitor;
	}

	public Hashtable<String, String> getFuns() {
		return funs;
	}

	public void setFuns(Hashtable<String, String> funs) {
		this.funs = funs;
	}

	public Hashtable<String, String> getPreds() {
		return preds;
	}

	public void setPreds(Hashtable<String, String> preds) {
		this.preds = preds;
	}

	public ArrayList<String> getMacros() {
		return macros;
	}

	public void setMacros(ArrayList<String> macros) {
		this.macros = macros;
	}

	public ArrayList<String> getSorts() {
		return sorts;
	}

	public void setSorts(ArrayList<String> sorts) {
		this.sorts = sorts;
	}

	public ArrayList<String> getAssumptions() {
		return assumptions;
	}

	public void setAssumptions(ArrayList<String> assumptions) {
		this.assumptions = assumptions;
	}

	public String getSmtGoal() {
		return smtGoal;
	}

	public void setSmtGoal(String smtGoal) {
		this.smtGoal = smtGoal;
	}

	public RodinToSMTPredicateParser(
			ArrayList<Predicate> hypotheses,
			Predicate goal
	) {
		
		this.hypotheses = hypotheses;
		this.goal = goal;
		getTypeEnvironment();
		parsePredicates();
	}
	
	private void getTypeEnvironment()
	{
		Hashtable<String, Type> typEnv = new Hashtable<String, Type>();
		FreeIdentifier[] freeVars = goal.getFreeIdentifiers();  //goalSimp.getFreeIdentifiers();
		if(freeVars != null)
		{
			
			for(int i = 0 ; i < freeVars.length ; i++)
			{
				String name = freeVars[i].getName();
				Type type = freeVars[i].getType();
				typEnv.put(name, type);//add(new Pair<String, String>(name, type));
			}
		}
		
		BoundIdentifier[] boundVars = goal.getBoundIdentifiers();     //goalSimp.getBoundIdentifiers();
		if(boundVars != null)
		{
			for(int i = 0 ; i < boundVars.length ; i++)
			{
				String name = boundVars[i].toString();
				Type type = boundVars[i].getType();
				typEnv.put(name,type);//(new Pair<String, String>(name, type));
			}
		}
		
		for(int j = 0 ; j < hypotheses.size() ; j++)   //smtHypsSimp.size() ; j++)
		{
			//hypothesis.add(hypotheses.get(j).toStringFullyParenthesized());
			freeVars = hypotheses.get(j).getFreeIdentifiers();
			if(freeVars != null)
			{
				for(int i = 0 ; i < freeVars.length ; i++)
				{
					String name = freeVars[i].getName();
					Type type = freeVars[i].getType();
					typEnv.put(name, type);//(new Pair<String, String>(name, type));
				}
			}
			
			boundVars = hypotheses.get(j).getBoundIdentifiers();
			if(boundVars != null)
			{
				for(int i = 0 ; i < boundVars.length ; i++)
				{
					String name = boundVars[i].toString();
					Type type = boundVars[i].getType();
					typEnv.put(name,type);//(new Pair<String, String>(name, type));
				}
			}			
		}
		parseTypeEnv(typEnv);	
	}
		
	public static String getSMTAtomicExpressionFormat(String atomicExpression) {
		if (atomicExpression.equals("\u2124")) // INTEGER
		{
			return "Int";
		} else if (atomicExpression.equals("\u2115")) // NATURAL
		{
			return "Nat";
		} else if (atomicExpression.equals("\u2124" + 1)) {
			return "Int1";
		} else if (atomicExpression.equals("\u2115" + 1)) {
			return "Nat1";
		} else if (atomicExpression.equals("BOOL")) {
			return "Bool";
		} else if (atomicExpression.equals("TRUE")) {
			return "True";
		} else if (atomicExpression.equals("FALSE")) {
			return "False";
		} else if (atomicExpression.equals("\u2205")) {
			return "emptyset";
		}
		return atomicExpression;
	}
	
	private String verifyQuotedVar(String name, Hashtable<String, Type> typenvironment)
	{
		if(name.lastIndexOf('\'') > 0)
		{
			int countofQuots = name.length() - name.lastIndexOf('\'');
			//String alternativeName = name.substring(0,name.lastIndexOf('\'')) + "_" + countofQuots;
			String alternativeName = name.replaceAll("'", "_" + countofQuots + "_");
			Type t = typenvironment.get(alternativeName);
			while(t != null)
			{
				++countofQuots;
				alternativeName = name + "_" + countofQuots;
				t = typenvironment.get(alternativeName);
			}
			this.singleQuotVars.put(name, alternativeName);
			return alternativeName;
		}
		return name;
	}
	
	private void parseTypeEnv(Hashtable<String, Type> typenvironment)
	{
		Set<Entry<String,Type>> typeVars = typenvironment.entrySet();
		Iterator<Entry<String,Type>> iterator = typeVars.iterator();
		while(iterator.hasNext())
		{
				
			Entry<String,Type> el = iterator.next();
			String varName = verifyQuotedVar(el.getKey(), typenvironment);			
			//String varName = el.getKey(); //lemmaData.getTypenv().get(i).getFirstElement();//((Element)variables.item(i)).getAttribute("name").trim();
			Type varType = el.getValue();//lemmaData.getTypenv().get(i).getSecondElement();//((Element)variables.item(i)).getAttribute("type").trim();
			//Regra 4
			if(varName.equals(varType.toString()))
			{
				sorts.add(varType.toString());
			}				
			//Regra 6
			else if(varType.getSource() != null)
			{				
				String pair = "(Pair " + getSMTAtomicExpressionFormat(varType.getSource().toString()) + " " + getSMTAtomicExpressionFormat(varType.getTarget().toString() + ")");
				preds.put(varName, pair);
			}
			else if(varType.getBaseType() != null)
			{
				if(varName.equals(varType.getBaseType().toString()))
				{
					sorts.add(varName);
				}
				else
				{
					preds.put(varName, getSMTAtomicExpressionFormat(varType.getBaseType().toString()));					
				}					
			}
			else
			{
				funs.put(varName,  getSMTAtomicExpressionFormat(varType.toString()));
			}				
		}
	}
	
	
	
	
	private void printLemmaOnFile()
	{		
		String benchmark = "(benchmark smtTesteComArvoreSintatica ";//nameOfThisLemma;
		final String FINAL_PATH = translatedPath + File.separator + nameOfThisLemma + ".smt";		
		try
		{
			String s = "";
			if(translatedPath != null)
			{
				s = translatedPath;
			}
			else
			{
				s = System.getProperty("user.home");
			}			
			translatedFile = new File(s + "/smtComArv.smt");
			if(!translatedFile.exists())
			{
				translatedFile.createNewFile();
			}						
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(translatedFile)));
			out.println(benchmark);
			out.println(":logic UNKNOWN");
			if(!sorts.isEmpty())
			{				
				String extrasorts = ":extrasorts (";
				for(int i = 0 ; i < sorts.size() ; i++)
				{
					extrasorts = extrasorts + " " + sorts.get(i);
				}
				extrasorts = extrasorts + ")";
				out.println(extrasorts);
			}
			
			if(!preds.isEmpty())
			{
				Set<Entry<String,String>> set = preds.entrySet();
				Iterator<Entry<String,String>> iterator = set.iterator();			
				String extrapreds = ":extrapreds (";
				while(iterator.hasNext())
				{
					Entry<String,String> el = iterator.next();
					extrapreds = extrapreds + "(" + el.getKey()  //preds.eget(i).getFirstElement() 
						+ " " + el.getValue() + ")"; // preds.get(i).getSecondElement() + ")";
				}
				extrapreds = extrapreds + ")";
				out.println(extrapreds);
			}
			
			
			if(!funs.isEmpty())
			{
				Set<Entry<String,String>> set = funs.entrySet();
				Iterator<Entry<String,String>> iterator = set.iterator();			
				String extrafuns = ":extrafuns (";
				while(iterator.hasNext())
				{
					Entry <String,String> el = iterator.next();
					extrafuns = extrafuns + "(" + el.getKey()  //preds.eget(i).getFirstElement() 
						+ " " + el.getValue() + ")"; // preds.get(i).getSecondElement() + ")";
				}
				extrafuns = extrafuns + ")";
				out.println(extrafuns);
			}
			
			
			
			out.println(":extramacros(");
			
			out.println("(union (lambda (?p1 ('t boolean)) (?q1 ('t boolean)) . (lambda (?x6 't) . (or (?p1 ?x6) (?q1 ?x6)))))");
			out.println("(emptyset (lambda (?x5 't). false))");
			out.println("(inter (lambda (?pd ('sd boolean))(?qd ('sd boolean)) . (lambda (?x7d 'sd) . (and (?pd ?x7d) (?qd ?x7d)))))");
			out.println("(setminus (lambda (?p2 ('t boolean)) (?q2 ('t boolean)) . (lambda (?x8 't) . (and (?p2 ?x8) (not (?q2 ?x8))))))");
			out.println("(in (lambda (?x9 't) (?p3 ('t boolean)) . (?p3 ?x9)))");
			out.println("(subseteq (lambda (?s ('t boolean)) (?h ('t boolean)) . (forall (?t 't). (implies (?s ?t) (?h ?t)))))");
			out.println("(subset (lambda (?p4 ('t boolean)) (?q3 ('t boolean)) . (and (subseteq ?p4 ?q3) 	(not (= ?p4 ?q3	)))))");
			out.println("(Nat (lambda (?i Int) . (<= 0 ?i)))");
			out.println("(ismax (lambda (?m Int) (?pi (Int boolean)) . (and (?pi ?m)(forall (?i1 Int) . (implies (?pi ?i1) (<= ?i1 ?m))))))");
			out.println("(ismin (lambda (?m2 Int) (?ta (Int boolean)) . (and(in ?m2 ?ta)(forall (?xb Int) . (implies (in ?xb ?ta)(<=?m2 ?xb))))))");
			out.println("(Nat1 (lambda (?i Int) . (<= 1 ?i)))");
			out.println("(cartesianproduct (lambda (?p12 ('t1 boolean)) (?q12 ('t2 boolean)) . (lambda (?x1 't1) (?x2 't2) . (and (?p12 ?x1) (?q12 ?x2)))))");			
			out.println("(range (lambda (?i1 Int) (?i2 Int) . (lambda (?i Int) . (and (<= ?i1 ?i) (<= ?i ?i2)))))");			
			out.println("(subseteq2 (lambda (?p11 ('t1 't2 boolean)) (?q ('t1 't2 boolean)) . (forall (?x1 't1) (?x2 't2) . (implies (?p11 ?x1 ?x2) (?q ?x1 ?x2)))))");
			out.println("(union2 (lambda (?p2c ('t1c 't2c boolean)) (?q2c ('t1c 't2c boolean)) . (lambda (?x1c 't1c) (?x2c 't2c) . (or (?p2c ?x1c ?x2c) (?q2c ?x1c ?x2c)))))");
			out.println("(emptyset2 (lambda (?x 't1) (?y 't2). false))");
			out.println("(inter2 (lambda (?p ('t1 't2 boolean)) (?q ('t1 't2 boolean)) . (lambda (?x 't1) (?y 't2) . (and (?p ?x ?y) (?q ?x ?y)))))");
			out.println("(Pair (lambda (?e1 't) (?e2 't) . (lambda (?f1 't) (?f2 't) . (and (= ?f1 ?e1) (= ?f2 ?e2)))))");			
			out.println("(finite (lambda (?tb ('s boolean)) (?pe boolean) (?f ('s Int)) (?k Int).(iff ?pe (and (forall (?xa 's).(implies (in ?xa ?tb)(in (?f ?xa)(range 1 ?k))))(forall (?xa 's)(?ya 's).(implies (and (in ?xa ?tb)(in ?ya ?tb)(not (= ?xa ?ya)))(not (= (?f ?xa)(?f ?ya)))))))))");
			out.println("(domain (lambda (?r ((Pair 't1 't2) boolean)) . (lambda (?x1 't1) . (exists (?x2 't2) . (?r (Pair ?x1 ?x2))))))");
			//Domain			
			if(isNecessaryAllMacros)
			{
				//range and relational image
				
				out.println("(ran (lambda (?r ((Pair 's 't) boolean) (lambda (?y 't)(exists (?x 's)(r (pair ?x ?y)))))))");
				out.println("(img (lambda (?r ((Pair 's 't) boolean)(?p ('s boolean) (lambda (?y 't) (exists (?x 's) (and (?p ?x)(r (pair ?x ?y)))))))))");
				
				//Todas essas macros estão feitas sem os pontos, inclusive a macro img acima
				//Domain restriction and subtraction, range restriction and subtraction
				out.println("(domr (lambda (?r ((Pair 's 't) boolean)(?s ('s boolean)) (lambda (?p (Pair 's 't)(and (?r ?p)(?s (fst ?p)))))))) ");
				out.println("(doms (lambda (?r ((Pair 's 't) boolean)(?s ('s boolean)) (lambda (?p (Pair 's 't)(and (?r ?p)(not (?s (fst ?p)))))))))");
				out.println("(ranr (lambda (?r ((Pair 's 't) boolean)(?s ('t boolean)) (lambda (?p (Pair 's 't)(and (?r ?p)(?s (snd ?p))))))))");
				out.println("(rans (lambda (?r ((Pair 's 't) boolean)(?s ('t boolean)) (lambda (?p (Pair 's 't)(and (?r ?p)(not (?s (snd ?p)))))))))");
				
				//Inverse, composition, overwrite and identidy
				out.println("(inv (lambda (?r ((Pair 's 't) boolean) (lambda (?p (Pair 's 't)(?r(pair (snd ?p)(fst ?p))))))))");
				out.println("(comp (lambda (?r1 ((Pair 's 't) boolean)(?r2 ((Pair 't 'u) boolean) (lambda (?p (Pair 's 'u)) (exists (?x 't)(and (?r1 (pair (fst ?p) ?x)) (?r2 (pair ?x (snd ?p))))))))))");
				out.println("(ovr (lambda (?r1 ((Pair 's 't) boolean)(?r2 ((Pair 's 't) boolean) (lambda (?p (Pair 's 'u))(or (?r2 ?p)(and (?r1 ?p)(not(exists(?q (Pair 's 't)) (and (?r2 ?q) (= (fst ?q)(fst ?p)))))))))))) ");
				out.println("(id (lambda (?s ('t boolean)) (lambda ?p (Pair 't 't)) (and (?s (fst ?p)) (= (fst ?p)(snd ?p)))))");
				
				//Auxiliary properties on relations
				out.println("(funp (lambda (?R ((Pair 's 't) boolean)) (forall (?p (Pair 's 't))(?p1 (Pair 's 't)) (implies (and (?R ?p)(?R ?p1))(implies (= (fst ?p)(fst ?p1))(= (snd ?p)(snd ?p1)))))))");
				out.println("(injp (lambda (?R ((Pair 's 't) boolean))(funp (inv ?R))))");
				out.println("(totp (lambda (?X ('s boolean))(?R((Pair 's 't) boolean)) (forall (?p (Pair 's 't)) (= (?R ?p)(?X (fst ?p))))))");
				out.println("(surp (lambda (?Y ('t boolean))(?R ((Pair 's 't) boolean)) (forall (?p (Pair 's 't)) (= (?R ?p)(?Y (snd ?p))))))");
				
				//Sets of relations, functions (partial/total, injective/surjective/bijective);
				out.println("(rel  (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean)) (forall (?p (Pair 's 't)) (implies (?R ?p)(and (?X (fst ?p))(?Y (snd ?p))))))))");
				out.println("(pfun (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean))(and ((rel ?X ?Y)  ?R) (funp ?R)))))");
				out.println("(tfun (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean))(and ((pfun ?X ?Y) ?R)(totp ?X ?R)))))");
				out.println("(pinj (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean))(and ((pfun ?X ?Y) ?R)(injp ?R)))))");
				out.println("(tinj (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean))(and ((pinj ?X ?Y) ?R)(totp ?X ?R)))))");
				out.println("(psur (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean))(and ((pfun ?X ?Y) ?R)(surp ?Y ?R)))))");
				out.println("(tsur (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean))(and ((psur ?X ?Y) ?R)(totp ?X ?R)))))");
				out.println("(bij  (lambda (?X ('s boolean))(?Y ('s boolean)) (lambda (?R ((Pair 's 't) boolean))(and ((tsur ?X ?Y) ?R)((tinj ?X ?Y) ?R)))))");
			}
			
			
			for(int i = 0 ; i < macros.size() ; i++)
			{
				out.println(macros.get(i));
			}			
			out.println(")");
			for(int i = 0 ; i < assumptions.size() ; i++)
			{
					out.println(":assumption " + assumptions.get(i));
			}
			out.println(":formula (not" + smtGoal + ")" );			
			out.println(")");
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	
	void parsePredicates()
	{
		SimpleSMTVisitor smv1 = null;
		if(!hypotheses.isEmpty())
		{
//			smv1 = new SimpleSMTVisitor(this);
//			hypotheses.get(0).accept(smv1);
//			this.getDataFromVisitor(smv1);
			for(int i = 0 ; i < hypotheses.size() ; i++ )
			{
				smv1 = new SimpleSMTVisitor(this);
				hypotheses.get(i).accept(smv1);
				assumptions.add(smv1.getSmtFormula());
				this.getDataFromVisitor(smv1);
			}
		}
		smv1 = new SimpleSMTVisitor(this);
		goal.accept(smv1);
		this.getDataFromVisitor(smv1);
		smtGoal = smv1.getSmtFormula();
		if(smv1.isNecessaryAllMacros())
		{
			this.isNecessaryAllMacros = true;
		}
		printLemmaOnFile();		
	}
	
	public static void main(String[] args)
	{
		
	}
	
	
	
}