package com.deliciouspizza.command;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        CommandExecutor executor = new CommandExecutor();

        while (true) {
            System.out.println(executor.start("menu", null));
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            System.out.println(executor.start(input, null));
        }

        scanner.close();
    }

}
