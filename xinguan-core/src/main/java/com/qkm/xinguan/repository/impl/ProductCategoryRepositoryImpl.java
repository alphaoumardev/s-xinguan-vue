package com.qkm.xinguan.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.entity.Product;
import com.qkm.xinguan.domain.entity.ProductCategory;
import com.qkm.xinguan.domain.infrastructure.mapper.ProductCategoryMapper;
import com.qkm.xinguan.domain.infrastructure.mapper.ProductMapper;
import com.qkm.xinguan.domain.infrastructure.utils.PageDataUtil;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.ProductCategoryRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import com.qkm.xinguan.vo.ProductCategoryTreeNodeVo;
import com.qkm.xinguan.vo.ProductCategoryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Service
public class ProductCategoryRepositoryImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryRepository {

    private final ProductMapper productMapper;

    @Autowired
    public ProductCategoryRepositoryImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    /**
     * 获取物资分类列表
     *
     * @param page              第几页
     * @param size              一页几条数据
     * @param productCategoryVo 查询实体
     * @return Result
     */
    @Override
    public Result findProductCategoryList(Integer page, Integer size, ProductCategoryVo productCategoryVo) {
        Page<ProductCategory> pageInfo = this.baseMapper.selectPage(new Page<>(page, size), buildWrapper(productCategoryVo));
        return Result.ok().data(pageInfo);
    }

    /**
     * 获取分类树
     *
     * @param page page
     * @param size size
     * @return Result
     */
    @Override
    public Result categoryTree(Integer page, Integer size) {
        List<ProductCategory> all = findAll();
        List<ProductCategoryTreeNodeVo> treeNodeVos = list2TreeNodeList(all);
        List<ProductCategoryTreeNodeVo> categoryTree = buildProductCategoryTree(treeNodeVos, false);

        Page<ProductCategoryTreeNodeVo> pageInfo = new Page<>();
        pageInfo.setCurrent(page);
        pageInfo.setSize(size);
        pageInfo.setTotal(categoryTree.size());
        pageInfo.setPages((long) Math.ceil((long) (categoryTree.size() / size)));
        pageInfo.setRecords(PageDataUtil.getPageData(page, size, categoryTree));

        return Result.ok().data(pageInfo);
    }

    /**
     * 获取父级分类树
     *
     * @return Result
     */
    @Override
    public List<ProductCategoryTreeNodeVo> getParentCategoryTree() {
        List<ProductCategory> all = findAll();
        List<ProductCategoryTreeNodeVo> treeNodeVos = list2TreeNodeList(all);
        return buildProductCategoryTree(treeNodeVos, true);
    }

    /**
     * 获取所有分类
     *
     * @return Result
     */
    @Override
    public List<ProductCategory> findAll() {
        return this.baseMapper.selectList(null);
    }

    /**
     * 添加分类
     *
     * @param productCategoryVo 数据对象
     * @return Result
     */
    @Override
    public Result add(ProductCategoryVo productCategoryVo) {
        ProductCategory productCategory = new ProductCategory();
        BeanUtils.copyProperties(productCategoryVo, productCategory);
        int res = this.baseMapper.insert(productCategory);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 根据ID查询
     *
     * @param id id
     * @return Result
     */
    @Override
    public Result findById(Long id) {
        ProductCategory ca = this.baseMapper.selectById(id);
        return Result.ok().data(ca);
    }

    /**
     * 更新分类信息
     *
     * @param id                id
     * @param productCategoryVo vo
     * @return Result
     */
    @Override
    public Result update(Long id, ProductCategoryVo productCategoryVo) {
        ProductCategory category = this.baseMapper.selectById(id);
        if (Objects.isNull(category)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该分类信息不存在，请重试！");
        }
        BeanUtils.copyProperties(productCategoryVo, category);
        int res = this.baseMapper.updateById(category);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 根据ID删除
     *
     * @param id id
     * @return Result
     */
    @Override
    public Result delete(Long id) {
        ProductCategory category = this.baseMapper.selectById(id);
        if (Objects.isNull(category)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该分类信息不存在，请重试！");
        }
        // 检查是否存在子分类
        Integer count = this.baseMapper.selectCount(Wrappers.lambdaQuery(ProductCategory.class).eq(ProductCategory::getPid, id));
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该分类存在子节点，无法直接删除！");
        }
        // 检查该分类是否有物资引用
        LambdaQueryWrapper<Product> wrapper = Wrappers.lambdaQuery(Product.class);
        wrapper.eq(Product::getOneCategoryId, id)
               .eq(Product::getTwoCategoryId, id)
               .eq(Product::getThreeCategoryId, id);
        Integer count1 = productMapper.selectCount(wrapper);
        if (count1 > 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该分类存在物资引用,无法直接删除");
        }
        int res = this.baseMapper.deleteById(id);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    private LambdaQueryWrapper<ProductCategory> buildWrapper(ProductCategoryVo productCategoryVo) {
        LambdaQueryWrapper<ProductCategory> wrapper = Wrappers.lambdaQuery(ProductCategory.class);
        if (!StringUtils.isEmpty(productCategoryVo.getName())) {
            wrapper.like(ProductCategory::getName, productCategoryVo.getName());
        }
        if (!StringUtils.isEmpty(productCategoryVo.getRemark())) {
            wrapper.like(ProductCategory::getRemark, productCategoryVo.getRemark());
        }
        if (!Objects.isNull(productCategoryVo.getSort())) {
            wrapper.eq(ProductCategory::getSort, productCategoryVo.getSort());
        }
        wrapper.orderByAsc(ProductCategory::getSort);
        return wrapper;
    }

    private static List<ProductCategoryTreeNodeVo> list2TreeNodeList(List<ProductCategory> list) {
        List<ProductCategoryTreeNodeVo> nodeList = new ArrayList<>();
        if (!list.isEmpty()) {
            for (ProductCategory productCategory : list) {
                ProductCategoryTreeNodeVo productCategoryTreeNodeVO = new ProductCategoryTreeNodeVo();
                BeanUtils.copyProperties(productCategory, productCategoryTreeNodeVO);
                nodeList.add(productCategoryTreeNodeVO);
            }
        }
        return nodeList;
    }

    private static List<ProductCategoryTreeNodeVo> buildProductCategoryTree(List<ProductCategoryTreeNodeVo> nodes, boolean isParent) {
        //根节点
        List<ProductCategoryTreeNodeVo> root = new ArrayList<>();
        for (ProductCategoryTreeNodeVo nav : nodes) {
            if (nav.getPid() == 0) {
                nav.setLev(1);
                root.add(nav);
            }
        }
        /* 根据Menu类的order排序 */
        root.sort(ProductCategoryTreeNodeVo.order());
        /*为根菜单设置子菜单，getChild是递归调用的*/
        for (ProductCategoryTreeNodeVo nav : root) {
            /* 获取根节点下的所有子节点 使用getChild方法*/
            List<ProductCategoryTreeNodeVo> childList;
            if (isParent) {
                childList = getParentChild(nav, nodes);
            } else {
                childList = getChild(nav, nodes);
            }
            nav.setChildren(childList);//给根节点设置子节点
        }
        return root;
    }

    private static List<ProductCategoryTreeNodeVo> getChild(ProductCategoryTreeNodeVo pNode, List<ProductCategoryTreeNodeVo> nodes) {
        // 子菜单
        List<ProductCategoryTreeNodeVo> childList = new ArrayList<>();
        for (ProductCategoryTreeNodeVo nav : nodes) {
            // 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较
            // 相等说明：为该根节点的子节点。
            if (nav.getPid().equals(pNode.getId())) {
                nav.setLev(pNode.getLev() + 1);
                childList.add(nav);
            }
        }
        //递归
        for (ProductCategoryTreeNodeVo nav : childList) {
            nav.setChildren(getChild(nav, nodes));
        }
        childList.sort(ProductCategoryTreeNodeVo.order());//排序
        //如果节点下没有子节点，返回一个空List（递归退出）
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }

    private static List<ProductCategoryTreeNodeVo> getParentChild(ProductCategoryTreeNodeVo pNode, List<ProductCategoryTreeNodeVo> nodes) {
        //子菜单
        List<ProductCategoryTreeNodeVo> childList = new ArrayList<>();
        for (ProductCategoryTreeNodeVo nav : nodes) {
            // 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较
            // 相等说明：为该根节点的子节点。
            if (nav.getPid().equals(pNode.getId())) {
                nav.setLev(2);
                childList.add(nav);
            }
        }
        return childList;
    }
}
