package pickify.pickifybackend.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "userLog")
public record UserLog(
        @Id
        String id,
        String userUuid,
        String mainKeyword,
        List<String> builtInAiKeywords,
        List<String> geminiReturnKeywords,
        String category
) {

}
