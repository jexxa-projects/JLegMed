package io.jexxa.jlegmed.core;

final class JLegMedVersion
{
    public static final String VERSION = "${project.version}";
    public static final String REPOSITORY = "${project.scm.developerConnection}";
    public static final String PROJECT_NAME= "${project.name}";
    public static final String BUILD_TIMESTAMP= "${build.timestamp}";

    private JLegMedVersion()
    {
        //private constructor
    }

    public static VersionInfo getVersion()
    {
        return VersionInfo.of()
                .version(VERSION)
                .repository(REPOSITORY)
                .buildTimestamp(BUILD_TIMESTAMP)
                .projectName(PROJECT_NAME)
                .create();
    }
}