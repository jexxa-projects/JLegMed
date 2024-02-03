package io.jexxa.jlegmed.plugins.socket.producer;

import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.common.facade.utils.function.ThrowingBiFunction;
import io.jexxa.common.facade.utils.function.ThrowingFunction;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;
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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.jexxa.common.facade.json.JSONManager.getJSONConverter;
import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;
import static io.jexxa.jlegmed.plugins.socket.SocketProperties.TCP_PORT;

public abstract class TCPReceiver<T> extends ActiveProducer<T> {

    private int port = -1;

    protected TCPReceiver()  {
        //Protected constructor so that only child classes can use them
    }

    private final TCPAdapter<T> tcpAdapter = new TCPAdapter<>();

    @Override
    public void init()
    {
        super.init();
        initFilter();
        validateFilterSettings();
        this.tcpAdapter.setPort(port);
        this.tcpAdapter.register(this);
    }

    @Override
    public void start()
    {
        super.start();
        tcpAdapter.start();
    }

    @Override
    public void stop()
    {
        super.stop();
        tcpAdapter.stop();
    }

    private void validateFilterSettings() {
        if (port == -1) {
            throw new IllegalArgumentException("Port must be set");
        }
    }


    private void initFilter() {
        if (properties().containsKey(TCP_PORT)) {
            try {
                port = Integer.parseInt(properties().getProperty(TCP_PORT));
            } catch (NumberFormatException e)
            {
                throw new IllegalArgumentException("Port must be an integer");
            }
        }
    }



    synchronized void processMessage(Socket clientSocket)
    {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

            while (clientSocket.isConnected()) {
                T message = receiveMessage(new SocketContext(bufferedReader, bufferedWriter, filterContext()));
                if (message == null) {
                    break;
                }
                InvocationManager
                        .getInvocationHandler(TCPReceiver.class)
                        .invoke(this, outputPipe()::forward, message);
            }

            bufferedReader.close();
            bufferedWriter.close();
            clientSocket.close();
        } catch (IOException e) {
            getLogger(TCPReceiver.class).error("Connection closed.", e);
        }
    }


    abstract T receiveMessage(SocketContext socketContext) throws IOException;


    public static <T> TCPReceiver<T> tcpReceiver(ThrowingFunction<SocketContext, T, IOException> consumer) {
        return new TCPReceiver<>() {
            @Override
            protected T receiveMessage(SocketContext context) {
                try {
                    return consumer.apply(context);
                } catch (IOException e) {
                    getLogger(TCPReceiver.class).error("Could not read message.", e);
                    return null;
                }
            }
        };
    }

    public static <T> TCPReceiver<T> tcpReceiver(ThrowingBiFunction<SocketContext, Class<T>, T, IOException> consumer) {
        return new TCPReceiver<>() {
            @Override
            protected T receiveMessage(SocketContext context) {
                try {
                    return consumer.apply(context, producingType());
                } catch (IOException e) {
                    getLogger(TCPReceiver.class).error("Could not read message.", e);
                    return null;
                }
            }
        };
    }

    public static String receiveLine(SocketContext context) throws IOException
    {
        return context.bufferedReader().readLine();
    }

    public static <T> T receiveAsJSON(SocketContext context, Class<T> dataType) throws IOException
    {
        return getJSONConverter().fromJson(receiveLine(context), dataType);
    }

    private static class TCPAdapter<T>
    {
        private boolean isListening = false;
        private int port = -1;
        private ServerSocket serverSocket;

        private ExecutorService executorService;

        private TCPReceiver<T> receiver;


        public void setPort(int port)
        {
            this.port = port;
        }

        public void register(TCPReceiver<T> object) {
            receiver = object;
        }

        public void start() {
            executorService = Executors.newCachedThreadPool();
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not start listening on port " + port, e);
            }
            isListening = true;
            executorService.execute(this::startListening);
        }


        public void stop() {
            isListening = false;
            try {
                serverSocket.close();
            } catch (IOException e) {
                getLogger(TCPReceiver.class).error("Could not proper close listening socket on port {}",port );
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
                    executorService.execute(() -> receiver.processMessage(clientSocket));
                }
            } catch (SocketException e){
                getLogger(TCPReceiver.class).warn("Connection closed.");
            }
            catch (IOException e) {
                getLogger(TCPReceiver.class).error("Could not accept client connection.", e);
            }
        }
    }

}
