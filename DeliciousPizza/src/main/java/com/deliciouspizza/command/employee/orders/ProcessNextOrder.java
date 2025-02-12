package com.deliciouspizza.command.employee.orders;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.service.OrderService;

import java.nio.channels.SocketChannel;

public class ProcessNextOrder implements Command {

    private final OrderService orderService;
    private final SessionManager manager;

    public ProcessNextOrder(OrderService orderService, SessionManager manager) {
        this.orderService = orderService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        if (manager.isLoggedIn(client)) {
            try {
                String customer = orderService.getUserForCurrentOrder();
                String addressCustomer = orderService.getDeliveryAddress(customer);

                orderService.processCurrentOrder();
                return "Order processed for user: " + customer + " . Delivering for address: " + addressCustomer ;

            } catch (InterruptedException err) {
                Thread.currentThread().interrupt();
                return "Error processing the order";
            }
        } else {
            return "Not logged in, error occurred";
        }
    }

//    public static void main(String[] args) {
//        String output = "Order processed for user: customer ; Delivering for address: 'st. baba meca' ";
//        String[] words = output.split(" ");
//        String customerName = words[4];
//        System.out.println(words[9]);
//    }

}


//  @Override
//    public String execute(String[] args, SocketChannel client) {
//        if (manager.isLoggedIn(client)) {
//            try {
//                orderService.processCurrentOrder();
//                return "Order processed successfully!";
//            } catch (InterruptedException err) {
//                Thread.currentThread().interrupt();
//                return "Error processing the order";
//            }
//        } else {
//            return "Not logged in, error occurred";
//        }
//    }