package server;

import common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FilesHandler extends SimpleChannelInboundHandler<AbstractMessage> {
    private Path currentDir;
    private boolean regOk;
    private static DataBaseAuthService db;

    static {
        db = new DataBaseAuthService();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        currentDir = Paths.get("serverDir");
        sendList(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage message) throws Exception {
        switch (message.getType()) {
            case FILE_REQUEST:
                FileRequest fileRequest = (FileRequest) message;
                ctx.writeAndFlush(new FileMessage(currentDir.resolve(fileRequest.getFileName())));
                break;
            case FILE_MESSAGE:
                FileMessage fileMessage = (FileMessage) message;
                Files.write(currentDir.resolve(fileMessage.getFileName()), fileMessage.getBytes());
                sendList(ctx);
            case STRING:
                StringCommand stringCommand = (StringCommand) message;
                String command = stringCommand.getCommand();
                if (command.startsWith("/reg")) {
                    String[] token = command.split("\\s+", 7);
                    if (token.length < 7) {
                        break;
                    }
                    regOk = db.registration(token[1], token[2], token[3], token[4], token[5], token[6]);
                    if (regOk) {
                        log.info("new user has been registered");
                        ctx.writeAndFlush(new StringCommand("/regOk"));
                    }
                }
                if (command.startsWith("/auth")) {
                    String[] token = command.split("\\s+", 3);
                    if (token.length < 3) {
                        break;
                    }
                    if (token[2].equals(db.getPasByLogin(token[1]))) {
                        log.info("new user has been authenticated");
                        ctx.writeAndFlush(new StringCommand("/authOk"));
                    }
                }
        }
    }

    private void sendList(ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(new FilesList(currentDir));
    }
}
