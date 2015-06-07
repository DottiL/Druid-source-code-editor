package com.example.green.bachelorproject.events;

import com.example.green.bachelorproject.internalFileSystem.InternalFile;
import com.example.green.bachelorproject.internalFileSystem.InternalUrl;

/**
 * Created by Green on 10/04/15.
 */
public class CloseFileEvent {
    private InternalFile file;
    private InternalUrl destination;

    public CloseFileEvent(InternalFile file) {
        this.file = file;
    }

    public InternalFile getFile() {
        return file;
    }
}
