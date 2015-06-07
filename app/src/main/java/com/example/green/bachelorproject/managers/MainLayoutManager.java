package com.example.green.bachelorproject.managers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.green.bachelorproject.R;
import com.example.green.bachelorproject.customViews.TemplatesView.TemplatesView;
import com.example.green.bachelorproject.customViews.mainLayout.MainLayout;
import com.example.green.bachelorproject.events.ChangeCodeViewEvent;
import com.example.green.bachelorproject.events.CloseFileEvent;
import com.example.green.bachelorproject.events.SaveFileEvent;
import com.example.green.bachelorproject.internalFileSystem.InternalFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import utils.Syntax;
import utils.SyntaxLoader;

/**
 * Created by Green on 31/05/15.
 */
public class MainLayoutManager {
    private Activity activity;
    private MainLayout mainLayout;
    private ListView openFiles;
    private List<InternalFile> openFilesList;
    private FileAdapter adapter;
    private List<String> fileNames;
    private Spinner spinner;
    private HashMap<String, TemplatesView> templateViews;
    private RelativeLayout templatesContainer;
    private SyntaxLoader syntaxLoader;
    private CodeEditHolderManager codeEditHolderManager;
    private View saveButton;

    public MainLayoutManager(Activity activity, MainLayout mainLayout, SyntaxLoader syntaxLoader, final CodeEditHolderManager codeEditHolderManager) {
        this.codeEditHolderManager = codeEditHolderManager;
        this.activity = activity;
        this.mainLayout = mainLayout;
        this.syntaxLoader = syntaxLoader;
        templateViews = new HashMap<>();
        saveButton = mainLayout.findViewById((R.id.main_layout_save_button));
        openFilesList = new ArrayList<>();
        fileNames = new ArrayList<>();

        templatesContainer = (RelativeLayout) mainLayout.findViewById(R.id.main_layout_templates_view);
        spinner = (Spinner) mainLayout.findViewById(R.id.main_layout_navigator_button);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().post(new ChangeCodeViewEvent(position, true));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InternalFile f = codeEditHolderManager.getCurrentFile();
                if(f != null) {
                    EventBus.getDefault().post(new SaveFileEvent(f, false));
                }

            }
        });
        this.adapter =  new FileAdapter(activity, R.layout.grid_item,fileNames);
        spinner.setAdapter(adapter);

    }

    public void addOpenFile(InternalFile file, String content) {
        openFilesList.add(file);
        fileNames.add(file.getFileName());
        spinner.setVisibility(View.VISIBLE);
        mainLayout.setLockTemplatesView(false);
        adapter =  new FileAdapter(activity, R.layout.grid_item,fileNames);
        codeEditHolderManager.addFile(file, content);
        spinner.setAdapter(adapter);
        spinner.setSelection(openFilesList.indexOf(file));
        Syntax s = syntaxLoader.getSyntaxByExtension(file.getExtension());
        if(s != null) {
            if(!templateViews.containsKey(s.getLanguageName())) {
                TemplatesView tv = new TemplatesView(activity);
                tv.initTemplate(s);
                templateViews.put(s.getLanguageName(), tv);
                templatesContainer.removeAllViews();
                templatesContainer.addView(tv);
            } else {
                templatesContainer.removeAllViews();
                templatesContainer.addView(templateViews.get(s.getLanguageName()));
            }

        } else {
            templatesContainer.removeAllViews();
            mainLayout.setLockTemplatesView(true);
        }
    }

    public void changeOpenFileItem(int position) {
        spinner.setSelection(position);
        codeEditHolderManager.changeCodeView(position);
    }

    public void removeOpenFile(InternalFile file) {
        openFilesList.remove(file);
        fileNames.remove(file.getFileName());
        this.adapter =  new FileAdapter(activity, R.layout.grid_item, fileNames);
        spinner.setAdapter(adapter);
        if(openFilesList.size() == 0) {
            spinner.setVisibility(View.INVISIBLE);
            mainLayout.setLockTemplatesView(true);
        }
        codeEditHolderManager.removeFile(file);
    }

    public void forceSyntax(Syntax s) {
        codeEditHolderManager.forceSyntax(s);
        TemplatesView tv = new TemplatesView(activity);
        tv.initTemplate(s);
        templateViews.remove(s.getLanguageName());
        templateViews.put(s.getLanguageName(), tv);

        templatesContainer.removeAllViews();
        templatesContainer.addView(tv);
        mainLayout.setLockTemplatesView(false);

    }

    public void setKeyboardState(boolean active) {
        mainLayout.setKeyboardState(active);
    }

    public InternalFile getCurrentFile() {
        return codeEditHolderManager.getCurrentFile();
    }

    public class FileAdapter extends ArrayAdapter<String> {

        private Context context;
        private List<String> values;
        private int res;
        public FileAdapter(Context ctx, int txtViewResourceId, List<String> objects) {
            super(ctx, txtViewResourceId, objects);
            this.context = ctx;
            this.values = objects;
            this.res = txtViewResourceId;
        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            return getCustomView(position, cnvtView, prnt);
        }
        @Override
        public View getView(int pos, View cnvtView, ViewGroup prnt) {
            return getCustomView(pos, cnvtView, prnt);
        }

        public View getCustomView(final int position, View convertView,
                                  ViewGroup parent) {
            View v = convertView;

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            View item = inflater.inflate(R.layout.grid_item, parent, false);
            TextView name = (TextView) item.findViewById(R.id.open_file_item_name);
            name.setText(values.get(position));
            v = item;
            item.findViewById(R.id.open_file_item_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new CloseFileEvent(getFileByName(values.get(position                                                                                                                                                                                                                                                                                                                                                      ))));
                }
            });

            return v;
        }
    }

    public InternalFile getFileByName(String name) {
        for(InternalFile f: openFilesList) {
            if(f.getFileName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    public void keyEvent() {
        if(codeEditHolderManager.getKeyboardState()) {
            mainLayout.setKeyboardState(false);
        }
    }
}





