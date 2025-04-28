package at.fhv.sys.eventbus.startup;

import at.fhv.sys.eventbus.services.EventProjectionService;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.logging.Logger;

@ApplicationScoped
public class StartupBean {
    private static final Logger LOGGER = Logger.getLogger(StartupBean.class.getName());

    @Inject
    EventProjectionService eventProjectionService;

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("Application starting...");
        LOGGER.info("Startup tasks completed.");
    }
}