package io.jexxa.jlegmedtest.architecture;

/**
 * This class defines the package names to validate the onion architecture.
 * <p>
 * In case you use another package structure for your application, please adjust these packages accordingly.
 */
final class PackageName
{
    public static final String DOMAIN_EVENT = "..domainevent..";
    public static final String TELEMETRY_DATA = "..dto.telemetrydata..";
    public static final String PUBLISHED_MESSAGE = "..dto.publishedmessage..";
    public static final String FLOW_DATA = "..dto.flowdata..";
    public static final String FLOW_ERROR = "..dto.flowerror..";
    public static final String PLUGINS = "..plugins..";

    private PackageName()
    {
        //Private Constructor
    }
}
