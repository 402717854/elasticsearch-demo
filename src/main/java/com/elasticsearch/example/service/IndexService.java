package com.elasticsearch.example.service;


import com.elasticsearch.example.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

/**
 * @ClassName IndexService
 * @Description: TODO 索引操作
 * @Author wys
 * @Date 2020/8/10-20:55
 * @Version V1.0
 **/
@Slf4j
@Service
public class IndexService {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 创建索引
     */
    public void createIndex() {
        try {
            IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(UserInfo.class);
            boolean b1 = indexOperations.create();
            boolean b = indexOperations.putMapping();
            log.info("是否创建成功：{},{}", b1,b);
        }catch (Exception e){
            log.error("创建索引异常", e);
        }
    }

    public void deleteIndex(){
        try {
            IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(UserInfo.class);
            boolean delete = indexOperations.delete();
            log.info("是否删除成功：{}", delete);
        }catch (Exception e){
            log.error("删除索引异常", e);
        }
    }
}
