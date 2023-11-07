package io.jexxa.jlegmed.plugins.socket.processor;

import io.jexxa.jlegmed.common.wrapper.logger.SLF4jLogger;
import io.jexxa.jlegmed.common.wrapper.utils.function.ThrowingBiConsumer;
import io.jexxa.jlegmed.common.wrapper.utils.function.ThrowingBiFunction;
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
import java.util.Properties;

import static io.jexxa.jlegmed.plugins.socket.SocketProperties.TCP_ADDRESS;
import static io.jexxa.jlegmed.plugins.socket.SocketProperties.TCP_PORT;

public abstract class TCPSender<T, R> extends Processor<T, R> {
    private int port = -1;
    private String ipAddress;
    private Socket clientSocket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    protected TCPSender(int port, String ipAddress)  {
        this.port = port;
        this.ipAddress = ipAddress;
    }

    protected TCPSender()  { }

    @Override
    public void init()
    {
        super.init();
        properties().ifPresent(this::setAddress);

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


    protected R doProcess(T data, FilterContext context) {
        try {
            R result = sendMessage(data, new SocketContext(bufferedReader, bufferedWriter, filterContext()));
            bufferedWriter.flush();
            return result;
        } catch (IOException e) {
            SLF4jLogger.getLogger(TCPReceiver.class).error("Could not send message.", e);
            return null;
        }
    }

    private void setAddress(Properties properties) {
        if (properties.containsKey(TCP_PORT)) {
            port = Integer.parseInt(properties.getProperty(TCP_PORT));
        }
        if (properties.containsKey(TCP_ADDRESS)) {
            ipAddress = properties.getProperty(TCP_ADDRESS);
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

    public static <T, R> TCPSender<T, R> tcpProcessor(int port, String ipAddress, ThrowingBiFunction<T, SocketContext, R, IOException> consumer) {
        return new TCPSender<>(port, ipAddress) {
            @Override
            protected R sendMessage(T message, SocketContext context) {
                try {
                    return consumer.apply(message, context);
                } catch (IOException e) {
                    SLF4jLogger.getLogger(TCPSender.class).error("Could not send message.", e);
                    return null;
                }
            }
        };
    }

    public static <T, R> TCPSender<T, R> tcpSender(int port, String ipAddress, ThrowingBiConsumer<T, SocketContext, IOException> consumer) {
        return new TCPSender<>(port, ipAddress) {
            @Override
            protected R sendMessage(T message, SocketContext context) {
                try {
                    consumer.accept(message, context);
                } catch (IOException e) {
                    SLF4jLogger.getLogger(TCPSender.class).error("Could not read message.", e);
                }
                return null;
            }
        };
    }

    public static <T, R> TCPSender<T, R> tcpSender(ThrowingBiConsumer<T, SocketContext, IOException> consumer) {
        return new TCPSender<>() {
            @Override
            protected R sendMessage(T message, SocketContext context) {
                try {
                    consumer.accept(message, context);
                } catch (IOException e) {
                    SLF4jLogger.getLogger(TCPSender.class).error("Could not read message.", e);
                }
                return null;
            }
        };
    }

}
