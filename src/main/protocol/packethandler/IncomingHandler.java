package main.protocol.packethandler;

import main.protocol.HMessage;
import main.protocol.HPacket;
import main.protocol.TrafficListener;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class IncomingHandler extends Handler {

    private volatile boolean onlyOnce = true;
    public IncomingHandler(OutputStream outputStream, Object[] listeners) {
        super(outputStream, listeners);

        ((List<TrafficListener>)listeners[0]).add(message -> {
            if (isDataStream && onlyOnce && message.getPacket().length() == 261) {
                onlyOnce = false;
                isEncryptedStream = message.getPacket().readBoolean(264);
            }
        });
    }

    @Override
    public void act(byte[] buffer) throws IOException {
        if (isDataStream)	{
            continuedAct(buffer);
        }
        else  {
            out.write(buffer);
        }
    }

    @Override
    protected void printForDebugging(byte[] bytes) {
        System.out.println("-- DEBUG INCOMING -- " + new HPacket(bytes).toString() + " -- DEBUG --");
    }
}
