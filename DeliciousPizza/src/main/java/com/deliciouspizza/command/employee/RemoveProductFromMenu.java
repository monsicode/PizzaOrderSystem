package com.deliciouspizza.command.employee;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.exception.ProductAlreadyDeactivatedException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.service.ProductService;

import java.nio.channels.SocketChannel;

public class RemoveProductFromMenu implements Command {

    private final ProductService productService;
    private final SessionManager manager;

    private static final int COUNT_NEEDED_ARGUMENTS = 1;
    private static final int PRODUCT_KEY_FIELD = 0;

    public RemoveProductFromMenu(ProductService productService, SessionManager manager) {
        this.productService = productService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        if (args.length != COUNT_NEEDED_ARGUMENTS) {
            return "Usage: add-product <product-key> <quantity>";
        }

        if (manager.isLoggedIn(client)) {
            String username = manager.getUsername(client);

            if (manager.isUserEmployee(username)) {
                String productKey = args[PRODUCT_KEY_FIELD];
                try {
                    Product product = productService.getProductByKey(productKey);
                    productService.deactivateProduct(product);
                    return "Product " + productKey + " removed from menu successfully!";
                } catch (ProductDoesNotExistException | ProductAlreadyDeactivatedException |
                         IllegalArgumentException err) {
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
