package org.ffsmon.utils;

import com.google.common.eventbus.EventBus;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class EventBusSingleton extends EventBus {
    @Provides @Singleton
    static EventBus getEventBus() {
        return new EventBus();
    }
}
