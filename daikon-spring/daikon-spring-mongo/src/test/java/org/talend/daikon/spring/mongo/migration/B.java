package org.talend.daikon.spring.mongo.migration;

import com.mongodb.DBObject;

public class B {

    private String _version = "1.0.0";

    private String value = "default";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @MigrationRule(version = "2.0.0")
    public static class Rule implements MongoDBMigration<B> {

        @Override
        public B apply(DBObject source, B target) {
            final DBObject nested = (DBObject) source.get("nested");
            target.setValue(String.valueOf(nested.get("fromPreviousVersion")));
            return target;
        }
    }
}
