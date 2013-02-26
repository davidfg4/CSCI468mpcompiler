import java.util.HashSet;
import java.util.LinkedList;

public class symbolTable {
	private LinkedList<HashSet<symbol>> symbolTables;
	
	symbolTable() {
		symbolTables = new LinkedList<HashSet<symbol>>();
	}
	
	public void insertSymbol(symbol s) {
		
	}
	
	public void findSymbol(String name) {
		
	}
	
	public void createSymbolTable() {
		symbolTables.add(0, new HashSet<symbol>());
	}
	
	public boolean deleteSymbolTable() {
		if (symbolTables.remove() == null)
			return false;
		else
			return true;
	}
}