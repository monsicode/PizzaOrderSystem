package com.deliciouspizza.command2;

import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.service.UserService;

import java.nio.channels.SocketChannel;

public class Log implements Command {

    SessionManager manager;
    UserService userService;

    public Log(SessionManager manager, UserService userService) {
        this.manager = manager;
        this.userService = userService;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        if (args.length != 2) {
            return "Usage: login <username> <password>";
        }
        String username = args[0].trim();

        manager.addSession(client, username);
        return "User " + username + " successfully logged in.";
    }

}
