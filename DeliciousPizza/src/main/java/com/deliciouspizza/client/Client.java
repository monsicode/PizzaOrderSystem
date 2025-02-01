package com.deliciouspizza.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            System.out.println("Connected to server");

            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            // Четене на начално съобщение от сървъра
            readServerMessage(inputStream);

            Scanner scanner = new Scanner(System.in);
            String command;

            while (true) {
                System.out.print("Enter command (or 'exit' to quit): ");
                command = scanner.nextLine();
                if ("exit".equalsIgnoreCase(command)) {
                    System.out.println("Exiting client...");
                    break;
                }
                outputStream.write((command + "\n").getBytes());
                outputStream.flush();

                readServerMessage(inputStream);
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readServerMessage(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);

        if (bytesRead > 0) {
            String serverResponse = new String(buffer, 0, bytesRead);
            System.out.println("Server response: " + serverResponse);
        }
    }
}
