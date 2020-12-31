package ac.technion.schemamatching.test;


/**
 *
 */
public class test {

    
    public static void main(String[] args) {
        try {
            Process p = Runtime.getRuntime().exec("C:\\Documents and Settings\\ps-user\\Desktop\\OntoParser\\OntoBuilder\\OntoParser (RDF full + light)\\OntoParser.exe");
            p.waitFor();
            System.out.println("hi");
        }catch(Exception e) {
           e.printStackTrace();
        }
    }
}
