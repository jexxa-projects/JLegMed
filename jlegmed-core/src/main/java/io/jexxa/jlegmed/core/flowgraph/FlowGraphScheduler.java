package io.jexxa.jlegmed.core.flowgraph;

import io.jexxa.common.drivingadapter.scheduler.IScheduled;
import io.jexxa.common.drivingadapter.scheduler.Scheduler;
import io.jexxa.jlegmed.core.filter.producer.PassiveProducer;

import java.util.concurrent.TimeUnit;

public class FlowGraphScheduler  {

    private Scheduler scheduler;

    public void start()
    {
        if (scheduler != null) {
            scheduler.start();
        }
    }

    public void stop()
    {
        if (scheduler != null) {
            scheduler.stop();
        }
    }

    public void waitUntilFinished()
    {

    }


    public <T> void configureFixedRate(PassiveProducer<T> producer, long period, TimeUnit timeUnit)
    {
        this.scheduler = new Scheduler();
        scheduler.register(new FixedRateStrategy(producer::produceData, 0, period,timeUnit));
    }

    public <T> void configureRepeatedRate(PassiveProducer<T> producer, long repeat, long period, TimeUnit timeUnit)
    {
        this.scheduler = new Scheduler();
        scheduler.register(new RepeatedFixedRateStrategy(repeat, producer::produceData, 0, period,timeUnit));
    }



    public static class FixedRateStrategy implements IScheduled {
        private final Runnable command;
        private final long initialDelay;
        private final long period;
        private final TimeUnit timeUnit;

        public FixedRateStrategy(Runnable command,
                                 long initialDelay,
                                 long period,
                                 TimeUnit timeUnit)
        {
            this.command = command;
            this.initialDelay = initialDelay;
            this.period = period;
            this.timeUnit = timeUnit;
        }


        @Override
        public long fixedRate() {
            return period;
        }

        @Override
        public TimeUnit timeUnit() {
            return timeUnit;
        }

        @Override
        public long initialDelay() {
            return initialDelay;
        }

        @Override
        public void execute() {
            command.run();
        }
    }

    public static class RepeatedFixedRateStrategy extends FixedRateStrategy {
        private final long maxRepeat;
        private long repeatCounter;

        public RepeatedFixedRateStrategy(long repeat, Runnable command,
                                         long initialDelay,
                                         long period,
                                         TimeUnit timeUnit)
        {
            super(command, initialDelay, period, timeUnit);
            this.maxRepeat = repeat;
        }

        @Override
        public void execute()
        {
            if (repeatCounter < maxRepeat)
            {
                super.execute();
                ++repeatCounter;
            }
        }
    }



}
