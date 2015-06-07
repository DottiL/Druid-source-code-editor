package com.example.green.bachelorproject.FileSystemsNavigatorManagers;

import android.app.Fragment;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.example.green.bachelorproject.R;
import com.example.green.bachelorproject.fragments.SaveFileDialogFragment;
import com.example.green.bachelorproject.internalFileSystem.FileType;
import com.example.green.bachelorproject.internalFileSystem.InternalFile;
import com.example.green.bachelorproject.internalFileSystem.InternalUrl;
import com.example.green.bachelorproject.managers.DropboxManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Green on 07/05/15.
 */
public class DbxListViewManager extends ListViewManager {
    private DropboxManager dbManager;
    private Fragment fragment;
    private DbxPath currentPath;
    private DbxPath currentSelectedFile;
    private DbxAccount currentAccount;
    private List<String> currentContent;
    private DbxFileSystem.PathListener currentListener;
    private View currentView;
    private int currentViewPostion;

    public DbxListViewManager(Fragment fragment, ListView listView, DropboxManager dbManager) {
        super(listView);
        this.dbManager = dbManager;
        dbManager.addListener(new DbxAccountManager.AccountListener() {
            @Override
            public void onLinkedAccountChange(DbxAccountManager dbxAccountManager, DbxAccount dbxAccount) {
                dbxAccount.addListener(new DbxAccount.Listener() {
                    @Override
                    public void onAccountChange(DbxAccount dbxAccount) {
                        showAccounts();
                    }
                });
            }
        });
        this.fragment = fragment;
        pathListener = (PathListener) fragment;
        currentContent = new ArrayList<String>();
        currentPath = DbxPath.ROOT;
        showAccounts();
    }

    public void show(InternalFile file) {
        currentAccount = dbManager.getAccountByString(file.getAccount());
        updateCurrentPath(dbManager.getFileInfo(file).path.getParent());
        show();
    }

    @Override
    public InternalUrl getSaveLocation() {
        return new InternalUrl(FileType.DBX, currentAccount.getAccountInfo().userName, currentPath.toString() + "/" + ((SaveFileDialogFragment) fragment).getFileName(), false);
    }

    @Override
    public InternalUrl getOpenFileLocation() {
        return new InternalUrl(FileType.DBX, currentAccount.getAccountInfo().userName, currentSelectedFile.toString(), false);
    }

    private void showAccounts() {
        List<DbxAccount> accounts = dbManager.getLinkedAccounts();
        currentContent = new ArrayList<String>();
        currentContent.add("..");

        for(DbxAccount account: accounts) {
            currentContent.add(account.getAccountInfo().userName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(fragment.getActivity(), R.layout.support_simple_spinner_dropdown_item, currentContent);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentContent.get(position).equals("..")) {
                    pathListener.onLocationChanged();
                } else {
                    currentAccount = dbManager.getAccountByString(currentContent.get(position));
                    pathListener.onPathChanged("DBX://" + currentAccount.getAccountInfo().userName + currentPath.toString(), true);
                    updateCurrentPath(currentPath);
//                    final ProgressDialog progressDialog = ProgressDialog.show(fragment.getActivity(),
//                            "Loading account",
//                            "Loading account ...", true);

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                show();
//                                Thread.sleep(10000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            if (progressDialog!=null) {
//                                if (progressDialog.isShowing()) {
//                                    progressDialog.dismiss();
//                                }
//                            }
//
//                        }
//                    }).start();
                    show();
                }
            }
        });
    }

    private void show() {
        DbxFileSystem fs = dbManager.getFileSystemByAccount(currentAccount);
        List<DbxFileInfo> content = null;
        try {
            content = fs.listFolder(currentPath);
        } catch (DbxException e) {
            e.printStackTrace();
        }
        currentContent = new ArrayList<String>();
        currentContent.add("..");
        for(DbxFileInfo info: content) {
            currentContent.add(info.path.getName());
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentContent.get(position).equals("..")) {
                    if(currentPath == DbxPath.ROOT) {
                        showAccounts();
                        pathListener.onPathChanged("DBX://" + currentPath.toString(), true);
                    } else {
                        updateCurrentPath(currentPath.getParent());
                        pathListener.onPathChanged("DBX://" + currentAccount.getAccountInfo().userName + currentPath.toString(), true);
                        show();
                    }
                } else {
                    TextView fileName = (TextView) view;
                    DbxPath temp = new DbxPath(currentPath + "/" + fileName.getText().toString());
                    DbxFileSystem fs = dbManager.getFileSystemByAccount(currentAccount);
                    DbxFileInfo info = null;

                    try {
                        info = fs.getFileInfo(temp);
                    } catch (DbxException e) {
                        e.printStackTrace();
                    }

                    if(info.isFolder) {
                        updateCurrentPath(temp);
                        pathListener.onPathChanged("DBX://" + currentAccount.getAccountInfo().userName + currentPath.toString(), true);
                        show();
                    } else {
                        currentSelectedFile = temp;
                        pathListener.onPathChanged("DBX://" + currentAccount.getAccountInfo().userName + currentPath.toString(), dbManager.getFileInfo(currentSelectedFile,currentAccount).isFolder);

                        if(currentView != null) {
                            listView.setItemChecked(currentViewPostion, false);
                            currentView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        }
                            listView.setItemChecked(position, true);
                            view.setBackgroundColor(Color.parseColor("#E0E0E0"));
                            currentView = view;
                            currentViewPostion = position;
                    }
                }
            }
        });

        if(fragment.getActivity() != null && currentContent != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(fragment.getActivity(), R.layout.support_simple_spinner_dropdown_item, currentContent);

            listView.setAdapter(adapter);

            pathListener.onPathChanged("DBX://" + currentAccount.getAccountInfo().userName + currentPath.toString(), true);
        }

    }

    public void updateCurrentPath(DbxPath newPath) {
        DbxFileSystem fs = dbManager.getFileSystemByAccount(currentAccount);
        if(currentListener != null) {
            fs.removePathListenerForAll(currentListener);
        }

        currentPath = newPath;
        currentListener = new DbxFileSystem.PathListener() {
            @Override
            public void onPathChange(DbxFileSystem dbxFileSystem, DbxPath dbxPath, Mode mode) {
                show();
            }
        };

        fs.addPathListener(currentListener,currentPath, DbxFileSystem.PathListener.Mode.PATH_OR_CHILD);
    }
}
