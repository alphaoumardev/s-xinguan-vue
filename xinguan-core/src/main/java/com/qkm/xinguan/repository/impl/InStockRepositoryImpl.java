package com.qkm.xinguan.repository.impl;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.constant.SystemConst;
import com.qkm.xinguan.domain.entity.*;
import com.qkm.xinguan.domain.infrastructure.mapper.*;
import com.qkm.xinguan.domain.infrastructure.security.LoginUser;
import com.qkm.xinguan.domain.infrastructure.utils.ExcelStyleUtil;
import com.qkm.xinguan.domain.infrastructure.utils.ExcelUtil;
import com.qkm.xinguan.domain.infrastructure.utils.UserUtil;
import com.qkm.xinguan.domain.transform.form.InStockSearchForm;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.InStockRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import com.qkm.xinguan.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 入库 服务实现类
 *
 * @author qiukangming
 * @since 2020-09-24
 */
@Slf4j
@Service
public class InStockRepositoryImpl extends ServiceImpl<InStockMapper, InStock> implements InStockRepository {

    private final ProductMapper productMapper;

    private final ProductStockMapper productStockMapper;

    private final SupplierMapper supplierMapper;

    private final InStockInfoMapper inStockInfoMapper;

    @Autowired
    public InStockRepositoryImpl(ProductMapper productMapper, ProductStockMapper productStockMapper, SupplierMapper supplierMapper, InStockInfoMapper inStockInfoMapper) {
        this.productMapper = productMapper;
        this.productStockMapper = productStockMapper;
        this.supplierMapper = supplierMapper;
        this.inStockInfoMapper = inStockInfoMapper;
    }

    /**
     * 分页获取入库记录
     *
     * @param page              page
     * @param size              size
     * @param inStockSearchForm 查询实体
     * @return Page<InStockVo>
     */
    @Override
    public Page<InStockVo> getInStockList(Integer page, Integer size, InStockSearchForm inStockSearchForm) {
        Page<InStock> inStockPage = this.baseMapper.selectPage(new Page<>(page, size), buildLambdaWrapper(inStockSearchForm));
        return toInStockVoPage(inStockPage);
    }

    /**
     * 导出入库记录列表信息
     *
     * @param response   请求头
     * @param searchForm 查询实体
     */
    @Override
    public void exportInStockList(HttpServletResponse response, InStockSearchForm searchForm) {
        LambdaQueryWrapper<InStock> wrapper = buildLambdaWrapper(searchForm);
        List<InStock> inStocks = this.baseMapper.selectList(wrapper);
        List<InStockExportVo> exportVos = toInStockExportVoList(inStocks);
        // 实现导出
        try {
            ExportParams params = new ExportParams("入库信息表格", "sheet1", ExcelType.XSSF);
            params.setStyle(ExcelStyleUtil.class);
            ExcelUtil.exportExcel(exportVos, InStockExportVo.class, "入库信息表格", params, response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ResultCode.EXPORT_FAIL);
        }
    }

    /**
     * 获取物资入库明细
     *
     * @param id   入库记录ID
     * @param page 第几页
     * @param size 一页几条数据
     * @return InStockDetailVo
     */
    @Override
    public InStockDetailVo getInStockDetailList(Long id, Integer page, Integer size) {
        InStock inStock = this.baseMapper.selectById(id);
        if (Objects.isNull(inStock)) {
            throw new BusinessException("查询错误，此入库单不存在，请重试！");
        }
        InStockDetailVo detailVo = new InStockDetailVo();
        BeanUtils.copyProperties(inStock, detailVo);
        Supplier supplier = supplierMapper.selectById(inStock.getSupplierId());
        if (Objects.isNull(supplier)) {
            throw new BusinessException("提供物资方不存在，或已被删除！");
        }
        SupplierVo supplierVo = new SupplierVo();
        BeanUtils.copyProperties(supplier, supplierVo);

        detailVo.setSupplierVo(supplierVo);
        // 获取入库单号
        String inNum = inStock.getInNum();
        // 查询该单所有的物资
        Page<InStockInfo> pageInfo = inStockInfoMapper.selectPage(new Page<>(page, size), Wrappers.lambdaQuery(InStockInfo.class).eq(InStockInfo::getInNum, inNum));
        detailVo.setTotal(pageInfo.getTotal());
        if (!pageInfo.getRecords().isEmpty()) {
            for (InStockInfo record : pageInfo.getRecords()) {
                // 找出物资单号
                String pNum = record.getPNum();
                List<Product> products = productMapper.selectList(Wrappers.lambdaQuery(Product.class).eq(Product::getPNum, pNum));
                if (!products.isEmpty()) {
                    Product product = products.get(0);
                    InStockItemVo inStockItemVo = new InStockItemVo();
                    BeanUtils.copyProperties(product, inStockItemVo);
                    inStockItemVo.setCount(record.getProductNumber());
                    detailVo.getItemVos().add(inStockItemVo);
                } else {
                    throw new BusinessException(ResultCode.PARAM_ERROR, "编号为:[ " + pNum + " ]的物资找不到,或已被删除！");
                }
            }
        } else {
            throw new BusinessException(ResultCode.PARAM_ERROR, "入库编号为:[ " + inNum + " ]的明细找不到,或已被删除！");
        }
        return detailVo;
    }

    /**
     * 移入回收站
     *
     * @param id id
     * @return Result
     */
    @Override
    public Result remove(Long id) {
        InStock inStock = this.baseMapper.selectById(id);
        if (Objects.isNull(inStock)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "入库单不存在");
        }
        Integer status = inStock.getStatus();
        if (SystemConst.ACTIVE.equals(status)) {
            throw new BusinessException(ResultCode.FAILED, "入库单状态不正确");
        }
        inStock.setStatus(1);
        boolean res = this.saveOrUpdate(inStock);
        if (res) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED, "移入回收站错误，请重试！");
    }

    /**
     * 物资入库审核
     *
     * @param id id
     * @return Result
     */
    @Override
    public Result push(Long id) {
        InStock inStock = this.baseMapper.selectById(id);
        if (Objects.isNull(inStock)) {
            throw new BusinessException(ResultCode.PARAM_NOT_VALID, "入库单不存在！");
        }
        if (inStock.getStatus() != 2) {
            throw new BusinessException(ResultCode.FAILED, "入库单状态错误！");
        }
        Supplier supplier = supplierMapper.selectById(inStock.getSupplierId());
        if (Objects.isNull(supplier)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "入库来源信息错误！");
        }
        List<InStockInfo> inStockInfos = inStockInfoMapper.selectList(Wrappers.lambdaQuery(InStockInfo.class).eq(InStockInfo::getInNum, inStock.getInNum()));
        if (!inStockInfos.isEmpty()) {
            for (InStockInfo inStockInfo : inStockInfos) {
                List<Product> products = productMapper.selectList(Wrappers.lambdaQuery(Product.class).eq(Product::getPNum, inStockInfo.getPNum()));
                if (products.isEmpty()) {
                    throw new BusinessException(ResultCode.PARAM_ERROR, "物资编号[ " + inStockInfo.getPNum() + " ]的物资不存在！");
                }
                Product product = products.get(0);
                // 入库如果存在，就增加数量，否则插入
                List<ProductStock> productStocks = productStockMapper.selectList(Wrappers.lambdaQuery(ProductStock.class).eq(ProductStock::getPNum, product.getPNum()));
                if (productStocks.isEmpty()) {
                    // 插入
                    ProductStock productStock = new ProductStock();
                    productStock.setPNum(product.getPNum());
                    productStock.setStock((long) inStockInfo.getProductNumber());
                    productStockMapper.insert(productStock);
                } else {
                    // 更新数据
                    ProductStock productStock = productStocks.get(0);
                    productStock.setStock(productStock.getStock() + inStockInfo.getProductNumber());
                    productStockMapper.updateById(productStock);
                }
                // 修改入库单状态
                inStock.setStatus(0);
                this.baseMapper.updateById(inStock);
            }
            return Result.ok();
        } else {
            throw new BusinessException(ResultCode.FAILED, "入库的明细不能为空！");
        }
    }

    /**
     * 删除入库单
     *
     * @param id id号
     * @return Result
     */
    @Override
    public Result delete(Long id) {
        InStock inStock = this.baseMapper.selectById(id);
        if (Objects.isNull(inStock)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "入库单不存在！");
        } else if (inStock.getStatus() != 1 && inStock.getStatus() != 2) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "入库单状态错误,无法删除！");
        }
        this.baseMapper.deleteById(id);
        inStockInfoMapper.delete(Wrappers.lambdaQuery(InStockInfo.class).eq(InStockInfo::getInNum, inStock.getInNum()));
        return Result.ok();
    }

    /**
     * 移出回收站
     *
     * @param id id
     * @return Result
     */
    @Override
    public Result back(Long id) {
        InStock inStock = this.baseMapper.selectById(id);
        if (Objects.isNull(inStock)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "入库单不存在！");
        } else if (inStock.getStatus() != 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "入库单状态错误！");
        }
        inStock.setStatus(0);
        int res = this.baseMapper.updateById(inStock);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED, "回收站还原失败！");
    }

    /**
     * 添加物资入库
     *
     * @param inStockVo 实体
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public Result addInStock(InStockVo inStockVo) {
        if (Objects.isNull(inStockVo.getSupplierId())) {
            // 说明添加物资来源
            SupplierVo supplierVo = new SupplierVo();
            BeanUtils.copyProperties(inStockVo, supplierVo);
            if (StringUtils.isEmpty(supplierVo.getName())) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "物资提供方名不能为空");
            }
            if (StringUtils.isEmpty(supplierVo.getEmail())) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "邮箱不能为空");
            }
            if(StringUtils.isEmpty(supplierVo.getContact())){
                throw new BusinessException(ResultCode.PARAM_ERROR, "联系人不能为空");
            }
            if(StringUtils.isEmpty(supplierVo.getAddress())){
                throw new BusinessException(ResultCode.PARAM_ERROR, "地址不能为空");
            }
            if(StringUtils.isEmpty(supplierVo.getPhone())){
                throw new BusinessException(ResultCode.PARAM_ERROR, "联系方式不能为空");
            }
            if(Objects.isNull(supplierVo.getSort())){
                throw new BusinessException(ResultCode.PARAM_ERROR, "排序不能为空");
            }
            // 添加物资提供方
            Supplier supplier = new Supplier();
            BeanUtils.copyProperties(supplierVo, supplier);
            int res = supplierMapper.insert(supplier);
            if (res <= 0) {
                throw new BusinessException(ResultCode.FAILED, "物资入库失败，请重试！");
            }
            // 设置提供方ID
            inStockVo.setSupplierId(supplier.getId());
        }
        // 添加入库
        // 随机生成入库单号
        String IN_STOCK_NUM = UUID.randomUUID().toString().replace("-", "");
        // 记录该单的总数
        int itemNumber = 0;
        List<Object> products = inStockVo.getProducts();
        if (products.isEmpty()) {
            throw new BusinessException(ResultCode.PRODUCT_IN_STOCK_EMPTY);
        }
        for (Object product : products) {
            JSONObject product_object = JSONUtil.parseObj(product);
            Integer productNumber = product_object.getInt("productNumber");
            Integer productId = product_object.getInt("productId");
            Product dbProduct = productMapper.selectById(productId);
            if (Objects.isNull(dbProduct)) {
                throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND);
            } else if (dbProduct.getStatus() == 1) {
                throw new BusinessException(ResultCode.PRODUCT_IS_REMOVE, dbProduct.getName() + "物资已被回收,无法入库");
            } else if (dbProduct.getStatus() == 2) {
                throw new BusinessException(ResultCode.PRODUCT_WAIT_PASS, dbProduct.getName() + "物资待审核,无法入库");
            } else if (productNumber <= 0) {
                throw new BusinessException(ResultCode.PRODUCT_IN_STOCK_NUMBER_ERROR, dbProduct.getName() + "入库数量不合法,无法入库");
            } else {
                itemNumber += productNumber;
                //插入入库单明细
                InStockInfo inStockInfo = new InStockInfo();
                inStockInfo.setProductNumber(productNumber);
                inStockInfo.setPNum(dbProduct.getPNum());
                inStockInfo.setInNum(IN_STOCK_NUM);
                inStockInfoMapper.insert(inStockInfo);
            }
        }
        InStock inStock = new InStock();
        BeanUtils.copyProperties(inStockVo, inStock);
        inStock.setProductNumber(itemNumber);
        LoginUser loginUser = UserUtil.getCurrentLoginUser();
        assert loginUser != null;
        inStock.setOperator(loginUser.getUsername());
        // 生成入库单
        inStock.setInNum(IN_STOCK_NUM);
        // 设置为待审核
        inStock.setStatus(2);
        int res = this.baseMapper.insert(inStock);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    private Page<InStockVo> toInStockVoPage(Page<InStock> inStockPage) {
        Page<InStockVo> pageInfo = new Page<>();
        BeanUtils.copyProperties(inStockPage, pageInfo);
        List<InStockVo> voList = toInStockVoList(inStockPage.getRecords());
        pageInfo.setRecords(voList);
        return pageInfo;
    }

    private static LambdaQueryWrapper<InStock> buildLambdaWrapper(InStockSearchForm inStockSearchForm) {
        LambdaQueryWrapper<InStock> wrapper = new LambdaQueryWrapper<>();
        if (!Objects.isNull(inStockSearchForm.getType())) {
            wrapper.eq(InStock::getType, inStockSearchForm.getType());
        }
        if (!StringUtils.isEmpty(inStockSearchForm.getInNum())) {
            wrapper.eq(InStock::getInNum, inStockSearchForm.getInNum());
        }
        if (!Objects.isNull(inStockSearchForm.getStatus())) {
            wrapper.eq(InStock::getStatus, inStockSearchForm.getStatus());
        }
        if (!Objects.isNull(inStockSearchForm.getStartTime()) && !Objects.isNull(inStockSearchForm.getEndTime())) {
            wrapper.between(InStock::getCreateTime, inStockSearchForm.getStartTime(), inStockSearchForm.getEndTime());
        }
        wrapper.orderByDesc(InStock::getCreateTime);
        return wrapper;
    }

    private List<InStockVo> toInStockVoList(List<InStock> inStockList) {
        List<InStockVo> voList = new ArrayList<>(inStockList.size());
        inStockList.forEach(instock -> {
            InStockVo insVo = new InStockVo();
            BeanUtils.copyProperties(instock, insVo);
            Supplier supplier = supplierMapper.selectById(instock.getSupplierId());
            if (supplier != null) {
                insVo.setSupplierName(supplier.getName());
                insVo.setPhone(supplier.getPhone());
            }
            voList.add(insVo);
        });
        return voList;
    }

    private List<InStockExportVo> toInStockExportVoList(List<InStock> inStockList) {
        List<InStockExportVo> voList = new ArrayList<>(inStockList.size());
        inStockList.forEach(instock -> {
            InStockExportVo exportVo = new InStockExportVo();
            BeanUtils.copyProperties(instock, exportVo);
            Supplier supplier = supplierMapper.selectById(instock.getSupplierId());
            if (supplier != null) {
                exportVo.setSupplierName(supplier.getName());
                exportVo.setPhone(supplier.getPhone());
            }
            exportVo.setType(getTagString("type", instock.getType()));
            exportVo.setStatus(getTagString("status", instock.getStatus()));
            voList.add(exportVo);
        });
        return voList;
    }

    private static String getTagString(String type, Integer value) {
        if ("type".equals(type)) {
            switch (value) {
                case 1:
                    return "捐赠";
                case 2:
                    return "下拨";
                case 3:
                    return "采购";
                case 4:
                    return "退货入库";
                default:
                    return "未知类型";
            }
        } else if ("status".equals(type)) {
            switch (value) {
                case 0:
                    return "回收";
                case 1:
                    return "已入库";
                case 2:
                    return "审核中";
                default:
                    return "未知";
            }
        }
        return "[System Error]";
    }

}
