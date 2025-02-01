package com.deliciouspizza.command.cutomer;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.service.OrderService;

public class CreateOrder implements Command {

    private final OrderService orderService;
    private final String username;

    public CreateOrder(OrderService orderService, String username) {
        this.orderService = orderService;
        this.username = username;
    }

    @Override
    public String execute(String[] args) {

        try {
            orderService.startNewOrder(username);
            return "New order started! ";
        } catch (IllegalStateException err) {
            return err.getMessage();
        }

    }
}
