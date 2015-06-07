package com.example.green.bachelorproject.customViews.codeEditView;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import com.example.green.bachelorproject.events.KeyboardEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import de.greenrobot.event.EventBus;
import utils.Lexer;
import utils.SpaceTokenizer;
import utils.Syntax;

/**
 * Created by Green on 27/02/15.
 */
public class TouchEditText extends MultiAutoCompleteTextView {
    TouchEditTextListener listener;
    private View root;
    private Lexer lexer;
    private List<String> currentHints;

    private boolean moveCursor;
    private boolean modified;
    private boolean init;
    private boolean moveCursorCurly;

    private int currentStartLine;
    private int currentEndLine;
    private int currentPageOffsetStart;
    private int currentPageOffsetEnd;
    private int pageSize = 100;

    private final Handler handler = new Handler();
    private final Runnable updateAction =
            new Runnable()
            {
                @Override
                public void run()
                {
                    Editable e = getText();
                    highlightTextChanged(e);
                }
            };

    interface TouchEditTextListener {
        public void onTyped(String text);

    }

    public TouchEditText(Context context) {
        super(context);
        init();
    }

    public TouchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        moveCursor = false;
        moveCursorCurly = false;
        modified = true;
        init = true;
        currentPageOffsetStart = 1;
        currentPageOffsetEnd = 1;

        setTokenizer(new SpaceTokenizer());
        setThreshold(1);

        setFilters(new InputFilter[]{new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend ) {
                        String indentation = "";

                        if(dstart+1 == dend) {
                            Editable e = getText();
                            char c1 = e.charAt(dstart);

                            if(dstart+1<e.length()) {
                                char c2 = e.charAt(dstart+1);

                                if(c1 == '(' && e.charAt(dstart+1) == ')') {
                                    e.replace(dstart+1,dstart+2, "");
                                    return source;
                                }

                                if(c1 == '[' && c2 == ']') {
                                    e.replace(dstart+1,dstart+2, "");
                                    return source;
                                }

                                if(c1 == '{' && c2 == '}') {
                                    e.replace(dstart+1,dstart+2, "");
                                    return source;
                                }

                                if(c1 == '"' && c2 == '"') {
                                    e.replace(dstart+1,dstart+2, "");
                                    return source;
                                }

                                if(c1 == '\'' && c2 == '\'') {
                                    e.replace(dstart+1,dstart+2, "");
                                    return source;
                                }
                            }
                        } else if (source.length() != 0) {
                            if (source.charAt(source.length() - 1) == '\n') {
                                CharSequence line = getCurrentLine();
                                indentation = getIndentation(line);
                                int lastIndex = getSelectionStart()-1;
                                char last = '\0';

                                if(lastIndex >= 0) {
                                    last = getText().charAt(getSelectionStart()-1);
                                }

                                if (last == '(' || last == '[') {
                                    indentation += "    ";
                                }

                                if(last == '{') {
                                    indentation += "    " + "\n" + indentation;

                                    moveCursorCurly = true;
                                }

                                return source+indentation;
                            } else if(source.charAt(source.length() - 1) == '{') {
                                moveCursor = true;
                                return source + "}";
                            } else if(source.charAt(source.length() - 1) == '[') {
                                moveCursor = true;
                                return source + "]";
                            } else if(source.charAt(source.length() - 1) == '(') {
                                moveCursor = true;
                                return source + ")";
                            } else if(source.charAt(source.length() - 1) == '"') {
                                moveCursor = true;
                                return source + "\"";
                            } else if (source.charAt(source.length() - 1) == '\'') {
                                moveCursor = true;
                                return source + "'";
                            }
                        }
                        return source;
                    }
                }});

        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(init) {
                    updateBoundaries();
                    Editable highlighted = highlightText(getText());
                    setText(highlighted);
                    init = false;
                }
            }
        });

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(moveCursor) {
                    setSelection(getSelectionStart()-1);
                    moveCursor = false;
                }

                if(moveCursorCurly) {
                    setSelection(getSelectionStart() - getIndentation(getCurrentLine()).length()-1);
                    moveCursorCurly = false;
                }

                handler.removeCallbacks(updateAction);
                listener.onTyped(s.toString());

                if(!modified) {
                    return;
                }

                handler.postDelayed(updateAction, 1000);
            }
        });

        this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                String s = lexer.autoCompleteStatement(item);
                int start = Math.max(getSelectionStart(), 0);
                int end = Math.max(getSelectionEnd(), 0);

                if(s != null) {
                    s = s.replace("\n", "\n"+getIndentation(getCurrentLine()));
                    getText().replace(Math.min(start, end)-item.length()-1, Math.max(start, end), s, 0, s.length());
                }

                highlightText(getText());
                }
        });
    }

    /**
     * This method returns the current line (i.e. the line where the cursor is)
     *
     * @return the current line
     */
    private CharSequence getCurrentLine() {
        int currentLineNumber = getCurrentCursorLine();
        int startPos = getLayout().getLineStart(currentLineNumber);
        int endPos = getLayout().getLineEnd(currentLineNumber);
        return getText().subSequence(startPos, endPos);
    }

    /**
     * This method returns the indentation of the line before the given one.
     *
     * @param line
     * @return the indentation of the previous line
     */
    private String getIndentation(CharSequence line) {
        int indentationCount = 0;

        for (char c : line.toString().toCharArray()) {
            if (c == ' ') {
                indentationCount++;
            } else {
                break;
            }
        }

        String indentation = "";

        for(int i = 0; i<indentationCount; i++) {
            indentation += " ";
        }

        return indentation;
    }

    /**
     * This method sets the Lexer object.
     *
     * @param lexer
     */
    public void setLexer(Lexer lexer) {
        this.lexer = lexer;
        setHintsList();
    }

    //http://stackoverflow.com/questions/7627347/android-edittext-get-current-line
    private int getCurrentCursorLine()
    {
        int selectionStart = Selection.getSelectionStart(getText());
        Layout layout = getLayout();

        if(layout != null) {
            if (!(selectionStart == -1)) {
                return layout.getLineForOffset(selectionStart);
            }
        }

        return -1;
    }

    private boolean updateBoundaries() {
        int height    = root.getHeight();
        int scrollY   = root.getScrollY();
        int temp = currentStartLine;
        Layout layout = getLayout();


        if(layout != null) {
            currentStartLine = layout.getLineForVertical(scrollY);
            currentEndLine  = layout.getLineForVertical(scrollY + height);
        }

        return temp != currentStartLine;
    }

    public void setRoot(View root) {
        this.root = root;
    }

    private void highlightTextChanged(Editable e) {
        modified = false;
        highlightText(e);
        modified = true;
    }

    private void clearAllSpans(Editable editable) {
        {
            ForegroundColorSpan spans[] = editable.getSpans(0, editable.length(), ForegroundColorSpan.class);

            for(int n = spans.length; n-- > 0;) {
                editable.removeSpan(spans[n]);
            }
        }
    }

    public void setListener(TouchEditTextListener listener) {
        this.listener = listener;
    }

    public void initText(String content) {
        setText(content);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hideKeyboard();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    private Editable getVisibleText() {
        updateBoundaries();

        if(getLineCount() > currentEndLine-currentStartLine) {
            Editable content = new SpannableStringBuilder();

            for(int i = currentStartLine; i<=currentEndLine; i++) {
                content.append(getText().subSequence(getLayout().getLineStart(i), getLayout().getLineEnd(i)));
            }

            return content;
        }

        return getText();
    }

    public Editable highlightText(Editable editable) {
        Editable visible = getTextToHighlight();
        clearAllSpans(editable);
        lexer.highlight(editable, visible);
        return editable;
    }

    public void scrollEvent() {
        if(updateBoundaries()) {
            handler.removeCallbacks(updateAction);

            if(!modified) {
                return;
            }

            handler.postDelayed(updateAction, 200);
        }
    }

    private Editable getPages() {
        Editable content;
        Editable original;
        updateBoundaries();
        Editable text = getText();
        int newPageOffsetStart = (int) Math.floor((((double) currentStartLine) / ((double) pageSize)) + 1);
        int newPageOffsetEnd = (int) Math.floor((((double) currentEndLine) / ((double) pageSize)) + 1);

        content = new SpannableStringBuilder(getTextByLines(newPageOffsetStart*pageSize-pageSize+1,newPageOffsetEnd*pageSize));
        original = new SpannableStringBuilder(content.toString());

        if(lexer.startsWithComment(content)) {
            int count = 2;
            int start = (newPageOffsetStart-1)*pageSize-pageSize+1;
            int end = (newPageOffsetStart-1)*pageSize;
            SpannableStringBuilder prepend = new SpannableStringBuilder(getTextByLines(start, end));

            while(!lexer.checkComment(content.insert(0,prepend), prepend.length()) && start >= 0) {
                start = (newPageOffsetStart-count)*pageSize-pageSize+1;
                end = (newPageOffsetStart-count)*pageSize;
                prepend = new SpannableStringBuilder(getTextByLines(start, end));
                count++;
            }

            if(start < 0) {
                return original;
            } else {
                return content;
            }
        }

        return original;
    }

    private Editable getTextToHighlight() {
        if(getLineCount() < pageSize) {
            return getText();
        }

        Editable content = new SpannableStringBuilder();
        Editable text = getText();
        updateBoundaries();

        int newPageOffsetStart = (int) Math.floor((((double) currentStartLine) / ((double) pageSize)) + 1);
        int newPageOffsetEnd = (int) Math.floor((((double) currentEndLine) / ((double) pageSize)) + 1);

        currentPageOffsetStart = newPageOffsetStart;
        currentPageOffsetEnd = newPageOffsetEnd;

        int eend = currentPageOffsetStart * pageSize;
        if(eend > getLineCount()-1) {
            eend = getLineCount()-1;
        }

        content.append(getTextByLines(currentPageOffsetStart*pageSize - pageSize, eend-1));

        if(currentPageOffsetStart != currentPageOffsetEnd) {
            int estart = currentPageOffsetEnd*pageSize;

            if(estart > getLineCount()-1) {
                estart = getLineCount()-1;
            }

            content.append(getTextByLines(currentPageOffsetEnd*pageSize - pageSize, estart));
        }

        Editable rest2;

        if(currentPageOffsetStart > 1) {
            rest2 = new SpannableStringBuilder(getTextByLines((currentPageOffsetStart-1)*pageSize - pageSize, (currentPageOffsetStart-1) * pageSize-1));
            rest2.append(content);
            return rest2;
        }

        return content;
    }

    private CharSequence getTextByLines(int start, int end) {
        return getText().subSequence(getLayout().getLineStart(start), getLayout().getLineEnd(end));
    }

    public void forceSyntax(Syntax s) {
        lexer.forceSyntax(s);
        highlightText(getText());
        setHintsList();
    }

    private void setHintsList() {
        currentHints= new ArrayList<>();

        if(lexer.hasSyntax()) {
            currentHints.addAll(lexer.getPrimitiveTypesList());
            currentHints.addAll(lexer.getKeywordsList());

            for (Map.Entry<String, String> entry : lexer.getStatements().entrySet()){
                String key = entry.getKey();

                if(!currentHints.contains(key)) {
                    currentHints.add(key);
                }
            }

            String[] hintsArray = new String[currentHints.size()];

            for(int i = 0; i < currentHints.size(); i++) {
                hintsArray[i] = currentHints.get(i);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, hintsArray);
            setAdapter(adapter);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        EventBus.getDefault().post(new KeyboardEvent(true, false));
        return true;
    }

    public boolean getKeyBoardState() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    public void insertTemplate(String template) {
        String s = lexer.autoCompleteStatement(template);
        int start = Math.max(getSelectionStart(), 0);
        int end = Math.max(getSelectionEnd(), 0);

        if(s != null) {
            s = s.replace("\n", "\n"+getIndentation(getCurrentLine()));
            getText().replace(Math.min(start, end), Math.max(start, end), s, 0, s.length());
        } else {
            getText().replace(Math.min(start, end), Math.max(start, end), template, 0, template.length());
        }

        highlightText(getText());
    }
}
