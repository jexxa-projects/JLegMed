package io.jexxa.jlegmed.plugins.socket.producer;

import io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger;
import io.jexxa.jlegmed.common.wrapper.utils.function.ThrowingFunction;
import io.jexxa.jlegmed.core.filter.producer.Producer;
import io.jexxa.jlegmed.plugins.generic.producer.ThreadedProducer;
import io.jexxa.jlegmed.plugins.socket.SocketContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger.getLogger;
import static io.jexxa.jlegmed.plugins.socket.SocketProperties.TCP_PORT;

public abstract class TCPReceiver<T> extends Producer<T> {

    private ExecutorService executorService;

    private boolean isListening = false;
    private int port = -1;
    private ServerSocket serverSocket;

    protected TCPReceiver(int port)  {
        this.port = port;
    }
    protected TCPReceiver()  { }

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
    public void init()
    {
        super.init();
        properties().ifPresent(this::setPort);
        if (port == -1) {
            throw new IllegalArgumentException("Port must be set");
        }
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

    private void setPort(Properties properties) {
        if (properties.containsKey(TCP_PORT)) {
            try {
                port = Integer.parseInt(properties.getProperty(TCP_PORT));
            } catch (NumberFormatException e)
            {
                throw new IllegalArgumentException("Port must be an integer");
            }
        }
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
                T message = receiveMessage(new SocketContext(bufferedReader, bufferedWriter, filterContext()));
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


    abstract T receiveMessage(SocketContext socketContext) throws IOException;

    public static <T> TCPReceiver<T> tcpReceiver(int port, ThrowingFunction<SocketContext, T, IOException> consumer) {
        return new TCPReceiver<>(port) {
            @Override
            protected T receiveMessage(SocketContext context) {
                try {
                    return consumer.apply(context);
                } catch (IOException e) {
                    SLF4jLogger.getLogger(TCPReceiver.class).error("Could not read message.", e);
                    return null;
                }
            }
        };
    }

    public static <T> TCPReceiver<T> tcpReceiver(ThrowingFunction<SocketContext, T, IOException> consumer) {
        return new TCPReceiver<>() {
            @Override
            protected T receiveMessage(SocketContext context) {
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
