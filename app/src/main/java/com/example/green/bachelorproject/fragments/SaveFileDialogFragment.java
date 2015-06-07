package com.example.green.bachelorproject.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.green.bachelorproject.R;
import com.example.green.bachelorproject.managers.DropboxManager;
import com.example.green.bachelorproject.managers.LocalFileSystemManager;

import java.util.ArrayList;
import java.util.List;

import com.example.green.bachelorproject.internalFileSystem.*;
import com.example.green.bachelorproject.FileSystemsNavigatorManagers.DbxListViewManager;
import com.example.green.bachelorproject.FileSystemsNavigatorManagers.ListViewManager;
import com.example.green.bachelorproject.FileSystemsNavigatorManagers.LocalListViewManager;


/**
* Created by Green on 30/03/15.
*/
public class SaveFileDialogFragment extends DialogFragment implements ListViewManager.PathListener {

    public interface SaveFileDialogListener {
        public void onSaveDialogSaveClick(DialogFragment dialog);
        public void onSaveDialogCancelClick(DialogFragment dialog);
    }

    private Fragment fragment;
    SaveFileDialogListener listener;

    private ListView list;
    private TextView currentPath;
    private Button saveButton;
    private Button cancelButton;
    private EditText fileName;

    private ListViewManager listViewManager;
    private DropboxManager dbManager;
    private LocalFileSystemManager localManager;
    private List<String> locations;

    private InternalFile file;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        fragment = this;

        locations = new ArrayList<String>();
        locations.add("DBX");
        locations.add("LOCAL");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout v = (LinearLayout) inflater.inflate(R.layout.save_file_fragment_layout, (ViewGroup) getActivity().findViewById(R.id.save_file_fragment_root));

        list = (ListView) v.findViewById(R.id.save_file_fragment_file_navigator);
        currentPath = (TextView) v.findViewById(R.id.save_file_fragment_current_path);
        saveButton = (Button) v.findViewById(R.id.save_file_fragment_save_button);
        cancelButton = (Button) v.findViewById(R.id.save_file_fragment_cancel_button);
        fileName = (EditText) v.findViewById(R.id.save_file_fragment_file_name);

        fileName.setText(file.getFileName());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSaveDialogSaveClick(SaveFileDialogFragment.this);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSaveDialogCancelClick(SaveFileDialogFragment.this);
            }
        });

        switch(file.getType()) {
            case CACHE:
                onLocationChanged();
                break;
            case DBX:
                listViewManager = new DbxListViewManager(this, list, dbManager);
                listViewManager.show(file);
                break;
            case LOCAL:
                listViewManager = new LocalListViewManager(fragment, list, localManager);
                listViewManager.show(file);
                break;
        }

        builder.setView(v);
        return builder.create();
    }

    public InternalUrl getSaveLocation() {
        return listViewManager.getSaveLocation();
    }

    public String getFileName() {
        return fileName.getText().toString();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (SaveFileDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onPathChanged(String path, boolean isFolder) {
        currentPath.setText(path);
    }

    @Override
    public void onLocationChanged() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, locations);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(locations.get(position).equals("DBX")) {
                    listViewManager = new DbxListViewManager(fragment, list, dbManager);
                } else if (locations.get(position).equals("LOCAL")) {
                    listViewManager = new LocalListViewManager(fragment, list, localManager);

                } else {

                }
                saveButton.setEnabled(true);
            }
        });

        currentPath.setText("Choose the location");

        saveButton.setEnabled(false);
    }

    public void setFile(InternalFile file) {
        this.file = file;
    }

    public InternalFile getFile() {
        return file;
    }

    public void setDbManager(DropboxManager dbManager) {
        this.dbManager = dbManager;
    }

    public void setLocalManager(LocalFileSystemManager localManager) {
        this.localManager = localManager;
    }
}

enum CurrentPosition {
    LOCATION, ACCOUNT, PATH;
}





