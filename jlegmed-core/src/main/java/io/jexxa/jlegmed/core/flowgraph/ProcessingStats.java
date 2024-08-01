package io.jexxa.jlegmed.core.flowgraph;

import java.math.BigInteger;

public record ProcessingStats(BigInteger forwardedMessages,
                              BigInteger processingErrorCount,
                              BigInteger handledProcessingErrors,
                              BigInteger unhandledProcessingErrors) { }
