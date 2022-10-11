package com.diegofigmedeiros.mensageria;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

public class ReceiveLogsDirect {

    private static final String EXCHANGE_NAME = "EXCHANGE_ROTEAMENTOS_FILAS";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("mqadmin");
        factory.setPassword("Admin123XX_");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queueName = channel.queueDeclare().getQueue();

        if(args.length < 1) {
            System.err.println("Usage: ReceiveLogsDirect [info] [warning] [error]");
            System.exit(1);
        }

        for (String severity : args) {
            channel.queueBind(queueName, EXCHANGE_NAME, severity);
        }

        System.out.println("[*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback callback = (consumerTag, deliver) -> {
            String message = new String(deliver.getBody(), StandardCharsets.UTF_8);
            System.out.println("[x] Received '" + deliver.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        channel.basicConsume(queueName, true, callback, consumerTag -> {});
    }
}
