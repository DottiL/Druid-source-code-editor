package com.example.green.bachelorproject.managers;

import android.app.Activity;
import android.util.Log;
import com.example.green.bachelorproject.internalFileSystem.InternalFile;
import com.example.green.bachelorproject.internalFileSystem.InternalUrl;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Green on 21/03/15.
 */
public class FileManager {
    private NavigatorManager navigatorManager;
    private DropboxManager dbManager;
    private LocalFileSystemManager localManager;
    private CacheManager cacheManager;
    private List<InternalUrl> openFiles;
    private List<InternalUrl> folders;
    private int nbTempFiles;
    private MainLayoutManager mainLayoutManager;

    public FileManager(MainLayoutManager mainLayoutManager, DropboxManager dbManager, LocalFileSystemManager localManager, CacheManager cacheManager, Activity activity) {
        this.mainLayoutManager = mainLayoutManager;
        this.dbManager = dbManager;
        this.localManager = localManager;
        this.cacheManager = cacheManager;
        this.nbTempFiles = 0;
        this.openFiles = new ArrayList<InternalUrl>();
        this.folders = new ArrayList<InternalUrl>();
    }

    public void openExistingFile(InternalUrl url) {
        switch(url.getType()) {
            case DBX:
                if(url.isFolder()) {
                    if(!folders.contains(url)) {
                        folders.add(url);
                    }
                } else {
                    if(!openFiles.contains(url)) {
                        openFiles.add(url);
                        InternalFile iFile = cacheManager.createCacheFile(url);
                        String content = dbManager.getFileContent(url);
                        cacheManager.writeToCacheFile(content, iFile);
                        mainLayoutManager.addOpenFile(iFile, content);
                    }
                }
                break;
            case LOCAL:
                if(!url.isFolder()) {
                    if(!openFiles.contains(url)) {
                        openFiles.add(url);
                        InternalFile iFile = cacheManager.createCacheFile(url);
                        String content = localManager.getFileContent(url);
                        cacheManager.writeToCacheFile(content, iFile);
                        mainLayoutManager.addOpenFile(iFile, content);
                    }
                }
                break;
        }
    }

    public void openNewFile() {
        InternalFile file = cacheManager.createNewFile();
        mainLayoutManager.addOpenFile(file, "");
    }

    public void saveFileTo(InternalFile file, InternalUrl destination) {
        String content = cacheManager.readFromCache(file);
        if(content == null) {
            content = "";
        }
        InternalUrl url;
        switch (destination.getType()) {
            case DBX:
                url = dbManager.createFile(file, destination);
                dbManager.writeToFile(content, url);
                break;
            case LOCAL:
                url = localManager.createFile(file, destination);
                localManager.writeToFile(content, url);
                break;
        }
    }

    public void closeFile(InternalFile file) {
        openFiles.remove(file.getUrl());
        mainLayoutManager.removeOpenFile(file);
        cacheManager.deleteFile(file);
    }

    public void updateCacheFile(String content, InternalFile file) {
        cacheManager.updateCacheFile(content, file);
    }

    public InternalFile getCurrentFile() {
        return mainLayoutManager.getCurrentFile();
    }
}
