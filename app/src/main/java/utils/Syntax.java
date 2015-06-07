package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Green on 07/05/15.
 */
public class Syntax {
    private String languageName;
    private String fileExtension;
    private boolean multiLineComment;
    private List<String> keywords;
    private List<String> primitiveTypes;
    private List<String> operators;
    private List<String> statementsList;
    private Map<String, String> statements;
    private String multiLineCommentEnd;
    private Pattern commentPattern;
    private Pattern commentEndPattern;
    private String multiLineCommentDelEnd;
    private String multiLineCommentDelStart;
    private String singleLineCommentDel;
    private List<String> stringDelList;

    private Pattern pattern;

    public Syntax(String languageName, String fileExtension, List<String> operators, List<String> keywords, List<String> primitiveTypes, List<String> stringDelimiters,
                  boolean multiLineComment, String multiLineCommentStart, String multiLineCommentEnd, String singleLineComment, Map<String,String> statements) {
        this.keywords = keywords;
        this.statements = statements;
        this.operators = operators;
        this.multiLineCommentDelEnd = multiLineCommentEnd;
        this.multiLineCommentDelStart = multiLineCommentStart;
        this.stringDelList = stringDelimiters;
        this.statementsList = new ArrayList<>();
        statementsList.addAll(statements.keySet());
        this.singleLineCommentDel = singleLineComment;
        this.primitiveTypes = primitiveTypes;
        this.languageName = languageName;
        this.fileExtension = fileExtension;
        this.multiLineComment = multiLineComment;
        this.multiLineCommentEnd = multiLineCommentEnd;
        if(multiLineCommentStart != null) {
            this.multiLineComment = true;
        }

        createPattern(operators, keywords, primitiveTypes, stringDelimiters, multiLineCommentStart, multiLineCommentEnd, singleLineComment);
    }

    public List<String> getKeywordsList() {
        return keywords;
    }

    public List<String> getPrimitiveTypesList() {
        return primitiveTypes;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getMultiLineCommentEnd() { return multiLineCommentEnd;}

    private void createPattern(List<String> operators, List<String> keywords, List<String> primitiveTypes, List<String> stringDelimiters,
                               String multiLineCommentStart, String multiLineCommentEnd, String singleLineComment) {
        String operatorsPattern = createOperatorsPattern(operators);
        String keywordsPattern = createKeywordPattern(keywords);
        String typesPattern = createTypePattern(primitiveTypes);
        String numbersPattern = createNumberPattern();
        String stringsPattern = createStringPattern(stringDelimiters);
        String charactersPattern = createCharacterPattern();
        String commentsPattern = createCommentPattern(singleLineComment, multiLineCommentStart, multiLineCommentEnd);
        String identifiersPattern = createIdentifiersPattern();
        String endCommentPattern = createEndCommentPattern(multiLineCommentEnd);
        commentEndPattern = Pattern.compile(endCommentPattern);
        commentPattern = Pattern.compile(commentsPattern);
        pattern = Pattern.compile("(" + commentsPattern + ")|" + "(" + stringsPattern + ")|" +  "(" + numbersPattern + ")|" + "(" + charactersPattern + ")|" + "(" + operatorsPattern + ")|" + "(" + keywordsPattern + ")|" + "(" + typesPattern + ")|" + "(" + identifiersPattern +")");
    }

    private String createEndCommentPattern(String multiLineCommentEnd) {
        String end = "";
        for(char c: multiLineCommentEnd.toCharArray()) {
            end += "\\"+ Character.toString(c);
        }
        return "(?:" + end + ")";
    }

    public Pattern getCommentPattern() {
        return commentPattern;
    }

    public Pattern getCommentEndPattern() {
        return commentEndPattern;
    }



    private String createCommentPattern(String singleLineComment, String multiLineCommentStart, String multiLineCommentEnd) {
        String pattern = "";
        if(multiLineComment) {
            String start = "";
            String end = "";
            for(char c: multiLineCommentStart.toCharArray()) {
                start += "\\"+ Character.toString(c);
            }

            for(char c: multiLineCommentEnd.toCharArray()) {
                end += "\\"+ Character.toString(c);
            }

            pattern += String.format("(?:(?s)(?:%s.*?(%s|$)))", start, end);
        }
        pattern += "|"+String.format("(?:(?m)(?:%s.*))", singleLineComment);

        if(!multiLineComment) {
            pattern.substring(1);
        }
        return pattern;
    }

    /**
     * Creates the string pattern.
     *
     * @param stringDelimiters
     * @return
     */
    private String createStringPattern(List<String> stringDelimiters) {
        String pattern = "";
        for(String s: stringDelimiters) {
            pattern += String.format("|(?s)\\%s(?:[^\\%s\\\\]|\\\\.)*(?:\\%s|$)", s, s, s);
        }
        return pattern.substring(1);
    }

    private String createCharacterPattern() {
        return "'(?:.*?|\\\\s)'";
    }

    private String createNumberPattern() {
        return "[0-9][0-9]*[\\.[0-9][0-9]*]*[fFdDlL]?";
    }

    private String createOperatorsPattern(List<String> operators) {
        String pattern = "";
        for(String s: operators) {
            String op = "";
            for(char c: s.toCharArray()) {
                if(!Character.isLetter(c)) {
                    op += "\\" + Character.toString(c);
                } else {
                    op += Character.toString(c);
                }

            }
            pattern += String.format("|(?:%s)", op);
        }

        return "\\b" + pattern.substring(1) + "\\b";
    }

    private String createKeywordPattern(List<String> keywords) {
        String keys = "";
        for(String s: keywords) {
            keys += "|" + s;
        }

        keys = keys.substring(1);

        return String.format("\\b(?:%s)\\b", keys);
    }

    private String createTypePattern(List<String> primitiveTypes) {
        String keys = "";
        for(String s: primitiveTypes) {
            keys += "|" + s;
        }

        keys = keys.substring(1);

        keys = String.format("\\b(?:%s)\\b", keys);

        return String.format("\\b(?:[a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[A-Z_$][a-zA-Z\\d_$]*\\b|%s", keys);
    }

    private String createIdentifiersPattern() {
        return "[$_A-Za-z][$_A-Za-z0-9]*";
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }

        if(o instanceof Syntax) {
            Syntax s = (Syntax) o;
            return this.languageName.equals(s.getLanguageName()) && this.fileExtension.equals(s.getFileExtension());
        }

        return false;
    }

    public String autoCompleteStatement(String key) {
        return statements.get(key);
    }

    public boolean containStatement(String s) {
        return statements.containsKey(s);
    }

    public Map<String,String> getStatements() {
        return statements;
    }

    public List<String> getKeywords() {
       return keywords;
   }

    public List<String> getOperators() {
        return operators;
    }

    public List<String> getPrimitiveTypes() {
        return primitiveTypes;
    }

    public List<String> getStatementsList() {
        return statementsList;
    }

    public String getMultiLineCommentDel() {
        return multiLineCommentDelStart+multiLineCommentDelEnd;
    }

    public String getSingleLineCommentDel() {
        return singleLineCommentDel;
    }

    public List<String> getStringDel() {
        return stringDelList;
    }
}
