package com.deliciouspizza.command;

import java.nio.channels.SocketChannel;

public interface Command {

    String execute(String[] args, SocketChannel client);

}
