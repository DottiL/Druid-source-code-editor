package com.example.green.bachelorproject.internalFileSystem;


/**
 * This class represents an internal url. This url is used in the application to represent the different files that it has to deal with. It generalize
 * all the files with a general url: <type>://<account>/<original path>.
 * <type>:  - LOCAL
 *          - DBX
 *          - CACHE
 *
 * <account>: the account that hold the file. If the file is not saved either in DBX, its account will be "Cache" if the type if cache or "Local" is it's
 *            local.
 *
 * <orignal path>: the path of the file in its file system.
 *
 * Created by Green on 04/04/15.
 */
public class InternalUrl {
    //type of the file
    private FileType type;
    //the account
    private String account;
    //the original path in its filesystem
    private String originalPath;
    //the file name
    private String fileName;
    //if its a folder or not
    private boolean isFolder;

    /**
     * The constructor. It take the type of the file, the account holding the file, the original path of the file and whether it is folder or not.
     *
     * @param type
     * @param account
     * @param originalPath
     * @param isFolder
     */
    public InternalUrl(FileType type, String account, String originalPath, boolean isFolder) {
        this.type = type;
        this.account = account;
        this.originalPath = originalPath;
        this.fileName = originalPath.substring(originalPath.lastIndexOf("/") + 1, originalPath.length());
        this.isFolder = isFolder;
    }


    /**
     * Returns the path usedin the cache. If for example we have DBX://account/folder1/test.txt, this will return DBX/account/folder1/test.txt.
     * This function also make sure that the original path starts with "/".
     *
     * @return path for the cache
     */
    public String getPathForCache() {
        if(originalPath.startsWith("/")) {
            return "/" + type.toString() + "/" + account + originalPath;
        } else {
            return "/" + type.toString() + "/" + account + "/" + originalPath;
        }
    }

    /**
     * Returns the original path of the file in its filesystem.
     *
     * @return originalPath
     */
    public String getOriginalPath() {
        return originalPath;
    }

    /**
     * Returns the type of the file.
     *
     * @return type
     */
    public FileType getType() {
        return type;
    }

    /**
     * Returns the account that hold the file.
     *
     * @return account
     */
    public String getAccount() {
        return account;
    }

    /**
     * Returns the name of this file.
     *
     * @return fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns whether it is a folder or not.
     *
     * @return isFolder
     */
    public boolean isFolder() {
        return isFolder;
    }

    /**
     * Returns the internal url used.
     *
     * @return internal url
     */
    @Override
    public String toString() {
        return type.toString() + "://" + account + originalPath;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;

        if ( !(o instanceof InternalUrl) ) return false;

        InternalUrl other = (InternalUrl) o;

        return
            this.type == other.type &&
            this.account.equals(other.account) &&
            this.originalPath.equals(other.originalPath) &&
            this.fileName.equals(other.fileName) &&
            this.isFolder == other.isFolder();
    }

    public void rename(String name) {
        fileName = name;
        originalPath = originalPath.substring(0, originalPath.lastIndexOf("/")) + fileName;
    }
 }
