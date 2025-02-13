package com.deliciouspizza.command.cutomer;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.service.UserService;

import java.nio.channels.SocketChannel;

public class ChangeAddressInfo implements Command {

    private final UserService userService;
    private final SessionManager manager;

    private static final int COUNT_NEEDED_ARGUMENTS = 1;
    private static final int ADDRESS_FIELD = 0;

    public ChangeAddressInfo(UserService userService, SessionManager manager) {
        this.userService = userService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        if (args.length != COUNT_NEEDED_ARGUMENTS) {
            return "Usage: change-address <new-address> ";
        }

        if (manager.isLoggedIn(client)) {
            String username = manager.getUsername(client);
            String newAddress = args[ADDRESS_FIELD];

            userService.changeAddressCustomer(username, newAddress);
            return "Address changed successfully to " + newAddress + " !";

        } else {
            return "Not logged in, error occurred";
        }
    }

}
