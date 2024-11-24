package pickify.pickifybackend.service;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;

import java.util.List;

public class PromptManager {

    public static UserMessage extractKeywordFromPhoto(String photoUrl, String langType) {
        return UserMessage.from(
                ImageContent.from(photoUrl),
                TextContent.from(String.format("""
                        You can extract some keywords with the pictures. Your response should be %s
                        Keyword should be related with it and never repeat itself.
                        such as input is a coat, keyword never be a coat.
                        Keyword should be winter coat, black coat, or whatever very related with it.
                        You also give maximum 10 keywords. 
                        output should be Json format.
                        
                        for example if the image is macbook, your keywords should be
                        "macbook", "laptop", "windows laptop", "mouse", "keyboard", "apple" like that.
                        
                        format : 
                        
                        {
                            "langType" : input_language,
                            "keywords" : [keyword_list],
                        }
                        """, langType)
                )
        );
    }

    public static UserMessage extractImagesWithKeywords(String photoUrl, List<String> keywords) {
        return UserMessage.from(
                ImageContent.from(photoUrl),
                TextContent.from(String.format("""
                        You should search result with picture(%s) and keywords list(%s) 
                        Your response should be and image url.
                        Give an image url from Google.
                        Follow bellowing format as a json
                        
                        format : 
                        
                        {
                            "relatedUrl" : [url_list]
                        }
                        """, photoUrl, keywords)
                )
        );
    }
}
