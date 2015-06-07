package com.example.green.bachelorproject.events;

/**
 * Created by Green on 31/05/15.
 */
public class KeyboardEvent {
    private boolean show;
    private boolean doAction;

    public KeyboardEvent(boolean show, boolean doAction) {
        this.show = show;
        this.doAction = doAction;
    }

    public boolean show() {
        return show;
    }

    public boolean doAction() { return doAction;}
}
