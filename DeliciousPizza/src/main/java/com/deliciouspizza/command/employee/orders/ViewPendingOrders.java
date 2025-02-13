package com.deliciouspizza.command.employee.orders;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.service.OrderService;

import java.nio.channels.SocketChannel;

public class ViewPendingOrders implements Command {

    private final OrderService orderService;
    private final SessionManager manager;

    public ViewPendingOrders(OrderService orderService, SessionManager manager) {
        this.orderService = orderService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        if (manager.isLoggedIn(client)) {
            String username = manager.getUsername(client);

            if (manager.isUserEmployee(username)) {
                if (!orderService.getPendingOrders().isEmpty()) {
                    return orderService.formatPendingOrders();
                } else {
                    return "No pending";
                }

            } else {
                return "You don't have the rights for this command!";
            }

        } else {
            return "Not logged in, error occurred";
        }
    }

}