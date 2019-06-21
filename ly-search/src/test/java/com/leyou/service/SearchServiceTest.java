package com.leyou.service;


import com.leyou.client.ItemClient;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SpuDTO;
import com.leyou.pojo.Goods;
import com.leyou.repository.GoodsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @Author: dzw
 * @Date: 2019/6/20 9:46
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchServiceTest {

    //循环查询Spu，然后调用IndexService中的方法，把SPU变为Goods，然后写入索引库

    @Autowired
    private SearchService searchService;

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Test
    public void buildGoods(){
        int page = 1,rows = 100,size = 0;   //用来控制循环的
        do {
            try {
                //查询spu
                PageResult<SpuDTO> spuDTOPageResult = itemClient.querySpuByPage(null, true, page, rows);
                //  取出spu
                List<SpuDTO> items = spuDTOPageResult.getItems();
                //变成goods
                List<Goods> goods = items.stream().map(searchService::buildGoods).collect(Collectors.toList());
                //保存
                goodsRepository.saveAll(goods);
                //下一页
                page++;
                //判断这页的数据是不是满的
                size = items.size();

            }catch (Exception e){
                //如果有异常就终止循环
                e.printStackTrace();
                break;

            }

        }while (size == 100);


    }


}
