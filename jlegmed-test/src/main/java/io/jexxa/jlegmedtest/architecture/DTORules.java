package io.jexxa.jlegmedtest.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import io.jexxa.jlegmed.annotation.DomainEvent;
import io.jexxa.jlegmed.annotation.FlowData;
import io.jexxa.jlegmed.annotation.FlowError;
import io.jexxa.jlegmed.annotation.PublishedMessage;
import io.jexxa.jlegmed.annotation.TelemetryData;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static io.jexxa.jlegmedtest.architecture.PackageName.DOMAIN_EVENT;
import static io.jexxa.jlegmedtest.architecture.PackageName.FLOW_DATA;
import static io.jexxa.jlegmedtest.architecture.PackageName.FLOW_ERROR;
import static io.jexxa.jlegmedtest.architecture.PackageName.PUBLISHED_MESSAGE;
import static io.jexxa.jlegmedtest.architecture.PackageName.TELEMETRY_DATA;


public class DTORules extends ProjectContent {
    DTORules(Class<?> project)
    {
        this(project, ImportOption.Predefined.DO_NOT_INCLUDE_TESTS);
    }
    protected DTORules(Class<?> project, ImportOption importOption)
    {
        super(project,importOption);
    }

    @Override
    public void validate() {
        validateFlowData();
        validateDomainEvent();
        validateFlowError();
        validateTelemetryData();
        validatePublishedMessage();
    }

    protected void validateDomainEvent()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(DOMAIN_EVENT)
                .and().areNotAnonymousClasses()
                .should().beAnnotatedWith(DomainEvent.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateTelemetryData()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(TELEMETRY_DATA)
                .and().areNotAnonymousClasses()
                .should().beAnnotatedWith(TelemetryData.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateFlowData()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(FLOW_DATA)
                .and().areNotAnonymousClasses()
                .should().beAnnotatedWith(FlowData.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateFlowError()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(FLOW_ERROR)
                .and().areNotAnonymousClasses()
                .should().beAnnotatedWith(FlowError.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validatePublishedMessage()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(PUBLISHED_MESSAGE)
                .and().areNotAnonymousClasses()
                .should().beAnnotatedWith(PublishedMessage.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }
}
