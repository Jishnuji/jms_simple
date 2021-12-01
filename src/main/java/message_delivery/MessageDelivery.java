package message_delivery;

import configuration.ActiveMQConfiguration;
import configuration.BrokerConfiguration;
import org.apache.activemq.broker.BrokerService;

import javax.jms.*;
import java.time.Duration;
import java.time.Instant;

public class MessageDelivery {
    private static ActiveMQConfiguration AMQConfiguration;
    private static Connection connection;
    private static Session session;
    private static MessageProducer producer;
    private static MessageConsumer consumer;

    public void dispatchMessage(boolean isTransacted) throws Exception {
        BrokerConfiguration brokerConfiguration = new BrokerConfiguration();
        BrokerService broker = brokerConfiguration.createBroker();
        try {
            broker.start();

            AMQConfiguration = new ActiveMQConfiguration();
            connection = AMQConfiguration.connectToActiveMQ();
            connection.start();

            if (isTransacted) {
                createTransactedSession();
                sendAndReceiveTransactedMessage();
            } else {
                createNonTransactedSession();
                sendAndReceiveNonTransactedMessage();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
            connection.close();
            broker.stop();
        }
    }

    public void createNonTransactedSession() throws JMSException {
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        createProducerAndConsumer();
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    public void sendAndReceiveNonTransactedMessage() throws JMSException {
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

    public void createTransactedSession() throws JMSException {
        session = connection.createSession(true, Session.SESSION_TRANSACTED);
        createProducerAndConsumer();
    }

    public void sendAndReceiveTransactedMessage() throws JMSException {
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
        System.out.println("Время, которое заняло отправка сообщений в нетранзакционном режиме, мс: " + elapsedTransactedProducer);

        Instant startTransactedConsumer = Instant.now();
        for (int i = 0; i <= 100_000; i++) {
            TextMessage textMessage = (TextMessage) consumer.receive();
            if (i != 0 && i % 1000 == 0){
                session.commit();
            }
        }
        Instant finishTransactedConsumer = Instant.now();
        long elapsedTransactedConsumer = Duration.between(startTransactedConsumer, finishTransactedConsumer).toMillis();
        System.out.println("Время, которое заняло получение сообщений в нетранзакционном режиме, мс: " + elapsedTransactedConsumer);
    }

    public void createProducerAndConsumer() throws JMSException {
        Destination destination = session.createQueue(AMQConfiguration.getDestination());
        producer = session.createProducer(destination);
        consumer = session.createConsumer(destination);
    }
}
