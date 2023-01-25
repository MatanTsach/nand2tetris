import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VMWriter {
    FileWriter writer;
    Map<Character, String> opTranslation;
    Map<Character, String> unaryOpTranslation;

    public VMWriter(File f) throws IOException {
        this.writer = new FileWriter(f.getAbsolutePath().replace(".jack", ".vm"));
        opTranslation = new HashMap<Character, String>();
        opTranslation.put('<', "lt");
        opTranslation.put('>', "gt");
        opTranslation.put('=', "eq");
        opTranslation.put('+', "add");
        opTranslation.put('-', "sub");
        opTranslation.put('<', "lt");
        opTranslation.put('*', "call Math.multiply 2");
        opTranslation.put('/', "call Math.divide 2");
        opTranslation.put('&', "and");
        opTranslation.put('|', "or");

        unaryOpTranslation = new HashMap<Character, String>();
        unaryOpTranslation.put('~', "not");
        unaryOpTranslation.put('-', "neg");

    }

    public void writePush(String segment, int index) throws IOException {
        writer.write("push " + segment + " " + index + "\n");
    }

    public void writePop(String segment, int index) throws IOException {
        writer.write("pop " + segment + " " + index + "\n");
    }

    public void writeArithmetic(char command, boolean unary) throws IOException {
        if(!unary) {
            writer.write(opTranslation.get(command) + '\n');
        } else {
            writer.write(unaryOpTranslation.get(command) + '\n');
        }
    }

    public void writeLabel(String label) throws IOException {
        writer.write("label " + label + "\n");
    }

    public void writeGoto(String label) throws IOException {
        writer.write("goto " + label + "\n");
    }

    public void writeIf(String label) throws IOException {
        writer.write("if-goto " + label + "\n");
    }

    public void writeCall(String name, int nArgs) throws IOException {
        writer.write("call " + name + " " + nArgs + "\n");
    }

    public void writeFunction(String name, int nVars) throws IOException {
        writer.write("function " + name + " " + nVars + "\n");
    }

    public void writeReturn() throws IOException {
        writer.write("return" + "\n");
    }

    public void close() throws IOException {
        this.writer.close();
    }

}
