package configuration;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;

public class ActiveMQConfiguration {
    private final String BROKER_URL = "vm://service";
    private static final String DESTINATION = "message.queue";

    public Connection connectToActiveMQ() {
        ActiveMQConnectionFactory cf =
                new ActiveMQConnectionFactory(BROKER_URL);
        Connection connection = null;

        try {
            connection = cf.createConnection();
        } catch (JMSException e) {
            e.printStackTrace();
        }

        return connection;
    }

    public static String getDestination() {
        return DESTINATION;
    }
}
