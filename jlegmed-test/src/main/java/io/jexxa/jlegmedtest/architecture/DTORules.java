package io.jexxa.jlegmedtest.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import io.jexxa.jlegmed.annotation.ConsumedMessage;
import io.jexxa.jlegmed.annotation.DomainEvent;
import io.jexxa.jlegmed.annotation.FlowData;
import io.jexxa.jlegmed.annotation.FlowError;
import io.jexxa.jlegmed.annotation.PublishedMessage;
import io.jexxa.jlegmed.annotation.TelemetryData;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static io.jexxa.jlegmedtest.architecture.PackageName.CONSUMED_MESSAGE;
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
        validateConsumedMessage();
    }

    protected void validateDomainEvent()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(DOMAIN_EVENT)
                .and().areNotAnonymousClasses()
                .and().areNotNestedClasses()
                .and().arePublic()
                .should().beAnnotatedWith(DomainEvent.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateTelemetryData()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(TELEMETRY_DATA)
                .and().areNotAnonymousClasses()
                .and().areNotNestedClasses()
                .and().arePublic()
                .should().beAnnotatedWith(TelemetryData.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateFlowData()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(FLOW_DATA)
                .and().areNotAnonymousClasses()
                .and().areNotNestedClasses()
                .and().arePublic()
                .should().beAnnotatedWith(FlowData.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validateFlowError()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(FLOW_ERROR)
                .and().areNotAnonymousClasses()
                .and().areNotNestedClasses()
                .and().arePublic()
                .should().beAnnotatedWith(FlowError.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }

    protected void validatePublishedMessage()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(PUBLISHED_MESSAGE)
                .and().areNotAnonymousClasses()
                .and().areNotNestedClasses()
                .and().arePublic()
                .should().beAnnotatedWith(PublishedMessage.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }
    protected void validateConsumedMessage()
    {
        var annotationRule = classes()
                .that().resideInAnyPackage(CONSUMED_MESSAGE)
                .and().areNotAnonymousClasses()
                .and().areNotNestedClasses()
                .and().arePublic()
                .should().beAnnotatedWith(ConsumedMessage.class)
                .allowEmptyShould(true);

        annotationRule.check(importedClasses());
    }
}
