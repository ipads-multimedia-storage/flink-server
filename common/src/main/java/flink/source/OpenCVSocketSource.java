package flink.source;

import flink.utils.BandwidthDetection;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.IOUtils;
import org.apache.flink.util.Preconditions;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class OpenCVSocketSource implements SourceFunction<Tuple2<Long, byte[]>> {
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
    public void run(SourceContext<Tuple2<Long, byte[]>> sourceContext) throws Exception {
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
                    byte[] lenBuf = new byte[16];
                    byte[] byteBuf, jsonBuf;
                    int bytesRead;
                    while (this.isRunning && reader.read(lenBuf, 0, 16) != -1) {
                        String lengthStr = new String(lenBuf);
                        int length = Integer.parseInt(lengthStr.trim());
                        int left = length; boolean eof = false; int pos = 0;
                        jsonBuf = new byte[length];
                        while(left > 0) {
                            bytesRead = reader.read(jsonBuf, pos, left);
                            if (bytesRead == -1) {
                                eof = true;
                                break;
                            }
                            left -= bytesRead;
                            pos += bytesRead;
                        }
                        if(eof) break;
                        JSONObject jsonObject = new JSONObject(new String(jsonBuf, StandardCharsets.UTF_8));
                        length = jsonObject.getInt("length");
                        long eventTime = jsonObject.getLong("event_time");
                        long beforeTime = jsonObject.getLong("current_time");
                        left = length; eof = false; pos = 0;
                        byteBuf = new byte[length];
                        while(left > 0) {
                            bytesRead = reader.read(byteBuf, pos, left);
                            if (bytesRead == -1) {
                                eof = true;
                                break;
                            }
                            left -= bytesRead;
                            pos += bytesRead;
                        }
                        if(eof) break;
                        long afterTime = System.currentTimeMillis();
                        long gap = afterTime - beforeTime;
//                        System.out.println("gap is: " + gap);
                        BandwidthDetection.record(gap, length);
                        sourceContext.collect(new Tuple2<>(eventTime, byteBuf));
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
