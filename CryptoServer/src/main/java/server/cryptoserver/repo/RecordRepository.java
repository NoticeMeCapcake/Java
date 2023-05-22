package server.cryptoserver.repo;

import org.springframework.data.repository.CrudRepository;
import server.cryptoserver.models.RecordModel;

public interface RecordRepository extends CrudRepository<RecordModel, Long> {
}
