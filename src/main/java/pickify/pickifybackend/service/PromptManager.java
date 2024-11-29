package pickify.pickifybackend.service;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;

import java.util.List;

public class PromptManager {

//    public static UserMessage extractKeywordFromPhoto(String photoUrl, String langType) {
//        return UserMessage.from(
//                ImageContent.from(photoUrl),
//                TextContent.from(String.format("""
//                        You can extract some keywords with the pictures. Your response should be %s
//                        Keyword should be related with it and never repeat itself.
//                        such as input is a coat, keyword never be a coat.
//                        Keyword should be winter coat, black coat, or whatever very related with it.
//                        You also give maximum 10 keywords.
//                        output should be Json format.
//
//                        for example if the image is macbook, your keywords should be
//                        "macbook", "laptop", "windows laptop", "mouse", "keyboard", "apple" like that.
//
//                        format :
//
//                        {
//                            "langType" : input_language,
//                            "keywords" : [keyword_list],
//                        }
//                        """, langType)
//                )
//        );
//    }

    public static UserMessage extractKeywordFromPhoto(String photoUrl, String langType) {
        return UserMessage.from(
                ImageContent.from(photoUrl),
                TextContent.from(String.format("""
                        You can extract some keywords with the pictures. Your response should be in %s.
                        Keyword should be related to the picture, but avoid direct repetition.
                        Provide up to 10 related keywords, emphasizing context and variations.
                                            
                        For example:
                        - If input is "macbook", your keywords could include "laptop", "macbook pro", "keyboard", "thin laptop".
                                            
                        Format:
                        {
                            "langType": "%s",
                            "keywords": ["keyword1", "keyword2", ...]
                        }
                        """, langType, langType)
                )
        );
    }

    public static UserMessage extractAndGenerateQueries(String photoUrl, String langType) {
        return UserMessage.from(
                ImageContent.from(photoUrl),
                TextContent.from(String.format("""
                        Analyze the provided image and generate relevant keywords.
                        Then, create 5 unique and meaningful search queries based on the keywords that could help find similar or related products.
                                            
                        Example:
                        If the input is a photo of "macbook":
                        - Keywords: ["laptop", "thin laptop", "apple macbook", "ultrabook"]
                        - Queries: ["best thin laptop", "cheap ultrabook", "alternatives to macbook", "top apple laptops", "buy macbook online"]

                        Your response should include:
                        1. Extracted keywords (up to 10, JSON array).
                        2. Generated search queries (5, JSON array).

                        Json Format never include MD grammars Just pure JSON Format:
                                            
                        {
                            "langType": "%s",
                            "keywords": ["keyword1", "keyword2", ...],
                            "queries": ["query1", "query2", ...]
                        }
                        """, langType)
                )
        );
    }

    public static UserMessage extractImagesWithKeywords(String photoUrl, List<String> keywords) {
        return UserMessage.from(
                ImageContent.from(photoUrl),
                TextContent.from(String.format("""
                        Perform a Google image search using the provided photo as context and the following keywords: %s.
                        Identify the first relevant image based on your search.

                        Your response must strictly follow this JSON format:
                                            
                        {
                            "relatedUrl": ["actual_image_url"]
                        }

                        Ensure that "actual_image_url" is a direct link to the image file (e.g., ending with .jpg, .png, or .gif) and not a Google search result URL.
                        Do not include any other details in the response.
                        """, keywords)
                )
        );
    }

    // AI에게 카테고리를 기반으로 가장 관련 있는 카테고리를 찾도록 요청하는 메서드
    public static UserMessage extractCategoryFromKeywords(List<String> keywords, List<String> categoryList) {
        String categories = String.join(",", categoryList);

        // 키워드를 문자열로 변환
        String keywordString = String.join(",", keywords);

        // AI에게 프롬프트 전달
        return UserMessage.from(
                TextContent.from(String.format("""
                    Extract the most relevant category from the provided keywords: %s.
                    
                    Here are the available categories:
                    %s

                    Your response must strictly follow this JSON format:

                    {
                        "category": "root_category_name"
                    }

                    Replace "root_category_name" with the most relevant category for the provided keywords.
                    """, keywordString, categories)
                )
        );

    }
}
