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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
 * Created by Green on 26/04/15.
 */
public class OpenFileFragment extends DialogFragment implements ListViewManager.PathListener {
    OpenFileDialogListener listener;

    public interface OpenFileDialogListener {
        public void onOpenDialogOpenClick(DialogFragment dialog);
        public void onOpenDialogCancelClick(DialogFragment dialog);
    }

    private ListView list;
    private TextView currentPath;
    private Button openButton;
    private Button cancelButton;
    private Button linkButton;
    private List<String> locations;
    private ListViewManager listViewManager;
    private Fragment fragment;
    private DropboxManager dbManager;
    private LocalFileSystemManager localManager;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        fragment = this;
        locations = new ArrayList<String>();
        locations.add("DBX");
        locations.add("LOCAL");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout v = (RelativeLayout) inflater.inflate(R.layout.open_file_fragment_layout, (ViewGroup) getActivity().findViewById(R.id.open_file_fragment_root));

        list = (ListView) v.findViewById(R.id.open_file_fragment_list_view);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        openButton = (Button) v.findViewById(R.id.open_file_fragment_open_button);
        cancelButton = (Button) v.findViewById(R.id.open_file_fragment_cancel_button);
        linkButton = (Button) v.findViewById(R.id.open_file_fragment_link_account_button);
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.linkAccount();
            }
        });
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOpenDialogOpenClick(OpenFileFragment.this);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOpenDialogCancelClick(OpenFileFragment.this);
            }
        });
        onLocationChanged();
        builder.setView(v);

        return builder.create();
    }

    public InternalUrl getOpenLocation() {
        return listViewManager.getOpenFileLocation();

    }

    @Override
    public void onPathChanged(String path, boolean isFolder) {
        if(isFolder) {
            openButton.setEnabled(false);
        } else {
            openButton.setEnabled(true);
        }
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
                    linkButton.setVisibility(View.VISIBLE);

                } else if (locations.get(position).equals("LOCAL")) {
                    listViewManager = new LocalListViewManager(fragment, list, localManager);
                    linkButton.setVisibility(View.GONE);
                } else {

                }
            }
        });

        openButton.setEnabled(false);
    }

    public void setDbManager(DropboxManager dbManager) {
        this.dbManager = dbManager;
    }

    public void setLocalManager(LocalFileSystemManager localManager) {
        this.localManager = localManager;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (OpenFileDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
