import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Parser {
    private String instruction;
    private BufferedReader reader;

    public Parser(File f) {
        try {
            FileReader rf = new FileReader(f);
            reader = new BufferedReader(rf);
        } catch(FileNotFoundException e) {
            System.out.println("Error: File not found");
            System.out.println(f.getAbsolutePath());
        }
    }

    public boolean hasMoreLines() throws IOException {
       return this.reader.ready();
    }

    public void close() throws IOException {
        this.reader.close();
    }

    public String getInstruction() {
        return this.instruction;
    }

    public void advance() throws IOException {
        String line = this.reader.readLine();
        if(line.contains("/")) {
            line = line.substring(0, line.indexOf("/", 0));
        }
        line = line.replaceAll(" ", "");
        this.instruction = line;
    }

    public String instructionType() {
        if(instruction.startsWith("@")) return "A_INSTRUCTION";
        if(instruction.startsWith("(")) return "L_INSTRUCTION";

        return "C_INSTRUCTION";
    }

    public String symbol() {
        return this.instruction.replaceAll("[@()]", "");
    }

    public String dest() {
        if(!this.instruction.contains("=")) {
            return "Null";
        }
        return this.instruction.substring(0, this.instruction.indexOf("="));
    }

    public String comp() {
        int Bindex = this.instruction.indexOf(";");
        int Aindex = this.instruction.indexOf("=");
        
        return Bindex == -1 ? this.instruction.substring(Aindex+1) : this.instruction.substring(Aindex+1, Bindex);
    }

    public String jump() {
        int Bindex = this.instruction.indexOf(";");

        return Bindex == -1 ? "Null" : this.instruction.substring(Bindex+1);
    }
}
