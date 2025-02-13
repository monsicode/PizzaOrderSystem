package com.deliciouspizza.command.main;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.service.ProductService;

import java.nio.channels.SocketChannel;
import java.util.Map;

public class ViewActiveProducts implements Command {

    private final ProductService productService;

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[38;5;214m";


    public ViewActiveProducts(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        StringBuilder result = new StringBuilder("List with active products:\n");

        result.append(BLUE).append("----------------------------").append(RESET).append("\n");

        Map<String, Product> activeProducts = productService.getAllActiveProducts();

        for (Map.Entry<String, Product> entry : activeProducts.entrySet()) {
            String key = entry.getKey();
            Product product = entry.getValue();
            double priceProduct = product.calculatePrice();
            String details = product.getFormattedDetails();

            result.append(String.format(
                BLUE + "- " + RESET + "%-48s" + GREEN + "PRICE: " + RESET + "%6.2f" + GREEN + "$   " + BLUE + "KEY: " +
                    RESET + " %s\n",
                details,
                priceProduct,
                key)
            );
        }

        result.append(BLUE).append("----------------------------").append(RESET).append("\n");
        return result.toString();
    }
}
