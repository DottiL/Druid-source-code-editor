package com.example.green.bachelorproject.managers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.green.bachelorproject.R;
import com.example.green.bachelorproject.customViews.navigatorView.NavigatorView;
import com.example.green.bachelorproject.events.ChangeCodeViewEvent;
import com.example.green.bachelorproject.events.CloseFileEvent;
import com.example.green.bachelorproject.events.SaveFileEvent;
import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import com.example.green.bachelorproject.internalFileSystem.InternalFile;


/**
 * Created by Green on 07/04/15.
 */
public class NavigatorManager {
    private Activity activity;
    private Spinner files;

    private NavigatorView navigatorView;
    private GridView openFilesListView;

    private TreeNode root;
    private int currentPadding;
    private int paddingStep;


    private List<InternalFile> openFiles;

    private ArrayAdapter<InternalFile> adapter;

    private View currentOpenFileView;
    private int currentOpenFile;

    public NavigatorManager(NavigatorView navigatorView, Activity activity) {
        this.activity = activity;
        this.navigatorView = navigatorView;
        this.openFiles = new ArrayList<InternalFile>();

        this.openFilesListView =  (GridView) navigatorView.findViewById(R.id.navigator_open_files_list_view);
        this.adapter = new ArrayAdapter<InternalFile>(activity, android.R.layout.simple_list_item_1, openFiles);
        openFilesListView.setAdapter(adapter);

        root = TreeNode.root();
        paddingStep = 50;


        openFilesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                EventBus.getDefault().post(new ChangeCodeViewEvent(position, true));
            }
        });

        openFilesListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }

    public void changeOpenFileItem(int position) {
        openFilesListView.getChildAt(currentOpenFile).setSelected(false);
        currentOpenFile = position;
        openFilesListView.getChildAt(position).setSelected(true);
    }

    public void addOpenFile(InternalFile file) {
        if(openFiles.size() == 0) {
//            navigatorView.showListView();
        }
        openFiles.add(file);
        currentOpenFile = openFiles.indexOf(file);
        adapter.notifyDataSetChanged();
//        changeOpenFileItem(openFiles.size()-1);
    }

    public void removeOpenFile(InternalFile file) {
        openFiles.remove(file);
        if(openFiles.size() == 0) {
//            navigatorView.hideListView();
        } else {
            if(openFiles.indexOf(file) == openFiles.size()-1) {
                currentOpenFile--;
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class NavigatorFileViewAdapter extends ArrayAdapter<InternalFile>{

        public NavigatorFileViewAdapter() {
            super(activity, R.layout.navigator_file_view_layout, openFiles);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null) {
                itemView = activity.getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView closeButton = (TextView) itemView.findViewById(R.id.navigator_file_view_close_button);
            PrintView fileIcon = (PrintView) itemView.findViewById(R.id.navigator_file_view_icon);
            TextView fileName = (TextView) itemView.findViewById(R.id.navigator_file_view_name);
            final InternalFile currentFile = openFiles.get(position);
            fileName.setText(currentFile.getFileName());
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new CloseFileEvent(currentFile));
                    removeOpenFile(currentFile);

                }
            });

            return itemView;
        }
    }

    static class Holder extends TreeNode.BaseNodeViewHolder<IconTreeItem> {
        private PrintView arrow;
        private View viewNode;

        public Holder(Context context) {
            super(context);
        }

        @Override
        public View createNodeView(TreeNode treeNode, IconTreeItem iconTreeItem) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.tree_view_file_layout, null, false);
            viewNode = view;
            final PrintView image = (PrintView) view.findViewById(R.id.tree_view_file_image);
            final TextView text = (TextView) view.findViewById(R.id.tree_view_file_text);
            arrow = (PrintView) view.findViewById(R.id.tree_view_folder_arrow);
            TextView openButton = (TextView) view.findViewById(R.id.tree_view_file_open_button);
            TextView newFileButton = (TextView) view.findViewById(R.id.tree_view_file_new_file);
            TextView newFolderButton = (TextView) view.findViewById(R.id.tree_view_file_new_folder);

            if(!iconTreeItem.isFolder) {
                arrow.setVisibility(View.INVISIBLE);
                newFileButton.setVisibility(View.GONE);
                newFolderButton.setVisibility(View.GONE);

            } else {
                openButton.setVisibility(View.GONE);
            }

            openButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            image.setIconText(iconTreeItem.icon);
            text.setText(iconTreeItem.text);

            return view;
        }

        @Override
        public void toggle(boolean active) {
        }
    }

    static class IconTreeItem {
        public int icon;
        public String text;
        public boolean isFolder;
    }
}
