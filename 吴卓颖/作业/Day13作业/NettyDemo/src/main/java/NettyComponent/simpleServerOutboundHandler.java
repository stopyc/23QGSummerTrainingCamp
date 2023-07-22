package NettyComponent;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class simpleServerOutboundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise){
//        System.out.println("Server delivered message: " );
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            String s = new String(bytes);
            System.out.println("Server delivered message: " + s);
            ctx.writeAndFlush("Server delivered message: " + s);
        }else{
            ctx.write(msg, promise);
        }
    }

}
