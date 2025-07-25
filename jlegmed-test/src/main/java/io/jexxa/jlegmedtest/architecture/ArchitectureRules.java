package io.jexxa.jlegmedtest.architecture;

/**
 * This class provides methods to validate the architecture of your application.
 */
@SuppressWarnings("unused")
public final class ArchitectureRules {
    public static DTORules dtoRules(Class<?> project)
    {
        return new DTORules(project);
    }

    public static FilterRules filterRules(Class<?> project)
    {
        return new FilterRules(project);
    }



    private ArchitectureRules()
    {
        //Empty private constructor
    }
}
