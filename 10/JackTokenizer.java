import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class JackTokenizer {
    BufferedReader reader;
    String currentToken;
    List<String> keywords;
    List<Character> symbols;

    /**
     * Opens the input stream and gets ready to tokenize it.
     * @param file
     * @throws IOException
     */
    public JackTokenizer(File file) throws IOException {
        FileReader fr = new FileReader(file);
        this.reader = new BufferedReader(fr);
        this.keywords = Arrays.asList("class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return");
        this.symbols = Arrays.asList('{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~');
    }   
    
    /**
     * Are there more tokens in the input?
     * @return true if there are more tokens, false otherwise
     * @throws IOException
     */
    public boolean hasMoreTokens() throws IOException {
        return !this.currentToken.equals("") && this.reader.ready();
    }
    /**
     * Reads the next token(token = text until space) from the input and makes it the current token.
     * @throws IOException
     */
    public void advance() throws IOException {
        this.currentToken = "";
        boolean inSlashComment = false;
        boolean inAsterickComment = false;
        boolean inString = false;
        int nextChar = this.reader.read();
        while(nextChar != -1) {
            if(inSlashComment || inAsterickComment) {
                if(nextChar == '\n') {
                    inSlashComment = false;
                } else if(nextChar == '*') {
                    if(this.reader.ready()) {
                        int nextNextChar = this.reader.read();
                        if(nextNextChar == '/') {
                            inAsterickComment = false;
                        }
                    }
                } 
            } else if(inString) {
                this.currentToken += (char)nextChar;
                if(nextChar == '"') {
                    inString = false;
                    break;
                }
            } else if(nextChar == '/' && this.reader.ready()) {
                this.reader.mark(1);
                int nextNextChar = this.reader.read();
                if(nextNextChar == '/') {
                    inSlashComment = true;
                } else if(nextNextChar == '*') {
                    inAsterickComment = true;
                } else {
                    this.reader.reset();
                    this.currentToken += (char)nextChar;
                    break;
                }
            } else if(nextChar == '"') {
                inString = true;
                this.currentToken += (char)nextChar;
            } else if(nextChar == ' ' || nextChar == '\n' || nextChar == '\t' || nextChar == '\r') {
                if(this.currentToken.length() > 0) {
                    break;
                }
            } else if(this.symbols.contains((char)nextChar)) {
                if(this.currentToken.length() > 0) {
                    this.reader.reset();
                } else {
                    this.currentToken += (char)nextChar;
                }
                break;
            } else {
                this.currentToken += (char)nextChar;
            }
            this.reader.mark(1);
            nextChar = this.reader.read();
        }
    }
    /**
     * Returns the type of the current token.
     * @return the type of the current token
     */
    public String tokenType() {
        if(this.keywords.contains(currentToken)) {
            return "keyword";
        } else if(this.symbols.contains(currentToken.charAt(0))) {
            return "symbol";
        } else if(currentToken.matches("^[0-9]*$")) {
            return "integerConstant";
        } else if(currentToken.matches("\".*\"")) {
            return "stringConstant";
        } else {
            return "identifier";
        }
    }

    /**
     * Returns the keyword which is the current token.
     * @return the keyword which is the current token
     */
    public String keyWord() {
        return this.currentToken;
    }

    /**
     * Returns the character which is the current token.
     * @return the character which is the current token
     */
    public char symbol() {
        return this.currentToken.charAt(0);
    }

    /**
     * Returns the identifier which is the current token.
     * @return the identifier which is the current token
     */
    public String identifier() {
        return this.currentToken;
    }

    /**
     * Returns the integer value of the current token.
     * @return
     */
    public int intVal() {
        return Integer.parseUnsignedInt(this.currentToken);
    }

    /**
     * Returns the string value of the current token, without the double quotes.
     * @return the string value of the current token, without the double quotes
     */
    public String stringVal() {
        if(this.currentToken.startsWith("\"")) {
            return this.currentToken.substring(1, this.currentToken.length() - 1);
        } else if(this.currentToken.equals("<")) {
            return "&lt;";
        } else if(this.currentToken.equals(">")) {
            return "&gt;";
        } else if(this.currentToken.equals("\"")) {
            return "&quot;";
        } else if(this.currentToken.equals("&")) {
            return "&amp;";
        }
        return this.currentToken;
    }
}