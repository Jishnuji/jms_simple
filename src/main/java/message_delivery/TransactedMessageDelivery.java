package message_delivery;

import configuration.ActiveMQConfiguration;

import javax.jms.*;
import java.time.Duration;
import java.time.Instant;

public class TransactedMessageDelivery {
    private static Session session;
    private static MessageProducer producer;
    private static MessageConsumer consumer;

    public static Session createTransactedSession(Connection connection) throws JMSException {
        session = connection.createSession(true, Session.SESSION_TRANSACTED);
        createProducerAndConsumer();
        return session;
    }

    public static void createProducerAndConsumer() throws JMSException {
        Destination destination = session.createQueue(new ActiveMQConfiguration().getDestination());
        producer = session.createProducer(destination);
        consumer = session.createConsumer(destination);
    }

    public static void sendAndReceiveTransactedMessage() throws JMSException {
        Instant startTransactedProducer = Instant.now();
        for (int i = 0; i <= 100_000; i++) {
            Message message = session.createTextMessage("message " + i);
            producer.send(message);
            if (i != 0 && i % 1000 == 0){
                session.commit();
            }
        }

        Instant finishTransactedProducer = Instant.now();
        long elapsedTransactedProducer = Duration.between(startTransactedProducer, finishTransactedProducer).toMillis();
        System.out.println("Время, которое заняло отправка сообщений в транзакционном режиме, мс: " + elapsedTransactedProducer);

        Instant startTransactedConsumer = Instant.now();
        for (int i = 0; i <= 100_000; i++) {
            TextMessage textMessage = (TextMessage) consumer.receive();
            if (i != 0 && i % 1000 == 0){
                session.commit();
            }
        }

        Instant finishTransactedConsumer = Instant.now();
        long elapsedTransactedConsumer = Duration.between(startTransactedConsumer, finishTransactedConsumer).toMillis();
        System.out.println("Время, которое заняло получение сообщений в транзакционном режиме, мс: " + elapsedTransactedConsumer);
    }
}
