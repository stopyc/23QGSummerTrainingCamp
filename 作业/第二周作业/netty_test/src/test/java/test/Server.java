package test;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class Server {
    public static void main(String[] args) {
        //服务器端的启动器,负责组装netty组件
        new ServerBootstrap()
                //BossEventLoop和WorkerEvenLoop的组合
                //相当于线程池 + Selector
                .group(new NioEventLoopGroup())
                //选择服务器Socket的实现类
                .channel(NioServerSocketChannel.class)
                // (handler) 告诉worker (child) 要执行什么逻辑
                .childHandler(
                        // (channel) 和客户端连接后进行读写的通道
                        // (Initializer) 初始化器,对channel里的handler进行初始化 , 本质也是一类handler
                        new ChannelInitializer<NioSocketChannel>() {
                            protected void initChannel(NioSocketChannel ch) {
                                //添加具体的handler
                                //对字节型的数据进行解码(ByteBuf转为字符串)
                                ch.pipeline().addLast(new StringDecoder());
                                //自定义handler
                                ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                                    @Override
                                    //处理 读 事件 , msg实际为StringDecoder返回的结果
                                    /**这个是自定义的handler, 需要改*/
                                    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                        //打印返回的字符串
                                        if (msg.equals("Hello Server")){
                                            System.out.println("回复客户端的消息 : " + msg);
                                            System.out.println("hello Client");
                                        }
                                    }
                                });
                            }
                        })
                //NioServerSocketChannel绑定的监听端口
                .bind(8080);
    }
}
