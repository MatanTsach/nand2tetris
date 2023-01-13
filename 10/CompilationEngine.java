import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {
    JackTokenizer tokenizer;
    FileWriter writer;

    public CompilationEngine(File input, File output) throws IOException {
        tokenizer = new JackTokenizer(input);
        writer = new FileWriter(output);
    }

    public void compileClass() throws IOException {
        tokenizer.advance();
        writer.write("<class>\n");
        process("class");
        writer.write("<identifier> " + tokenizer.currentToken + " </identifier>\n");
        tokenizer.advance();
        process("{");
        while(tokenizer.currentToken.equals("static") || tokenizer.currentToken.equals("field")) {
            compileClassVarDec();
        }
        while(tokenizer.currentToken.equals("constructor") || tokenizer.currentToken.equals("function") || tokenizer.currentToken.equals("method")) {
            compileSubroutine();
        }
        process("}");
        writer.write("</class>\n");
    }

    public void compileClassVarDec() throws IOException {
        writer.write("<classVarDec>\n");
        while(!(tokenizer.currentToken.equals(";"))) {
            process();
        }
        process();
        writer.write("</classVarDec>\n");
    }

    public void compileSubroutine() throws IOException {
        writer.write("<subroutineDec>\n");

        process();
        process();
        process();
        process("(");

        compileParameterList(); // handles the parameter list

        process(")");
        compileSubroutineBody();
        writer.write("</subroutineDec>\n");
    }

    public void compileParameterList() throws IOException {
        writer.write("<parameterList>\n");
        while(!(tokenizer.currentToken.equals(")"))) {
            process();
        }
        writer.write("</parameterList>\n");
    }

    public void compileSubroutineBody() throws IOException {
        writer.write("<subroutineBody>\n");
        process("{");
        while(tokenizer.currentToken.equals("var")) {
            compileVarDec();
        }
        compileStatements();
        process("}");
        writer.write("</subroutineBody>\n");
    }

    public void compileVarDec() throws IOException {
        writer.write("<varDec>\n");
        process("var");
        while(!(tokenizer.currentToken.equals(";"))) {
            process();
        }
        process();
        writer.write("</varDec>\n");
    }

    public void compileStatements() throws IOException {
        writer.write("<statements>\n");
        while(!(tokenizer.currentToken.equals("}"))) {
            if(tokenizer.currentToken.equals("let")) {
                compileLet();
            } else if(tokenizer.currentToken.equals("if")) {
                compileIf();
            } else if(tokenizer.currentToken.equals("while")) {
                compileWhile();
            } else if(tokenizer.currentToken.equals("do")) {
                compileDo();
            } else {
                compileReturn();
            }
        }
        writer.write("</statements>\n");
    }


    public void compileLet() throws IOException {
        writer.write("<letStatement>\n");
        process("let");
        while(!(tokenizer.currentToken.equals("="))) {
            process();
            if(tokenizer.currentToken.equals("[")) {
                process("[");
                compileExpression();
                process("]");
            }
        }
        process("=");
        compileExpression();
        process(";");
        writer.write("</letStatement>\n");
    }

    public void compileIf() throws IOException {
        writer.write("<ifStatement>\n");
        process("if");
        process("(");
        compileExpression();
        process(")");
        process("{");
        compileStatements();
        process("}");
        if(tokenizer.currentToken.equals("else")) { // handling else
            process("else");
            process("{");
            compileStatements();
            process("}");
        }
        writer.write("</ifStatement>\n");
    }

    public void compileWhile() throws IOException {
        writer.write("<whileStatement>\n");
        process("while");
        process("(");
        compileExpression();
        process(")");
        process("{");
        compileStatements();
        process("}");
        writer.write("</whileStatement>\n");
    }

    public void compileDo() throws IOException {
        writer.write("<doStatement>\n");
        process("do");
        while(!(tokenizer.currentToken.equals("("))) {
            process();
        }
        process("(");
        compileExpressionList();
        process(")");
        process(";");
        writer.write("</doStatement>\n");
    }

    public void compileReturn() throws IOException {
        writer.write("<returnStatement>\n");
        process("return");
        if(!tokenizer.currentToken.equals(";")) {
            compileExpression();
        }
        process();
        writer.write("</returnStatement>\n");
    }

    public void compileExpression() throws IOException {
        writer.write("<expression>\n");
        while(!(tokenizer.currentToken.equals(";") || tokenizer.currentToken.equals(")") || tokenizer.currentToken.equals(",") || tokenizer.currentToken.equals("]"))) {
            if(tokenizer.tokenType().equals("symbol") && !tokenizer.currentToken.equals("(") && !tokenizer.currentToken.equals("~")) {
                process();
            } else {
                compileTerm();
            }
        }
        writer.write("</expression>\n");
    }

    public void compileTerm() throws IOException {
        writer.write("<term>\n");
        if(tokenizer.currentToken.equals("~")) {
            process("~");
            compileTerm();
        } else if(tokenizer.currentToken.equals("(")) {
            process("(");
            compileExpression();
            process(")");
        } else {
            process();
            if(tokenizer.currentToken.equals(".")) {
                process(".");
                process(); // new or other subroutine
                process("(");
                compileExpressionList();
                process(")");
            } else if(tokenizer.currentToken.equals("[")) {
                process("[");
                compileExpression();
                process("]");
            }
        }
        writer.write("</term>\n");
    }

    public void compileExpressionList() throws IOException {
        writer.write("<expressionList>\n");
        while(!(tokenizer.currentToken.equalsIgnoreCase(")"))) {
            compileExpression();
            if(tokenizer.currentToken.equalsIgnoreCase(",")) {
                process();
            }
        }
        writer.write("</expressionList>\n");
    }

    private void process(String input) throws IOException {
        if(input.equals(tokenizer.currentToken)) {
            writer.write("<" + tokenizer.tokenType() + ">");
            writer.write(tokenizer.stringVal());
            writer.write("</" + tokenizer.tokenType() + ">\n");
        } else {
            System.out.println("Syntax Error: " + tokenizer.currentToken + " instead of: " + input);
        }
        tokenizer.advance();
    }

    public void close() throws IOException {
        this.writer.close();
    }

    private void process() throws IOException {
        writer.write("<" + tokenizer.tokenType() + ">");
        writer.write(tokenizer.stringVal());
        writer.write("</" + tokenizer.tokenType() + ">\n");
        tokenizer.advance();
    }

    private boolean isOp() {
        return tokenizer.currentToken.equals("<") || tokenizer.currentToken.equals(">") || tokenizer.currentToken.equals("=")
                            || tokenizer.currentToken.equals("+") || tokenizer.currentToken.equals("-");
    }
}
