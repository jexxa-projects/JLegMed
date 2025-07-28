package io.jexxa.jlegmed.plugins.messaging.tcp;

import io.jexxa.common.facade.logger.SLF4jLogger;
import io.jexxa.jlegmed.core.filter.FilterContext;
import io.jexxa.jlegmed.core.filter.FilterProperties;
import io.jexxa.jlegmed.plugins.messaging.tcp.producer.TCPReceiver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import static io.jexxa.common.facade.json.JSONManager.getJSONConverter;

public class TCPConnection {


    private static final String COULD_NOT_SEND_MESSAGE = "Could not send message.";
    private final int port;
    private final String ipAddress;
    private Socket clientSocket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Duration timeout;

    public TCPConnection(String ipAddress, int port)
    {
        this.port = port;
        this.ipAddress = ipAddress;
        timeout = Duration.of(0, ChronoUnit.MILLIS);
        validateFilterSettings();
    }

    public TCPConnection(FilterProperties filterProperties)
    {
        this(filterProperties.properties().getProperty(TCPProperties.TCP_ADDRESS),
                Integer.parseInt(filterProperties.properties().getProperty(TCPProperties.TCP_PORT)));
    }

    public void connectionTimeout(Duration timeout)
    {
        this.timeout = timeout;
    }


    private void validateFilterSettings() {
        if (port == -1) {
            throw new IllegalArgumentException( TCPConnection.class.getSimpleName()  + ": Port must be set");
        }

        if (ipAddress.isEmpty()) {
            throw new IllegalArgumentException(TCPConnection.class.getSimpleName()  + ": IP address must be set");
        }
    }


    public synchronized void close() {
        try {
            if(clientSocket != null) {
                clientSocket.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            SLF4jLogger.getLogger(TCPConnection.class).error("Could not proper close listening socket on port {}",port );
        }
        clientSocket = null;
        bufferedReader = null;
        bufferedWriter = null;
    }

    public <T> void sendMessage(T data, Function<T, String> encoder, Delimiter delimiter) {
        sendMessage(encoder.apply(data), delimiter);
    }

    public void sendMessage(String data, Delimiter delimiter) {
        sendMessage(data, delimiter.asString());
    }

    public void sendMessage(String data, String delimiter) {
        try {
            validateConnection();
            bufferedWriter.write(data);
            bufferedWriter.write(delimiter);
            bufferedWriter.flush();
        } catch (IOException e) {
            SLF4jLogger.getLogger(TCPReceiver.class).error(COULD_NOT_SEND_MESSAGE, e);
        }
    }



    private void validateConnection() {
        if (clientSocket == null || !clientSocket.isConnected() || clientSocket.isClosed() ) {
            try {
                clientSocket = new Socket();
                clientSocket.connect(new InetSocketAddress(ipAddress, port), new BigDecimal(timeout.toMillis()).intValueExact() );
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new IllegalStateException("Could not connect to server.", e);
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Invalid connection timeout defined.", e);
            }
        }
    }

    public static String sendTextMessage(String message, FilterContext filterContext)
    {
        TCPConnectionPool.tcpConnection(filterContext).sendMessage(message , Delimiter.NEWLINE);
        return message;
    }

    public static <T> T sendJSONMessage(T message, FilterContext filterContext)
    {
        sendTextMessage(getJSONConverter().toJson(message), filterContext);
        return message;
    }
}
