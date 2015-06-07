package com.example.green.bachelorproject.managers;

import android.app.Activity;
import android.graphics.Typeface;
import com.example.green.bachelorproject.customViews.codeEditHolder.CodeEditHolder;
import com.example.green.bachelorproject.customViews.codeEditView.CodeEditView;

import java.util.ArrayList;
import java.util.List;

import utils.Colorizer;
import utils.Lexer;
import utils.Syntax;
import utils.SyntaxLoader;

import com.example.green.bachelorproject.internalFileSystem.InternalFile;

/**
 * Created by Green on 11/04/15.
 */
public class CodeEditHolderManager {
    private Activity activity;
    private CodeEditHolder codeEditHolder;
    private List<InternalFile> files;
    private List<CodeEditView> codeEditViews;
    private InternalFile currentFile;
    private CodeEditView currentCodeEditView;
    private Colorizer colorizer;
    private SyntaxLoader syntaxLoader;


    public CodeEditHolderManager(CodeEditHolder codeEditHolder, Colorizer colorizer, SyntaxLoader syntaxLoader, final Activity activity) {
        this.activity = activity;
        this.colorizer = colorizer;
        this.syntaxLoader = syntaxLoader;
        this.codeEditHolder = codeEditHolder;
        this.files = new ArrayList<InternalFile>();
        this.codeEditViews = new ArrayList<CodeEditView>();
    }

    public void addFile(InternalFile file, String content) {
        files.add(file);
        currentFile = file;

        createView(file, content);
        show();
    }

    public int getSize() {
        return files.size();
    }

    private void createView(InternalFile file, String content) {
        CodeEditView cev = createCodeEditView(file, content);
        currentCodeEditView = cev;
        codeEditViews.add(cev);
    }

    private CodeEditView createCodeEditView(InternalFile file, String content) {
        CodeEditView cev = new CodeEditView(activity);
        cev.setFile(file);
        Lexer l = new Lexer(colorizer, syntaxLoader.getSyntaxByExtension(file.getExtension()));

        cev.setLexer(l);
        cev.initText(content);

        cev.setFont(Typeface.createFromAsset(activity.getAssets(), "fonts/courbd.ttf"));
        return cev;
    }

    private void show() {
        codeEditHolder.setCodeEdiView(currentCodeEditView);
    }

    public void changeCodeView(int position) {
        currentFile = files.get(position);
        currentCodeEditView = codeEditViews.get(position);
        show();
    }

    public void removeFile(InternalFile file) {
        int position = files.indexOf(file);
        files.remove(position);
        codeEditViews.remove(position);

        if(files.size() == 0) {
            codeEditHolder.clearContent();
            currentFile = null;
            currentCodeEditView = null;
        } else {
            if(position == files.size()-1) {
                changeCodeView(codeEditViews.size()-1);
            } else {
                changeCodeView(position-1);
            }
        }
    }

    public InternalFile getCurrentFile() {
        return currentFile;
    }

    public void forceSyntax(Syntax s) {
        if(currentCodeEditView != null) {
            currentCodeEditView.forceSyntax(s);
        }
    }

    public void hideKeyboard() {
        if(currentCodeEditView != null) {
            currentCodeEditView.hideKeyboard();
        }

    }

    public void showKeyboard() {
        if(currentCodeEditView != null) {
            currentCodeEditView.showKeyboard();
        }
    }

    public void insertTemplate(String template) {
        currentCodeEditView.insertTemplate(template);
    }

    public boolean getKeyboardState() {
        return currentCodeEditView.getKeyboardState();
    }
}
