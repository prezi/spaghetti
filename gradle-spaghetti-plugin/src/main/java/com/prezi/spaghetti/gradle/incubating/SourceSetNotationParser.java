package com.prezi.spaghetti.gradle.incubating;

import org.gradle.internal.typeconversion.NotationParser;
import org.gradle.internal.typeconversion.NotationParserBuilder;
import org.gradle.internal.typeconversion.TypeInfo;
import org.gradle.internal.typeconversion.TypedNotationParser;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class SourceSetNotationParser {
	public static NotationParser<Object, Set<LanguageSourceSet>> parser() {
        return NotationParserBuilder
                .toType(new TypeInfo<Set<LanguageSourceSet>>(Set.class))
                .parser(new FunctionalSourceSetConverter())
                .parser(new SingleLanguageSourceSetConverter())
                .parser(new LanguageSourceSetCollectionConverter())
                .toComposite();
    }

    private static class FunctionalSourceSetConverter extends TypedNotationParser<FunctionalSourceSet, Set<LanguageSourceSet>> {
        private FunctionalSourceSetConverter() {
            super(FunctionalSourceSet.class);
        }

        @Override
        protected Set<LanguageSourceSet> parseType(FunctionalSourceSet notation) {
            return notation;
        }
    }

    private static class SingleLanguageSourceSetConverter extends TypedNotationParser<LanguageSourceSet, Set<LanguageSourceSet>> {
        private SingleLanguageSourceSetConverter() {
            super(LanguageSourceSet.class);
        }

        @Override
        protected Set<LanguageSourceSet> parseType(LanguageSourceSet notation) {
            return Collections.singleton(notation);
        }
    }

    private static class LanguageSourceSetCollectionConverter extends TypedNotationParser<Collection<LanguageSourceSet>, Set<LanguageSourceSet>> {
        private LanguageSourceSetCollectionConverter() {
            super(new TypeInfo<Collection<LanguageSourceSet>>(Collection.class));
        }

        @Override
        protected Set<LanguageSourceSet> parseType(Collection<LanguageSourceSet> notation) {
            return new LinkedHashSet<LanguageSourceSet>(notation);
        }
    }
}
