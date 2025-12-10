
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.io.IOException;

public class TestUIResources {
    public static void main(String[] args) {
        System.out.println("Testing resource loading...");
        try (InputStream is = TestUIResources.class.getClassLoader().getResourceAsStream("resources/menu_bg.png")) {
            if (is != null) {
                if (ImageIO.read(is) != null) {
                    System.out.println("[PASS] Background image loaded successfully.");
                } else {
                    System.out.println("[FAIL] Background image stream found but ImageIO returned null.");
                }
            } else {
                System.out.println("[FAIL] Background image stream is null.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Testing Sudoku XML path...");
        try (InputStream is = TestUIResources.class.getClassLoader()
                .getResourceAsStream("resources/sudoku_puzzles.xml")) {
            if (is != null) {
                System.out.println("[PASS] Sudoku XML found.");
            } else {
                System.out.println("[FAIL] Sudoku XML not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
