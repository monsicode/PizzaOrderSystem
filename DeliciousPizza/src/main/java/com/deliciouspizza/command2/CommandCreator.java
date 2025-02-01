package com.deliciouspizza.command2;

import com.deliciouspizza.service.OrderService;
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
                                     OrderService orderService) {
        if (clientInput == null || clientInput.isBlank()) {
            System.out.println("No command provided.");
            return null;
        }

        String[] tokens = parseCommandArguments(clientInput);

        String commandName = tokens[0].toLowerCase();

        return factoryCommand(commandName, manager, client, userService, orderService);

    }

    private static Command factoryCommand(String commandName, SessionManager manager, SocketChannel client,
                                          UserService userService,
                                          OrderService orderService) {

        return switch (commandName) {
            case "login" -> new Log(manager, userService);
            case "create-order" -> new Create(manager, orderService);
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
