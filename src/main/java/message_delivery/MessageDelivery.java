package message_delivery;

import configuration.ActiveMQConfiguration;
import configuration.BrokerConfiguration;
import org.apache.activemq.broker.BrokerService;

import javax.jms.Connection;
import javax.jms.Session;

public class MessageDelivery {
    private static Session session;

    public void dispatchMessage(boolean isTransacted) throws Exception {
        BrokerConfiguration brokerConfiguration = new BrokerConfiguration();
        BrokerService broker = brokerConfiguration.createBroker();
        ActiveMQConfiguration AMQConfiguration = new ActiveMQConfiguration();
        Connection connection = AMQConfiguration.connectToActiveMQ();

        try {
            broker.start();
            connection.start();

            if (isTransacted) {
                session = TransactedMessageDelivery.createTransactedSession(connection);
                TransactedMessageDelivery.sendAndReceive();
            } else {
                session = NonTransactedMessageDelivery.createNonTransactedSession(connection);
                NonTransactedMessageDelivery.sendAndReceive();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(session != null && connection != null) {
                session.close();
                connection.close();
            }
            broker.stop();
        }
    }
}
