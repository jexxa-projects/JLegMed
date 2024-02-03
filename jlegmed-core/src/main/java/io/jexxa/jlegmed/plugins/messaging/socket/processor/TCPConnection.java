package io.jexxa.jlegmed.plugins.messaging.socket.processor;

import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.plugins.messaging.socket.producer.TCPReceiver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class TCPConnection {

    private static final String COULD_NOT_SEND_MESSAGE = "Could not send message.";
    private final int port;
    private final String ipAddress;
    private Socket clientSocket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public TCPConnection(String ipAddress, int port)
    {
        this.port = port;
        this.ipAddress = ipAddress;
        validateFilterSettings();
    }


    private void validateFilterSettings() {
        if (port == -1) {
            throw new IllegalArgumentException("Port must be set");
        }

        if (ipAddress.isEmpty()) {
            throw new IllegalArgumentException("IP address must be set");
        }
    }


    public synchronized void close() {

        try {
           clientSocket.close();
           bufferedWriter.close();
           bufferedReader.close();
        } catch (IOException e) {
            SLF4jLogger.getLogger(TCPConnection.class).error("Could not proper close listening socket on port {}",port );
        }
        clientSocket = null;
    }

    public <T> void sendMessage(T data, Function<T, String> encoder) {
        sendMessage(encoder.apply(data));
    }

    public void sendMessage(String data) {
        try {
            validateConnection();
            bufferedWriter.write(data);
            bufferedWriter.flush();
        } catch (IOException e) {
            SLF4jLogger.getLogger(TCPReceiver.class).error(COULD_NOT_SEND_MESSAGE, e);
        }
    }



    private void validateConnection() {
        if (clientSocket == null || !clientSocket.isConnected() || clientSocket.isClosed() ) {
            try {
                clientSocket = new Socket(ipAddress, port);
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new IllegalStateException("Could not connect to server.", e);
            }
        }
    }


}
