package com.example.green.bachelorproject.FileSystemsNavigatorManagers;

import android.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.green.bachelorproject.R;
import com.example.green.bachelorproject.fragments.SaveFileDialogFragment;
import com.example.green.bachelorproject.internalFileSystem.FileType;
import com.example.green.bachelorproject.internalFileSystem.InternalFile;
import com.example.green.bachelorproject.internalFileSystem.InternalUrl;
import com.example.green.bachelorproject.managers.LocalFileSystemManager;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Green on 07/05/15.
 */
public class LocalListViewManager extends ListViewManager {
    private Fragment fragment;

    private List<String> currentContent;
    private LocalFileSystemManager manager;
    private File currentPath;
    private File currentSelectedFile;
    private int currentListenerID;
    private int mask = JNotify.FILE_CREATED
            | JNotify.FILE_DELETED
            | JNotify.FILE_MODIFIED
            | JNotify.FILE_RENAMED;

    public LocalListViewManager(Fragment fragment, ListView listView, LocalFileSystemManager manager) {
        super(listView);
        this.fragment = fragment;
        this.pathListener = (PathListener) fragment;
        this.manager = manager;
        this.currentContent = new ArrayList<String>();
        this.currentPath = manager.getRoot();
        show();
    }

    @Override
    public void show(InternalFile file) {
        currentPath = manager.getFile(file.getUrl()).getParentFile();
        show();
    }

    @Override
    public InternalUrl getSaveLocation() {
        return new InternalUrl(FileType.LOCAL, "Local", currentPath.getAbsolutePath() + "/" + ((SaveFileDialogFragment) fragment).getFileName(), false);
    }

    @Override
    public InternalUrl getOpenFileLocation() {
        return new InternalUrl(FileType.LOCAL, "Local", currentSelectedFile.getAbsolutePath(), true);
    }

    private void show() {
        currentContent = new ArrayList<String>(Arrays.asList(currentPath.list()));
        currentContent.add(0, "..");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentContent.get(position).equals("..")) {
                    if(currentPath.equals(manager.getRoot())) {
                        pathListener.onLocationChanged();
                    } else {
                        currentPath = currentPath.getParentFile();
                        show();
                        pathListener.onPathChanged("LOCAL://" + formatPath(currentPath.getAbsolutePath()), true);
                    }
                } else {
                    File selectedFile = new File(currentPath, currentContent.get(position));

                    if(selectedFile.isDirectory()) {
                        currentPath = selectedFile;
                        show();
                        pathListener.onPathChanged("LOCAL://" + formatPath(currentPath.getAbsolutePath()), true);
                    } else {
                        currentSelectedFile = selectedFile;
                        pathListener.onPathChanged("LOCAL://" + formatPath(currentSelectedFile.getAbsolutePath()), false);

                    }
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(fragment.getActivity(), R.layout.support_simple_spinner_dropdown_item, currentContent);

        listView.setAdapter(adapter);

        pathListener.onPathChanged("LOCAL://" + formatPath(currentPath.getAbsolutePath()), true);
    }

    private String formatPath(String path) {
        path = path.substring(path.lastIndexOf("0")+1, path.length());
        if(path.startsWith("/")) {
            path = path.substring(1, path.length());
        }
        return path;
    }

    public void updatePath(File path) {
        try {
            JNotify.removeWatch(currentListenerID);
        } catch (JNotifyException e) {
            e.printStackTrace();
        }
        currentPath = path;
        try {
            currentListenerID = JNotify.addWatch(currentPath.getAbsolutePath(), mask, false, new LocalPathListener());
        } catch (JNotifyException e) {
            e.printStackTrace();
        }
    }

    class LocalPathListener implements JNotifyListener {
        public void fileRenamed(int wd, String rootPath, String oldName,String newName) {
            show();
        }
        public void fileModified(int wd, String rootPath, String name) {
            show();
        }
        public void fileDeleted(int wd, String rootPath, String name) {
            show();
        }
        public void fileCreated(int wd, String rootPath, String name) {
            show();
        }
    }
}
