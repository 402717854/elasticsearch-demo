package com.elasticsearch.example.service;

import com.alibaba.fastjson.JSON;
import com.elasticsearch.example.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName DocService
 * @Description: TODO 文档操作
 * @Author wys
 * @Date 2020/8/10-21:14
 * @Version V1.0
 **/
@Slf4j
@Service
public class DocService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    /**
     * 增加文档信息
     */
    public void addDocument() {
        try {
            List<UserInfo> list=new ArrayList();
            // 创建员工信息
            for (int i = 0; i < 100; i++) {
                UserInfo userInfo = new UserInfo();
                userInfo.setId(i+1L);
                userInfo.setAge(29+i);
                userInfo.setSalary(100.00f+i);
                if(i%3==0){
                    userInfo.setName("李三");
                    userInfo.setAddress("第"+i+"个南京市");
                    userInfo.setRemark("来自南京市的李先生");
                }if(i%3==1){
                    userInfo.setName("张三");
                    userInfo.setAddress("第"+i+"个西京市");
                    userInfo.setRemark("来自西京市的张先生");
                }if(i%3==2){
                    userInfo.setName("张四");
                    userInfo.setAddress("第"+i+"个东京市");
                    userInfo.setRemark("来自东京市的张先生");
                }else{
                    userInfo.setName("李四");
                    userInfo.setAddress("第"+i+"个北京市");
                    userInfo.setRemark("来自北京市的李先生");
                }
                userInfo.setCreateTime(new Date());
                userInfo.setBirthDate("1990-02-10 12:12:12");
                list.add(userInfo);
            }
            elasticsearchOperations.save(list);
//            userInfoRepository.saveAll(list);
        } catch (Exception e) {
            log.error("增加文档信息", e);
        }
    }

    /**
     * 获取文档信息
     */
    public void getDocument(Long id) {
        try {
            Optional<UserInfo> optionalUserInfo = userInfoRepository.findById(id);
            boolean present = optionalUserInfo.isPresent();
            if (present) {
                log.info("文档信息：{}", JSON.toJSON(optionalUserInfo));
                UserInfo userInfo = optionalUserInfo.get();
                log.info("员工信息：{}", JSON.toJSON(userInfo));
            }
        } catch (Exception e) {
            log.error("获取文档信息", e);
        }
    }

    /**
     * 更新文档信息
     */
    public void updateDocument(Long id) {
        try {
            // 设置员工更新信息
            UserInfo userInfo = new UserInfo();
            userInfo.setId(id);
            userInfo.setSalary(200.00f);
            userInfo.setAddress("北京市海淀区");
            UserInfo save = userInfoRepository.save(userInfo);
            log.info("更新状态：{}", JSON.toJSON(save));
        } catch (Exception e) {
            log.error("更新文档信息", e);
        }
    }

    /**
     * 删除文档信息
     */
    public void deleteDocument(Long id) {
        try {
            userInfoRepository.deleteById(id);
        } catch (Exception e) {
            log.error("删除文档信息", e);
        }
    }
}
