package pickify.pickifybackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pickify.pickifybackend.entity.UserLog;

public interface UserLogRepository extends MongoRepository<UserLog, String> {

}
