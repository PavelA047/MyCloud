package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import model.AbstractMessage;

@Slf4j
public class Network {
    private CallBack callBack;
    private SocketChannel channel;
    private static Network INSTANCE;

    public static Network getInstance(CallBack callBack) {
        if (INSTANCE == null) {
            INSTANCE = new Network(callBack);
        }
        return INSTANCE;
    }

    private Network(CallBack callBack) {
        this.callBack = callBack;
        Thread thread = new Thread(this::start);
        thread.setDaemon(true);
        thread.start();
    }

    public void write(AbstractMessage message) {
        channel.writeAndFlush(message);
    }

    private void start() {
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class)
                    .group(worker)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            channel = socketChannel;
                            channel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ClientHandler(callBack)
                            );
                        }
                    });
            ChannelFuture future = bootstrap.connect("localhost", 8189)
                    .sync();
            future.channel().closeFuture().sync();
            log.info("server started...");
        } catch (Exception e) {
            log.error("e=", e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
