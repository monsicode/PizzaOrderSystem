package com.deliciouspizza.command.main;

import com.deliciouspizza.command.Command;

public class Exit implements Command {

    @Override
    public String execute(String[] args) {
        System.exit(0);
        return "";
    }

}
