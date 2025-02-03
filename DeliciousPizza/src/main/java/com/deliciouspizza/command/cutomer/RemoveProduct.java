package com.deliciouspizza.command.cutomer;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.exception.ProductException;
import com.deliciouspizza.service.OrderService;

import java.nio.channels.SocketChannel;

public class RemoveProduct implements Command {

    private final OrderService orderService;
    private final SessionManager manager;

    public RemoveProduct(OrderService orderService, SessionManager manager) {
        this.orderService = orderService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {

        if (args.length != 2) {
            return "Usage: add-product %s %s<product-key> <quantity>";
        }

        if (manager.isLoggedIn(client)) {
            String username = manager.getUsername(client);
            String productKey = args[0];
            int quantity = Integer.parseInt(args[1]);

            try {
                orderService.removeFromCurrentOrderForUser(username, productKey, quantity);
                return "Product " + productKey + " removed!\n" + orderService.showCurrentOrderForUser(username);
            } catch (IllegalStateException | ProductException err) {
                return err.getMessage();
            }

        } else {
            return "Not logged in, error occurred";
        }

    }
}
