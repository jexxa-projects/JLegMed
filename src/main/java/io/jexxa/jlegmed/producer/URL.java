package io.jexxa.jlegmed.producer;

import io.jexxa.jlegmed.JLegMed;
import io.jexxa.jlegmed.Message;
import io.jexxa.jlegmed.processor.Processor;

import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class URL {

    private final String url;
    private final Properties properties;
    private final JLegMed jLegMed;

    Function<Properties, ?> myFunction;
    BiFunction<Properties, ?, ?> myTransformer;
    public URL(String myurl, Class<?> clazz, JLegMed jLegMed) {
        //this.flowGraph = flowGraph;
        this.url = myurl;
        this.properties = new Properties();// TODO Read properties
        this.jLegMed = jLegMed;
    }

    public <T, R> JLegMed with(BiFunction<Properties, T, R> myFunction)
    {
        this.myTransformer = myFunction;
       // return flowGraph;
        return jLegMed;
    }
    public <T> JLegMed with( Function<Properties, T> myFunction)
    {
        this.myFunction = myFunction;
        // return flowGraph;
        return jLegMed;
    }



}
