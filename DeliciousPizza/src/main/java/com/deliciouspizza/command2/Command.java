package com.deliciouspizza.command2;

import java.nio.channels.SocketChannel;

public interface Command {

    public String execute(String[] args, SocketChannel client);

}
