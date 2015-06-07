package com.example.green.bachelorproject.customViews.codeEditView;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import utils.Lexer;
import com.example.green.bachelorproject.events.UpdateCacheFileEvent;
import com.example.green.bachelorproject.R;
import de.greenrobot.event.EventBus;
import utils.Syntax;
import com.example.green.bachelorproject.internalFileSystem.InternalFile;

/**
 * This view displays the TouchEditText view and the TextView containing the line numbers of the code.
 */
public class CodeEditView extends LinearLayout implements TouchEditText.TouchEditTextListener{
    private View root;
    private InternalFile file;
    private TouchEditText code;
    private ScrollView scroll;
    private int linesCount;
    private EditText lines;

    private final Handler handler = new Handler();
    private final Runnable updateAction =
            new Runnable()
            {
                @Override
                public void run()
                {
                    setLines();
                }
            };


    public CodeEditView(Context context) {
        super(context);
        init(context);
    }

    public CodeEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = layoutInflater.inflate(R.layout.edit_code_layout, this).findViewById(R.id.scroll_view);

        lines = (EditText) findViewById(R.id.edit_code_lines_view);
        lines.setPadding(5,0,5,0);

        code = (TouchEditText) findViewById(R.id.edit_code_content_view);
        code.setRoot(root);
        code.setListener(this);

        linesCount = 0;

        scroll = (ScrollView) findViewById(R.id.scroll_view);
        scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                int scrollY = scroll.getScrollY();
                if(scrollY != 0) {
                    code.scrollEvent();
                }
            }
        });
    }

    public void setFile(InternalFile file) {
        this.file = file;
    }

    public void setLexer(Lexer lexer) {
        code.setLexer(lexer);
    }

    private void initLines() {
        int usedLines = code.getLineCount();
        String text = "";

        for(int i = 1; i <= usedLines; i++) {
            text += Integer.toString(i) + System.lineSeparator();
        }

        lines.setText(text);
        linesCount = usedLines;
    }

    public void initText(String content) {
        code.initText(content);
        handler.post(new Runnable() {
            @Override
            public void run() {
                initLines();
            }
        });
    }

    public void setFont(Typeface typeFace) {
        lines.setTypeface(typeFace);
        code.setTypeface(typeFace);
    }

    @Override
    public void onTyped(String text) {
        EventBus.getDefault().post(new UpdateCacheFileEvent(text,file));
        handler.removeCallbacks(updateAction);
        handler.postDelayed(updateAction, 500);
    }

    public void forceSyntax(Syntax s) {
        code.forceSyntax(s);
    }

    private void setLines() {
        int usedLines = code.getLineCount();
        Editable text;
        text = lines.getText();

        if(usedLines > linesCount) {
            for(int i = linesCount+1; i <= usedLines; i++) {
                text.replace(text.length(), text.length(),  Integer.toString(i) + System.lineSeparator());
            }
        } else if(usedLines < linesCount) {
            text.replace(text.length()-1, text.length(), "");

            for(int i = usedLines; i < linesCount; i++) {
                text.replace(text.toString().lastIndexOf(System.lineSeparator()), text.length(), "");
            }

            text.replace(text.length(), text.length(), System.lineSeparator());
        }
        linesCount = usedLines;
    }

    public void showKeyboard() {
        code.showKeyboard();
    }

    public void hideKeyboard() {
        code.hideKeyboard();
    }

    public void insertTemplate(String template) {
        code.insertTemplate(template);
    }

    public boolean getKeyboardState() {
        return code.getKeyBoardState();
    }
}


