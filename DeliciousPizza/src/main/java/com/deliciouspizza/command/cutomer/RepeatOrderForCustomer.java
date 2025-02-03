package com.deliciouspizza.command.cutomer;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.enums.StatusOrder;
import com.deliciouspizza.repository.Warehouse;
import com.deliciouspizza.service.OrderService;
import com.deliciouspizza.service.UserService;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RepeatOrderForCustomer implements Command {

    private final UserService userService;
    private final OrderService orderService;
    private final Warehouse warehouse;
    private final SessionManager manager;

    private static final int COUNT_NEEDED_ARGUMENTS = 1;
    private static final int CHOICE_FIELD = 0;

    public RepeatOrderForCustomer(UserService userService, OrderService orderService, Warehouse warehouse,
                                  SessionManager manager) {
        this.userService = userService;
        this.orderService = orderService;
        this.warehouse = warehouse;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {

        if (args.length != COUNT_NEEDED_ARGUMENTS) {
            return "Usage: repeat-order <orderId>";
        }

        if (manager.isLoggedIn(client)) {
            String username = manager.getUsername(client);
            int choice = Integer.parseInt(args[CHOICE_FIELD]);

            Set<Order> orderHistory = userService.getOrderHistory(username);
            if (orderHistory.isEmpty()) {
                return "No order history found.";
            }

            Order selectedOrder = selectedOrder(orderHistory, choice);
            return checkIfOrderIsValid(selectedOrder);

        } else {
            return "Not logged in, error occurred";
        }
    }

    private Order selectedOrder(Set<Order> orderHistory, int choice) {
        Map<Integer, Order> orderMap = new HashMap<>();
        int currentOrderID = 1;
        for (Order order : orderHistory) {
            orderMap.put(currentOrderID++, order);
        }
        return orderMap.get(choice);
    }

    private String checkIfOrderIsValid(Order selectedOrder) {
        if (selectedOrder != null) {
            try {
                warehouse.reduceStockWithOrder(selectedOrder);
            } catch (IllegalArgumentException err) {
                return err.getMessage();
            }

            selectedOrder.setStatusOrder(StatusOrder.PROCESSING);
            selectedOrder.resetOrderDate();

            orderService.finalizeRepeatedOrder(selectedOrder);
            return "Order repeated successfully!";
        } else {
            return "Invalid order number. Try again.";
        }
    }

}
