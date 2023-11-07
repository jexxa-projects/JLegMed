package io.jexxa.jlegmed.plugins.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public record TCPContext(BufferedReader bufferedReader, BufferedWriter bufferedWriter) { }
