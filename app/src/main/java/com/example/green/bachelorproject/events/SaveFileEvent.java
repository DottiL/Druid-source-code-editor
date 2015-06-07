package com.example.green.bachelorproject.events;

import com.example.green.bachelorproject.internalFileSystem.InternalFile;

/**
 * Created by Green on 06/04/15.
 */
public class SaveFileEvent {
    private InternalFile file;
    private boolean close;

    public SaveFileEvent(InternalFile file, boolean close) {
        this.file = file;
        this.close = close;
    }

    public InternalFile getFile() {
        return file;
    }

    public boolean getClose() {
        return close;
    }
}
