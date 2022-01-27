package client;

import common.AbstractMessage;

import java.io.IOException;

public interface CallBack {
    void onMessageReceived(AbstractMessage message) throws IOException;
}
