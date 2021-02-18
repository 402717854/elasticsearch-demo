package com.elasticsearch.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @ClassName UserInfo
 * @Description: TODO
 * @Author wys
 * @Date 2020/8/10-21:07
 * @Version V1.0
 *
 *
 * @Document
 *
 * 作用在类，标记实体类为文档对象，一般有两个属性
 *
 * indexName：对应索引库名称
 * type：对应在索引库中的类型
 * shards：分片数量，默认5
 * replicas：副本数量，默认1
 * @Id 作用在成员变量，标记一个字段作为id主键
 *
 * @Field
 *
 * 作用在成员变量，标记为文档的字段，并指定字段映射属性：
 *
 * type：字段类型，是是枚举：FieldType
 * index：是否索引，布尔类型，默认是true
 * store：是否存储，布尔类型，默认是false
 * analyzer：分词器名称
 **/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "mydlq-user", shards = 3, replicas = 1)
public class UserInfo {

    @Id
    private Long id;
    /**
     * 精准匹配
     */
    @Field(type = FieldType.Keyword)
    private String name;
    @Field(type = FieldType.Keyword,index = false)
    private Integer age;
    @Field(type = FieldType.Float)
    private Float salary;
    /**
     * 分词器模糊匹配
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String address;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String remark;
    @Field(type = FieldType.Date)
    private Date createTime;
    @Field(type = FieldType.Date,format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    private String birthDate;
}
