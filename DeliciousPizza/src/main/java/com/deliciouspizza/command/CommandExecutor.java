package com.deliciouspizza.command;

import com.deliciouspizza.repository.Warehouse;
import com.deliciouspizza.service.OrderService;
import com.deliciouspizza.service.ProductService;
import com.deliciouspizza.service.UserService;
import com.deliciouspizza.utils.Singleton;

import java.nio.channels.SocketChannel;

public class CommandExecutor {

    private final UserService userService = Singleton.getInstance(UserService.class);
    private final OrderService orderService = Singleton.getInstance(OrderService.class);
    private final ProductService productService = Singleton.getInstance(ProductService.class);
    private final Warehouse warehouse = Singleton.getInstance(Warehouse.class);

    private final SessionManager sessionManager = new SessionManager();

    public String start(String input, SocketChannel channel) {
        Command command =
            CommandCreator.newCommand(input, sessionManager, channel, userService, orderService, productService,
                warehouse);

        if (command != null) {
            return command.execute(CommandCreator.getArguments(input), channel);
        } else {
            return "Invalid command.";
        }
    }

    public SocketChannel getChannelByUser(String customer) {
        return sessionManager.getClientByUsername(customer);
    }

}
