package io.jexxa.jlegmed.plugins.socket.producer;

import io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger;
import io.jexxa.jlegmed.plugins.generic.producer.ThreadedProducer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.BiFunction;

import static io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger.getLogger;

public abstract class TCPReceiver<T> extends ThreadedProducer<T> {

    private boolean isListening = false;
    private final int port;
    private ServerSocket serverSocket;

    protected TCPReceiver(int port)
    {
        this.port = port;
    }

    @Override
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not start listening on port " + port, e);
        }
        isListening = true;
        super.start();
    }

    @Override
    public synchronized void stop() {
        isListening = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            SLF4jLogger.getLogger(TCPReceiver.class).error("Could not proper close listening socket on port {}",port );
        }
        serverSocket = null;
    }

    @Override
    protected void produceData() {
        try {
            while (isListening) {
                var clientSocket = serverSocket.accept();
                var listener = new TCPListener<>(clientSocket, this);
                listener.start();
            }
        } catch (SocketException e){
            getLogger(TCPReceiver.class).warn("Connection closed.");
        }
        catch (IOException e) {
            getLogger(TCPReceiver.class).error("Could not accept client connection.", e);
        }
    }

    synchronized T processMessage(BufferedReader bufferedReader, BufferedWriter bufferedWriter)
    {
        try {
            T message = readMessage(bufferedReader, bufferedWriter);
            if (message != null) {
                forwardData(message);
            }
            return message;
        } catch (IOException e) {
            SLF4jLogger.getLogger(TCPReceiver.class).error("Could not process message.", e);
        }
        return null;
    }

    abstract T readMessage(BufferedReader bufferedReader, BufferedWriter bufferedWriter) throws IOException;


    public static class TCPListener<T> extends Thread {
        private final Socket clientSocket;
        private final TCPReceiver<T> receiver;

        protected TCPListener(Socket clientSocket, TCPReceiver<T> receiver) {
            this.clientSocket = clientSocket;
            this.receiver = receiver;
        }

        @Override
        public void run() {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                while (clientSocket.isConnected()) {
                    var result = receiver.processMessage(bufferedReader, bufferedWriter);
                    if (result == null) {
                        break;
                    }
                }

                bufferedReader.close();
                clientSocket.close();
            } catch (IOException e) {
                SLF4jLogger.getLogger(TCPListener.class).error("Connection closed.", e);
            }
        }
    }

    public static <T> TCPReceiver<T> createTCPReceiver(int port, BiFunction<BufferedReader, BufferedWriter, T> consumer) {
        return new TCPReceiver<>(port) {
            @Override
            protected T readMessage(BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
                return consumer.apply(bufferedReader, bufferedWriter);
            }
        };
    }
}