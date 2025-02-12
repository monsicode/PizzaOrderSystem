package com.deliciouspizza.command;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final ConcurrentHashMap<SocketChannel, String> userSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, SocketChannel> userToClientMap = new ConcurrentHashMap<>();

    public void addSession(SocketChannel clientChannel, String username) {
        userSessions.put(clientChannel, username);
        userToClientMap.put(username, clientChannel);
    }

    public String getUsername(SocketChannel clientChannel) {
        return userSessions.get(clientChannel);
    }

    public SocketChannel getClientByUsername(String username) {
        return userToClientMap.get(username);
    }

    public void removeSession(SocketChannel clientChannel) {
        userSessions.remove(clientChannel);
    }

    public boolean isLoggedIn(SocketChannel clientChannel) {
        return userSessions.containsKey(clientChannel);
    }
}
