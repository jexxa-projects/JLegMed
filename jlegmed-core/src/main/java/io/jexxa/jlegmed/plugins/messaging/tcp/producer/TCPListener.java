package io.jexxa.jlegmed.plugins.messaging.tcp.producer;

import io.jexxa.common.facade.utils.function.ThrowingConsumer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;

class TCPListener {
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final ThrowingConsumer<Socket, IOException> receiver;
    private final int port;
    private ServerSocket serverSocket;
    private boolean isListening = false;


    public TCPListener(int port, ThrowingConsumer<Socket, IOException> receiver)
    {
        this.port = port;
        this.receiver = receiver;
        init();
    }

    protected void init()
    {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not start listening on port " + port, e);
        }
    }

    public void start() {
        isListening = true;
        executorService.execute(this::startListening);
    }


    public void stop() {
        isListening = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            getLogger(TCPListener.class).error("Could not proper close listening socket on port {}", port);
        }
        serverSocket = null;

        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            getLogger(TCPListener.class).warn("ThreadedProducer could not be stopped -> Force shutdown.", e);
            Thread.currentThread().interrupt();
        }
    }


    @SuppressWarnings("java:S2589") // isListening can be set to false outside of this loop
    private void startListening() {
        int reconnectionCounter = 0;
        while (isListening) {
            try {
                var clientSocket = serverSocket.accept();
                executorService.execute(() -> acceptConnection(clientSocket));
                reconnectionCounter = 0;
            } catch (IOException e) {
                if (isListening) {
                    ++reconnectionCounter;
                    getLogger(TCPReceiver.class).warn("{} occurred during listening on port {}. Cause: {}",
                            e.getClass().getSimpleName(),
                            port,
                            e.getMessage());
                    if (reconnectionCounter < 10) {
                        getLogger(TCPReceiver.class).warn("Try to restart listening on port {}", port);
                    } else {
                        getLogger(TCPReceiver.class).error("We tried to restart listening 10 times without success -> we give up listening on port {}", port);
                        return;
                    }
                }
            }
        }

        getLogger(TCPReceiver.class).info("Stop listening on port {} .", port);
    }

    private void acceptConnection(Socket clientSocket)
    {
        try {
            receiver.accept(clientSocket);
            getLogger(TCPReceiver.class).info("Connection closed by server for client {} on port {} after successfully processing its request.", clientSocket.getRemoteSocketAddress(), port);
        } catch (IOException e) {
            getLogger(TCPReceiver.class).info("Connection closed by client {} on port {}.", clientSocket.getRemoteSocketAddress(), port);
        }
    }
}
