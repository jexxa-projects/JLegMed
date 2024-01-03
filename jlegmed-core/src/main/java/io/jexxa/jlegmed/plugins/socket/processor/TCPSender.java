package io.jexxa.jlegmed.plugins.socket.processor;

import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.common.facade.utils.function.ThrowingBiFunction;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.processor.Processor;
import io.jexxa.jlegmed.plugins.socket.SocketContext;
import io.jexxa.jlegmed.plugins.socket.producer.TCPReceiver;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static io.jexxa.common.facade.json.JSONManager.getJSONConverter;
import static io.jexxa.jlegmed.plugins.socket.SocketProperties.TCP_ADDRESS;
import static io.jexxa.jlegmed.plugins.socket.SocketProperties.TCP_PORT;

public abstract class TCPSender<T, R> extends Processor<T, R> {

    private static final String COULD_NOT_SEND_MESSAGE = "Could not send message.";
    private int port = -1;
    private String ipAddress;
    private Socket clientSocket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    protected TCPSender()
    {
        //Protected constructor so that only child classes can use them
    }

    @Override
    public void init()
    {
        super.init();
        initFilter();
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

    @Override
    protected R doProcess(T data, FilterContext context) {
        try {
            R result = sendMessage(data, new SocketContext(bufferedReader, bufferedWriter, filterContext()));
            bufferedWriter.flush();
            return result;
        } catch (IOException e) {
            SLF4jLogger.getLogger(TCPReceiver.class).error(COULD_NOT_SEND_MESSAGE, e);
            return null;
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
        if (properties().containsKey(TCP_ADDRESS)) {
            ipAddress = properties().getProperty(TCP_ADDRESS);
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

    abstract R sendMessage(T data, SocketContext socketContext) throws IOException;

    public static <T, R> TCPSender<T, R> tcpSender(ThrowingBiFunction<T, SocketContext, R, IOException> consumer) {
        return new TCPSender<>() {
            @Override
            protected R sendMessage(T message, SocketContext context) {
                try {
                    return consumer.apply(message, context);
                } catch (IOException e) {
                    SLF4jLogger.getLogger(TCPSender.class).error(COULD_NOT_SEND_MESSAGE, e);
                    return null;
                }
            }
        };
    }

    public static  <T> T sendLine(T data, SocketContext context) throws IOException
    {
        context.bufferedWriter().write(data.toString() + "\n");
        return data;
    }

    public static  <T> T sendAsJSON(T data, SocketContext context) throws IOException {
        sendLine(getJSONConverter().toJson(data), context);
        return data;
    }
}
