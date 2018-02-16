package org.talend.daikon.spring.mongo.migration;

import com.mongodb.DBObject;

import java.util.function.Function;

public interface MongoDBMigration<T> extends Migration<DBObject, T> {

    Function<DBObject, String> MONGODB_VERSION = dbo -> dbo.containsField("_version") ? String.valueOf(dbo.get("_version")) : null;
}
