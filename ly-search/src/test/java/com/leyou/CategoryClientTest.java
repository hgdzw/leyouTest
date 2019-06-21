package com.leyou;

import com.leyou.client.ItemClient;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.dto.SpecParamDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class CategoryClientTest {

    @Autowired
    private ItemClient itemClient;

    @Test
    public void queryByIdList() {
        List<CategoryDTO> list = itemClient.queryCategoryByIds(Arrays.asList(1L, 2L, 3L));
        for (CategoryDTO category : list) {
            System.out.println("category = " + category);
        }
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void queryBeandById() {

//        itemClient.queryBrandById()

        List<SpecParamDTO> specParamDTOS = itemClient.queryParamByGid(null, 76l, true);

    }
}