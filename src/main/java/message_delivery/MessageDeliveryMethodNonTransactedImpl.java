package message_delivery;

import configuration.ActiveMQConfiguration;

import javax.jms.*;
import java.time.Duration;
import java.time.Instant;

public class MessageDeliveryMethodNonTransactedImpl implements MessageDeliveryMethod {
    private static Session session;
    private static MessageProducer producer;
    private static MessageConsumer consumer;

    public Session createSession(Connection connection) throws JMSException {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        createProducerAndConsumer();
        return session;
    }

    public void createProducerAndConsumer() throws JMSException {
        Destination destination = session.createQueue(ActiveMQConfiguration.getDestination());
        producer = session.createProducer(destination);
        consumer = session.createConsumer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    public void sendAndReceive() throws JMSException {
        Instant startNonTransactedProducer = Instant.now();
        for (int i = 0; i < 100_000; i++) {
            Message message = session.createTextMessage("message " + i);
            producer.send(message);
        }
        Instant finishNonTransactedProducer = Instant.now();
        long elapsedTransactedProducer = Duration.between(startNonTransactedProducer, finishNonTransactedProducer).toMillis();
        System.out.println("Время, которое заняло отправка сообщений в нетранзакционном режиме, мс: " + elapsedTransactedProducer);

        Instant startNonTransactedConsumer = Instant.now();
        for (int i =0; i < 100_000; i++) {
            TextMessage textMessage = (TextMessage) consumer.receive();
        }
        Instant finishNonTransactedConsumer = Instant.now();
        long elapsedNonTransactedConsumer = Duration.between(startNonTransactedConsumer, finishNonTransactedConsumer).toMillis();
        System.out.println("Время, которое заняло получение сообщений в нетранзакционном режиме, мс: " + elapsedNonTransactedConsumer );
    }
}
