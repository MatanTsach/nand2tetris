import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SymbolTable {
    // create 4 lists for name(string), type(string), kind(string), and index(int)
    private List<String> name;
    private List<String> type;
    private List<String> kind;
    private List<Integer> index;

    public SymbolTable() {
        name = new ArrayList<String>();
        type = new ArrayList<String>();
        kind = new ArrayList<String>();
        index = new ArrayList<Integer>();
    }

    /**
     * Reset all the lists.
     */
    public void reset() {
        name.clear();
        type.clear();
        kind.clear();
        index.clear();
    }

    /**
     * Add an item to the list
     * 
     * @param name
     * @param type
     * @param kind
     */
    public void define(String name, String type, String kind) {
        // add the name, type, kind, and index to the lists
        this.name.add(name);
        this.type.add(type);
        this.kind.add(kind);
        this.index.add(varCount(kind));
    }

    /**
     * Returns the number of variables of the given kind in the list
     * 
     * @param kind
     * @return number of variables.
     */
    public int varCount(String kind) {
        int count = -1;
        for (int i = 0; i < this.kind.size(); i++) {
            if (this.kind.get(i).equals(kind)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the kind of the names identifier, if not exists returns NONE
     * 
     * @param name
     * @return the kind, or NONE
     */
    public String kindOf(String name) {
        if(this.name.contains(name)) {
            return this.kind.get(this.name.indexOf(name)).equals("field") ? "this" : this.kind.get(this.name.indexOf(name));
        }
        return "NONE";
    }

    /**
     * Returns the type of the names identifier, if not exists returns NONE
     * 
     * @param name
     * @return the type, or NONE
     */
    public String typeOf(String name) {
        return this.name.contains(name) ? this.type.get(this.name.indexOf(name)) : "NONE";
    }

    /**
     * Returns the index of the names identifier, if not exists returns NONE
     * 
     * @param name
     * @return the index, or -1
     */
    public int indexOf(String name) {
        return this.name.contains(name) ? this.index.get(this.name.indexOf(name)) : -1;
    }

    public int tableSize() {
        return this.name.size();
    }

    public void debug() {
        for(int i = 0; i < this.name.size(); i++) {
            System.out.println("DEBUG " + this.name.get(i) + " " + this.type.get(i) + this.kind.get(i));
        }
    }

}
