package com.deliciouspizza.command.employee.stock;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.repository.Warehouse;

import java.nio.channels.SocketChannel;

public class ViewWarehouse implements Command {

    private final Warehouse warehouse;
    private final SessionManager manager;

    public ViewWarehouse(Warehouse warehouse, SessionManager manager) {
        this.warehouse = warehouse;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        if (manager.isLoggedIn(client)) {
            String username = manager.getUsername(client);

            if (manager.isUserEmployee(username)) {
                return warehouse.getStock();
            } else {
                return "You don't have the rights for this command!";
            }

        } else {
            return "Not logged in, error occurred";
        }
    }

}