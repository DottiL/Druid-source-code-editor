package com.example.green.bachelorproject.events;

/**
 * Created by Green on 11/04/15.
 */
public class ChangeCodeViewEvent {
    private boolean firedByNavigator;
    private int position;

    public ChangeCodeViewEvent(int position, boolean firedByNavigator) {
        this.position = position;
        this.firedByNavigator = firedByNavigator;
    }

    public int getPosition() {
        return position;
    }

    public boolean getFiredByNavigator() {
        return firedByNavigator;
    }
}
