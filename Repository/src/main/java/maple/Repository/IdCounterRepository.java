package maple.Repository;

import maple.Model.IdCounter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdCounterRepository extends MongoRepository<IdCounter, String> {
}
