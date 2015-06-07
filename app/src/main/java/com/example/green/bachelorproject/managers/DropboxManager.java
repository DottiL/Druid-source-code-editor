package com.example.green.bachelorproject.managers;

import android.app.Activity;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.green.bachelorproject.internalFileSystem.FileType;
import com.example.green.bachelorproject.internalFileSystem.InternalFile;
import com.example.green.bachelorproject.internalFileSystem.InternalUrl;

/**
 * Created by Green on 18/03/15.
 */
public class DropboxManager {

    private Activity linkedActivity;

    private String appKey = "2ulv2aqfu2g1eii";
    private String appSecret = "f0xbvyg11j2i1zl";
    private DbxAccountManager dbxAccMgr;
    static final int REQUEST_LINK_TO_DBX = 10;

    public DropboxManager(Activity linkedActivity) {
        this.linkedActivity = linkedActivity;
        dbxAccMgr = DbxAccountManager.getInstance(linkedActivity.getApplicationContext(), appKey, appSecret);
    }

    public void linkAccount() {
        dbxAccMgr.startLink((Activity) linkedActivity, REQUEST_LINK_TO_DBX);
    }

    public List<DbxAccount> getLinkedAccounts() {
        return dbxAccMgr.getLinkedAccounts();
    }

    public DbxFileSystem getFileSystemByAccount(DbxAccount account) {
        DbxFileSystem dbfs = null;
        try {
            dbfs = DbxFileSystem.forAccount(account);
        } catch (DbxException.Unauthorized unauthorized) {
            unauthorized.printStackTrace();
        }
        return dbfs;
    }

    public List<DbxFileInfo> getFolderContent(InternalUrl folder) {
        DbxFileSystem fs = getFileSystemByAccount(getAccountByString(folder.getAccount()));
        List<DbxFileInfo> content = null;

        try {
             content = fs.listFolder(new DbxPath(folder.getOriginalPath()));
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return content;
    }

    public DbxFileInfo getFileInfo(DbxPath path, DbxAccount account) {
        DbxFileSystem fs = getFileSystemByAccount(account);
        DbxFileInfo info = null;

        try {
            info = fs.getFileInfo(path);
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return info;
    }

    public DbxPath getParent(InternalFile file) {
        DbxFile dbFile = getFileByUrl(file.getUrl());
        DbxPath parent = null;
        try {
            parent = dbFile.getInfo().path.getParent();
            dbFile.close();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return parent;
    }

    public void addListener(DbxAccountManager.AccountListener listener) {
        dbxAccMgr.addListener(listener);
    }

    public DbxAccount getAccountByString(String account) {
        List<DbxAccount> accounts = getLinkedAccounts();
        for(DbxAccount a: accounts) {
            if(account.equals(a.getAccountInfo().userName)) {
                return a;
            }
        }
        return null;
    }

    public DbxFileInfo getFileInfo(InternalFile file) {
        DbxFile dbFile = getFileByUrl(file.getUrl());
        DbxFileInfo info = null;
        try {
            info = dbFile.getInfo();
            dbFile.close();
        } catch (DbxException e) {
            e.printStackTrace();
        }

        return info;
    }

    private DbxFile getFileByUrl(InternalUrl url) {
        DbxAccount account = getAccountByString(url.getAccount());
        DbxFileSystem fs = getFileSystemByAccount(account);
        DbxFile file = null;

        try {
            file = fs.open(new DbxPath(url.getOriginalPath()));
        } catch (DbxException e) {
            e.printStackTrace();
        }

        return file;
    }

    public String getFileContent(InternalUrl url) {
        DbxFile file = getFileByUrl(url);
        String content = null;
        try {
            content = file.readString();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public void writeToFile(String content, InternalUrl destination) {
        DbxFile file = getFileByUrl(destination);
        try {
            file.writeString(content);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Toast toast = Toast.makeText(linkedActivity, destination.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public List<String> listParentFolder(InternalFile file) {
        List<String> content = new ArrayList<>();
        DbxPath parent = getParent(file);
        DbxFileSystem fs = getFileSystemByAccount(getAccountByString(file.getAccount()));
        List<DbxFileInfo> cont = null;
        try {
            cont = fs.listFolder(parent);
        } catch (DbxException e) {
            e.printStackTrace();
        }

        for(DbxFileInfo info: cont) {
            content.add(info.path.getName());
        }

        return content;
    }

    public InternalUrl createFile(InternalFile file, InternalUrl destination) {
        DbxFileSystem fs = getFileSystemByAccount(getAccountByString(destination.getAccount()));
        DbxPath path;

        if(destination.isFolder()) {
             path = new DbxPath(destination.getOriginalPath() + "/" + file.getFileName());
        } else {
            path = new DbxPath(destination.getOriginalPath());
        }

        DbxFile dbFile = null;
        InternalUrl url = null;
        try {
            dbFile = fs.create(path);
            url = new InternalUrl(FileType.DBX, destination.getAccount(), dbFile.getPath().toString(), dbFile.getInfo().isFolder);
            dbFile.close();
        } catch (DbxException e) {
            url = new InternalUrl(FileType.DBX, destination.getAccount(), path.toString(), destination.isFolder());
        }

        return url;
    }

}
