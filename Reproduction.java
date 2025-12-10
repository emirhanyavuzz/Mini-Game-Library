
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.File;

public class Reproduction {
    public static void main(String[] args) {
        testPath("sudoku_puzzles.xml");
        testPath("resources/sudoku_puzzles.xml");
        testPath("/resources/sudoku_puzzles.xml");
    }

    private static void testPath(String path) {
        System.out.println("Testing path: " + path);
        try (InputStream is = Reproduction.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                System.out.println("  -> is NULL (via ClassLoader)");
                // Try file system directly to be sure where we are
                File f = new File(path);
                System.out.println("  -> File exists? " + f.exists() + " (Absolute: " + f.getAbsolutePath() + ")");
            } else {
                System.out.println("  -> FOUND via ClassLoader!");
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(is);
                doc.getDocumentElement().normalize();
                NodeList list = doc.getElementsByTagName("sudoku");
                System.out.println("  -> Parse success. Sudokus found: " + list.getLength());
            }
        } catch (Exception e) {
            System.out.println("  -> Exception: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("--------------------------------------------------");
    }
}
