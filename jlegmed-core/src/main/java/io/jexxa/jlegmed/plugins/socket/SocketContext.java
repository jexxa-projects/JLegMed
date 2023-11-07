package io.jexxa.jlegmed.plugins.socket;

import io.jexxa.jlegmed.core.filter.FilterContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public record SocketContext(BufferedReader bufferedReader, BufferedWriter bufferedWriter, FilterContext filterContext) { }
