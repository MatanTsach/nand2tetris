import java.util.HashMap;
import java.util.Map;

public class Code {

    private Map<String, String> specifications;
    private Map<String, String> destSpecifications;
    private Map<String, String> jumpSpecifications;

    public Code() {
        specifications = new HashMap<String, String>();
        specifications.put("0", "0101010");
        specifications.put("1", "0111111");
        specifications.put("-1", "0111010");
        specifications.put("D", "0001100");
        specifications.put("A", "0110000");
        specifications.put("M", "1110000");
        specifications.put("!D", "0001101");
        specifications.put("!A", "0110001");
        specifications.put("!M", "1110001");
        specifications.put("-D", "0001111");
        specifications.put("-A", "0110011");
        specifications.put("-M", "1110011");
        specifications.put("D+1", "0011111");
        specifications.put("A+1", "0110111");
        specifications.put("M+1", "1110111");
        specifications.put("D-1", "0001110");
        specifications.put("A-1", "0110010");
        specifications.put("M-1", "1110010");
        specifications.put("D+A", "0000010");
        specifications.put("D+M", "1000010");
        specifications.put("D-A", "0010011");
        specifications.put("D-M", "1010011");
        specifications.put("A-D", "0000111");
        specifications.put("M-D", "1000111");
        specifications.put("D&A", "0000000");
        specifications.put("D&M", "1000000");
        specifications.put("D|A", "0010101");
        specifications.put("D|M", "1010101");

        destSpecifications = new HashMap<String, String>();
        destSpecifications.put("Null", "000");
        destSpecifications.put("M", "001");
        destSpecifications.put("D", "010");
        destSpecifications.put("DM", "011");
        destSpecifications.put("MD", "011");
        destSpecifications.put("A", "100");
        destSpecifications.put("AM", "101");
        destSpecifications.put("AD", "110");
        destSpecifications.put("ADM", "111");

        jumpSpecifications = new HashMap<String, String>();
        jumpSpecifications.put("Null", "000");
        jumpSpecifications.put("JGT", "001");
        jumpSpecifications.put("JEQ", "010");
        jumpSpecifications.put("JGE", "011");
        jumpSpecifications.put("JLT", "100");
        jumpSpecifications.put("JNE", "101");
        jumpSpecifications.put("JLE", "110");
        jumpSpecifications.put("JMP", "111");
    }

    public String dest(String cmd) {
        return this.destSpecifications.get(cmd);
    }

    public String comp(String cmd) {
        return this.specifications.get(cmd);
    }

    public String jump(String cmd) {
        return this.jumpSpecifications.get(cmd);
    }


}
