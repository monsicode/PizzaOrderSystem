package com.deliciouspizza.command.cutomer;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.service.OrderService;

import java.nio.channels.SocketChannel;

public class CreateOrder implements Command {

    private final OrderService orderService;
    private final SessionManager manager;

    public CreateOrder(OrderService orderService, SessionManager manager) {
        this.orderService = orderService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {

        if (manager.isLoggedIn(client)) {
            String username = manager.getUsername(client);

            try {
                orderService.startNewOrder(username);
                return "New order started! ";
            } catch (IllegalStateException err) {
                return err.getMessage();
            }

        } else {
            return "Not logged in, error occurred";
        }

    }
}
