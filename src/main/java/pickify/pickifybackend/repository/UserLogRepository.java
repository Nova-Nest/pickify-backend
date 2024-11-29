package pickify.pickifybackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pickify.pickifybackend.domain.UserLog;

public interface UserLogRepository extends MongoRepository<UserLog, String> {

}
