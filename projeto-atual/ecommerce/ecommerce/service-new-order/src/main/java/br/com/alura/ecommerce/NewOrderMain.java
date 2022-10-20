package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<Email>()) {
                for (var i = 0; i < 10; i++) {
                    var key = UUID.randomUUID().toString();

                    var userId = UUID.randomUUID().toString();
                    var orderId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1);
                    var order = new Order(userId, orderId, amount);

                    System.out.println("Ammount:" + amount);
                    orderDispatcher.send("ECOMMERCE2_NEW_ORDER", userId, order);

                    var email = new Email("Teste", "Thank you for your order! We are processing your order!");

                    emailDispatcher.send("ECOMMERCE2_SEND_EMAIL", key, email);
                }
            }
        }

    }

}