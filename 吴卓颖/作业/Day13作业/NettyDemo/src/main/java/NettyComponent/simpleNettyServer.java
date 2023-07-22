package NettyComponent;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class simpleNettyServer {

    private static CopyOnWriteArrayList<Channel> channelsList = new CopyOnWriteArrayList<>();
    EventLoopGroup bossEventLoop;
    EventLoopGroup workerEventLoop;
    ChannelFuture channelFuture;
    ServerBootstrap serverBootstrap;

    public simpleNettyServer() {
        bossEventLoop = new NioEventLoopGroup();
        workerEventLoop = new NioEventLoopGroup();
        try {
            serverBootstrap = new ServerBootstrap().group(bossEventLoop, workerEventLoop)
                    .channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<NioSocketChannel>() {

                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline channelPipeline = nioSocketChannel.pipeline();
                            channelPipeline.addLast(new StringEncoder());

                            channelPipeline.addLast(new simpleServerOutboundHandler());

                            channelPipeline.addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) {
                                    channelsList.add(ctx.channel());
                                    System.out.println("Client connected: " + ctx.channel().remoteAddress());
                                    ctx.fireChannelRead(ctx);
                                }
                            });

                            channelPipeline.addLast(new StringDecoder());
                            channelPipeline.addLast(new SimpleChannelInboundHandler<String>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) {
                                    System.out.println("Server received: " + s);
                                }
                            });
                        }


                    });//.bind(8081).sync();
        } finally {
            // bossEventLoop.shutdownGracefully();
            // workerEventLoop.shutdownGracefully();
        }
    }

    public void startServer(String host, int port) {
        try {
            channelFuture = serverBootstrap.bind(host, port).sync();
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();
                channelFuture.channel().write(s);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // bossEventLoop.shutdownGracefully();
            // workerEventLoop.shutdownGracefully();
        }
    }

    public void startServer(int port) {
        try {

            channelFuture = serverBootstrap.bind(port).sync();
            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNextLine()) {
                    String s = scanner.nextLine();
                    for (Channel channel : channelsList) {
                        ByteBuf byteBuf = channel.alloc().buffer();
                        byteBuf.writeBytes(s.getBytes());
                        ChannelFuture channelFutures = channel.write(byteBuf);
                        channel.flush();
                        channelFutures.addListener(future -> {
                            if (future.isSuccess()) {
                                System.out.println(channel.remoteAddress()+"Write operation completed successfully!");
                            } else {
                                System.err.println("Write operation failed.-");
                                future.cause().printStackTrace();
                            }
                        });
                    }
                }
            }).start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // bossEventLoop.shutdownGracefully();
            // workerEventLoop.shutdownGracefully();
        }
    }

}
