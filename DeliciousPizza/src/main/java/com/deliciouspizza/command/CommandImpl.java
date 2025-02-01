package com.deliciouspizza.command;

import com.deliciouspizza.command2.SessionManager;

public abstract class CommandImpl implements Command {

    private final SessionManager sessionManager;

    public CommandImpl(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

}
