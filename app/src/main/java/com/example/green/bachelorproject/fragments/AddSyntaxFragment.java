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
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.green.bachelorproject.R;
import com.example.green.bachelorproject.internalFileSystem.InternalUrl;
import com.example.green.bachelorproject.managers.DropboxManager;
import com.example.green.bachelorproject.managers.LocalFileSystemManager;
import com.example.green.bachelorproject.FileSystemsNavigatorManagers.DbxListViewManager;
import com.example.green.bachelorproject.FileSystemsNavigatorManagers.ListViewManager;
import com.example.green.bachelorproject.FileSystemsNavigatorManagers.LocalListViewManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Green on 07/05/15.
 */
public class AddSyntaxFragment extends DialogFragment implements ListViewManager.PathListener{
    private Fragment fragment;
    AddSyntaxDialogListener listener;
    private List<String> locations;
    private ListView list;
    private Button openButton;
    private Button cancelButton;
    private ListViewManager listViewManager;
    private DropboxManager dbManager;
    private LocalFileSystemManager localManager;

    @Override
    public void onPathChanged(String path, boolean isFolder) {

    }

    @Override
    public void onLocationChanged() {
        fragment = this;
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
                openButton.setEnabled(true);
            }
        });

        openButton.setEnabled(false);
    }

    public interface AddSyntaxDialogListener {
        public void onAddSyntaxDialogOpenClick(DialogFragment dialog);
        public void onAddSyntaxDialogCancelClick(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        locations = new ArrayList<String>();
        locations.add("DBX");
        locations.add("LOCAL");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.add_syntax_fragment_layout, (ViewGroup) getActivity().findViewById(R.id.open_file_fragment_root));

        list = (ListView) v.findViewById(R.id.add_syntax_fragment_list_view);
        openButton = (Button) v.findViewById(R.id.add_syntax_fragment_open_button);
        cancelButton = (Button) v.findViewById(R.id.add_syntax_fragment_cancel_button);

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddSyntaxDialogOpenClick(AddSyntaxFragment.this);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddSyntaxDialogCancelClick(AddSyntaxFragment.this);
            }
        });
        onLocationChanged();
        builder.setView(v);

        return builder.create();
    }

    public void setDbManager(DropboxManager dbManager) {
        this.dbManager = dbManager;
    }

    public void setLocalManager(LocalFileSystemManager localManager) {
        this.localManager = localManager;
    }

    public InternalUrl getOpenLocation() {
        return listViewManager.getOpenFileLocation();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (AddSyntaxDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
