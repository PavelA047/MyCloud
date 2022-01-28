package org.example.client;

import org.example.model.AbstractMessage;

import java.io.IOException;

public interface CallBack {
    void onMessageReceived(AbstractMessage message) throws IOException;
}
