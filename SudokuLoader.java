
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SudokuLoader {
    public static class SudokuEntry {
        public final String id;
        public final String difficulty;
        public final int[][] grid; // 0 boş
        public final int[][] solution; // opsiyonel, yoksa null

        public SudokuEntry(String id, String difficulty, int[][] grid, int[][] solution) {
            this.id = id;
            this.difficulty = difficulty;
            this.grid = grid;
            this.solution = solution;
        }
    }

    public static List<SudokuEntry> loadAllFromResource(String resourcePath) throws Exception {
        try (InputStream is = SudokuLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) throw new IllegalArgumentException("Resource not found: " + resourcePath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("sudoku");
            List<SudokuEntry> result = new ArrayList<>();
            for (int i = 0; i < list.getLength(); i++) {
                Element el = (Element) list.item(i);
                String id = el.getAttribute("id");
                String diff = el.getAttribute("difficulty");
                String gridStr = el.getElementsByTagName("grid").item(0).getTextContent().trim();
                String solStr = null;
                NodeList solNodes = el.getElementsByTagName("solution");
                if (solNodes != null && solNodes.getLength() > 0) {
                    solStr = solNodes.item(0).getTextContent().trim();
                }
                int[][] grid = parseLinear81(gridStr);
                int[][] solution = solStr != null && !solStr.isEmpty() ? parseLinear81(solStr) : null;
                result.add(new SudokuEntry(id, diff, grid, solution));
            }
            return result;
        }
    }

    public static SudokuEntry pickRandomByDifficulty(String resourcePath, String difficulty) throws Exception {
        List<SudokuEntry> all = loadAllFromResource(resourcePath);
        List<SudokuEntry> filtered = new ArrayList<>();
        for (SudokuEntry e : all) {
            if (e.difficulty != null && e.difficulty.equalsIgnoreCase(difficulty)) filtered.add(e);
        }
        if (filtered.isEmpty()) return all.isEmpty() ? null : all.get(0);
        java.util.Random rnd = new java.util.Random();
        return filtered.get(rnd.nextInt(filtered.size()));
    }

    private static int[][] parseLinear81(String s) {
        if (s == null) throw new IllegalArgumentException("Sudoku string is null");
        s = s.replaceAll("[^0-9]", "");
        if (s.length() != 81) throw new IllegalArgumentException("Sudoku string must be 81 chars, got: " + s.length());
        int[][] grid = new int[9][9];
        for (int i = 0; i < 81; i++) {
            int r = i / 9;
            int c = i % 9;
            char ch = s.charAt(i);
            int val = ch - '0';
            grid[r][c] = val; // 0 boş kabul edilir
        }
        return grid;
    }
}
