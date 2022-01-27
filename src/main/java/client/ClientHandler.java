package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import common.AbstractMessage;

public class ClientHandler extends SimpleChannelInboundHandler<AbstractMessage> {
    private final CallBack callBack;

    public ClientHandler(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext cx, AbstractMessage message) throws Exception {
        callBack.onMessageReceived(message);
    }
}
