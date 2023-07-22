import NettyComponent.simpleNettyServer;

public class Server {
    public static void main(String[] args) {
        new simpleNettyServer().startServer(8081);
    }
}
