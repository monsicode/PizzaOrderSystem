package com.deliciouspizza.command.cutomer;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.service.ProductService;

import java.nio.channels.SocketChannel;
import java.util.Map;

public class ViewActiveProducts implements Command {

    private final ProductService productService;

    public ViewActiveProducts(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        StringBuilder result = new StringBuilder("List with active products:\n");
        Map<String, Product> activeProducts = productService.getAllActiveProducts();

        for (Map.Entry<String, Product> entry : activeProducts.entrySet()) {
            String key = entry.getKey();
            Product product = entry.getValue();
            String details = product.getFormattedDetails();

            result.append(String.format("- %-45s KEY: %s%n", details, key));
        }

        result.append("-----------------------------------");
        return result.toString();
    }
}
