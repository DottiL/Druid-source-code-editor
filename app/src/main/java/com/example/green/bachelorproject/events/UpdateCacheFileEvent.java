package com.example.green.bachelorproject.events;

import com.example.green.bachelorproject.internalFileSystem.InternalFile;

/**
 * Created by Green on 06/04/15.
 */
public class UpdateCacheFileEvent {
    private String content;
    private InternalFile internalFile;

    public UpdateCacheFileEvent(String content, InternalFile internalFile) {
        this.content = content;
        this.internalFile = internalFile;
    }

    public InternalFile getInternalFile() {
        return internalFile;
    }

    public String getContent() {
        return content;
    }
}
