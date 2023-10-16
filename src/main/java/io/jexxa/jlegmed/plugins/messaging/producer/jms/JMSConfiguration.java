package io.jexxa.jlegmed.plugins.messaging.producer.jms;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JMSConfiguration
{
    enum MessagingType {QUEUE, TOPIC}
    enum DurableType {DURABLE, NON_DURABLE}

    String destination() ;
    String selector() default "";
    MessagingType messagingType() ;

    /**
     *  Defines the shared subscription name introduced in JMS 2.0
     * @return shared subscription name in case a shared subscription should be used or an empty string otherwise
     */
    String sharedSubscriptionName() default "";
    DurableType durable() default DurableType.NON_DURABLE;
}
