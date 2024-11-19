package com.deliciouspizza.mains;

import com.deliciouspizza.ui.EmployeeInterfaceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;

public class TestEmployeeInterfaceImpl {
    private static EmployeeInterfaceImpl currentUserInterface;

    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        currentUserInterface = new EmployeeInterfaceImpl(scanner);
//        boolean running = true;
//
//        while (running) {
//            currentUserInterface.showMainMenuEmployee("worker1");
//        }
//
//        scanner.close();

        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.println("From (yyyy-MM-dd):");
        String fromInput = scanner.nextLine();
        LocalDate fromDate = LocalDate.parse(fromInput, formatter);

        System.out.println("From: " + fromDate);

    }
}
