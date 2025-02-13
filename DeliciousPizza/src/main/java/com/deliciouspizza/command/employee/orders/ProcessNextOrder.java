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
            String username = manager.getUsername(client);

            if (manager.isUserEmployee(username)) {
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
                return "You don't have the rights for this command!";
            }

        } else {
            return "Not logged in, error occurred";
        }
    }

}