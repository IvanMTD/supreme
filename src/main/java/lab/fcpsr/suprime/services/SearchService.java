package lab.fcpsr.suprime.services;

import com.manticoresearch.client.api.IndexApi;
import com.manticoresearch.client.api.SearchApi;
import com.manticoresearch.client.api.UtilsApi;
import com.manticoresearch.client.model.InsertDocumentRequest;
import com.manticoresearch.client.model.SuccessResponse;
import lab.fcpsr.suprime.models.Post;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class SearchService {

    @Value("${manticore.table}")
    private String table;

    private final IndexApi index;
    private final UtilsApi utils;
    private final SearchApi search;

    @SneakyThrows
    public Mono<Void> insertPost(Post post){
        InsertDocumentRequest doc = new InsertDocumentRequest();
        Map<String,Object> docMap = new HashMap<>();
        docMap.put("post_id",String.valueOf(post.getId()));
        docMap.put("name",post.getName());
        docMap.put("annotation",post.getAnnotation());
        docMap.put("content",post.getContent());
        doc.index(table).doc(docMap);
        SuccessResponse response = index.insert(doc);
        log.info("INSERT IN MANTICORE: " + response.toString());
        return Mono.empty();
    }
}
