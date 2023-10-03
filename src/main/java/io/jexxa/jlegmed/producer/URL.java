package io.jexxa.jlegmed.producer;

import io.jexxa.jlegmed.JLegMed;

import java.util.Properties;
import java.util.function.Function;

public class URL {

    private final String url;
    private final Properties properties;
    private final JLegMed jLegMed;

    Function<Properties, ?> myFunction;
    public URL(String myurl, Class<?> clazz, JLegMed jLegMed) {
        //this.flowGraph = flowGraph;
        this.url = myurl;
        this.properties = new Properties();// TODO Read properties
        this.jLegMed = jLegMed;
    }

    public <T> JLegMed with( Function<Properties, T> myFunction)
    {
        this.myFunction = myFunction;
       // return flowGraph;
        return jLegMed;
    }


}
