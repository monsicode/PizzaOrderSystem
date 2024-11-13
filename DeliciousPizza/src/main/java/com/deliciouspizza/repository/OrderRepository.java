package com.deliciouspizza.repository;

import com.deliciouspizza.Singleton;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.utils.StatusOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

//-----------------
//За бързодействие --> база данни или in-memory storage(при рестарт вс изчезва)
//-->периодично записване на поръчките, на всеки 5-10 мин или при затваряне на приложението
//-----------------
//finishOrderForUser(User, Order);
//savePendingOrder(Order) --> save pending order to the file
//loadPendingOrders()
//-----------------

public class OrderRepository {
    private BlockingQueue<Order> pendingOrders;
    private static final String FILE_PATH_ORDERS = "src/main/resources/pendingOrders.json";
    private final ObjectMapper objectMapper;
    private final File jsonFile = new File(FILE_PATH_ORDERS);

    // private static final ProductRepository productRepository = Singleton.getInstance(ProductRepository.class);

    //за периодично записване
    //private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public OrderRepository() {
        pendingOrders = new LinkedBlockingQueue<>();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        //loadPendingOrders(); // Зарежда чакащите поръчки от JSON файла при стартиране
    }

    public void addOrder(Order order) {
        pendingOrders.add(order);
        savePendingOrders();
    }

    public Order getNextOrder() throws InterruptedException {
        Order order = pendingOrders.take();
        savePendingOrders();
        return order;
    }

    private void loadPendingOrders() {
        try {
            if (jsonFile.exists() && jsonFile.length() > 0) {
                BlockingQueue<Order> loadedOrders =
                    objectMapper.readValue(jsonFile, new TypeReference<LinkedBlockingQueue<Order>>() {
                    });
                pendingOrders.addAll(loadedOrders);
            }
        } catch (IOException e) {
            System.err.println("Грешка при зареждане на поръчките: " + e.getMessage());
        }
    }

    private void savePendingOrders() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, pendingOrders);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Грешка при записването на поръчките.");
        }
    }

    public BlockingQueue<Order> getPendingOrders() {
        return pendingOrders;
    }

    public void completeOrder(Order order) {
        order.setStatusOrder(StatusOrder.COMPLETED);
        System.out.println("Завършена поръчка: " + order);
        savePendingOrders();
    }

}
