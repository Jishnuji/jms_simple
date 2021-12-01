import message_delivery.MessageDelivery;

public class StartApplication {
    public static void main(String[] args) {
        MessageDelivery delivery = new MessageDelivery();

        try {
            delivery.dispatchMessage(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
