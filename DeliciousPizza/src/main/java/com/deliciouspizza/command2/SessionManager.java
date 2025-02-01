package com.deliciouspizza.command2;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    // Съхраняваме единствената инстанция на SessionManager
    private static final SessionManager instance = new SessionManager();

    // Използваме ConcurrentHashMap за безопасност при многозадачност
    private final ConcurrentHashMap<SocketChannel, String> userSessions = new ConcurrentHashMap<>();

    // Приватен конструктор за да не се създават нови инстанции извън класа
    SessionManager() {
    }

    // Статичен метод за достъп до единствената инстанция
    public static SessionManager getInstance() {
        return instance;
    }

    // Добавяме сесия за даден клиент
    public void addSession(SocketChannel clientChannel, String username) {
        userSessions.put(clientChannel, username);
    }

    // Връщаме потребителското име на клиента по неговия SocketChannel
    public String getUsername(SocketChannel clientChannel) {
        return userSessions.get(clientChannel);
    }

    // Премахваме сесията за даден клиент
    public void removeSession(SocketChannel clientChannel) {
        userSessions.remove(clientChannel);
    }

    // Проверяваме дали даден клиент е логнат
    public boolean isLoggedIn(SocketChannel clientChannel) {
        return userSessions.containsKey(clientChannel);
    }
}
