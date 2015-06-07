package com.example.green.bachelorproject.internalFileSystem;


/**
 * Created by Green on 04/04/15.
 */
public class InternalFile {
    //the internal url of this file
    private InternalUrl url;
    //the absolute path of this file in the cache
    private String absoluteCachePath;

    /**
     * Constructor that take an internal url as paramenter.
     *
     * @param url
     */
    public InternalFile(InternalUrl url, String absoluteCachePath) {
        this.url = url;
        this.absoluteCachePath = absoluteCachePath;
    }

    public String getExtension() {
        int position = getFileName().lastIndexOf(".");
        if(position >= 0)
            return getFileName().substring(getFileName().lastIndexOf("."), getFileName().length());
        return null;
    }
    /**
     * Returns the internal url.
     *
     * @return url
     */
    public InternalUrl getUrl() {
        return url;
    }

    /**
     * Returns the file type.
     *
     * @return url.getType()
     */
    public FileType getType() {
        return url.getType();
    }

    /**
     * Returns the account holding the file.
     *
     * @return url.getAccount()
     */
    public String getAccount() {
        return url.getAccount();
    }

    /**
     * Returns whether the file is folder or not.
     *
     * @return url.isFolder()
     */
    public boolean isFolder() {
        return url.isFolder();
    }

    /**
     * Returns the file name.
     *
     * @return url.getFileName()
     */
    public String getFileName() {
        return url.getFileName();
    }

    /**
     * Returns the path for the cache.
     *
     * @return url.getPathForCache()
     */
    public String getPathForCache() {
        return url.getPathForCache();
    }

    /**
     * Returns the actual cache path.
     *
     * @return absoluteCachePath
     */
    public String getAbsoluteCachePath() {
        return absoluteCachePath;
    }

    /**
     * Set the absolute cache path
     * @param absoluteCachePath
     */
    public void setAbsoluteCachePath(String absoluteCachePath) {
        this.absoluteCachePath = absoluteCachePath;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;

        if ( !(o instanceof InternalFile) ) return false;

        InternalFile other = (InternalFile) o;

        return this.url.equals(other.url);
    }
}




