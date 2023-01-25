import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    
    private Map<String, Integer> symbolTable;
    public SymbolTable() {
        symbolTable = createPredefinedSymbols();
    }

    private  Map<String, Integer> createPredefinedSymbols() {
        Map<String, Integer> ret = new HashMap<String, Integer>();

        for(int i = 0; i <= 15; i++) { // inserting these
            ret.put("R" + i, i);
        }

        ret.put("SCREEN", 16384);
        ret.put("KBD", 24576);
        ret.put("SP", 0);
        ret.put("LCL", 1);
        ret.put("ARG", 2);
        ret.put("THIS", 3);
        ret.put("THAT", 4);

        return ret;
    }

    public void addEntry(String symbol, int address) {
        this.symbolTable.put(symbol, address);
    }

    public boolean contains(String symbol) {
        return this.symbolTable.containsKey(symbol);
    }

    public int getAddress(String symbol) {
        return this.symbolTable.get(symbol);
    }
}
