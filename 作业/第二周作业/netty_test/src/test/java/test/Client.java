package test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.TimeUnit;


public class Client{
    public static void main(String[] args) throws InterruptedException {
        while (true) {
            new Bootstrap()
                    .group(new NioEventLoopGroup()) // 1
                    .channel(NioSocketChannel.class) // 2
                    .handler(new ChannelInitializer<Channel>() { // 3
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new StringEncoder()); // 8
                        }
                    })
                    .connect("127.0.0.1", 8080) // 4
                    .sync() // 5
                    .channel() // 6
                    .writeAndFlush("Hello Server"); // 7
            TimeUnit.SECONDS.sleep(1);
        }
    }}