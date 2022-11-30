package com.qkm.xinguan;

import com.qkm.xinguan.domain.entity.Product;
import com.qkm.xinguan.domain.infrastructure.mapper.ProductMapper;
import com.qkm.xinguan.repository.DepartmentRepository;
import com.qkm.xinguan.repository.OssRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author qiukangming
 * @version 1.0
 * @description SpringBoot Test Code
 * @since 2020/10/17 20:27
 */

@SpringBootTest
public class XinguanTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ProductMapper productMapper;

    @Test
    public void testDeptNames() {
        List<String> names = departmentRepository.getDeptNamesByIds(Arrays.asList(1L, 1L, 2L, 3L, 3L));
        System.out.println(names);
    }

    @Autowired
    private OssRepository ossRepository;

    @Test
    public void testOss() {
        System.out.println(ossRepository.listFiles());
    }

    @Test
    public void test(){
        // System.out.println(productMapper.selectById(51));
        Product product = new Product();
        product.setName("邱康明");
        productMapper.insert(product);
    }
}
