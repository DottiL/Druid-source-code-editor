package com.example.green.bachelorproject;

import android.app.DialogFragment;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.green.bachelorproject.events.KeyboardEvent;
import com.example.green.bachelorproject.events.TemplateEvent;
import com.example.green.bachelorproject.fragments.AddSyntaxFragment;
import com.example.green.bachelorproject.customViews.codeEditHolder.CodeEditHolder;
import com.example.green.bachelorproject.customViews.mainLayout.MainLayout;
import com.example.green.bachelorproject.events.ChangeCodeViewEvent;
import com.example.green.bachelorproject.events.SaveFileEvent;
import com.example.green.bachelorproject.events.CloseFileEvent;
import com.example.green.bachelorproject.events.UpdateCacheFileEvent;
import com.example.green.bachelorproject.fragments.ForceSyntaxFragment;
import com.example.green.bachelorproject.internalFileSystem.InternalUrl;
import com.example.green.bachelorproject.managers.CacheManager;
import com.example.green.bachelorproject.managers.CodeEditHolderManager;
import com.example.green.bachelorproject.managers.DropboxManager;
import com.example.green.bachelorproject.managers.FileManager;
import com.example.green.bachelorproject.managers.LocalFileSystemManager;
import com.example.green.bachelorproject.managers.MainLayoutManager;
import com.example.green.bachelorproject.managers.NavigatorManager;

import java.io.File;

import de.greenrobot.event.EventBus;
import utils.Colorizer;
import utils.SyntaxLoader;

import com.example.green.bachelorproject.internalFileSystem.FileType;
import com.example.green.bachelorproject.internalFileSystem.InternalFile;
import com.example.green.bachelorproject.fragments.SaveChangesFragment;
import com.example.green.bachelorproject.fragments.OpenFileFragment;
import com.example.green.bachelorproject.fragments.SaveFileDialogFragment;

public class Editor extends ActionBarActivity implements SaveFileDialogFragment.SaveFileDialogListener, SaveChangesFragment.SaveChangesDialogListener, OpenFileFragment.OpenFileDialogListener, AddSyntaxFragment.AddSyntaxDialogListener, ForceSyntaxFragment.ForceSyntaxDialogListener {
    private MainLayout mainLayout;
    private MainLayoutManager mainLayoutManager;
    private DropboxManager dbxManager;
    private LocalFileSystemManager localManager;
    private NavigatorManager navigatorManager;
    private CacheManager cacheManager;
    private CodeEditHolderManager codeEditHolderManager;
    private FileManager fileManager;
    private SaveFileDialogFragment saveDialog;
    private OpenFileFragment openDialog;
    private SyntaxLoader syntaxLoader;
    private Colorizer colorizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colorizer = new Colorizer();
        this.colorizer.setColor("String", Color.rgb(218, 220, 95));
        this.colorizer.setColor("Number", Color.rgb(173, 125, 255));
        this.colorizer.setColor("Character", Color.rgb(218, 220, 95));
        this.colorizer.setColor("Operator", Color.rgb(234, 38, 116));
        this.colorizer.setColor("Keyword", Color.rgb(234, 38, 116));
        this.colorizer.setColor("Identifier", Color.rgb(255,255,255));
        this.colorizer.setColor("Type", Color.rgb(105, 216, 238));
        this.colorizer.setColor("Comment", Color.rgb(117, 113, 91));
        this.colorizer.setColor("Comment_closed", Color.rgb(117, 113, 91));
        this.colorizer.setColor("Comment_open", Color.rgb(117, 113, 91));
        dbxManager = new DropboxManager(this);

        mainLayout = (MainLayout) this.getLayoutInflater().inflate(R.layout.activity_editor, null);
        this.setContentView(mainLayout);
        localManager = new LocalFileSystemManager(this);
        cacheManager = new CacheManager(this);

        syntaxLoader = new SyntaxLoader(this, dbxManager, localManager, getFilesDir());

        codeEditHolderManager = new CodeEditHolderManager((CodeEditHolder) findViewById(R.id.main_layout_content), colorizer, syntaxLoader, this);
        mainLayoutManager = new MainLayoutManager(this, mainLayout, syntaxLoader, codeEditHolderManager);
        fileManager = new FileManager(mainLayoutManager, dbxManager, localManager, cacheManager, this);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if(codeEditHolderManager.getSize() == 0) {
            menu.getItem(1).setEnabled(false);
        } else {
            menu.getItem(1).setEnabled(true);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                break;
            case R.id.action_new_file:
                fileManager.openNewFile();
                break;
            case R.id.action_save_as:
                showSaveAsDialog(fileManager.getCurrentFile());
                break;
            case R.id.action_open:
                showOpenFileDialog();
                break;
            case R.id.action_add_syntax:
                showAddSyntaxDialog();
                break;
            case R.id.action_force_syntax:
                showForceSyntaxDialog();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        codeEditHolderManager.hideKeyboard();
        super.onStop();
    }

    private void deleteContent(File f) {
        if(f.isDirectory()) {
            for(File c: f.listFiles()) {
                deleteContentRec(c);
            }
        }
    }

    private void deleteContentRec(File f) {
        if(f.isDirectory()) {
            for(File c: f.listFiles()) {
                deleteContentRec(c);
            }
        }
        f.delete();
    }

    public void onEvent(UpdateCacheFileEvent event) {
        fileManager.updateCacheFile(event.getContent(), event.getInternalFile());
    }

    public void onEvent(SaveFileEvent event) {
        if(event.getFile().getType() == FileType.CACHE) {
            showSaveAsDialog(event.getFile());
        } else {
            fileManager.saveFileTo(event.getFile(), event.getFile().getUrl());
        }
    }

    public void onEvent(CloseFileEvent event) {
        if(event.getFile().getType() == FileType.CACHE) {
            showSaveAsDialog(event.getFile());
        } else {
            showSaveChangesDialog(event.getFile());
        }
    }

    public void onEvent(ChangeCodeViewEvent event) {
        if(event.getFiredByNavigator()) {
            codeEditHolderManager.changeCodeView(event.getPosition());
        } else {
            mainLayoutManager.changeOpenFileItem(event.getPosition());
        }
    }

    public void onEvent(KeyboardEvent event) {
        if(event.doAction()) {
            if(event.show()) {
                codeEditHolderManager.showKeyboard();
            } else {
                codeEditHolderManager.hideKeyboard();
            }
        } else {
            mainLayoutManager.setKeyboardState(event.show());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(mainLayout.isTemplatesViewVisible()) {
                mainLayout.hideTemplatesView();
                return false;
            }
            if(codeEditHolderManager.getKeyboardState()) {
                mainLayout.setKeyboardState(true);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onEvent(TemplateEvent event) {
        codeEditHolderManager.insertTemplate(event.getTemplate());
    }

    private void showSaveChangesDialog(InternalFile file) {
        SaveChangesFragment fr = new SaveChangesFragment();
        fr.setFile(file);
        fr.show(getFragmentManager(), "Save changes");
    }

    /**
     * This method is called when the user click on the Yes button of the save file dialog. It will
     * close the file by calling the fileManager which will save and close the file.
     * @param dialog
     */
    @Override
    public void onSaveDialogSaveClick(DialogFragment dialog) {
        SaveFileDialogFragment fr = (SaveFileDialogFragment) dialog;
        fileManager.saveFileTo(fr.getFile(), fr.getSaveLocation());
        fileManager.closeFile(fr.getFile());
        dialog.dismiss();
    }

    @Override
    public void onSaveDialogCancelClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    /**
     * This method popup the save file dialog
     *
     * @param file
     */
    private void showSaveAsDialog(InternalFile file) {
        this.saveDialog = new SaveFileDialogFragment();
        saveDialog.setFile(file);
        saveDialog.setDbManager(dbxManager);
        saveDialog.setLocalManager(localManager);
        saveDialog.show(getFragmentManager(), "Save as");
    }

    private void showOpenFileDialog() {
        this.openDialog = new OpenFileFragment();
        openDialog.setDbManager(dbxManager);
        openDialog.setLocalManager(localManager);
        openDialog.show(getFragmentManager(), "Open");
    }

    private void showAddSyntaxDialog() {
        AddSyntaxFragment dialog = new AddSyntaxFragment();
        dialog.setDbManager(dbxManager);
        dialog.setLocalManager(localManager);
        dialog.show(getFragmentManager(), "AddSyntax");
    }

    private void showForceSyntaxDialog() {
        ForceSyntaxFragment fr = new ForceSyntaxFragment();
        fr.setSyntaxList(syntaxLoader.getSyntaxList());
        fr.show(getFragmentManager(), "Force syntax");
    }

    @Override
    public void onChangesDialogSaveClick(DialogFragment dialog) {
        SaveChangesFragment fr = (SaveChangesFragment) dialog;
        fileManager.saveFileTo(fr.getFile(), fr.getFile().getUrl());
        fileManager.closeFile(fr.getFile());
        dialog.dismiss();
    }

    @Override
    public void onChangesDialogCancelClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onChangesDialogNoSaveClick(DialogFragment dialog) {
        SaveChangesFragment fr = (SaveChangesFragment) dialog;
        fileManager.closeFile(fr.getFile());
        dialog.dismiss();
    }

    @Override
    public void onOpenDialogOpenClick(DialogFragment dialog) {
        OpenFileFragment fr = (OpenFileFragment) dialog;
        fileManager.openExistingFile(fr.getOpenLocation());
        dialog.dismiss();
    }

    @Override
    public void onOpenDialogCancelClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onAddSyntaxDialogOpenClick(DialogFragment dialog) {
        AddSyntaxFragment fr = (AddSyntaxFragment) dialog;

        syntaxLoader.addSyntax(fr.getOpenLocation());

        dialog.dismiss();
    }

    @Override
    public void onAddSyntaxDialogCancelClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onForceSyntaxDialogClick(DialogFragment dialog) {
        ForceSyntaxFragment d = (ForceSyntaxFragment) dialog;
        Toast.makeText(this, "Switched syntax for " + d.getSelectedSyntax().getLanguageName() + " language", Toast.LENGTH_SHORT).show();
        mainLayoutManager.forceSyntax(d.getSelectedSyntax());
        dialog.dismiss();
    }
}