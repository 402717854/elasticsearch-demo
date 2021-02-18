package com.elasticsearch.example;

import com.elasticsearch.example.model.UserInfo;
import com.elasticsearch.example.service.DocService;
import com.elasticsearch.example.service.IndexService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.metrics.InternalAvg;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchQueryTests {

    @Autowired
    private DocService docService;

    @Autowired
    private IndexService indexService;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Test
    void testCreateIndex() {
        indexService.createIndex();
    }
    @Test
    void testDeleteIndex() {
        indexService.deleteIndex();
    }
    @Test
    void testAddDocument(){
        docService.addDocument();
    }
    @Test
    void testGetDocument(){
        docService.getDocument(1L);
    }
    @Test
    void testUpdateDocument(){
        docService.updateDocument(2L);
    }
    @Test
    void testDeleteDocument(){
        docService.deleteDocument(3L);
    }
    /**
     * 自定义查询
     */
    @Test
    public void search() {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("remark", "西京"));
        // 搜索，获取结果
        SearchHits<UserInfo> search = elasticsearchOperations.search(queryBuilder.build(), UserInfo.class);
        // 总条数
        long total = search.getTotalHits();
        System.out.println("total = " + total);
        search.forEach(item -> System.out.println("userInfo = " + item));
    }
    /**
     * 分页查询
     */
    @Test
    public void searchByPage() {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "手机"));
        // 分页：
        int page = 0;
        int size = 2;
        queryBuilder.withPageable(PageRequest.of(page, size));
        // 搜索，获取结果
        SearchHits<UserInfo> search = elasticsearchOperations.search(queryBuilder.build(), UserInfo.class);
        long total = search.getTotalHits();
        int currentSize = search.getSearchHits().size();
        System.out.println("总条数 = " + total);
        System.out.println("总页数 = " + total/currentSize);
        System.out.println("当前页：" + page+1);
        System.out.println("每页大小：" + size);
        search.getSearchHits().forEach(item -> System.out.println("item = " + item));
    }
    /**
     * 排序
     */
    @Test
    public void searchAndSort() {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.termQuery("category", "手机"));
        // 排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC));
        // 搜索，获取结果
        SearchHits<UserInfo> searchHits = elasticsearchOperations.search(queryBuilder.build(), UserInfo.class);

        // 总条数
        long total = searchHits.getTotalHits();
        System.out.println("总条数 = " + total);
        searchHits.forEach(item -> System.out.println("item = " + item));
    }

    /**
     * 聚合为桶
     */
    @Test
    public void testAgg() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brand"));
        // 2、查询,需要把结果强转为AggregatedPage类型
        SearchHits<UserInfo> search = elasticsearchOperations.search(queryBuilder.build(), UserInfo.class);

        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        Aggregations aggregations = search.getAggregations();
        StringTerms agg = aggregations.get("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称
            System.out.println(bucket.getKeyAsString());
            // 3.5、获取桶中的文档数量
            System.out.println(bucket.getDocCount());
        }
    }

    /**
     * 嵌套聚合，求平均值
     */
    @Test
    public void testSubAgg() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
        // 1、添加一个新的聚合，聚合类型为terms，聚合名称为brands，聚合字段为brand
        queryBuilder.addAggregation(
                AggregationBuilders.terms("brands").field("brand")
                        .subAggregation(AggregationBuilders.avg("priceAvg").field("price")) // 在品牌聚合桶内进行嵌套聚合，求平均值
        );
        // 2、查询,需要把结果强转为AggregatedPage类型
        SearchHits<UserInfo> search = elasticsearchOperations.search(queryBuilder.build(), UserInfo.class);
        // 3、解析
        // 3.1、从结果中取出名为brands的那个聚合，
        // 因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
        Aggregations aggregations = search.getAggregations();
        StringTerms agg =  aggregations.get("brands");
        // 3.2、获取桶
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        // 3.3、遍历
        for (StringTerms.Bucket bucket : buckets) {
            // 3.4、获取桶中的key，即品牌名称  3.5、获取桶中的文档数量
            System.out.println(bucket.getKeyAsString() + "，共" + bucket.getDocCount() + "台");

            // 3.6.获取子聚合结果：
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("priceAvg");
            System.out.println("平均售价：" + avg.getValue());
        }
    }

    /**
     * 高亮显示
     */
    @Test
    public void searchHighlight() {
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "搜索引擎"));

        HighlightBuilder.Field hfield= new HighlightBuilder.Field("title")
                .preTags("<em style='color:red'>")
                .postTags("</em>")
                .fragmentSize(100);
        queryBuilder.withHighlightFields(hfield);

        // 搜索，获取结果
        SearchHits<UserInfo> search = elasticsearchOperations.search(queryBuilder.build(), UserInfo.class);
        // 总条数
        long total = search.getTotalHits();
        System.out.println("total = " + total);
        search.forEach(item -> System.out.println("item = " + item));
    }
}
