package io.jexxa.jlegmed.plugins.messaging.socket.producer;

import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.common.facade.utils.function.ThrowingBiFunction;
import io.jexxa.common.facade.utils.function.ThrowingFunction;
import io.jexxa.jlegmed.core.filter.producer.ActiveProducer;
import io.jexxa.jlegmed.plugins.messaging.socket.SocketContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static io.jexxa.common.facade.json.JSONManager.getJSONConverter;
import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;
import static io.jexxa.jlegmed.plugins.messaging.socket.SocketProperties.TCP_PORT;

public abstract class TCPReceiver<T> extends ActiveProducer<T> {

    private int port = -1;

    protected TCPReceiver()  {
        //Protected constructor so that only child classes can use them
    }

    private TCPListener tcpListener;

    @Override
    public void init()
    {
        super.init();
        initFilter();
        validateFilterSettings();
        tcpListener = new TCPListener(port, this::processMessage);
    }

    @Override
    public void start()
    {
        super.start();
        tcpListener.start();
    }

    @Override
    public void stop()
    {
        super.stop();
        tcpListener.stop();
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



    synchronized void processMessage(Socket clientSocket) throws IOException
    {
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

}
