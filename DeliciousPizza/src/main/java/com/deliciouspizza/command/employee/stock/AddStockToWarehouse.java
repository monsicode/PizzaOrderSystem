package com.deliciouspizza.command.employee.stock;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.repository.Warehouse;

import java.nio.channels.SocketChannel;

public class AddStockToWarehouse implements Command {

    private final Warehouse warehouse;
    private final SessionManager manager;

    private static final int COUNT_NEEDED_ARGUMENTS = 2;
    private static final int PRODUCT_KEY_FIELD = 0;
    private static final int QUANTITY_KEY_FIELD = 1;

    public AddStockToWarehouse(Warehouse warehouse, SessionManager manager) {
        this.warehouse = warehouse;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        if (args.length != COUNT_NEEDED_ARGUMENTS) {
            return "Usage: add-stock <product-key> <quantity>";
        }

        if (manager.isLoggedIn(client)) {
            String username = manager.getUsername(client);

            if (manager.isUserEmployee(username)) {
                String productKey = args[PRODUCT_KEY_FIELD];
                int quantity = Integer.parseInt(args[QUANTITY_KEY_FIELD]);

                try {
                    warehouse.addStockInWarehouse(productKey, quantity);
                    return "Product " + productKey + " stocked successfully with " + quantity;
                } catch (ProductDoesNotExistException | IllegalArgumentException err) {
                    return err.getMessage();
                }
            } else {
                return "You don't have the rights for this command!";
            }

        } else {
            return "Not logged in, error occurred";
        }

    }

}