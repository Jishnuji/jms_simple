import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import javax.jms.*;
import java.time.Duration;
import java.time.Instant;

public class TransactedProducer {
    public static void main(String[] args) throws Exception {
        TransactedProducer.sendTransacted();
    }
    public static void sendTransacted() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName("service");
        broker.addConnector("tcp://localhost:61616");
        broker.start();

        ActiveMQConnectionFactory cf =
                new ActiveMQConnectionFactory("vm://service");
        Connection connection = cf.createConnection();
        connection.start();
        Session session =
                connection.createSession(true, Session.SESSION_TRANSACTED);

        Destination destination = session.createQueue("message.queue");
        MessageProducer producer = session.createProducer(destination);
        MessageConsumer consumer = session.createConsumer(destination);

        // Producer
        Instant start = Instant.now();
        for (int i = 0; i <= 100_000; i++) {
            Message message = session.createTextMessage("message " + i);
            producer.send(message);
            if (i != 0 && i % 1000 == 0){
                session.commit();
            }
        }
        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);

        // Consumer
        Instant start2 = Instant.now();
        for (int i = 0; i <= 100_000; i++) {
            TextMessage textMessage = (TextMessage) consumer.receive();
            if (i != 0 && i % 1000 == 0){
                session.commit();
            }
        }
//        session.commit();
        Instant finish2 = Instant.now();
        long elapsed2 = Duration.between(start2, finish2).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed2);

        session.close();
        connection.close();
        broker.stop();
    }
}
