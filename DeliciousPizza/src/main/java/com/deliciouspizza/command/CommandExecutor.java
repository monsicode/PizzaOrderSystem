package com.deliciouspizza.command;

import com.deliciouspizza.service.OrderService;
import com.deliciouspizza.service.UserService;

import java.nio.channels.SocketChannel;

public class CommandExecutor {

    private final UserService userService = new UserService();
    private final OrderService orderService = new OrderService();

    public String start(String input, SocketChannel channel) {
        Command command = CommandCreator.newCommand(input, channel, userService, orderService);

        if (command != null) {
            return command.execute(CommandCreator.getArguments(input));
        } else {
            return "Invalid command.";
        }

    }

}
