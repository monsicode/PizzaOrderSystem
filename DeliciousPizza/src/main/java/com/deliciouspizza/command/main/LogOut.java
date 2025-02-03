package com.deliciouspizza.command.main;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;

import java.nio.channels.SocketChannel;

public class LogOut implements Command {

    private final SessionManager manager;

    public LogOut(SessionManager manager) {
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {

        if (manager.isLoggedIn(client)) {
            manager.removeSession(client);
            return "Logging out... " + new ShowMainMenu().execute(args, client);
        } else {
            return "Not logged in, error occurred";
        }

    }

}
