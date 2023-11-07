package io.jexxa.jlegmed.plugins.socket.processor;

import io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger;
import io.jexxa.jlegmed.common.wrapper.utils.function.ThrowingBiFunction;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.plugins.socket.TCPContext;
import io.jexxa.jlegmed.plugins.socket.producer.TCPReceiver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public abstract class TCPSender<T, R> extends Processor<T, R> {
    private final int port;
    private final String ipAddress;
    private Socket clientSocket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    protected TCPSender(int port, String ipAddress)  {
        this.port = port;
        this.ipAddress = ipAddress;
    }

    @Override
    public void start() {
        super.start();

        initConnection();
    }

    @Override
    public synchronized void stop() {
        super.stop();

        try {
           clientSocket.close();
           bufferedWriter.close();
           bufferedReader.close();
        } catch (IOException e) {
            SLF4jLogger.getLogger(TCPSender.class).error("Could not proper close listening socket on port {}",port );
        }
        clientSocket = null;
    }


    protected R doProcess(T data, FilterContext context) {
        try {
            return sendMessage(data, new TCPContext(bufferedReader, bufferedWriter));
        } catch (IOException e) {
            SLF4jLogger.getLogger(TCPReceiver.class).error("Could not read message.", e);
            return null;
        }
    }


    private void initConnection() {
        try {
            clientSocket = new Socket(ipAddress, port);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new IllegalStateException("Could not connect to server.", e);
        }
    }

    abstract R sendMessage(T data, TCPContext tcpContext) throws IOException;

    public static <T, R> TCPSender<T, R> createTCPSender(int port, String ipAddress, ThrowingBiFunction<T, TCPContext, R, IOException> consumer) {
        return new TCPSender<>(port, ipAddress) {
            @Override
            protected R sendMessage(T message, TCPContext context) {
                try {
                    return consumer.apply(message, context);
                } catch (IOException e) {
                    SLF4jLogger.getLogger(TCPSender.class).error("Could not read message.", e);
                    return null;
                }
            }
        };
    }
}
