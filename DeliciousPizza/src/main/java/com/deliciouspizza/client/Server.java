package com.deliciouspizza.client;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.deliciouspizza.api.DistanceClient;
import com.deliciouspizza.api.data.Delivery;
import com.deliciouspizza.api.exceptions.ApiException;
import com.deliciouspizza.command.CommandExecutor;

public class Server {
    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";

    private final CommandExecutor commandExecutor;
    private final int port;
    private boolean isServerWorking;

    private ByteBuffer buffer;
    private Selector selector;

    public Server(int port, CommandExecutor commandExecutor) {
        this.port = port;
        this.commandExecutor = commandExecutor;
    }

    @SuppressWarnings("checkstyle:MethodLength")
    public void start() {
        System.out.println("Server is waiting for a client...");

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            isServerWorking = true;
            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {
                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            String clientInput = getClientInput(clientChannel);
                            System.out.println(clientInput);

                            if (clientInput == null) {
                                continue;
                            }

                            String output = commandExecutor.start(clientInput, clientChannel);

                            try {
                                informClientForProcessedOrder(output, clientChannel);
                            } catch (ApiException err) {
                                System.out.println(err.getMessage());
                                writeClientOutput(clientChannel, "Problem with the address of the customer. ");
                            }

                            writeClientOutput(clientChannel, output);

                        } else if (key.isAcceptable()) {
                            accept(selector, key);
                        }

                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    System.out.println("Error occurred while processing client request: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to start server", e);
        }
    }

    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, this.port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        byte[] outputBytes = output.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.wrap(outputBytes);

        while (buffer.hasRemaining()) {
            clientChannel.write(buffer);
        }
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);

        System.out.println("New client connected: " + accept.getRemoteAddress());

        String welcomeMenu = commandExecutor.start("main", null);
        writeClientOutput(accept, welcomeMenu);
    }

    private void informClientForProcessedOrder(String output, SocketChannel employeeChannel)
        throws IOException, ApiException {
        if (output.contains("processed")) {
            String[] words = output.split(" ");
            String customerName = words[4];
            String customerAddress;

            Pattern pattern = Pattern.compile(" --- Address: (.+)");
            Matcher matcher = pattern.matcher(output);

            if (matcher.find()) {
                customerAddress = matcher.group(1);
                SocketChannel clientToInform = commandExecutor.getChannelByUser(customerName);

                try {
                    Delivery delivery = getEstimatedTimeForDelivery(customerAddress);

                    if (clientToInform != null && delivery != null) {
                        writeClientOutput(clientToInform, delivery.toString());
                    }

                } catch (NoSuchElementException e) {
                    if (clientToInform != null) {
                        writeClientOutput(clientToInform, "Your address is invalid. Check for mistakes!");
                    }
                    throw new ApiException("Error! Problem with the address of the customer.");
                }
                writeClientOutput(employeeChannel, "Order processed successfully! ");
            } else {
                System.out.println("No address found.");
            }
        }
    }

    private Delivery getEstimatedTimeForDelivery(String address) throws ApiException {
        DistanceClient client = new DistanceClient();
        return client.getDistanceAndDuration(address);
    }

    public static void main(String[] args) {
        CommandExecutor commandExecutor = new CommandExecutor();

        int port = 8080;
        Server server = new Server(port, commandExecutor);
        server.start();
    }

}