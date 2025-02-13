package com.deliciouspizza.command.cutomer;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.service.UserService;

import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ViewHistoryOfOrders implements Command {

    private final UserService userService;
    private final SessionManager manager;

    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";

    public ViewHistoryOfOrders(UserService userService, SessionManager manager) {
        this.userService = userService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        if (manager.isLoggedIn(client)) {
            String username = manager.getUsername(client);

            try {
                Set<Order> orderHistory = userService.getOrderHistory(username);
                if (orderHistory.isEmpty()) {
                    return "No orders in history.";
                } else {
                    Map<Integer, Order> temp = new HashMap<>();
                    return getOrderHistoryString(orderHistory, temp);
                }
            } catch (IllegalStateException err) {
                return err.getMessage();
            }

        } else {
            return "Not logged in, error occurred";
        }
    }

    private String getOrderHistoryString(Set<Order> orderHistory, Map<Integer, Order> orderMap) {
        StringBuilder historyBuilder = new StringBuilder();
        historyBuilder.append("Order history:\n");

        int currentOrderID = 1;
        for (Order order : orderHistory) {
            orderMap.put(currentOrderID, order);

            historyBuilder.append(String.format("%sOrder #%d:%s\n", YELLOW, currentOrderID, RESET));

            LocalDateTime orderDate = order.getOrderDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = orderDate.format(formatter);
            historyBuilder.append("Date of ordering: ").append(formattedDate).append("\n");

            for (Map.Entry<String, Integer> entry : order.getOrder().entrySet()) {
                String product = entry.getKey().replaceAll("_", " ");
                historyBuilder.append(String.format(" - %s, Quantity: %d\n", product, entry.getValue()));
            }

            currentOrderID++;
            historyBuilder.append("-----------------------------------\n");
        }

        return historyBuilder.toString();
    }

}
