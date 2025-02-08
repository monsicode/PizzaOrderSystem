package com.deliciouspizza.command;

//import org.mockito.Mockito;

import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        CommandExecutor executor = new CommandExecutor();
       // SocketChannel mockChannel = Mockito.mock(SocketChannel.class);

        System.out.println(executor.start("main", null));

        while (true) {
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            System.out.println(executor.start(input, null));
        }

        scanner.close();
    }

}

