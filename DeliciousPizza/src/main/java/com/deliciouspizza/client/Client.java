package com.deliciouspizza.client;

import java.io.IOException;
import java.nio.ByteBuffer;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try {
            // Open client socket channel
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false); // Set non-blocking mode
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            Thread inputThread = inputThread(socketChannel);
            inputThread.start();

            while (true) {
                selector.select(); // Wait for an event
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isConnectable()) {

                        SocketChannel channel = (SocketChannel) key.channel();
                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                        }
                        channel.register(selector, SelectionKey.OP_READ);
                        System.out.println("Connected to server!");
                    } else if (key.isReadable()) {

                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                        int bytesRead = channel.read(buffer);

                        if (bytesRead == -1) {
                            System.out.println("Server closed the connection.");
                            channel.close();
                            System.exit(0);
                        }

                        buffer.flip();
                        byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);
                        System.out.println("Server: " + new String(data));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Thread inputThread(SocketChannel socketChannel) {
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (true) {
//                System.out.print("Enter command: ");
                String message = scanner.nextLine();

                if ("exit".equalsIgnoreCase(message)) {
                    try {
                        socketChannel.close();
                        System.out.println("Client disconnected.");
                        System.exit(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                buffer.clear();
                buffer.put(message.getBytes());
                buffer.flip();

                try {
                    socketChannel.write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        inputThread.setDaemon(true);
        return inputThread;
    }
}
//package com.deliciouspizza.client;
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.channels.SocketChannel;
//import java.net.InetSocketAddress;
//import java.util.Scanner;
//
//public class Client {
//
//    private static final String SERVER_HOST = "localhost";
//    private static final int SERVER_PORT = 8080;
//
//    public static void main(String[] args) {
//
//        try (SocketChannel socketChannel = SocketChannel.open()) {
//            socketChannel.configureBlocking(false);
//            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
//
//            while (!socketChannel.finishConnect()) {
//                // Изчакване на успешна връзка (не блокира)
//            }
//
//            System.out.println("Connected to the server.");
//
//            String initialMessage = receiveMessage(socketChannel);
//            System.out.println("Initial message from server: " + initialMessage);
//
//            try (Scanner scanner = new Scanner(System.in)) {
//                while (true) {
//                    System.out.print("Enter command (or 'exit' to quit): ");
//                    String message = scanner.nextLine();
//
//                    if ("exit".equalsIgnoreCase(message)) {
//                        break;
//                    }
//
//                    System.out.println("Sending message: <" + message + "> to the server...");
//
//                    sendMessage(socketChannel, message);
//
//                    String reply = receiveMessage(socketChannel);
//                    System.out.println("The server replied: " + reply);
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Метод за изпращане на съобщение към сървъра
//    private static void sendMessage(SocketChannel socketChannel, String message) throws IOException {
//        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
//        while (buffer.hasRemaining()) {
//            socketChannel.write(buffer);
//        }
//    }
//
//    // Метод за получаване на целия отговор от сървъра
//    private static String receiveMessage(SocketChannel socketChannel) throws IOException {
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        StringBuilder response = new StringBuilder();
//
//        // Четене на съобщението от сървъра
//        while (true) {
//            buffer.clear();
//            int bytesRead = socketChannel.read(buffer);
//
//            if (bytesRead == -1) {
//                break;
//            }
//
//            buffer.flip();
//            response.append(new String(buffer.array(), 0, bytesRead));
//
//            // Ако сървърът е приключил с изпращането на данни, излизаме от цикъла
//            if (bytesRead < buffer.capacity()) {
//                break;
//            }
//        }
//
//        return response.toString();
//    }
//}
