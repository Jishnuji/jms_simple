package message_delivery;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

public interface MessageDeliveryMethod {
    Session createSession(Connection connection) throws JMSException;
    void createProducerAndConsumer() throws JMSException;
    void sendAndReceive() throws JMSException;
}
