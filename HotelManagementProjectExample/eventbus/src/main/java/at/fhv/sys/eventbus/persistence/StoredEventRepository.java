package at.fhv.sys.eventbus.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StoredEventRepository implements PanacheRepository<StoredEvent> {
}