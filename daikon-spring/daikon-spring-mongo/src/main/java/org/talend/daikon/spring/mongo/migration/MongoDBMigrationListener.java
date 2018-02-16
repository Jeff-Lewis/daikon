package org.talend.daikon.spring.mongo.migration;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.stereotype.Component;

@Component
@Conditional(MongoDBMigrationListener.class)
public class MongoDBMigrationListener implements ApplicationListener<AfterConvertEvent>, Condition {

    @Override
    public void onApplicationEvent(AfterConvertEvent afterConvertEvent) {
        Migrations.migrate(afterConvertEvent.getDBObject(), //
                MongoDBMigration.MONGODB_VERSION, //
                afterConvertEvent.getSource() //
        );
    }

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return Boolean.valueOf(conditionContext.getEnvironment().getProperty("mongodb.migrations.enabled", "true"));
    }
}
