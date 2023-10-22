package io.jexxa.jlegmed.core.processor;

public class ProcessorConfig {
    private int processCounter = 0;
    private boolean processAgain;
    private Object config;

    public boolean isProcessedAgain() {
        return processCounter > 0 || processAgain;
    }

    public void processAgain()
    {
        ++processCounter; //Increment the internal counter so that a scheduler knows that it must call it again
        processAgain = true; // Set this flag to true so that the scheduled filter/processor knows that it is called again
    }

    void decreaseCall()
    {
        if ( processCounter > 0)
        {
            --processCounter;
        }
    }

    void resetRepeatActive()
    {
        // reset the trigger only of the counter is <= 0. This enables the processor
        // 1. to inform a scheduler that the method is called multiple times
        // 2. the method itself can check if it is in re-trigger mode or not
        if (processCounter <= 0) {
            processAgain = false;
        }
    }

    public void setConfig(Object config)
    {
        this.config = config;
    }

    public <T> T getConfig(Class<T> configType)
    {
        return configType.cast(config);
    }
}
