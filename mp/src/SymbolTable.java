import java.util.HashSet;
import java.util.LinkedList;

public class SymbolTable {
	private LinkedList<HashSet<Symbol>> symbolTables;
	
	SymbolTable() {
		symbolTables = new LinkedList<HashSet<Symbol>>();
	}
	
	public void insertSymbol(Symbol s) {
		
	}
	
	public void findSymbol(String name) {
		
	}
	
	public void createSymbolTable() {
		symbolTables.add(0, new HashSet<Symbol>());
	}
	
	public boolean deleteSymbolTable() {
		if (symbolTables.remove() == null)
			return false;
		else
			return true;
	}
}