package io.jexxa.jlegmed.core.flowgraph;

import java.math.BigInteger;

public record ProcessingStats(BigInteger processingErrorCount, BigInteger handledProcessingErrors, BigInteger unhandledProcessingErrors) { }
