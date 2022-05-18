package org.sonar.samples.java.checks;

import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.semantic.SymbolMetadata;
import org.sonar.plugins.java.api.semantic.SymbolMetadata.AnnotationInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;

class LocatePersonalAnnotation implements Predicate<Symbol> {
    private final Predicate<AnnotationInstance> isVisibleForTestingAnnotation;

    LocatePersonalAnnotation(){
        this(new IsPersonalAnnotationPresent());
    }

    LocatePersonalAnnotation(final @Nonnull Predicate<AnnotationInstance> isVisibleForTestingAnnotation) {
        this.isVisibleForTestingAnnotation = isVisibleForTestingAnnotation;
    }

    @Override
    public boolean test(final @Nullable Symbol symbol) {
        final Optional<Collection<AnnotationInstance>> annotationsOptional = Optional.ofNullable(symbol).//
                map(Symbol::metadata).//
                map(SymbolMetadata::annotations);
        final Collection<AnnotationInstance> annotations = annotationsOptional.orElse(Collections.emptyList());
        final boolean hasVisibleForTesting = annotations.stream().anyMatch(isVisibleForTestingAnnotation);
        return hasVisibleForTesting;
    }

}