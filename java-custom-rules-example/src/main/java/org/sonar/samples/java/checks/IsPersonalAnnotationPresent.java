package org.sonar.samples.java.checks;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.semantic.SymbolMetadata.AnnotationInstance;


class IsPersonalAnnotationPresent implements Predicate<AnnotationInstance> {

    final static String ANNOTATION_PACKAGE = "org.uvm";
    final static String ANNOTATION_CLASS = "PersonalData";

    @Override
    public boolean test(final @Nullable AnnotationInstance annotationInstance) {
        final Optional<String> annotationClass = Optional.ofNullable(annotationInstance).//
                map(AnnotationInstance::symbol).//
                map(Symbol::name);
        final Optional<String> annotationPackage = Optional.ofNullable(annotationInstance).//
                map(AnnotationInstance::symbol).//
                map(Symbol::owner).//
                map(Symbol::name);

        final boolean samePackage = Objects.equals(ANNOTATION_PACKAGE, annotationPackage.orElse(null));
        final boolean sameClass = Objects.equals(ANNOTATION_CLASS, annotationClass.orElse(null));

        return sameClass && samePackage;
    }
}