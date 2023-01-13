import java.io.File;
import java.io.IOException;

public class JackAnalyzer {
    public static void main(String[] args) {
        try {
            File in = new File(args[0]);
            File out;
            CompilationEngine engine;
            if(in.isDirectory()) {
                for(File f : in.listFiles()) {
                    if(f.getName().endsWith(".jack")) {
                        out = new File(f.getAbsolutePath().replace(".jack", ".xml"));
                        engine = new CompilationEngine(f, out);
                        engine.compileClass();
                        engine.close();
                    }
                }
            } else {
                out = new File(in.getAbsolutePath().replace(".jack", "A.xml"));
                engine = new CompilationEngine(in, out);
                engine.compileClass();
                engine.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
