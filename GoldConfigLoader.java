
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GoldConfigLoader {
    public static class ItemDef {
        public final String type; // gold/stone/diamond/
        public final String size; // small/medium/large
        public final int w, h;
        public final int value;
        public final Color color;
        public final int pullPenalty; // çekme sırasında yavaşlatma miktarı
        public ItemDef(String type, String size, int w, int h, int value, Color color, int pullPenalty) {
            this.type = type; this.size = size; this.w = w; this.h = h; this.value = value; this.color = color; this.pullPenalty = pullPenalty;
        }
    }

    public static List<ItemDef> load(InputStream is) throws Exception {
        if (is == null) throw new IllegalArgumentException("InputStream is null");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(is);
        doc.getDocumentElement().normalize();
        NodeList nodes = doc.getElementsByTagName("item");
        List<ItemDef> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            String type = el.getAttribute("type");
            String size = el.getAttribute("size");
            int w = Integer.parseInt(el.getAttribute("w"));
            int h = Integer.parseInt(el.getAttribute("h"));
            int value = Integer.parseInt(el.getAttribute("value"));
            String colorHex = el.getAttribute("color");
            Color color = Color.decode(colorHex);
            int pullPenalty = Integer.parseInt(el.getAttribute("pullPenalty"));
            list.add(new ItemDef(type, size, w, h, value, color, pullPenalty));
        }
        return list;
    }
}