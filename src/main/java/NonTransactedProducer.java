import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.time.Duration;
import java.time.Instant;

public class NonTransactedProducer {
    public static void main(String[] args) throws JMSException {
        NonTransactedProducer.sendNonTransacted();
    }

    public static void sendNonTransacted() throws JMSException {
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
        Connection connection = cf.createConnection();
        connection.start();

        Session session =
                connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue("message.queue");
        MessageProducer producer = session.createProducer(destination);
        //   Включал эту опцию для проверки скорости доставки не сохраняемых сообщений
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        Instant start = Instant.now();

        for (int i =0; i < 100_000; i++) {
            Message message = session.createTextMessage("message " + i);
            producer.send(message);
        }

        Instant finish = Instant.now();
        long elapsed = Duration.between(start, finish).toMillis();
        System.out.println("Прошло времени, мс: " + elapsed);

        session.close();
        connection.close();
    }
}

