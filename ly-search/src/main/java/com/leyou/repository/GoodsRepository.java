package com.leyou.repository;

import com.leyou.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 *
 * @Author: dzw
 * @Date: 2019/6/19 20:50
 * @Version 1.0
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
