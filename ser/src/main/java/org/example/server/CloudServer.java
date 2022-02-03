package org.example.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CloudServer {
    public static void main(String[] args) {

        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .group(auth, worker)
                    .childHandler(new FilePipeLine());
            ChannelFuture future = serverBootstrap.bind(8189).sync();
            log.info("org.example.server started...");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("e=", e);
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
