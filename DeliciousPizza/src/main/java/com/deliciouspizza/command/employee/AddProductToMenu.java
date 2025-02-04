package com.deliciouspizza.command.employee;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.exception.ProductAlreadyActiveException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.service.ProductService;

import java.nio.channels.SocketChannel;

public class AddProductToMenu implements Command {

    private final ProductService productService;
    private final SessionManager manager;

    private static final int COUNT_NEEDED_ARGUMENTS = 1;
    private static final int PRODUCT_KEY_FIELD = 0;

    public AddProductToMenu(ProductService productService, SessionManager manager) {
        this.productService = productService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        if (args.length != COUNT_NEEDED_ARGUMENTS) {
            return "Usage: add-product <product-key> <quantity>";
        }

        if (manager.isLoggedIn(client)) {
            String productKey = args[PRODUCT_KEY_FIELD];

            try {
                Product product = productService.getProductByKey(productKey);
                productService.activateProduct(product);
                return "Product " + productKey + " activated successfully";
            } catch (ProductDoesNotExistException | ProductAlreadyActiveException err) {
                return err.getMessage();
            }

        } else {
            return "Not logged in, error occurred";
        }
    }
}
