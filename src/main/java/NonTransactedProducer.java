import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import javax.jms.*;
import java.time.Duration;
import java.time.Instant;

public class NonTransactedProducer {
    public static void main(String[] args) throws Exception {
        NonTransactedProducer.sendNonTransacted();
    }

    public static void sendNonTransacted() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName("service");
        broker.setPersistent(false);
        broker.addConnector("tcp://localhost:61616");
        broker.start();

        ActiveMQConnectionFactory cf =
                new ActiveMQConnectionFactory("vm://service");

        Connection connection = cf.createConnection();
        connection.start();

        Session session =
                connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue("message.queue");
        MessageProducer producer = session.createProducer(destination);
        //   Включал эту опцию для проверки скорости доставки несохраняемых сообщений
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        MessageConsumer consumer = session.createConsumer(destination);

        // Producer
        Instant start = Instant.now();
        for (int i = 0; i < 100_000; i++) {
            Message message = session.createTextMessage("message " + i);
            producer.send(message);
        }
        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);

        // Consumer
        Instant start2 = Instant.now();
        for (int i =0; i < 100_000; i++) {
            TextMessage textMessage = (TextMessage) consumer.receive();
        }
        Instant finish2 = Instant.now();
        long elapsed2 = Duration.between(start2, finish2).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed2);

        session.close();
        connection.close();
        broker.stop();
    }
}

