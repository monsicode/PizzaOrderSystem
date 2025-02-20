package com.deliciouspizza.command.cutomer;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.exception.ProductException;
import com.deliciouspizza.exception.UnderAgedException;
import com.deliciouspizza.service.OrderService;

import java.nio.channels.SocketChannel;

public class AddProduct implements Command {

    private final OrderService orderService;
    private final SessionManager manager;

    private static final int COUNT_NEEDED_ARGUMENTS = 2;
    private static final int PRODUCT_KEY_FIELD = 0;
    private static final int QUANTITY_FIELD = 1;

    public AddProduct(OrderService orderService, SessionManager manager) {
        this.orderService = orderService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {

        if (args.length != COUNT_NEEDED_ARGUMENTS) {
            return "Usage: add-product <product-key> <quantity>";
        }

        if (manager.isLoggedIn(client)) {
            String username = manager.getUsername(client);
            String productKey = args[PRODUCT_KEY_FIELD];

            try {
                int quantity = Integer.parseInt(args[QUANTITY_FIELD]);
                orderService.addProductToActiveOrder(username, productKey, quantity);
                return "Product " + productKey + " added!\n" + orderService.showCurrentOrderForUser(username);
            } catch (ProductException | UnderAgedException | IllegalStateException | NumberFormatException err) {
                return err.getMessage();
            }

        } else {
            return "Not logged in, error occurred";
        }

    }
}
