package org.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileMessage extends AbstractMessage{
    private String fileName;
    private byte[] bytes;

    public FileMessage(String fileName, byte[] bytes) {
        this.fileName = fileName;
        this.bytes = bytes;
    }

    @Override
    public CommandType getType() {
        return CommandType.FILE_MESSAGE;
    }
}
