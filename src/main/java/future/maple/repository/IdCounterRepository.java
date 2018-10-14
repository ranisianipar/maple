package future.maple.repository;

import future.maple.model.IdCounter;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IdCounterRepository extends MongoRepository<IdCounter, String> {
}
