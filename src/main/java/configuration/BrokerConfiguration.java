package configuration;

import org.apache.activemq.broker.BrokerService;

public class BrokerConfiguration {
    private final String BROKER_NAME = "service";
    private final String TCP = "tcp://localhost:61616";

    public BrokerService createBroker() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName(BROKER_NAME);
        broker.setPersistent(false);
        broker.addConnector(TCP);

        return broker;
    }
}
