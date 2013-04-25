import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Symbol {
	public enum Kind {
		VARIABLE, PARAMETER, PROCEDURE, FUNCTION, MAIN
	}
	public enum Type {
		NONE(0), INTEGER(1), FLOAT(1), STRING(1), BOOLEAN(1);
		int size;
		Type(int s) { size = s; }
	}
	public enum ParameterMode {
		NONE, COPY, REFERENCE
	}

	public String lexeme;
	public Kind kind;
	public Type type;
	public ParameterMode mode;	
	public List<Symbol> parameters;
	private Iterator<Symbol> parameterIterator = null;
	public int size, offset, variableOffset, nestLevel;	// for semantics
	public boolean negative = false;	
	public String label1, label2;
	
	public Symbol() {/*for semantic records */}
	
	public Symbol(String l, Kind k, Type t)
	{
		lexeme = l;
		kind = k;
		type = t;
		if (k == Kind.PROCEDURE || k == Kind.FUNCTION)
			parameters = new ArrayList<Symbol>();
		size = t.size;
	}
	
	/**
	 * Used for matching number of actual parameters with number of formal parameters
	 * @return
	 */
	public boolean hasNextParameter() { return parameterIterator.hasNext(); }
	public void resetParameterIterator() { parameterIterator = null; }
	
	/**
	 * Used for type checking actual vs formal parameters and checking for too many actual
	 * parameters supplied
	 * @return
	 */
	public Symbol getNextParameter() {
		if(parameterIterator == null) 
			parameterIterator = parameters.iterator();
		if(hasNextParameter())
			return parameterIterator.next();
		return null;
	}
	
	/**
	 * For Symbols that represent functions or procedures this totals the offset needed for 
	 * parameters (and thus variables) in the activation record
	 * @return
	 */
	public int getParameterOffset() {
		int funcOrProcParamOffset = 0;
		if(parameters != null) {
			for(Symbol s : parameters)
				funcOrProcParamOffset += s.size;// total offset for parameters, sort of trivial 
			return funcOrProcParamOffset;		// since each param is size 1, could call parameters.size()
		}
		return 0;
	}
	
	/**
	 * Returns total activation record size for functions/procedure/main 
	 * activation record setup
	 * @return
	 */
	public int getActivationRecordSize() {
		// activation rec size is param offset total + var offset total + display register space
		int arSize = getParameterOffset() + variableOffset + Type.INTEGER.size;
		if(kind == Kind.FUNCTION || kind == Kind.PROCEDURE)
			arSize += Type.INTEGER.size ; 	// save space for return addr when not main's activation rec
		return arSize;
	}

	public String toString() {
		return lexeme + " - kind: " + kind + ", type: " + type;
	}
}