package org.example.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FilesHandler extends SimpleChannelInboundHandler<AbstractMessage> {
    private Path currentDir;
    private boolean regOk;
    private static DataBaseAuthService db;
    private static List<String> authList;

    static {
        db = new DataBaseAuthService();
        authList = new ArrayList<>();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        currentDir = Paths.get("serverDir");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage message) throws Exception {
        switch (message.getType()) {
            case FILE_REQUEST:
                FileRequest fileRequest = (FileRequest) message;
                String fileName = fileRequest.getFileName();
                byte[] buf = new byte[8192];
                try (InputStream is = new FileInputStream(currentDir.resolve(fileName).toFile())) {
                    while (is.available() > 0) {
                        int cnt = is.read(buf);
                        if (cnt < 8192) {
                            byte[] tmp = new byte[cnt];
                            System.arraycopy(buf, 0, tmp, 0, cnt);
                            ctx.writeAndFlush(new FileMessage(fileName, tmp.clone()));
                        } else {
                            ctx.writeAndFlush(new FileMessage(fileName, buf.clone()));
                        }
                    }

                }
                break;
            case FILE_MESSAGE:
                FileMessage fileMessage = (FileMessage) message;
                Files.write(
                        currentDir.resolve(fileMessage.getFileName()),
                        fileMessage.getBytes(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
                sendList(ctx);
                break;
            case STRING:
                StringCommand stringCommand = (StringCommand) message;
                String command = stringCommand.getCommand();
                if (command.startsWith("/reg")) {
                    String[] token = command.split("\\s+", 7);
                    if (token.length < 7) {
                        return;
                    }
                    regOk = db.registration(token[1], token[2], token[3], token[4], token[5], token[6]);
                    if (regOk) {
                        log.info("new user has been registered");
                        currentDir = Files.createDirectory(currentDir.resolve(token[1]));
                        sendList(ctx);
                        currentDir = Paths.get("serverDir");
                    }
                }
                if (command.startsWith("/auth")) {
                    String[] token = command.split("\\s+", 3);
                    if (token.length < 3) {
                        return;
                    }
                    if (authList.size() != 0) {
                        for (String log : authList) {
                            if (token[1].equals(log)) {
                                return;
                            }
                        }
                    }
                    if (token[2].equals(db.getPasByLogin(token[1]))) {
                        log.info("new user has been authenticated");
                        authList.add(token[1]);
                        ctx.writeAndFlush(new StringCommand("/authOk"));
                    }
                    currentDir = currentDir.resolve(token[1]);
                    sendList(ctx);
                }
                if (command.startsWith("/dir")) {
                    String[] token = command.split("\\s+", 2);
                    if (token.length < 2) {
                        return;
                    }
                    Files.createDirectory(currentDir.resolve(token[1]));
                    sendList(ctx);
                }
                if (command.startsWith("/goTo")) {
                    String[] token = command.split("\\s+", 2);
                    if (token.length < 2) {
                        return;
                    }
                    currentDir = currentDir.resolve(token[1]);
                    sendList(ctx);
                }
                if (command.equals("/up")) {
                    if (!currentDir.getParent().toString().equals("serverDir")) {
                        currentDir = currentDir.getParent();
                        sendList(ctx);
                    } else break;
                }
                if (command.startsWith("/del")) {
                    String[] token = command.split("\\s+", 2);
                    if (token.length < 2) {
                        return;
                    }
                    Files.deleteIfExists(currentDir.resolve(token[1]));
                    sendList(ctx);
                }
                if (command.startsWith("/rename")) {
                    String[] token = command.split("\\s+", 3);
                    if (token.length < 3) {
                        return;
                    }
                    Files.move(currentDir.resolve(token[1]), currentDir.resolve(token[2]), StandardCopyOption.REPLACE_EXISTING);
                    sendList(ctx);
                }
                break;
        }
    }

    private void sendList(ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(new FilesList(currentDir));
    }
}
