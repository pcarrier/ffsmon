package org.ffsmon.collectd.tests;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;

public class CollectdLogger extends AbstractModule {
    @Override
    protected void configure() {
        bind(EventBus.class).toInstance(new EventBus());
    }
}
