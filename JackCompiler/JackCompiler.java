import java.io.File;
import java.io.IOException;

public class JackCompiler {
    public static void main(String[] args) {
        try {
            File in = new File(args[0]);
            CompilationEngine engine;

            if(in.isDirectory()) {
                for(File f : in.listFiles()) {
                    if(f.getName().endsWith(".jack")) {
                        engine = new CompilationEngine(f);
                        engine.compileClass();
                        engine.close();
                    }
                }
            } else {
                engine = new CompilationEngine(in);
                engine.compileClass();
                engine.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
