package com.deliciouspizza.command.main;

import com.deliciouspizza.command.Command;

import java.nio.channels.SocketChannel;

public class Exit implements Command {

    @Override
    public String execute(String[] args, SocketChannel client) {
        System.exit(0);
        return "";
    }

}
