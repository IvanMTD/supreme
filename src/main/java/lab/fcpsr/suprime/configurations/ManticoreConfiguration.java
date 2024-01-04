package lab.fcpsr.suprime.configurations;

import com.manticoresearch.client.api.IndexApi;
import com.manticoresearch.client.api.SearchApi;
import com.manticoresearch.client.api.UtilsApi;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.manticoresearch.client.*;

import java.util.List;

@Slf4j
@Configuration
public class ManticoreConfiguration {

    @Value("${manticore.server}")
    private String server;
    @Value("${manticore.table}")
    private String table;
    @Bean
    public ApiClient client(){
        ApiClient client = com.manticoresearch.client.Configuration.getDefaultApiClient();
        client.setBasePath(server);
        return client;
    }

    @Bean
    @SneakyThrows
    public UtilsApi utils(){
        UtilsApi utils = new UtilsApi(client());
        String tableCommand = "create table if not exists " + table + " (post_id text, name text, annotation text, content text, tag text) morphology='stem_enru, libstemmer_ru' html_strip = '1'";
        List<Object> response = utils.sql(tableCommand,true);
        for(Object r : response){
            log.info("CREATE TABLE RESPONSE: " + r.toString());
        }
        return utils;
    }

    @Bean
    public IndexApi index(){
        return new IndexApi(client());
    }

    @Bean
    SearchApi search(){
        return new SearchApi(client());
    }
}
