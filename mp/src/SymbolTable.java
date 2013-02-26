import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class SymbolTable {
	private LinkedList<SubSymbolTable<String,Symbol>> symbolTables;
	
	SymbolTable() {
		symbolTables = new LinkedList<SubSymbolTable<String,Symbol>>();
	}
	
	public void insertSymbol(Symbol s) {
		try {
			symbolTables.getFirst().put(s.lexeme, s);
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
	
	// For testing purposes only
	public static void main(String[] args) {
		SymbolTable testTable = new SymbolTable();
		testTable.createSymbolTable("main");
		testTable.insertSymbol(new Symbol("x"));
		testTable.insertSymbol(new Symbol("y"));
		testTable.insertSymbol(new Symbol("z"));
		testTable.createSymbolTable("fred");
		testTable.insertSymbol(new Symbol("a"));
		testTable.insertSymbol(new Symbol("b"));
		testTable.insertSymbol(new Symbol("c"));
		testTable.deleteSymbolTable();
		testTable.createSymbolTable("mary");
		testTable.insertSymbol(new Symbol("d"));
		testTable.insertSymbol(new Symbol("e"));
		testTable.insertSymbol(new Symbol("f"));
		System.out.println(testTable);
		System.out.println(testTable.findSymbol("d"));
		System.out.println(testTable.findSymbol("x"));
	}
	
	@SuppressWarnings("serial")
	private class SubSymbolTable<K,V> extends HashMap<K,V> {
		public String name;
		SubSymbolTable(String n) {
			super();
			name = n;
		}
	}
}