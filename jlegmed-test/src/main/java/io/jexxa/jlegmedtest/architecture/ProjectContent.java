package io.jexxa.jlegmedtest.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

@SuppressWarnings("unused")
public abstract class ProjectContent
{
    private final Class<?> project;
    private JavaClasses importedClasses;

    protected ProjectContent(Class<?> project, ImportOption importOption)
    {
        this.project = project;
        importedClasses = new ClassFileImporter()
                .withImportOption(importOption)
                .importPackages(
                        project.getPackageName()+ ".dto..",
                        project.getPackageName()+ ".plugins..");

    }

    public ProjectContent ignoreClass(Class<?> clazz)
    {
        importedClasses = importedClasses.that(isNot(clazz));
        return this;
    }

    public ProjectContent ignorePackage(String packageName)
    {
        importedClasses = importedClasses.that(areNotIn(packageName));
        return this;
    }
    public abstract void validate();

    protected JavaClasses importedClasses()
    {
        return importedClasses;
    }

    protected Class<?> project()
    {
        return project;
    }
    private static DescribedPredicate<JavaClass> isNot(Class<?>clazz) {
        return new DescribedPredicate<>("Ignore class " + clazz.getSimpleName()) {
            @Override
            public boolean test(JavaClass javaClass) {
                return !javaClass.isEquivalentTo(clazz);
            }
        };
    }

    private static DescribedPredicate<JavaClass> areNotIn(String packageName) {
        return new DescribedPredicate<>("Ignore package " + packageName) {
            @Override
            public boolean test(JavaClass javaClass) {
                return !javaClass.getPackage().getName().contains(packageName);
            }
        };
    }

}
