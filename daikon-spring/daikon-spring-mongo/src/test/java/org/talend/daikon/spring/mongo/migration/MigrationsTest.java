package org.talend.daikon.spring.mongo.migration;

import com.mongodb.DBObject;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.talend.daikon.spring.mongo.migration.MongoDBMigration.MONGODB_VERSION;

public class MigrationsTest {

    @Test
    public void shouldApplyMigration() {
        // given
        DBObject dbObject = mock(DBObject.class);
        when(dbObject.get(eq("oldField"))).thenReturn("my old value");
        when(dbObject.containsField(eq("_version"))).thenReturn(true);
        when(dbObject.get(eq("_version"))).thenReturn("0.0.0");

        // when
        A original = new A();
        final A a = Migrations.migrate(dbObject, MONGODB_VERSION, original);

        // then
        assertNotNull(a);
        assertEquals("my old value (1.0.1 version)", a.getMyNewField());
    }

    @Test
    public void shouldNotApplyMigration() {
        // given
        DBObject dbObject = mock(DBObject.class);
        when(dbObject.get(eq("oldField"))).thenReturn("my old value");
        when(dbObject.get(eq("_version"))).thenReturn("3.0.0");

        // when
        A original = new A();
        final A a = Migrations.migrate(dbObject, MONGODB_VERSION, original);

        // then
        assertNotNull(a);
        assertEquals("my new default value", a.getMyNewField());
    }

}