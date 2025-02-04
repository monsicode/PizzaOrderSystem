package com.deliciouspizza.command.employee;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.service.OrderService;

import java.nio.channels.SocketChannel;

public class ProcessNextOrder implements Command {

    private final OrderService orderService;
    private final SessionManager manager;

    public ProcessNextOrder(OrderService orderService, SessionManager manager) {
        this.orderService = orderService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        if (manager.isLoggedIn(client)) {
            try {
                orderService.processCurrentOrder();
                return "Order processed successfully!";
            } catch (InterruptedException err) {
                Thread.currentThread().interrupt();
                return "Error processing the order";
            }
        } else {
            return "Not logged in, error occurred";
        }
    }

}
