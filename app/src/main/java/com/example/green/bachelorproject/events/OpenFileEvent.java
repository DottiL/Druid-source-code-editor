package com.example.green.bachelorproject.events;

import com.example.green.bachelorproject.internalFileSystem.InternalUrl;

/**
 * Created by Green on 04/04/15.
 */
public class OpenFileEvent {
    private InternalUrl url;

    public OpenFileEvent(InternalUrl url) {
        this.url = url;
    }

    public InternalUrl getUrl() {
        return url;
    }
}
