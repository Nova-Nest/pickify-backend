package pickify.pickifybackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pickify.pickifybackend.entity.UserLog;

import java.util.List;

public interface UserLogRepository extends MongoRepository<UserLog, String> {
    List<UserLog> findAllByCategoryAndUserUuid(String category, String userUuid);
}
