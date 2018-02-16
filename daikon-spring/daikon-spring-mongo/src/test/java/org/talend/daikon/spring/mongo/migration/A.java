package org.talend.daikon.spring.mongo.migration;

import com.mongodb.DBObject;

public class A {

    private String myNewField = "my new default value";

    public String getMyNewField() {
        return myNewField;
    }

    public void setMyNewField(String myNewField) {
        this.myNewField = myNewField;
    }

    @MigrationRule(version = "1.0.0")
    public static class Rule_100 implements MongoDBMigration<A> {

        @Override
        public A apply(DBObject source, A a) {
            a.setMyNewField(String.valueOf(source.get("oldField")));
            return a;
        }
    }

    @MigrationRule(version = "1.0.1")
    public static class Rule_101 implements MongoDBMigration<A> {

        @Override
        public A apply(DBObject source, A a) {
            a.setMyNewField(a.getMyNewField() + " (1.0.1 version)");
            return a;
        }
    }

    @MigrationRule(version = "1.0.0")
    public static class InvalidRule implements MongoDBMigration<A> {

        public InvalidRule(String arg) {
        }

        @Override
        public A apply(DBObject source, A a) {
            return a; // Nothing to do (test is more about the constructor).
        }
    }
}
