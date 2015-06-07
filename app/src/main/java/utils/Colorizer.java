package utils;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Green on 25/04/15.
 */
public class Colorizer {

    private Map<String, Integer> colors;

    public Colorizer() {
        this.colors = new HashMap<>();
    }

    public void setColor(String name, int color) {
        colors.put(name, color);
    }

    public int getColor(Lexer.Token token) {
        Integer color = colors.get(token.type);
        if(color == null) {
            return Color.WHITE;
        } else {
            return color;
        }
    }
}
