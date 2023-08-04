import NettyComponent.simpleNettyClient;

public class Client {
    public static void main(String[] args) {
        new simpleNettyClient().startClient("localhost", 8081);
    }
}
