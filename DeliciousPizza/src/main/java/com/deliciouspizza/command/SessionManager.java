package com.deliciouspizza.command;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final ConcurrentHashMap<SocketChannel, String> userSessions = new ConcurrentHashMap<>();

    public void addSession(SocketChannel clientChannel, String username) {
        userSessions.put(clientChannel, username);
    }

    public String getUsername(SocketChannel clientChannel) {
        return userSessions.get(clientChannel);
    }

    public void removeSession(SocketChannel clientChannel) {
        userSessions.remove(clientChannel);
    }

    public boolean isLoggedIn(SocketChannel clientChannel) {
        return userSessions.containsKey(clientChannel);
    }
}
