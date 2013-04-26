import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class SymbolTable {
	private LinkedList<SubSymbolTable<String,Symbol>> symbolTables;
	static private Symbol mostRecentFunctionOrParameter;
	private int currentOffset, currentNestLevel;	// for recording offset/nest level for symbols
	
	SymbolTable() {
		symbolTables = new LinkedList<SubSymbolTable<String,Symbol>>();
		currentOffset = Symbol.Type.INTEGER.size;	// save space on stack for display register
		currentNestLevel = 0;
	}
	
	public int getCurrentOffset() { return currentOffset; }
	public void incrementOffset() { currentOffset += Symbol.Type.INTEGER.size; }
	
	public void insertSymbol(Symbol s) throws SymbolAlreadyExistsException {
		try {
			if (symbolTables.getFirst().get(s.lexeme) != null) {
				throw new SymbolAlreadyExistsException("Error: Symbol '" + s.lexeme + "' already exists in the current scope");
			}
			s.nestLevel = currentNestLevel;			// record nesting level
			s.offset = this.currentOffset;			// record offset
			currentOffset += s.size;				// update current offset by symbol size
			symbolTables.getFirst().put(s.lexeme.toLowerCase(), s);
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
			Symbol s = st.get(name.toLowerCase());
			if (s != null)
				return s;
		}
		return null;
	}
	
	public void createSymbolTable(String name) {
		if(symbolTables.size() > 0) {
			symbolTables.getFirst().lastOffset = currentOffset;	// save offset for previous table
			currentNestLevel++;	// increase nest level
		}
		symbolTables.add(0, new SubSymbolTable<String,Symbol>(name));
		currentOffset = Symbol.Type.INTEGER.size;	// reset offset, save space for display register
	}
	
	public void deleteSymbolTable() {
		try {
			symbolTables.remove();
			// It seems to me as though symbols cannot be added to parent tables after
			// a new scope is created and then removed, but just in case:
			currentNestLevel--;	// adjust nest level
			if(symbolTables.size() > 0)
				currentOffset = symbolTables.getFirst().lastOffset;	// reset offset
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
		public int lastOffset;
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