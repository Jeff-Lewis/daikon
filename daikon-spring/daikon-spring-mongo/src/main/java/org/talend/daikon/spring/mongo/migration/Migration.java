package org.talend.daikon.spring.mongo.migration;

public interface Migration<S, T> {

    T apply(S source, T target);

}
