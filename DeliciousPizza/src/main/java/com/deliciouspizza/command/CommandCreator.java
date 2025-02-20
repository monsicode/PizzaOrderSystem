package com.deliciouspizza.command;

import com.deliciouspizza.command.cutomer.AddProduct;
import com.deliciouspizza.command.cutomer.ChangeAddressInfo;
import com.deliciouspizza.command.cutomer.CreateOrder;
import com.deliciouspizza.command.cutomer.FinishOrder;
import com.deliciouspizza.command.cutomer.RemoveProduct;
import com.deliciouspizza.command.cutomer.RepeatOrderForCustomer;
import com.deliciouspizza.command.cutomer.ViewHistoryOfOrders;
import com.deliciouspizza.command.employee.AddProductToMenu;
import com.deliciouspizza.command.employee.reports.CountOrdersForPeriod;
import com.deliciouspizza.command.employee.reports.ProfitForPeriod;
import com.deliciouspizza.command.employee.stock.AddStockToWarehouse;
import com.deliciouspizza.command.employee.orders.ProcessNextOrder;
import com.deliciouspizza.command.employee.RemoveProductFromMenu;
import com.deliciouspizza.command.employee.stock.ViewCatalogProducts;
import com.deliciouspizza.command.employee.orders.ViewFinishedOrders;
import com.deliciouspizza.command.employee.orders.ViewPendingOrders;
import com.deliciouspizza.command.employee.stock.ViewWarehouse;
import com.deliciouspizza.command.main.Exit;
import com.deliciouspizza.command.main.LogIn;
import com.deliciouspizza.command.main.LogOut;
import com.deliciouspizza.command.main.RegisterCustomer;
import com.deliciouspizza.command.main.RegisterEmployee;
import com.deliciouspizza.command.main.ShowMainMenu;
import com.deliciouspizza.command.main.ViewActiveProducts;
import com.deliciouspizza.repository.Warehouse;
import com.deliciouspizza.service.OrderService;
import com.deliciouspizza.service.ProductService;
import com.deliciouspizza.service.UserService;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandCreator {

    private static String[] parseCommandArguments(String input) {
        input = input.strip().replaceAll("\\s+", " ");
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        boolean insideQuote = false;

        for (char c : input.toCharArray()) {
            if (c == '"') {
                insideQuote = !insideQuote;
            } else if (c == ' ' && !insideQuote) {
                if (!sb.isEmpty()) {
                    tokens.add(sb.toString());
                    sb.setLength(0);
                }
            } else {
                sb.append(c);
            }
        }

        if (!sb.isEmpty()) {
            tokens.add(sb.toString());
        }

        return tokens.toArray(new String[0]);
    }

    public static Command newCommand(String clientInput, SessionManager manager,
                                     SocketChannel client,
                                     UserService userService,
                                     OrderService orderService,
                                     ProductService productService,
                                     Warehouse warehouse) {

        if (clientInput == null || clientInput.isBlank()) {
            System.out.println("No command provided.");
            return null;
        }

        String[] tokens = parseCommandArguments(clientInput);

        String commandName = tokens[0].toLowerCase();

        return factoryCommand(commandName, manager, client, userService, orderService, productService, warehouse);

    }

    private static Command factoryCommand(String commandName, SessionManager manager, SocketChannel client,
                                          UserService userService,
                                          OrderService orderService,
                                          ProductService productService,
                                          Warehouse warehouse) {

        return switch (commandName) {
            case "register-customer" -> new RegisterCustomer(userService);
            case "register-employee" -> new RegisterEmployee(userService);
            case "login" -> new LogIn(userService, manager);
            case "main" -> new ShowMainMenu();
            case "exit" -> new Exit();

            case "create-order" -> new CreateOrder(orderService, manager);
            case "add-product" -> new AddProduct(orderService, manager);
            case "remove-product" -> new RemoveProduct(orderService, manager);
            case "finish-order" -> new FinishOrder(orderService, manager);
            case "menu" -> new ViewActiveProducts(productService);
            case "view-history" -> new ViewHistoryOfOrders(userService, manager);
            case "repeat-order" -> new RepeatOrderForCustomer(userService, orderService, warehouse, manager);
            case "change-address" -> new ChangeAddressInfo(userService, manager);
            case "logout" -> new LogOut(manager);

            case "process" -> new ProcessNextOrder(orderService, manager);
            case "view-pending" -> new ViewPendingOrders(orderService, manager);
            case "view-finished" -> new ViewFinishedOrders(orderService, manager);
            case "add" -> new AddProductToMenu(productService, manager);
            case "remove" -> new RemoveProductFromMenu(productService, manager);
            case "catalog" -> new ViewCatalogProducts(warehouse, manager);
            case "warehouse" -> new ViewWarehouse(warehouse, manager);
            case "add-stock" -> new AddStockToWarehouse(warehouse, manager);
            case "profit" -> new ProfitForPeriod(orderService, manager);
            case "count-orders" -> new CountOrdersForPeriod(orderService, manager);

            default -> {
                System.out.println("Unknown command: " + commandName);
                yield null;
            }
        };

    }

    public static String[] getArguments(String clientInput) {
        String[] tokens = parseCommandArguments(clientInput);
        if (tokens.length == 0) {
            System.out.println("No command provided.");
            return null;
        }

        return Arrays.copyOfRange(tokens, 1, tokens.length);
    }

}
