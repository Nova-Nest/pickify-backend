package pickify.pickifybackend.domain;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "userLog")
public class UserLog {
    @Id
    private String id;
    private String userUuid;
    private String mainKeyword;
    private List<String> builtInAiKeywords;
    private List<String> geminiReturnKeywords;
    private String category;

    @Builder
    private UserLog(String userUuid, String mainKeyword, List<String> builtInAiKeywords, List<String> geminiReturnKeywords, String category) {
        this.userUuid = userUuid;
        this.mainKeyword = mainKeyword;
        this.builtInAiKeywords = builtInAiKeywords;
        this.geminiReturnKeywords = geminiReturnKeywords;
        this.category = category;
    }
}
