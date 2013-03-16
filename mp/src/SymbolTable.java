import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class SymbolTable {
	private LinkedList<SubSymbolTable<String,Symbol>> symbolTables;
	static private Symbol mostRecentFunctionOrParameter;
	
	SymbolTable() {
		symbolTables = new LinkedList<SubSymbolTable<String,Symbol>>();
	}
	
	public void insertSymbol(Symbol s) throws SymbolAlreadyExistsException {
		try {
			if (symbolTables.getFirst().get(s.lexeme) != null) {
				throw new SymbolAlreadyExistsException("Error: Symbol '" + s.lexeme + "' already exists in the current scope");
			}
			symbolTables.getFirst().put(s.lexeme, s);
			// If adding a parameter, also add it to the parameter list of the
			// appropriate function or procedure.
			if (s.kind == Symbol.Kind.FUNCTION || s.kind == Symbol.Kind.PROCEDURE)
				mostRecentFunctionOrParameter = s;
			if (s.kind == Symbol.Kind.PARAMETER)
				mostRecentFunctionOrParameter.parameters.add(s);
		} catch (NoSuchElementException e) {
			System.err.println("Error: No symbol table to insert into. Try createSymbolTable()");
			System.exit(1);
		}
	}
	
	public Symbol findSymbol(String name) {
		for (SubSymbolTable<String,Symbol> st : symbolTables) {
			Symbol s = st.get(name);
			if (s != null)
				return s;
		}
		return null;
	}
	
	public void createSymbolTable(String name) {
		symbolTables.add(0, new SubSymbolTable<String,Symbol>(name));
	}
	
	public void deleteSymbolTable() {
		try {
			symbolTables.remove();
		} catch (NoSuchElementException e) {
			System.err.println("Error: Attempted to remove a symbol table but there are none. :(");
			System.exit(1);
		}
	}
	
	public String toString() {
		String s = ""; // Note: s it built in reverse order.
		if (symbolTables.size() == 0) {
			s += "  <no symbol tables>";
		} else {
			for (SubSymbolTable<String,Symbol> st : symbolTables) {
				String s2 = "  " + st.name + ":\n";
				if (st.size() == 0) {
					s2 += "  <empty>\n";
				} else {
					for (Symbol symbol : st.values()) {
						s2 += "    " + symbol.toString() + "\n";
					}
				}
				s = s2 + s;
			}
		}
		s = "Symbol Tables:\n" + s;
		return s;
	}
	
	@SuppressWarnings("serial")
	private class SubSymbolTable<K,V> extends HashMap<K,V> {
		public String name;
		SubSymbolTable(String n) {
			super();
			name = n;
		}
	}
	
	@SuppressWarnings("serial")
	public class SymbolAlreadyExistsException extends Exception {
		SymbolAlreadyExistsException(String s) {
			super(s);
		}
	}
}