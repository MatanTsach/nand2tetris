import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CodeWriter {
    FileWriter writer;
    Map<String, String> segmentPointers;
    int jmpcounter = 0;

    public CodeWriter(File f) throws IOException {
        this.writer = new FileWriter(f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".")) + ".asm");
        segmentPointers = new HashMap<String,String>();

        segmentPointers.put("local", "LCL");
        segmentPointers.put("argument", "ARG");
        segmentPointers.put("this", "THIS");
        segmentPointers.put("that", "THAT");
        segmentPointers.put("static", "16");
        segmentPointers.put("temp", "5");
    }

    public void WritePushPop(String cmd, String segment, int index) throws IOException {
        System.out.println(cmd + " " + segment + " " + index);
        if(cmd.equals("push")) {
            if(segment.equals("constant")) {
                writeLine("@" + index);
                writeLine("D=A");
                writeLine("@SP");
                writeLine("A=M");
                writeLine("M=D");
            } else if(segment.equals("pointer")) {
                if(index == 0) {
                    writeLine("@THIS");
                } else {
                    writeLine("@THAT");
                }
                writeLine("D=M");
                writeLine("@SP");
                writeLine("A=M");
                writeLine("M=D");
            } else {
                writeLine("@" + this.segmentPointers.get(segment));
                if(segment.equals("static") || segment.equals("temp")) {
                    writeLine("D=A");
                } else {
                    writeLine("D=M");
                }
                writeLine("@" + index);
                writeLine("D=D+A");
                writeLine("A=D");
                writeLine("D=M");
                writeLine("@SP");
                writeLine("A=M");
                writeLine("M=D");
            }
            // SP++
            writeLine("@SP");
            writeLine("M=M+1");

        } else { // POP

            if(segment.equals("pointer")) {
                writeLine("@SP");
                writeLine("AM=M-1");
                writeLine("D=M");

                if(index == 0) {
                    writeLine("@THIS");
                } else {
                    writeLine("@THAT");
                }

                writeLine("M=D");
            } else {

                writeLine("@SP"); 
                writeLine("M=M-1"); // dec by one

                writeLine("@" + this.segmentPointers.get(segment));
                if(segment.equals("static") || segment.equals("temp")) {
                    writeLine("D=A");
                } else {
                    writeLine("D=M");
                }
                writeLine("@" + index);
                writeLine("D=D+A");
                writeLine("@R13");
                writeLine("M=D"); // saving in R13 the val of addr
                writeLine("@SP");
                writeLine("A=M");
                writeLine("D=M"); // D now has the value inside RAM[SP--]
                writeLine("@R13");
                writeLine("A=M");
                writeLine("M=D"); // COMPLETE
            }
        }
    }

    public void writeArithmetic(String cmd) throws IOException {
        if(cmd.equals("neg")) {
            writeLine("D=0");
            writeLine("@SP");
            writeLine("A=M-1");
            writeLine("M=D-M");
            return;
        } else if(cmd.equals("not")) {
            writeLine("@SP");
            writeLine("A=M-1");
            writeLine("M=!M");
            return;
        }

        writeLine("@SP");
        writeLine("AM=M-1");
        writeLine("D=M");
        writeLine("@SP");
        writeLine("A=M-1");

        if(cmd.equals("add")) {
            writeLine("M=M+D"); // x + y
        } else if(cmd.equals("sub")) {
            writeLine("M=M-D"); // x - y
        } else if(cmd.equals("and")) {
            writeLine("M=D&M");
        } else if(cmd.equals("or")) {
            writeLine("M=D|M");
        } else {
            writeLine("D=M-D"); // x - y
            writeLine("M=-1"); // inserting -1, now jumping
            writeLine("@" + cmd.toUpperCase() + this.jmpcounter);
            writeLine("D;" + getJumpOperation(cmd));
            writeLine("@SP");
            writeLine("A=M-1"); 
            writeLine("M=0");
            writeLine("(" + cmd.toUpperCase() + this.jmpcounter + ")");
            this.jmpcounter++;
        }
    }

    public void close() throws IOException {
        this.writer.close();
    }
    private String getJumpOperation(String cmd) {
        if(cmd.equals("eq")) return "JEQ";
        if(cmd.equals("lt")) return "JLT";

        return "JGT"; // gt
    }

    private void writeLine(String line) throws IOException {
        this.writer.write(line + '\n');
    }


}
