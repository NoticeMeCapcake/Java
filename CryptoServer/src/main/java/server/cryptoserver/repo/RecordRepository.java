package server.cryptoserver.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import server.cryptoserver.models.FileShortcut;
import server.cryptoserver.models.RecordModel;

import java.util.List;


public interface RecordRepository extends CrudRepository<RecordModel, Long> {
    @Query("SELECT NEW server.cryptoserver.models.FileShortcut(e.id, e.name, e.mode, e.size, e._date) FROM RecordModel e")
    List<FileShortcut> findAllFields();
}
