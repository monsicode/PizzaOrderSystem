package com.deliciouspizza.command;

import com.deliciouspizza.service.OrderService;
import com.deliciouspizza.service.ProductService;
import com.deliciouspizza.service.UserService;

import java.nio.channels.SocketChannel;

public class CommandExecutor {

    private final UserService userService = new UserService();
    private final OrderService orderService = new OrderService();
    private final ProductService productService = new ProductService();

    private final SessionManager sessionManager = new SessionManager();

    public String start(String input, SocketChannel channel) {
        Command command =
            CommandCreator.newCommand(input, sessionManager, channel, userService, orderService, productService);

        if (command != null) {
            return command.execute(CommandCreator.getArguments(input), channel);
        } else {
            return "Invalid command.";
        }

    }

}
