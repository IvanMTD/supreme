package lab.fcpsr.suprime.services;

import com.manticoresearch.client.api.IndexApi;
import com.manticoresearch.client.api.SearchApi;
import com.manticoresearch.client.api.UtilsApi;
import com.manticoresearch.client.model.*;
import lab.fcpsr.suprime.models.Post;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

    @SneakyThrows
    public Flux<Integer> searchPosts(String request){
        if(request.equals("")){
            return Flux.empty();
        }else {
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.setIndex(table);
            QueryFilter queryFilter = new QueryFilter();
            queryFilter.setQueryString(request);
            searchRequest.setFulltextFilter(queryFilter);
            SearchResponse response = search.search(searchRequest);
            return parse(response);
        }
    }

    private Flux<Integer> parse(SearchResponse response){
        List<Object> hits = response.getHits().getHits();
        return Flux.fromIterable(hits).map(hit -> {
            Map<String, Object> mainSource = (LinkedHashMap<String,Object>)hit;
            Map<String, Object> postMap = (LinkedHashMap<String,Object>)mainSource.get("_source");
            String postId = postMap.get("post_id").toString();
            return Integer.valueOf(postId);
        });
    }
}
