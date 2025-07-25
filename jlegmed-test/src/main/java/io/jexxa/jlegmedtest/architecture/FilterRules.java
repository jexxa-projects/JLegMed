package io.jexxa.jlegmedtest.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import io.jexxa.jlegmed.annotation.Filter;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static io.jexxa.jlegmedtest.architecture.PackageName.PLUGINS;

public class FilterRules extends ProjectContent {
    FilterRules(Class<?> project)
    {
        this(project, ImportOption.Predefined.DO_NOT_INCLUDE_TESTS);
    }
    protected FilterRules(Class<?> project, ImportOption importOption)
    {
        super(project,importOption);
    }

    @Override
    public void validate() {
        ArchCondition<JavaClass> condition = new ArchCondition<>("public static methods must be annotated with @Filter if class is not") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                boolean classHasFilter = javaClass.isAnnotatedWith(Filter.class);

                if (!classHasFilter) {
                    for (JavaMethod method : javaClass.getMethods()) {
                        boolean isPublicStatic = method.getModifiers().contains(JavaModifier.PUBLIC) &&
                                method.getModifiers().contains(JavaModifier.STATIC);
                        boolean methodHasFilter = method.isAnnotatedWith(Filter.class);

                        if (isPublicStatic && !methodHasFilter) {
                            String msg = String.format(
                                    "Method %s in class %s must be annotated with @Filter because the class is not",
                                    method.getFullName(), javaClass.getName());
                            events.add(SimpleConditionEvent.violated(method, msg));
                        }
                    }
                }
            }
          };

          classes()
                  .that()
                  .resideInAnyPackage(PLUGINS)
                  .should(condition)
                  .check(importedClasses());
      }


}
