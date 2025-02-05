package com.deliciouspizza.command.employee.stock;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.repository.Warehouse;

import java.nio.channels.SocketChannel;

public class ViewCatalogProducts implements Command {

    private final Warehouse warehouse;
    private final SessionManager manager;

    private static final int COUNT_NEEDED_ARGUMENTS = 1;
    private static final int PRODUCT_NAME_FIELD = 0;

    public ViewCatalogProducts(Warehouse warehouse, SessionManager manager) {
        this.warehouse = warehouse;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        if (args.length != COUNT_NEEDED_ARGUMENTS) {
            return "Usage: catalog <product-name>";
        }

        if (manager.isLoggedIn(client)) {
            return warehouse.getCatalogProduct(args[PRODUCT_NAME_FIELD]);
        } else {
            return "Not logged in, error occurred";
        }
    }

}