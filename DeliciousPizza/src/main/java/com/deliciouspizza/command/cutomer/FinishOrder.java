package com.deliciouspizza.command.cutomer;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.service.OrderService;

import java.nio.channels.SocketChannel;

public class FinishOrder implements Command {

    private final OrderService orderService;
    private final SessionManager manager;

    public FinishOrder(OrderService orderService, SessionManager manager) {
        this.orderService = orderService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {

        if (manager.isLoggedIn(client)) {
            String username = manager.getUsername(client);

            try {
                orderService.finalizeOrder(username);
                return "Order finalized successfully!\n" + orderService.showCurrentOrderForUser(username);
            } catch (IllegalStateException err) {
                return err.getMessage();
            }

        } else {
            return "Not logged in, error occurred";
        }

    }

}
