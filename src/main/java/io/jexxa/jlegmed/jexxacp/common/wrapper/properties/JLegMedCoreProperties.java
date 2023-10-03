package io.jexxa.jlegmed.jexxacp.common.wrapper.properties;

public class JLegMedCoreProperties {
    /** Define an additional import file that is loaded. Default value is empty.  */
    public static final String JLEGMED_CONFIG_IMPORT =  "io.jexxa.jlegmed.config.import";

    /** Define the name of the bounded context. Default value is the name of the main class of an application   */
    public static final String JLEGMED_APPLICATION_NAME =  "io.jexxa.jlegmed.application.name";

    /** Defines the version number of the context. This is typically set via maven */
    public static final String JLEGMED_APPLICATION_VERSION = "io.jexxa.jlegmed.application.version";

    /** Defines the repository of the context. This is typically set via maven */
    public static final String JLEGMED_APPLICATION_REPOSITORY = "io.jexxa.jlegmed.application.repository";

    /** Defines the build timestamp of the context. This is typically set via maven */
    public static final String JLEGMED_APPLICATION_BUILD_TIMESTAMP = "io.jexxa.jlegmed.application-build.timestamp";

    /** Configures the global system property user.timezone to define the timezone used by the application */
    public static final String JLEGMED_USER_TIMEZONE = "io.jexxa.jlegmed.user.timezone";

    /** Defines the default properties file which is /jexxa-application.properties */
    public static final String JLEGMED_APPLICATION_PROPERTIES = "/jlegmed-application.properties";

    private JLegMedCoreProperties()
    {
        //Private constructor
    }
}
