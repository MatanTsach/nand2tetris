import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    BufferedReader reader;
    String instruction;
    List<String> commands;
    
    public Parser(File f) throws IOException {
        FileReader fr = new FileReader(f);
        this.reader = new BufferedReader(fr);
        this.commands = new ArrayList<String>();

        this.commands.add("add"); 
        this.commands.add("sub"); 
        this.commands.add("neg"); 
        this.commands.add("eq"); 
        this.commands.add("gt"); 
        this.commands.add("lt"); 
        this.commands.add("and"); 
        this.commands.add("or"); 
        this.commands.add("not"); 
        this.commands.add("push"); 
        this.commands.add("pop"); 
        

    }

    public boolean hasMoreLines() throws IOException {
        return reader.ready();
    }  
    
    public void advance() throws IOException {
        String line = this.reader.readLine();
        if(line.contains("/")) {
            line = line.substring(0, line.indexOf("/", 0));
        }
        this.instruction = line;
    } 
    public String commandType() {
        if(this.instruction.contains("push")) return "C_PUSH";
        else if(this.instruction.contains("pop")) return "C_POP";

        return "C_ARITHMETIC";
    }

    public String arg1() {
        String[] splitted = this.instruction.split(" ");
        if(commandType().equals("C_ARITHMETIC")) {
            return splitted[0];
        }
        return splitted[1];
    }

    public String arg2() {
        return this.instruction.split(" ")[2];
    }

    public void close() throws IOException {
        this.reader.close();
    }



}
