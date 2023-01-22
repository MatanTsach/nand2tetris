import java.io.File;
import java.io.IOException;

class CompilationEngine {
    VMWriter writer;
    JackTokenizer tokenizer;
    SymbolTable classTable;
    SymbolTable subroutineTable;
    String className;
    int generatedLabels;

    public CompilationEngine(File f) throws IOException {
        writer = new VMWriter(f);
        tokenizer = new JackTokenizer(f);
        classTable = new SymbolTable();
        subroutineTable = new SymbolTable();
        generatedLabels = 0;
    }

    public void compileClass() throws IOException {
        tokenizer.advance(); // class
        tokenizer.advance(); // class name
        className = tokenizer.stringVal();
        tokenizer.advance(); // {
        tokenizer.advance(); // starting variable values
        
        while(tokenizer.currentToken.equals("static") || tokenizer.currentToken.equals("field")) {
            compileClassVarDec();
        }
        while(tokenizer.currentToken.equals("constructor") || tokenizer.currentToken.equals("function") || tokenizer.currentToken.equals("method")) {
            compileSubroutine();
        }
        tokenizer.advance(); // }
    }

    private void compileClassVarDec() throws IOException {
        String type = "";
        String kind = "";

        while(!(tokenizer.currentToken.equals(";"))) {
            if(kind.isEmpty()) {
                kind = tokenizer.stringVal();
            } else if(type.isEmpty()) {
                type = tokenizer.stringVal();
            } else if(tokenizer.stringVal().equals(",")) {
                tokenizer.advance();
                continue;
            } else {
                classTable.define(tokenizer.stringVal(), type, kind);
            }
            tokenizer.advance();
        }
        tokenizer.advance(); // to continue to the next
    }

    private void compileSubroutine() throws IOException {
        subroutineTable.reset();
        String subroutineType = tokenizer.stringVal(); // constructor, method or function
        tokenizer.advance();
        tokenizer.advance();
        String subroutineName = tokenizer.stringVal(); // name
        tokenizer.advance();
        tokenizer.advance();
        if(subroutineType.equals("method")) {
            subroutineTable.define("this", className, "argument");
        }
        compileParameterList();
        tokenizer.advance();
        tokenizer.advance();
        int nVars = 0;
        while(tokenizer.currentToken.equals("var")) {
            nVars += compileVarDec();
            
        }
        writer.writeFunction(className + "." + subroutineName, nVars);
        if(subroutineType.equals("method")) {
            writer.writePush("argument", 0);
            writer.writePop("pointer", 0); // slide 67
        } else if(subroutineType.equals("constructor")) {
            writer.writePush("constant", classTable.varCount("field")+1);
            writer.writeCall("Memory.alloc", 1); // slide 67
            writer.writePop("pointer", 0);
        }
        compileSubroutineBody();
    }

    private void compileParameterList() throws IOException {
        String type = "";
        String kind = "argument";
        String name = "";
        while(!(tokenizer.stringVal().equals(")"))) {
            if(type.isEmpty()) {
                type = tokenizer.stringVal();
                tokenizer.advance();
            } else if(name.isEmpty()) {
                name = tokenizer.currentToken;
                tokenizer.advance();
            } else if(tokenizer.currentToken.equals(",")) {
                subroutineTable.define(name, type, kind);
                type = "";
                name = "";
                tokenizer.advance();
            }
        }
        if(!name.isEmpty()) {
            subroutineTable.define(name, type, kind);
        }
    }

    private void compileSubroutineBody() throws IOException {
        compileStatements();
        tokenizer.advance(); // }
    }

    private int compileVarDec() throws IOException {
        tokenizer.advance(); // var
        String type = "";
        String kind = "local";
        int count = 0;
        while(!(tokenizer.currentToken.equals(";"))) {
            if(type.isEmpty()) {
                type = tokenizer.stringVal();
            } else if(tokenizer.stringVal().equals(",")) {
                count++;
                tokenizer.advance();
                continue;
            } else {
                subroutineTable.define(tokenizer.stringVal(), type, kind);
            }
            tokenizer.advance();
        }
        count++;
        tokenizer.advance(); // ;
        return count;
    }

    private void compileStatements() throws IOException {
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
    }

    private void compileLet() throws IOException {
        tokenizer.advance(); // let
        String letName = tokenizer.stringVal();
        tokenizer.advance(); // name
        if(tokenizer.currentToken.equals("[")) {
            handleArray(letName);
            //tokenizer.advance(); // ]
            tokenizer.advance(); // =
            compileExpression();
            writer.writePop("temp", 0);
            writer.writePop("pointer", 1);
            writer.writePush("temp", 0);
            writer.writePop("that", 0);
        } else {
            tokenizer.advance(); // =
            compileExpression();
            pushPopVariable(letName, false);
        }
        if(tokenizer.currentToken.equalsIgnoreCase("]")) {
            tokenizer.advance();
        }
        tokenizer.advance(); // ;
    }

    
    private void compileExpression() throws IOException {
        compileTerm(tokenizer.currentToken); // compiling the first term
        char currentOp = 0;
        while(!(tokenizer.currentToken.equals(";") || tokenizer.currentToken.equals(")") || tokenizer.currentToken.equals(",") || tokenizer.currentToken.equals("]"))) {
            if(tokenizer.tokenType().equals("symbol") && !tokenizer.currentToken.equals("(") && !tokenizer.currentToken.equals("~")) {
                currentOp = tokenizer.symbol();
                tokenizer.advance();
            } else { // op
                compileTerm(tokenizer.stringVal());
                if(!(currentOp == 0)) {
                    writer.writeArithmetic(currentOp, false);
                    currentOp = 0;
                }
            }
        }
    }
    

    private void compileTerm(String term) throws IOException {
        String strVal = tokenizer.stringVal();
        tokenizer.advance();

        if(term.startsWith("\"")) {
            writer.writePush("constant", strVal.length());
            writer.writeCall("String.new", 1);
            for(int i = 0; i < strVal.length(); i++) {
                writer.writePush("constant", strVal.charAt(i));
                writer.writeCall("String.appendChar", 2);
            }
        } else if(isConstant(term)) {
            writer.writePush("constant", Integer.parseInt(term));
        } else if(isVariable(term)  && !tokenizer.stringVal().equals("(") && !tokenizer.stringVal().equals("[") && !tokenizer.stringVal().equals(".")) {
            System.out.println(term + " is a variable!");

            if(term.equals("this")) {
                writer.writePush("pointer", 0);
            } else if(classTable.indexOf(term) >= 0) {
                writer.writePush(classTable.kindOf(term), classTable.indexOf(term));
            } else if(subroutineTable.indexOf(term) >= 0) {
                writer.writePush(subroutineTable.kindOf(term), subroutineTable.indexOf(term));
            } else if(term.equals("true")) {
                writer.writePush("constant", 1);
                writer.writeArithmetic('-', true);
            } else if(term.equals("false") || term.equals("null")) {
                writer.writePush("constant", 0);
            }
        } else if(isUnaryTerm(term)) {
            char unary = term.charAt(0);
            compileTerm(tokenizer.stringVal());
            writer.writeArithmetic(unary, true);
        } else if(term.startsWith("(")) {
            compileExpression();
            tokenizer.advance(); // ) will be advanced later in compileExpression
        } else {
            String firstPart = term;
            if(tokenizer.currentToken.equals(".")) {
                int nArgs = 0;
                tokenizer.advance(); // process(".");
                String subRoutineCall = tokenizer.stringVal();
                if(classTable.indexOf(firstPart) >= 0) {
                    writer.writePush(classTable.kindOf(firstPart), classTable.indexOf(firstPart));
                    firstPart = classTable.typeOf(firstPart);
                    nArgs++;
                }
                if(subroutineTable.indexOf(firstPart) >= 0) {
                    writer.writePush(subroutineTable.kindOf(firstPart), subroutineTable.indexOf(firstPart));
                    firstPart = subroutineTable.typeOf(firstPart);
                    nArgs++;
                }
                tokenizer.advance(); // process subRoutineCall
                tokenizer.advance(); // process (
                nArgs += compileExpressionList();
                writer.writeCall(firstPart + "." + subRoutineCall, nArgs); // fishy
            } else if(tokenizer.currentToken.equals("[")) {
                handleArray(firstPart);
                writer.writePop("pointer", 1);
                writer.writePush("that", 0);
            } else { // (
                tokenizer.advance(); // process (
                writer.writePush("pointer", 0);
                int nArgs = compileExpressionList();
                writer.writeCall(className + "." + firstPart, nArgs+1); // for method shit
            }
        }
    }

    private void handleArray(String arr) throws IOException {
        tokenizer.advance(); // [
        pushPopVariable(arr, true);
        compileExpression();
        writer.writeArithmetic('+', false);
        tokenizer.advance(); // ]
    }

    private void pushPopVariable(String var, boolean push) throws IOException {
        SymbolTable useTable;
        if(classTable.indexOf(var) >= 0) {
            useTable = classTable;
        } else {
            useTable = subroutineTable;
        }

        if(push) {
            writer.writePush(useTable.kindOf(var), useTable.indexOf(var));
        } else {
            writer.writePop(useTable.kindOf(var), useTable.indexOf(var));
        }
    }

    private int compileExpressionList() throws IOException {
        int counter = 0;
        while(!(tokenizer.currentToken.equalsIgnoreCase(")"))) {
            counter++;
            compileExpression();
            if(tokenizer.currentToken.equalsIgnoreCase(",")) {
                tokenizer.advance();
            }
        }
        tokenizer.advance();
        return counter;
    }

    private void compileIf() throws IOException {
        tokenizer.advance(); // process("if");
        tokenizer.advance(); // process("(");
        compileExpression();
        tokenizer.advance(); // process(")");
        tokenizer.advance(); // process("{");
        writer.writeArithmetic('~', true); // NOT
        int currentGenerated = generatedLabels;
        generatedLabels += 2;
        writer.writeIf("L" + currentGenerated);
        compileStatements();
        tokenizer.advance(); // process("}");
        writer.writeGoto("L" + (currentGenerated+1));
        writer.writeLabel("L" + currentGenerated);
        if(tokenizer.currentToken.equals("else")) { // handling else
            tokenizer.advance(); // process("else");
            tokenizer.advance(); //  process("{");
            compileStatements();
            tokenizer.advance(); // process("}");
        }
        writer.writeLabel("L" + (currentGenerated+1));
        generatedLabels++;
    }

    private void compileWhile() throws IOException {
        tokenizer.advance(); // process("while");
        tokenizer.advance(); //  process("(");
        int currentGenerated = generatedLabels;
        generatedLabels += 2;
        writer.writeLabel("L" + currentGenerated);
        compileExpression();
        writer.writeArithmetic('~', true); // NOT
        writer.writeIf("L" + (currentGenerated+1));

        tokenizer.advance(); // process(")");
        tokenizer.advance(); //  process("{");
        compileStatements();
        writer.writeGoto("L" + currentGenerated);
        writer.writeLabel("L" + (currentGenerated+1));
        tokenizer.advance(); // process("}");
    }

    private void compileDo() throws IOException {
        tokenizer.advance(); // process("do");
        compileExpression();
        tokenizer.advance(); // process(";");
        writer.writePop("temp", 0);
    }

    private void compileReturn() throws IOException {
        tokenizer.advance(); // 
        if(!tokenizer.currentToken.equals(";")) {
            compileExpression();
        } else {
            writer.writePush("constant", 0);
        }
        tokenizer.advance(); // ;
        writer.writeReturn();
    }

    private boolean isConstant(String term) {
        try {
            return Integer.parseInt(term) >= 0;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    private boolean isVariable(String term) {
        return subroutineTable.indexOf(term) >= 0 || classTable.indexOf(term) >= 0 || term.equals("true") || term.equals("false") || term.equals("null") || term.equals("this");
    }

    private boolean isUnaryTerm(String term) {
        return term.charAt(0) == '-' || term.charAt(0) == '~';
    }

    public void close() throws IOException {
        this.writer.close();
    }
}
