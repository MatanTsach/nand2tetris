import java.io.File;
import java.io.IOException;

public class VMTranslator {

    public  static boolean ignoreLine(String line) {
        return line.replaceAll(" ", "").equals("") || line.startsWith("//");
    }

    public static void main(String[] args) {
        File f = new File(args[0]);
        try {
            Parser vmParser = new Parser(f);
            CodeWriter writer = new CodeWriter(f);

            while(vmParser.hasMoreLines()) {
                vmParser.advance();
                if(ignoreLine(vmParser.instruction)) continue;

                if(vmParser.commandType().equals("C_PUSH")) {
                    writer.WritePushPop("push", vmParser.arg1(), Integer.parseInt(vmParser.arg2()));
                } else if(vmParser.commandType().equals("C_POP")) {
                    writer.WritePushPop("pop", vmParser.arg1(), Integer.parseInt(vmParser.arg2()));
                } else {
                    writer.writeArithmetic(vmParser.arg1());
                }
            }

            vmParser.close();
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}