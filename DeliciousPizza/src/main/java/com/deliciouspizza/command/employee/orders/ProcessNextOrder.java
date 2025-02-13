package com.deliciouspizza.command.employee.orders;

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
                String customer = orderService.getUserForCurrentOrder();
                String addressCustomer = orderService.getDeliveryAddress(customer);

                orderService.processCurrentOrder();
                return "Order processed for user: " + customer + " --- Address: " + addressCustomer;

            } catch (InterruptedException err) {
                Thread.currentThread().interrupt();
                return "Error processing the order";
            }
        } else {
            return "Not logged in, error occurred";
        }
    }

}