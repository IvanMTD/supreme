package lab.fcpsr.suprime.services;

import com.manticoresearch.client.api.IndexApi;
import com.manticoresearch.client.api.SearchApi;
import com.manticoresearch.client.api.UtilsApi;
import com.manticoresearch.client.model.*;
import lab.fcpsr.suprime.models.Post;
import lab.fcpsr.suprime.models.SportTag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

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
    public Mono<SuccessResponse> insertPost(Post post, List<SportTag> sportTags){
        InsertDocumentRequest doc = new InsertDocumentRequest();
        Map<String,Object> docMap = new HashMap<>();
        docMap.put("post_id",String.valueOf(post.getId()));
        docMap.put("name",post.getName());
        docMap.put("annotation",post.getAnnotation());
        docMap.put("content",post.getContent());
        docMap.put("tag", getStringFromArray(sportTags));
        doc.index(table).doc(docMap);
        SuccessResponse response = index.insert(doc);
        return Mono.just(response);
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

    @SneakyThrows
    public Mono<SuccessResponse> updatePost(Post post){
        Map<String,Object> postMap = getPostData(post);
        long sourceId = Long.parseLong(postMap.get("id").toString());
        log.info("SOURCE ID: " + sourceId);
        String tag = postMap.get("tag").toString();

        InsertDocumentRequest replaceRequest = new InsertDocumentRequest();
        Map<String,Object> docMap = new HashMap<>();
        docMap.put("post_id",String.valueOf(post.getId()));
        docMap.put("name",post.getName());
        docMap.put("annotation",post.getAnnotation());
        docMap.put("content",post.getContent());
        docMap.put("tag", tag);
        replaceRequest.index(table).id(sourceId).setDoc(docMap);
        return Mono.just(index.replace(replaceRequest));
    }

    @SneakyThrows
    public Mono<DeleteResponse> deletePost(Post post){
        Map<String,Object> postMap = getPostData(post);
        long sourceId = Long.parseLong(postMap.get("id").toString());
        log.info("SOURCE ID: " + sourceId);

        DeleteDocumentRequest deleteRequest = new DeleteDocumentRequest();
        deleteRequest.index(table).setId(sourceId);
        return Mono.just(index.delete(deleteRequest));
    }

    @SneakyThrows
    private Map<String,Object> getPostData(Post post){
        String sqlCommand = "select * from post_table where match('@post_id " + post.getId() + "')";
        List<Object> results = utils.sql(sqlCommand,true);
        Object result = results.get(0);
        Map<String, Object> mainSourceMap = (LinkedHashMap<String,Object>)result;
        List<Object> sourceList = (ArrayList<Object>) mainSourceMap.get("data");
        Object data = sourceList.get(0);
        return (LinkedHashMap<String,Object>)data;
    }

    private String getStringFromArray(List<SportTag> sportTags){
        StringBuilder builder = new StringBuilder();
        for(SportTag sportTag : sportTags){
            builder.append(sportTag.getName() + " ");
        }
        return builder.toString();
    }

    private Flux<Integer> parse(SearchResponse response){
        List<Object> hits = response.getHits().getHits();
        return Flux.fromIterable(hits).map(hit -> {
            Map<String, Object> mainSource = (LinkedHashMap<String,Object>)hit;
            Map<String, Object> postMap = (LinkedHashMap<String,Object>)mainSource.get("_source");
            String postId = postMap.get("post_id").toString();
            log.info("Found id " + postId);
            return Integer.valueOf(postId);
        });
    }
}
