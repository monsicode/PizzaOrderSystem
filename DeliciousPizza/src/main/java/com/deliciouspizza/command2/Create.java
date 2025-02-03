package com.deliciouspizza.command2;

import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.service.OrderService;

import java.nio.channels.SocketChannel;

public class Create implements Command {

    SessionManager manager;
    OrderService orderService;

    public Create(SessionManager manager, OrderService orderService) {
        this.manager = manager;
        this.orderService = orderService;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        if (manager.isLoggedIn(client)) {
            String username = manager.getUsername(client);

            return "User " + username + "created";
        } else {
            return "Not logged in, error occured";
        }
    }

}
