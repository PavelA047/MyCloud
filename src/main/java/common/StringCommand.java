package common;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;

@EqualsAndHashCode(callSuper = true)
@Data
public class StringCommand extends AbstractMessage {
    private String command;

    public StringCommand(String command) throws IOException {
        this.command = command;
    }

    @Override
    public CommandType getType() {
        return CommandType.STRING;
    }
}
