package utils;

import android.text.Editable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Green on 23/04/15.
 */

import java.util.List;


/**
 * Created by Green on 23/04/15.
 */

public class Lexer {
    private HashMap<String, String> patterns;
    private Pattern pattern;
    private Syntax currentSyntax;
    private Colorizer colorizer;
    private int openCommentOffsetStart;
    private int openCommentOffsetEnd;
    private Pattern openCommentPattern;
    private boolean comment;
    private int endComment;
    private int startComment;
    private Pattern commentPattern;
    private int currentCommentOffset;

    public static class Token {
        public String type;
        public String data;
        public int start;
        public int end;

        public Token(String type, String data, int start, int end) {
            this.type = type;
            this.data = data;
            this.start = start;
            this.end = end;
        }

        @Override
        public boolean equals(Object o) {
            if(o == null) {
                return false;
            }

            if(o instanceof Token) {
                return type.equals(((Token) o).type);
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return "(" + type + ", " + data + ")";
        }
    }

    public Lexer(Colorizer colorizer, Syntax syntax) {
        comment = false;
        endComment = -1;
        startComment = -1;
        currentSyntax = syntax;
        openCommentOffsetStart = -1;
        openCommentOffsetEnd = -1;
        currentCommentOffset = -1;
        this.colorizer = colorizer;
        if(currentSyntax != null) {
            pattern = syntax.getPattern();
            openCommentPattern = syntax.getCommentEndPattern();
            commentPattern = syntax.getCommentPattern();
        }
    }

    public boolean hasSyntax() {
        return currentSyntax == null ? false : true;
    }
    public List<String> getKeywordsList() {
        return currentSyntax.getKeywordsList();
    }

    public List<String> getPrimitiveTypesList() {
        return currentSyntax.getPrimitiveTypesList();
    }

    public void forceSyntax(Syntax s) {
        currentSyntax = s;
        pattern = currentSyntax.getPattern();
    }

    public List<Token> tokenize(Editable editable, Editable visible) {
        ArrayList<Token> tokens = new ArrayList<>();
        if (currentSyntax != null) {
            int offSet = editable.toString().indexOf(visible.toString());
            Token t;
            Matcher matcher;
            matcher = pattern.matcher(visible);

            while (matcher.find()) {
                if (matcher.group(1) != null) {
                    t = new Token("Comment", matcher.group(1), matcher.start(), matcher.end());
                    tokens.add(t);
                    continue;
                } else if (matcher.group(3) != null) {
                    t = new Token("Comment", matcher.group(3), matcher.start(), matcher.end());
                    tokens.add(t);
                    continue;
                } else if (matcher.group(4) != null) {
                    t = new Token("String", matcher.group(4), matcher.start(), matcher.end());
                    tokens.add(t);
                    continue;
                } else if (matcher.group(5) != null) {
                    t = new Token("Number", matcher.group(5), matcher.start(), matcher.end());
                    tokens.add(t);
                    continue;
                } else if (matcher.group(6) != null) {
                    t = new Token("Character", matcher.group(6), matcher.start(), matcher.end());
                    tokens.add(t);
                    continue;
                } else if (matcher.group(8) != null) {
                    t = new Token("Keyword", matcher.group(8), matcher.start(), matcher.end());
                    tokens.add(t);
                    continue;
                } else if (matcher.group(7) != null) {
                    t = new Token("Operator", matcher.group(7), matcher.start(), matcher.end());
                    tokens.add(t);
                    continue;
                } else if (matcher.group(9) != null) {
                    t = new Token("Type", matcher.group(9), matcher.start(), matcher.end());
                    tokens.add(t);
                    continue;
                } else if (matcher.group(10) != null) {
                    t = new Token("Identifier", matcher.group(10), matcher.start(), matcher.end());
                    tokens.add(t);
                    continue;
                }
            }
        }
        return tokens;
    }

    private ForegroundColorSpan highlightElement(Matcher matcher, List<Token> tokens) {
        Token t;
        ForegroundColorSpan fg;
        if(matcher.group(1) != null) {
            t = new Token("Comment", matcher.group(1), matcher.start(), matcher.end());
            tokens.add(t);
            fg = new ForegroundColorSpan(colorizer.getColor(t));
            return fg;
        }

        else if(matcher.group(3) != null) {
            t = new Token("String",matcher.group(3),matcher.start(), matcher.end());
            tokens.add(t);
            fg = new ForegroundColorSpan(colorizer.getColor(t));
            return fg;
        }
        else if(matcher.group(4) != null) {
            t = new Token("Number",matcher.group(4),matcher.start(), matcher.end());
            tokens.add(t);
            fg = new ForegroundColorSpan(colorizer.getColor(t));
            return fg;
        }
        else if(matcher.group(5) != null) {
            t = new Token("Character",matcher.group(5),matcher.start(), matcher.end());
            tokens.add(t);
            fg = new ForegroundColorSpan(colorizer.getColor(t));
            return fg;
        }
        else if(matcher.group(7) != null) {
            t = new Token("Keyword",matcher.group(7),matcher.start(), matcher.end());
            tokens.add(t);
            fg = new ForegroundColorSpan(colorizer.getColor(t));
            return fg;
        }
        else if(matcher.group(6) != null) {
            t = new Token("Operator",matcher.group(6),matcher.start(), matcher.end());
            tokens.add(t);
            fg = new ForegroundColorSpan(colorizer.getColor(t));
            return fg;
        }
        else if(matcher.group(8) != null) {
            t = new Token("Type",matcher.group(8),matcher.start(), matcher.end());
            tokens.add(t);
            fg = new ForegroundColorSpan(colorizer.getColor(t));
            return fg;
        }
        else if(matcher.group(9) != null) {
            t = new Token("Identifier",matcher.group(9),matcher.start(), matcher.end());
            tokens.add(t);
            fg = new ForegroundColorSpan(colorizer.getColor(t));
            return fg;
        }
        return null;
    }

    public void highlight(Editable editable, Editable visible) {
        ArrayList<Token> tokens = new ArrayList<>();
        if(currentSyntax != null) {
            int offSet = editable.toString().indexOf(visible.toString());
            Matcher matcher;
            matcher = pattern.matcher(visible);
            ForegroundColorSpan fg = null;
            int spanStart = 0;
            int spanEnd = 0;
            ForegroundColorSpan tempFg;
            if(matcher.find()) {
                fg = highlightElement(matcher, tokens);
                spanStart = matcher.start();
                spanEnd = matcher.end();
            }

            while(matcher.find()) {
                tempFg = highlightElement(matcher, tokens);

                if(tempFg != fg) {
                    editable.setSpan(fg, offSet+spanStart, offSet+spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    fg = tempFg;
                    spanStart = matcher.start();
                    spanEnd = matcher.end();
                }
            }

            editable.setSpan(fg, offSet+spanStart, offSet+spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public boolean containStatement(String s) {
        return currentSyntax.containStatement(s);
    }

    public Map<String,String> getStatements() {
        return currentSyntax.getStatements();
    }

    public String autoCompleteStatement(String s) {
        return currentSyntax.autoCompleteStatement(s);
    }

    public boolean startsWithComment(Editable e) {
        Matcher m = openCommentPattern.matcher(e.toString());
        currentCommentOffset = m.end();
        return m.find();
    }

    public boolean checkComment(Editable e, int size) {
        Matcher m = commentPattern.matcher(e.toString());
        while(m.find()) {
            if(currentCommentOffset + size == m.end()) {
                return true;
            }
        }
        return false;
    }
}