package io.jexxa.jlegmedtest;

import io.jexxa.common.facade.utils.annotation.CheckReturnValue;
import io.jexxa.common.facade.utils.function.ThrowingConsumer;
import io.jexxa.jlegmed.core.JLegMed;

import java.util.Optional;
import java.util.Properties;

import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;


public class JLegMedTest
{
    public static final String JLEGMED_TEST_PROPERTIES = "/jlegmed-test.properties";

    private static JLegMed jLegMed;

    private JLegMedTest()
    {
        // Private to hide public one
    }

    public static synchronized <T> JLegMedTest getJLegMedTest(Class<T> jexxaApplication)
    {
        if (jLegMed == null) {
            jLegMed = new JLegMed(jexxaApplication, loadJLegMedTestProperties());
        }
        return new JLegMedTest();
    }

    public JLegMed getJLegMed()
    {
        return jLegMed;
    }

    @CheckReturnValue
    public Properties getProperties()
    {
        return getJLegMed().getProperties();
    }

    static Properties loadJLegMedTestProperties()
    {
        var properties = new Properties();
        Optional.ofNullable(JLegMed.class.getResourceAsStream(JLEGMED_TEST_PROPERTIES))
                .ifPresentOrElse(
                        ThrowingConsumer.exceptionLogger(properties::load, getLogger(JLegMedTest.class)),
                        () -> getLogger(JLegMedTest.class).warn("Properties file '{}' not found", JLEGMED_TEST_PROPERTIES)
                );
        return properties;
    }
}
