package com.leyou.thymeleaf.service;

import com.leyou.client.ItemClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.dto.SpuDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: dzw
 * @Date: 2019/6/22 21:50
 * @Version 1.0
 */
@Slf4j
@Service
public class PageService {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${ly.static.itemDir}")
    private String itemDir;

    @Value("${ly.static.itemTemplate}")
    private String itemTemplate;
    /**
     * 根据 页面 的id  来查询 渲染页面所需要的数据
     * @param id
     * @return
     */
    public Map<String, Object> loadItemData(Long id) {
        //渲染页面所需要
        HashMap<String, Object> map = new HashMap<>();

        // 查询spu
        SpuDTO spu = itemClient.querySpuById(id);

        // - categories：商品分类对象集合
        map.put("categories", itemClient.queryCategoryByIds(spu.getCategoryIds()));
        //- brand：品牌对象
        map.put("brand", itemClient.queryBrandById(spu.getBrandId()));
        //- spuName：应该 是spu表中的name属性
        map.put("spuName", spu.getName());
        //- subTitle：spu中 的副标题
        map.put("subTitle", spu.getSubTitle());
        //- detail：商品详情SpuDetail
        map.put("detail", itemClient.queryDatailBySpId(spu.getId()));
        //- skus：商品spu下的sku集合
        map.put("skus", spu.getSkus());
        //- specs：规格参数这个比较 特殊：
        map.put("specs", itemClient.querySpecsByCid(spu.getCid3()));
//        # TODO 抄好了 等待测试
        return map;
    }


    /**
     * 这是页面静态化 传入good id
     * @param id
     */
    public void createIndexHtml(Long id){
        //1.创建上下文
        Context context = new Context();
        //2.查询模板数据 并放进上下文
        context.setVariables(loadItemData(id));
        //3.打开文件 渲染 文件
        File dir = new File(itemDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                // 创建失败，抛出异常
                log.error("【静态页服务】创建静态页目录失败，目录地址：{}", dir.getAbsolutePath());
                throw new LyException(ExceptionEnum.DIRECTORY_WRITER_ERROR);
            }
        }
        File filePath = new File(itemDir, id + ".html");

        //4.将文件静态化

        try (PrintWriter writer = new PrintWriter(filePath, "UTF-8")) {
            templateEngine.process(itemTemplate, context, writer);
        } catch (IOException e) {
            log.error("【静态页服务】静态页生成失败，商品id：{}", id, e);
            throw new LyException(ExceptionEnum.FILE_WRITER_ERROR);
        }
    }



}
