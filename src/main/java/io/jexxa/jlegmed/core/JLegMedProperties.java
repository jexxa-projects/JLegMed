package io.jexxa.jlegmed.core;

public final class JLegMedProperties
{
    /** Define an additional import file that is loaded. The Default value is empty.  */
    public static final String JLEGMED_CONFIG_IMPORT =  "jlegmed.config.import";

    /** Define the name of the bounded context. The Default value is the name of the main class of an application   */
    public static final String JLEGMED_APPLICATION_NAME =  "jlegmed.application.name";

    /** Defines the version number of the context. This is typically set via maven */
    public static final String JLEGMED_APPLICATION_VERSION = "jlegmed.application.version";

    /** Defines the repository of the context. This is typically set via maven */
    public static final String JLEGMED_APPLICATION_REPOSITORY = "jlegmed.application.repository";

    /** Defines the build timestamp of the context. This is typically set via maven */
    public static final String JLEGMED_APPLICATION_BUILD_TIMESTAMP = "jlegmed.application.build.timestamp";

    /** Configures the global system property user.timezone to define the timezone used by the application */
    public static final String JLEGMED_USER_TIMEZONE = "jlegmed.user.timezone";

    /** Defines the default properties file which is /JLEGMED-application.properties */
    public static final String JLEGMED_APPLICATION_PROPERTIES = "/jlegmed-application.properties";

    private JLegMedProperties()
    {
        //Private constructor
    }
}
