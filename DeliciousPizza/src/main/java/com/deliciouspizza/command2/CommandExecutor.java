package com.deliciouspizza.command2;

import com.deliciouspizza.command.CommandCreator;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.service.OrderService;
import com.deliciouspizza.service.UserService;

import java.nio.channels.SocketChannel;

public class CommandExecutor {

    private final UserService userService = new UserService();
    private final OrderService orderService = new OrderService();

    private final SessionManager sessionManager = new SessionManager();

    public String start(String input, SocketChannel channel) {
        Command command =
            com.deliciouspizza.command2.CommandCreator.newCommand(input, sessionManager, channel, userService,
                orderService);

        if (command != null) {
            return command.execute(CommandCreator.getArguments(input), channel);
        } else {
            return "Invalid command.";
        }

    }

}
