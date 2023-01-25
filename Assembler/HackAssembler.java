import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class HackAssembler {

    public  static boolean ignoreLine(String line) {
        return line.replaceAll(" ", "").equals("") || line.startsWith("//");
    }


    public static void main(String[] args) {
        File f = new File(args[0]);
        try {
        
        Parser asmParser = new Parser(f);
        SymbolTable symbolTable = new SymbolTable();
        Code CInst = new Code();
        char nl = '\n';
        int linecount = 0;
        int startingVariableCount = 16;
        while(asmParser.hasMoreLines()) { // first loop
            asmParser.advance();
            if(ignoreLine(asmParser.getInstruction())) continue;
            linecount++;
            if(asmParser.instructionType() == "L_INSTRUCTION") {
                linecount--;
                symbolTable.addEntry(asmParser.symbol(), linecount);
            }
        }
        asmParser.close();
        asmParser = new Parser(f); // restarting
        FileWriter writer = new FileWriter(f.getAbsolutePath().replaceAll(".asm", ".hack"));
        writer.flush();
        while(asmParser.hasMoreLines()) {
            asmParser.advance();
            if(ignoreLine(asmParser.getInstruction())) continue;
            System.out.println("Inst " + asmParser.getInstruction());
            if(asmParser.instructionType() == "A_INSTRUCTION") {
                if(!symbolTable.contains(asmParser.symbol())) {
                    try {
                        symbolTable.addEntry(asmParser.symbol(), Integer.parseInt(asmParser.symbol()));
                    } catch(NumberFormatException e) {
                        symbolTable.addEntry(asmParser.symbol(), startingVariableCount);
                        startingVariableCount++;
                    }
                }
                writer.write("" + Integer.toBinaryString(0x10000 | symbolTable.getAddress(asmParser.symbol())).substring(1) + '\n');
            } else if(asmParser.instructionType() == "C_INSTRUCTION") {
                    StringBuilder sb = new StringBuilder();
                    sb.append("111");
                    sb.append(CInst.comp(asmParser.comp()));
                    sb.append(CInst.dest(asmParser.dest()));
                    sb.append(CInst.jump(asmParser.jump()));
                    sb.append('\n');
                    String s = sb.toString();
                    writer.write(s);
            }
        }
        writer.close();
        asmParser.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
