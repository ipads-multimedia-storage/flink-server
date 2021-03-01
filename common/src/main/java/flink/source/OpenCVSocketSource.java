package flink.source;

import flink.types.SourceData;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.IOUtils;
import org.apache.flink.util.Preconditions;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class OpenCVSocketSource implements SourceFunction<SourceData> {
    private volatile boolean isRunning;
    private transient Socket currentSocket;
    private transient ServerSocket serverSocket;
    private int port;

    public OpenCVSocketSource(int port) {
        Preconditions.checkArgument(port > 0 && port < 65536, "port is out of range");
        this.port = port;
        this.isRunning = true;
    }

    @Override
    public void run(SourceContext<SourceData> sourceContext) throws Exception {
        this.serverSocket = new ServerSocket(this.port);
        while(this.isRunning) {
            Socket socket = null;
            Throwable throwable0 = null;

            try {
                socket = this.serverSocket.accept();
                this.currentSocket = socket;
                InputStream reader = socket.getInputStream();
                Throwable throwable1 = null;

                try {
                    byte[] lenBuf, byteBuf, jsonBuf;
                    while (this.isRunning) {
                        lenBuf = getBytes(reader, 16);
                        if(lenBuf == null) break;
                        String lengthStr = new String(lenBuf);
                        int length = Integer.parseInt(lengthStr.trim());
                        jsonBuf = getBytes(reader, length);
                        if (jsonBuf == null) break;
                        JSONObject jsonObject = new JSONObject(new String(jsonBuf, StandardCharsets.UTF_8));
                        length = jsonObject.getInt("length");
                        long eventTime = jsonObject.getLong("event_time");
                        long beforeTime = jsonObject.getLong("current_time");
                        byteBuf = getBytes(reader, length);
                        if (byteBuf == null) break;
                        long afterTime = System.currentTimeMillis();
                        sourceContext.collect(new SourceData(eventTime, afterTime, beforeTime, byteBuf));
                    }
                } catch (Throwable throwable2) {
                    throwable1 = throwable2;
                    throw throwable2;
                } finally {
                    if (throwable1 != null) {
                        try {
                            reader.close();
                        } catch (Throwable throwable3) {
                            throwable1.addSuppressed(throwable3);
                        }
                    } else {
                        reader.close();
                    }
                }
            } catch (Throwable throwable4) {
                throwable0 = throwable4;
                throw throwable4;
            } finally {
                if (socket != null) {
                    if (throwable0 != null) {
                        try {
                            socket.close();
                        } catch (Throwable throwable5) {
                            throwable0.addSuppressed(throwable5);
                        }
                    } else {
                        socket.close();
                    }
                }
            }
        }
        serverSocket.close();
    }

    private byte[] getBytes(InputStream reader, int length) throws IOException {
        int left = length, pos = 0;
        boolean eof = false;
        byte[] byteBuf = new byte[length];
        int bytesRead;
        while(left > 0) {
            bytesRead = reader.read(byteBuf, pos, left);
            if (bytesRead == -1) {
                eof = true;
                break;
            }
            left -= bytesRead;
            pos += bytesRead;
        }
        if(eof) return null;
        return byteBuf;
    }

    @Override
    public void cancel() {
        this.isRunning = false;
        Socket theSocket = this.currentSocket;
        if (theSocket != null) {
            IOUtils.closeSocket(theSocket);
        }
        ServerSocket ss = this.serverSocket;
        if(ss != null) {
            try {
                ss.close();
            } catch (IOException ignored) {}
        }
    }

}
