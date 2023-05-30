package io.jexxa.jlegmed.asyncreceive.processor;

import io.jexxa.jlegmed.asyncreceive.dto.incoming.NewContract;

public interface PersistContract
{
    void persist(NewContract newContract);

}
