package org.talend.daikon.spring.mongo.migration;

import com.github.fakemongo.Fongo;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.daikon.spring.mongo.TestMultiTenantConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MongoTemplateIntegrationTest.Config.class})
public class MongoTemplateIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Mongo fongo;

    @Autowired
    private MongoTemplate template;

    @Rule
    public MongoDbRule mongoDbRule = MongoDbRule.MongoDbRuleBuilder.newMongoDbRule().defaultSpringMongoDb("standard");

    @Configuration
    @ComponentScan
    @EnableMongoRepositories
    public static class Config {
        // A fake mongo for tests
        @Bean
        public Mongo fongo() {
            return new Fongo("MongoDB").getMongo();
        }

        @Bean
        public MongoDbFactory defaultMongoDbFactory(final Mongo fongo) {
            // Applications are expected to have one MongoDbFactory available
            return new SimpleMongoDbFactory(fongo, "standard");
        }

        @Bean
        public MongoTemplate mongoTemplate(final MongoDbFactory factory) {
            // Used in tests
            return new MongoTemplate(factory);
        }
    }

    @Test
    @UsingDataSet(locations = "content.json")
    public void shouldConvert() {
        // when
        final List<B> all = template.findAll(B.class, "b");

        // then
        assertEquals(1, all.size());
        for (B b : all) {
            assertEquals("From a previous version", b.getValue());
        }
    }
}
