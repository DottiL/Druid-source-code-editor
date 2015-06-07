package com.example.green.bachelorproject.FileSystemsNavigatorManagers;

import android.widget.ListView;

import com.example.green.bachelorproject.internalFileSystem.InternalFile;
import com.example.green.bachelorproject.internalFileSystem.InternalUrl;

/**
 * Created by Green on 07/05/15.
 */
public abstract class ListViewManager {
    protected ListView listView;
    protected PathListener pathListener;

    public interface PathListener {
        public void onPathChanged(String path, boolean isFolder);
        public void onLocationChanged();
    }

    public ListViewManager(ListView listView) {
        this.listView = listView;
    }
    public abstract void show(InternalFile file);
    public abstract InternalUrl getSaveLocation();
    public abstract InternalUrl getOpenFileLocation();
}
