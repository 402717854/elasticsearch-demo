package com.elasticsearch.example.service;

import com.elasticsearch.example.model.UserInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserInfoRepository extends ElasticsearchRepository<UserInfo,Long>{

}
