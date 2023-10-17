package io.jexxa.jlegmed.common.scheduler;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

 /**
  * Annotation that marks a method to be scheduled by the {@link Scheduler}. The {@link Scheduler}
  * utilizes the ScheduledExecutorService and provides most of its functionality as driving adapter.
  * <p>So, exactly one of the attributes {@link #fixedDelay}, or {@link #fixedRate} with a value >= 0
  * must be specified for each annotated method. The annotated method must expect no arguments. It will
  * typically have a {@code void} return type; if not, the returned value will be ignored
  * when called through the scheduler.
  */
public interface IScheduled
{
    /**
     * Execute the annotated method with a fixed period between invocations.
     * <p>The time unit is milliseconds by default but can be overridden via
     * {@link #timeUnit()}.
     * @return the fixed period
     */
    default int fixedRate() {return -1;}

    /**
     * Execute the annotated method with a fixed period between the end of the
     * last invocation and the start of the next.
     * <p>The time unit is milliseconds by default but can be overridden via
     * {@link #timeUnit}.
     * @return the delay between invocations
     */
    default int fixedDelay() {return -1;}

    /**
     * The {@link TimeUnit} used for {@link #fixedDelay}, {@link #fixedRate}, and {@link #initialDelay}
     * <p>Defaults to {@link TimeUnit#MILLISECONDS}.
     * @return Used timeUnit
     */
    default TimeUnit timeUnit() {return  MILLISECONDS;}

    /**
     *  Number of units of time to delay before the first execution of a
	 * {@link #fixedRate} task.
     * <p>The time unit is milliseconds by default but can be overridden via {@link #timeUnit()}
     */
    default int initialDelay() {return 0;}

    void execute();
}
