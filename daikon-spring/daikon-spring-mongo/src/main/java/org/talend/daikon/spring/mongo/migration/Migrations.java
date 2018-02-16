package org.talend.daikon.spring.mongo.migration;

import com.github.zafarkhaja.semver.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * A class to run {@link Migration migrations} against on a <code>T</code> object built using a <code>S</code> instance.
 * This code can be invoked when reading a POJO object out of a MongoDB {@link com.mongodb.DBObject} for example.
 * </p>
 * <p>
 * This class covers this use case:
 * <ul>
 * <li>Source objects (Database object) have a <i>version</i> that can be read.</li>
 * <li>Target object (POJOs) have {@link MigrationRule migration rules} defined.</li>
 * </ul>
 * </p>
 */
public class Migrations {

    private static final Logger LOGGER = LoggerFactory.getLogger(Migrations.class);

    /**
     * <p>
     * Run migrations on <code>T</code> target (a POJO for example), using <code>S</code> as additional input.
     * The <code>sourceVersion</code> is a {@link Function} to extract version information from source object.
     * </p>
     * <p>
     * This
     * </p>
     *
     * @param source        The source object (a {@link com.mongodb.DBObject} for example).
     * @param sourceVersion A {@link Function} to extract source version as a string.
     * @param target        The target object (a POJO for example).
     * @param <S>           The source type.
     * @param <T>           The target type.
     * @return An eventually modified target object based on applicable {@link MigrationRule migration rules} that may
     * (but are not required to) use source object data as input.
     */
    public static <S, T> T migrate(S source, Function<S, String> sourceVersion, T target) {
        final Class<T> targetClass = (Class<T>) target.getClass();
        final List<Migration<S, T>> migrations = compute(targetClass, sourceVersion.apply(source));
        for (Migration<S, T> migration : migrations) {
            target = migration.apply(source, target);
        }
        return target;
    }

    // An internal method to return ordered migration steps.
    private static <S, T> List<Migration<S, T>> compute(Class<T> target, String sourceVersion) {
        if (StringUtils.isEmpty(sourceVersion)) {
            return Collections.emptyList();
        }
        final Version parsedSourceVersion = Version.valueOf(sourceVersion);

        return Stream.of(target.getClasses()) //
                .filter(Migration.class::isAssignableFrom) //
                .filter(r -> AnnotationUtils.findAnnotation(r, MigrationRule.class) != null) //
                .map(r -> {
                    final MigrationRule migrationRule = AnnotationUtils.findAnnotation(r, MigrationRule.class);
                    final Version ruleVersion = Version.valueOf(migrationRule.version());
                    if (parsedSourceVersion.lessThan(ruleVersion)) {
                        try {
                            final Migration<S, T> migration = (Migration<S, T>) r.newInstance();
                            return new MigrationMatch<>(migration, ruleVersion);
                        } catch (InstantiationException | IllegalAccessException e) {
                            LOGGER.error("Migration rule '" + r.getName() + "' must have default constructor.", e);
                            return null;
                        }
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull) //
                .sorted() //
                .map(r -> r.migration) //
                .collect(Collectors.toList());
    }

    // An internal representation to ease sort of applicable migrations.
    private static class MigrationMatch<S, T> implements Comparable<MigrationMatch<S, T>> {

        private final Migration<S, T> migration;

        private final Version version;

        private MigrationMatch(Migration<S, T> migration, Version version) {
            this.migration = migration;
            this.version = version;
        }

        @Override
        public int compareTo(MigrationMatch<S, T> o) {
            return this.version.compareTo(o.version);
        }
    }
}
