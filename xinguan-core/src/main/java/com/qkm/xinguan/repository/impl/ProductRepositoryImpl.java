package com.qkm.xinguan.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.entity.Product;
import com.qkm.xinguan.domain.infrastructure.mapper.ProductMapper;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.ProductRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import com.qkm.xinguan.vo.ProductStockVo;
import com.qkm.xinguan.vo.ProductVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class ProductRepositoryImpl extends ServiceImpl<ProductMapper, Product> implements ProductRepository {

    private final ProductMapper productMapper;

    public ProductRepositoryImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    /**
     * 获取物资资料数据
     *
     * @param page      第几页
     * @param size      一页几条数据
     * @param categorys 分类
     * @param filter 是否全部，为 true 的时候用于入库页面使用
     * @param productVo 数据传输对象
     * @return Result
     */
    @Override
    public Result findProductList(Integer page, Integer size, String categorys, Boolean filter,  ProductVo productVo) {
        if (filter) {
            productVo.setStatus(0);
        }
        buildCategorySearch(categorys, productVo);
        Page<Product> pageProducts = this.baseMapper.selectPage(new Page<>(page, size), buildLambdaQueryWrapper(productVo));
        return Result.ok().data(pageProducts);
    }

    /**
     * 获取库存列表数据
     *
     * @param page      第几页
     * @param size      一页几条数据
     * @param categorys 分类
     * @param productVo 查询条件
     * @return Result
     */
    @Override
    public Result findProductStocks(Integer page, Integer size, String categorys, ProductVo productVo) {
        buildCategorySearch(categorys, productVo);
        // 计算出 limit 的参数
        page = (page - 1) * size;
        List<ProductStockVo> list = productMapper.findProductStocks(page, size, productVo);
        Page<ProductStockVo> pageInfo = new Page<>();
        pageInfo.setRecords(list);
        pageInfo.setCurrent(page);
        pageInfo.setSize(size);
        long total = productMapper.findProductStocksCount(productVo);
        pageInfo.setTotal(total);
        pageInfo.setPages((long) Math.ceil((double) (total / size)));
        return Result.ok().data(pageInfo);
    }

    /**
     * 获取所有库存列表数据
     *
     * @param productVo 查询条件
     * @return Result
     */
    @Override
    public Result findAllProductStocks(String categorys, ProductVo productVo) {
        buildCategorySearch(categorys, productVo);
        List<ProductStockVo> list = productMapper.findAllProductStocks(productVo);
        return Result.ok().data(list);
    }

    /**
     * 添加物资
     *
     * @param productVo 物资数据
     * @return Result
     */
    @Override
    public Result add(ProductVo productVo) {
        if (productVo.getCategoryKeys().length != 3) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "物资需要 3 级分类！");
        }
        Product product = getProduct(productVo);
        product.setStatus(2); // 未审核
        product.setPNum(UUID.randomUUID().toString().replace("-", ""));
        int res = this.baseMapper.insert(product);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 更新物资
     *
     * @param productVo 数据对象
     * @return Result
     */
    @Override
    public Result update(ProductVo productVo) {
        if (productVo.getCategoryKeys().length != 3) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "物资需要3级分类");
        }
        Product product = getProduct(productVo);
        int res = this.baseMapper.updateById(product);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 删除物资
     *
     * @param id id
     * @return Result
     */
    @Override
    public Result delete(Long id) {
        Product product = this.baseMapper.selectById(id);
        if (Objects.isNull(product)) {
            throw new BusinessException(ResultCode.FAILED, "物资不存在，请重试！");
        }
        if (product.getStatus() != 1 && product.getStatus() != 2) {
            throw new BusinessException(ResultCode.FAILED, "物资状态错误！");
        }
        int res = this.baseMapper.deleteById(product);
        if (res > 0){
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 移除物资
     *
     * @param id id
     * @return Result
     */
    @Override
    public Result remove(Long id) {
        Product product = this.baseMapper.selectById(id);
        if (Objects.isNull(product)) {
            throw new BusinessException(ResultCode.FAILED, "物资不存在，请重试！");
        }
        if (product.getStatus() != 0) {
            throw new BusinessException(ResultCode.FAILED, "物资状态错误,请重试！");
        }
        product.setStatus(1);
        int res = this.baseMapper.updateById(product);
        if (res > 0){
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 物资审核
     *
     * @param id id
     * @return Result
     */
    @Override
    public Result publish(Long id) {
        Product product = this.baseMapper.selectById(id);
        if (Objects.isNull(product)) {
            throw new BusinessException(ResultCode.FAILED, "物资不存在，请重试！");
        }
        if (product.getStatus() != 2) {
            throw new BusinessException(ResultCode.FAILED, "物资状态错误,请重试！");
        }
        product.setStatus(0);
        int res = this.baseMapper.updateById(product);
        if (res > 0){
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 物资恢复
     *
     * @param id id
     * @return Result
     */
    @Override
    public Result back(Long id) {
        Product product = this.baseMapper.selectById(id);
        if (Objects.isNull(product)) {
            throw new BusinessException(ResultCode.FAILED, "物资不存在，请重试！");
        }
        if (product.getStatus() != 1) {
            throw new BusinessException(ResultCode.FAILED, "物资状态错误,请重试！");
        }
        product.setStatus(0);
        int res = this.baseMapper.updateById(product);
        if (res > 0){
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 封装物资查询条件
     *
     * @param categorys 物资分类
     * @param productVo 数据对象
     */
    private void buildCategorySearch(String categorys, ProductVo productVo) {
        if (!StringUtils.isEmpty(categorys)) {
            String[] split = categorys.split(",");
            switch (split.length) {
                case 1:
                    productVo.setOneCategoryId(Long.parseLong(split[0]));
                    break;
                case 2:
                    productVo.setOneCategoryId(Long.parseLong(split[0]));
                    productVo.setTwoCategoryId(Long.parseLong(split[1]));
                    break;
                case 3:
                    productVo.setOneCategoryId(Long.parseLong(split[0]));
                    productVo.setTwoCategoryId(Long.parseLong(split[1]));
                    productVo.setThreeCategoryId(Long.parseLong(split[2]));
                    break;
            }
        }
    }

    private LambdaQueryWrapper<Product> buildLambdaQueryWrapper(ProductVo productVo) {
        LambdaQueryWrapper<Product> wrapper = Wrappers.lambdaQuery();
        if (!Objects.isNull(productVo.getStatus())) {
            wrapper.eq(Product::getStatus, productVo.getStatus());
        }
        boolean flag = false;
        if (!Objects.isNull(productVo.getThreeCategoryId())) {
            wrapper.eq(Product::getOneCategoryId, productVo.getOneCategoryId());
            wrapper.eq(Product::getTwoCategoryId, productVo.getTwoCategoryId());
            wrapper.eq(Product::getThreeCategoryId, productVo.getThreeCategoryId());
            flag = true;
        }
        else if (!Objects.isNull(productVo.getTwoCategoryId())) {
            wrapper.eq(Product::getOneCategoryId, productVo.getOneCategoryId());
            wrapper.eq(Product::getTwoCategoryId, productVo.getTwoCategoryId());
            flag = true;
        }
        else if (!Objects.isNull(productVo.getOneCategoryId())) {
            wrapper.eq(Product::getOneCategoryId, productVo.getOneCategoryId());
            flag = true;
        }
        if (flag) return wrapper;
        wrapper.orderByAsc(Product::getSort);
        if (!StringUtils.isEmpty(productVo.getName())) {
            wrapper.like(Product::getName, productVo.getName());
        }
        if (!StringUtils.isEmpty(productVo.getPNum())) {
            wrapper.eq(Product::getPNum, productVo.getPNum());
        }
        return wrapper;
    }

    private static Product getProduct(ProductVo productVo) {
        Product product = new Product();
        BeanUtils.copyProperties(productVo, product);
        @NotNull(message = "分类不能为空") Long[] categoryKeys = productVo.getCategoryKeys();
        if(categoryKeys.length == 3){
            product.setOneCategoryId(categoryKeys[0]);
            product.setTwoCategoryId(categoryKeys[1]);
            product.setThreeCategoryId(categoryKeys[2]);
        }
        return product;
    }
}
