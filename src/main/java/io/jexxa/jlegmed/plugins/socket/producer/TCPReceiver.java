package io.jexxa.jlegmed.plugins.socket.producer;

import io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger;
import io.jexxa.jlegmed.common.wrapper.utils.function.ThrowingFunction;
import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.plugins.generic.producer.ThreadedProducer;
import io.jexxa.jlegmed.plugins.socket.TCPContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger.getLogger;

public abstract class TCPReceiver<T> extends Producer<T> {

    private ExecutorService executorService;

    private boolean isListening = false;
    private final int port;
    private ServerSocket serverSocket;

    protected TCPReceiver(int port)  {
        this.port = port;
    }

    @Override
    public void start() {
        super.start();
        executorService = Executors.newCachedThreadPool();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not start listening on port " + port, e);
        }
        isListening = true;
        executorService.execute(this::startListening);
    }

    @Override
    public void stop() {
        isListening = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            SLF4jLogger.getLogger(TCPReceiver.class).error("Could not proper close listening socket on port {}",port );
        }
        serverSocket = null;

        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            getLogger(ThreadedProducer.class).warn("ThreadedProducer could not be stopped -> Force shutdown.", e);
            Thread.currentThread().interrupt();
        }
        executorService = null;
    }

    private void startListening() {
        try {
            while (isListening) {
                var clientSocket = serverSocket.accept();
                executorService.execute(() -> processMessage(clientSocket));
            }
        } catch (SocketException e){
            getLogger(TCPReceiver.class).warn("Connection closed.");
        }
        catch (IOException e) {
            getLogger(TCPReceiver.class).error("Could not accept client connection.", e);
        }
    }

    synchronized void processMessage(Socket clientSocket)
    {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            while (clientSocket.isConnected()) {
                T message = receiveMessage(new TCPContext(bufferedReader, bufferedWriter));
                if (message == null) {
                    break;
                }
                forwardData(message);
            }

            bufferedReader.close();
            bufferedWriter.close();
            clientSocket.close();
        } catch (IOException e) {
            SLF4jLogger.getLogger(TCPReceiver.class).error("Connection closed.", e);
        }
    }


    abstract T receiveMessage(TCPContext tcpContext) throws IOException;

    public static <T> TCPReceiver<T> createTCPReceiver(int port, ThrowingFunction<TCPContext, T, IOException> consumer) {
        return new TCPReceiver<>(port) {
            @Override
            protected T receiveMessage(TCPContext context) {
                try {
                    return consumer.apply(context);
                } catch (IOException e) {
                    SLF4jLogger.getLogger(TCPReceiver.class).error("Could not read message.", e);
                    return null;
                }
            }
        };
    }
}
