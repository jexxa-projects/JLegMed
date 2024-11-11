package io.jexxa.jlegmed.plugins.generic.muxer;

import io.jexxa.jlegmed.core.filter.FilterContext;

import java.io.Serializable;


public interface MultiplexFunction<U, V, R>  extends Serializable {
    R apply(U firstData, V secondData, FilterContext filterContext);
}
