package com.deliciouspizza.command;

import com.deliciouspizza.command.cutomer.CreateOrder;
import com.deliciouspizza.command.main.Exit;
import com.deliciouspizza.command.main.LogIn;
import com.deliciouspizza.command.main.RegisterCustomer;
import com.deliciouspizza.command.main.RegisterEmployee;
import com.deliciouspizza.command.main.ShowMainMenu;
import com.deliciouspizza.service.OrderService;
import com.deliciouspizza.service.UserService;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandCreator {

    private static final Map<SocketChannel, String> loggedUsers = new HashMap<>();

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

    public static Command newCommand(String clientInput, SocketChannel client, UserService userService,
                                     OrderService orderService) {
        if (clientInput == null || clientInput.isBlank()) {
            System.out.println("No command provided.");
            return null;
        }

        String[] tokens = parseCommandArguments(clientInput);

        String commandName = tokens[0].toLowerCase();

        return factoryCommand(commandName, client, userService, orderService);

    }

    private static Command factoryCommand(String commandName, SocketChannel client, UserService userService,
                                          OrderService orderService) {

        return switch (commandName) {
            case "register-customer" -> new RegisterCustomer(userService);
            case "register-employee" -> new RegisterEmployee(userService);
            case "login" -> new LogIn(userService);
            case "menu" -> new ShowMainMenu();
            case "exit" -> new Exit();
            case "create-order" -> new CreateOrder(orderService, loggedUsers.get(client));
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
