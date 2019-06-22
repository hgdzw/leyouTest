package com.leyou.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.client.ItemClient;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.dto.GoodsDTO;
import com.leyou.dto.SearchRequest;
import com.leyou.item.dto.*;
import com.leyou.pojo.Goods;
import com.leyou.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @Author: dzw
 * @Date: 2019/6/19 20:59
 * @Version 1.0
 */
@Service
public class SearchService {


    @Autowired
    private ItemClient itemClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate esTemplate;
    /**
     * 将spu对象 转化为 goods对象
     * @param spu
     * @return
     */
    public Goods buildGoods(SpuDTO spu){

        //1.all中包含标题 分类 品牌的信息
        //1.1获取分类名称的名称字段
        String categoryNames = itemClient.queryCategoryByIds(spu.getCategoryIds())
                .stream().map(CategoryDTO::getName)
                .collect(Collectors.joining(","));
        //1.2 获取品牌的名称信息
        BrandDTO brandDTO = itemClient.queryBrandById(spu.getBrandId());
        // 标题 +  品牌名  + 分类名
        String all = spu.getName() + brandDTO.getName() + categoryNames;

        //2. skus 的信息  因为不是要所有的sku信息
        //  响应包括的 sku信息有  id  titel  价格  和图片地址
        //2.1 根据spuid 查找skus信息
        // 用 list  map 来代替sku 因为只要里面的有些
        List<SkuDTO> skuDTOS = itemClient.querySkuBySpuId(spu.getId());
        List<Map<String,Object>> skuList = new ArrayList<>();
        for (SkuDTO skuDTO : skuDTOS) {
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id",skuDTO.getId());
            skuMap.put("title",skuDTO.getTitle());
            skuMap.put("price",skuDTO.getPrice());
            //????????????????
            skuMap.put("image", StringUtils.substringBefore(skuDTO.getImages(), ","));
            skuList.add(skuMap);
        }

        //3.  价格的集合
        Set<Long> prices = skuDTOS.stream().map(SkuDTO::getPrice).collect(Collectors.toSet());

        //4. 用于搜索框下的  搜索的规格参数  当前spu的规格参数
        //规格参数的话   key是在spec_param中  value 是在 spu_datail中的
        //4.1 分为特有规格参数 和 通用规格参数  根据三级分类查 规格组 在用规格组 查规格组datail
        Map<String,Object> specs = new HashMap<>();
        //获取规格参数
        List<SpecParamDTO> specParamDTOS = itemClient.queryParamByGid(null, spu.getCid3(), true);
        //获取 spu_datail 根据spu_id
        SpuDetailDTO spuDetail = itemClient.queryDatailBySpId(spu.getId());

        //通用规格参数的特有和通用的值 都在spudatailDTo中
        //两个一一对应的关系 放进map中 ？
        //通用规格参数

        //查询数据库中specdtail中 通用的key
        // 4.2.1 通用规格参数值    ????
        Map<Long, Object> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, Object.class);
        // 4.2.2 特有规格参数值
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });
        //将 key-value 一一放进 map中
        for (SpecParamDTO specParam : specParamDTOS) {

            String key = specParam.getName();
            Object value = null;

            //判断是否是通用的
            if (specParam.getGeneric()) {
                value = genericSpec.get(specParam.getId());
            }else {
                value = specialSpec.get(specParam.getId());
            }
            // 判断是否是数字类型   ？？？
            if(specParam.getNumeric()){
                // 是数字类型，分段
                value = chooseSegment(value, specParam);
            }
            // 添加到specs
            specs.put(key, value);
        }


        Goods goods = new Goods();
        goods.setAll(all);
        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setBrandId(spu.getBrandId());
        goods.setCategoryId(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime().getTime());
        goods.setSkus(JsonUtils.toString(skuList));// spu下的所有sku的JSON数组
        //sku的信息   拼接成字符串
        goods.setSpecs(specs); // 当前spu的规格参数
        goods.setPrice(prices); // 当前spu下所有sku的价格的集合

        return goods;
    }

    private String chooseSegment(Object value, SpecParamDTO p) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    //解析double的
    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }


    /**
     * 根据搜索条件去查询数据
     * @param searchRequest
     * @return
     */
    public PageResult<GoodsDTO> queryGoodPageBySearch(SearchRequest searchRequest) {

        String key = searchRequest.getKey();

        //构建查询对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //创建搜索条件
        //1.source 过滤
        // 1.1 source过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));        //2. 构建查询条件         ??

        //添加查询条件
        QueryBuilder basicQuery = buildBasicQuery(searchRequest);
        queryBuilder.withQuery(basicQuery);
//        queryBuilder.withQuery(QueryBuilders.matchQuery("all", key).operator(Operator.AND));

        //分页
        Integer page = searchRequest.getPage()-1;
        Integer size = searchRequest.getSize();
        //分页及排序条件
        queryBuilder.withPageable(PageRequest.of(page, size, Sort.Direction.ASC,"price"));

        //发起请求并且解析结果
        AggregatedPage<Goods> goods = esTemplate.queryForPage(queryBuilder.build(), Goods.class);

        //拿到总条数 总页数 和里面的数据
        long total = goods.getTotalElements();
        int totalPages = goods.getTotalPages();

        List<Goods> list = goods.getContent();
        List<GoodsDTO> goodsDTOS = BeanHelper.copyWithCollection(list, GoodsDTO.class);

        return new PageResult<>(total,totalPages,goodsDTOS);
    }


    /**
     * 查询用来搜索品牌和规格参数的过滤项
     * @param searchRequest
     * @return
     */
    public Map<String, List<?>> searchFilter(SearchRequest searchRequest) {

        //构建返回的集合
        HashMap<String, List<?>> map = new HashMap<>();

        //构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件 根据key或者复杂查询
        QueryBuilder basicQuery = buildBasicQuery(searchRequest);
        queryBuilder.withQuery(basicQuery);
        // 2.2.减少查询结果(这里只需要聚合结果)
        // 每页显示1个   ????
        queryBuilder.withPageable(PageRequest.of(0, 1));
        // 显示空的source   ????
        queryBuilder.withSourceFilter(new FetchSourceFilterBuilder().build());


        //添加聚合条件
        //根据  分类和品牌进行聚合
        String categoryAgg = "categoryAgg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAgg).field("categoryId"));

        String brandAgg = "brandAgg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAgg).field("brandId"));


        //发起查询
        AggregatedPage<Goods> goods = esTemplate.queryForPage(queryBuilder.build(), Goods.class);
        //解析查询结果
        //解析分类聚合结果
        Aggregations aggregations = goods.getAggregations();

        LongTerms cTerms = aggregations.get(categoryAgg);
        List<Long> idList = handleCategoryAgg(cTerms, map);

        //解析品牌聚合结果
        LongTerms bTerms = aggregations.get(brandAgg);
        handlebeandAgg(bTerms,map);

        //规格参数处理   如果分类数量为1 则处理
        if (null != idList && idList.size() == 1) {
            //处理规格参数的聚合  需要参数  分类id  查询条件  过滤项集合
            handleSpecAgg(idList.get(0),basicQuery,map);

        }
        return map;
    }

    /**
     * 查询条件   因为查询条件可以用来扩展
     * @param searchRequest
     * @return
     */
    private QueryBuilder buildBasicQuery(SearchRequest searchRequest) {

        //这是高级查询  bool 查询
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        //添加基础查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all", searchRequest.getKey()).operator(Operator.AND));


        //这个是 取出过滤的查询条件
        Map<String, String> filter = searchRequest.getFilter();

        //如果不为空 则把条件放进查询的里面
        if (!CollectionUtils.isEmpty(filter)) {

            for (Map.Entry<String, String> stringEntry : filter.entrySet()) {
                //获取过滤条件的key
                String key = stringEntry.getKey();
                //是把里面的key 变成 索引库对应的key
                if ("分类".equals(key)) {
                    key = "categoryId";
                } else if ("品牌".equals(key)) {
                    key = "brandId";
                }else {
                    key = "specs." + key;
                }
                //获取值
                String value = stringEntry.getValue();

                //添加过滤条件
                queryBuilder.filter(QueryBuilders.termQuery(key, value));
            }
        }
        return queryBuilder;

    }

    /**
     * 根据分类id 来进行规格参数的聚合
     * @param aLong
     * @param basicQuery
     * @param map
     */
    private void handleSpecAgg(Long aLong, QueryBuilder basicQuery, HashMap<String, List<?>> map) {

        //根据分类id查询对应的 规格参数
        List<SpecParamDTO> specParamDTOS = itemClient.queryParamByGid(null, aLong, true);
        //获取名称
        List<String> specNames = specParamDTOS.stream().map(SpecParamDTO::getName).collect(Collectors.toList());
        //构建查询条件 添加查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);
        // 2.2.减少查询结果(这里只需要聚合结果)
        // 每页显示1个
        queryBuilder.withPageable(PageRequest.of(0, 1));
        // 显示空的source
        queryBuilder.withSourceFilter(new FetchSourceFilterBuilder().build());

        // 3.聚合条件
        specNames.forEach(name -> queryBuilder.addAggregation(AggregationBuilders.terms(name)
                .field("specs."+name)));

        /*for (SpecParamDTO param : specParamDTOS) {
            // 获取param的name，作为聚合名称
            String name = param.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." + name));
        }*/

        //4.发起请求
        AggregatedPage<Goods> goods = esTemplate.queryForPage(queryBuilder.build(), Goods.class);

        //5.解析结果
        //5.1 遍历名称 获取里面的每一个
        Aggregations aggregations = goods.getAggregations();
        for (String specName : specNames) {
            //获取其中的值
            StringTerms terms = aggregations.get(specName);

            List<String> paramValues = terms.getBuckets().stream()
                    //获取里面的名称
                    .map(StringTerms.Bucket::getKeyAsString)
                    //过滤掉空的待选项
                    .filter(StringUtils::isNoneBlank)
                    .collect(Collectors.toList());
            //将key  - value  加入map中
            map.put(specName, paramValues);
        }
    }

    /**
     * 将传进来的聚合的beand 解析到 map集合中
     * @param brandAgg
     * @param map
     */
    private void handlebeandAgg(LongTerms brandAgg, HashMap<String, List<?>> map) {
        //解析bucket 得到id集合
        List<LongTerms.Bucket> buckets = brandAgg.getBuckets();
        for (LongTerms.Bucket bucket : buckets) {
            Number keyAsNumber = bucket.getKeyAsNumber();
            long l = keyAsNumber.longValue();
        }

        List<Long> collect = brandAgg.getBuckets()
                .stream()
                .map(LongTerms.Bucket::getKeyAsNumber)
                .map(Number::longValue)
                .collect(Collectors.toList());
        //根据id的集合 获取id的名称集合
        List<BrandDTO> brandDTOS = itemClient.queryBrandsByIds(collect);
//        List<String> stringStream = brandDTOS.stream().map(BrandDTO::getName).collect(Collectors.toList());
        //将集合放进map中
        map.put("品牌", brandDTOS);
    }

    /**
     * 将传进来的分类聚合 解析到map集合中
     *
     * @param categoryAgg
     * @param map
     */
    private List<Long> handleCategoryAgg(LongTerms categoryAgg, HashMap<String, List<?>> map) {

        //解析bucket 得到id集合
        List<Long> collect = categoryAgg.getBuckets().stream().map(LongTerms.Bucket::getKeyAsNumber)
                .map(Number::longValue)
                .collect(Collectors.toList());

        //根据集合 得到分类的集合
        List<CategoryDTO> categoryDTOS = itemClient.queryCategoryByIds(collect);

        // 存入map
        map.put("分类", categoryDTOS);

        //返回ids的数量   用来判断分类的数量是不是等于 一  是否发起查询规格参数的请求
        return collect;
    }
}
