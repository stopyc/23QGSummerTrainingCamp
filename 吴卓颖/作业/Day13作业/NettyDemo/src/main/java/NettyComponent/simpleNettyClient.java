package NettyComponent;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Date;
import java.util.Scanner;

public class simpleNettyClient {
    EventLoopGroup workerEventLoop;
    Bootstrap bootstrap;
    ChannelFuture channelFuture;
    public simpleNettyClient() {
        this.workerEventLoop = new NioEventLoopGroup();
        bootstrap = new Bootstrap().group(workerEventLoop).channel(NioSocketChannel.class).handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                ChannelPipeline channelPipeline = nioSocketChannel.pipeline();
                channelPipeline.addLast(new StringEncoder());


                channelPipeline.addLast(new StringDecoder());
                    channelPipeline.addLast(new SimpleChannelInboundHandler<String>() {
                        @Override
                        public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                            System.out.println("Client received: " + msg);

                        }
                    });

            }
        }).option(ChannelOption.SO_KEEPALIVE, true);
    }

public void startClient(String host, int port) {
        try {
            channelFuture = bootstrap.connect(host, port).sync().channel().writeAndFlush("Client delivered message: " + new Date() + ": hello world!");
            Scanner scanner = new Scanner(System.in);
            while(scanner.hasNextLine()) {
                String s = scanner.nextLine();
                channelFuture.channel().writeAndFlush("Client delivered message: " + s);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // workerEventLoop.shutdownGracefully();
        }
    }

}
