package com.qkm.xinguan.repository.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qkm.xinguan.domain.entity.*;
import com.qkm.xinguan.domain.infrastructure.mapper.*;
import com.qkm.xinguan.domain.infrastructure.security.LoginUser;
import com.qkm.xinguan.domain.infrastructure.utils.UserUtil;
import com.qkm.xinguan.exception.BusinessException;
import com.qkm.xinguan.repository.OutStockRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qkm.xinguan.response.Result;
import com.qkm.xinguan.response.ResultCode;
import com.qkm.xinguan.vo.ConsumerVo;
import com.qkm.xinguan.vo.OutStockDetailVo;
import com.qkm.xinguan.vo.OutStockItemVo;
import com.qkm.xinguan.vo.OutStockVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
public class OutStockRepositoryImpl extends ServiceImpl<OutStockMapper, OutStock> implements OutStockRepository {

    private final ConsumerMapper consumerMapper;

    private final ProductMapper productMapper;

    private final ProductStockMapper productStockMapper;

    private final OutStockInfoMapper outStockInfoMapper;

    @Autowired
    public OutStockRepositoryImpl(ConsumerMapper consumerMapper, ProductMapper productMapper, ProductStockMapper productStockMapper, OutStockInfoMapper outStockInfoMapper) {
        this.consumerMapper = consumerMapper;
        this.productMapper = productMapper;
        this.productStockMapper = productStockMapper;
        this.outStockInfoMapper = outStockInfoMapper;
    }

    /**
     * 添加出库记录
     *
     * @param outStockVo 数据
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public Result addOutStock(OutStockVo outStockVo) {
        if(Objects.isNull(outStockVo.getConsumerId())) {
            // 说明现在添加物资来源
            ConsumerVo consumerVo = new ConsumerVo();
            BeanUtils.copyProperties(outStockVo, consumerVo);
            if(StringUtils.isEmpty(consumerVo.getName())){
                throw new BusinessException(ResultCode.FAILED, "物资去向名不能为空");
            }
            if(StringUtils.isEmpty(consumerVo.getContact())){
                throw new BusinessException(ResultCode.FAILED, "联系人不能为空");
            }
            if(StringUtils.isEmpty(consumerVo.getAddress())){
                throw new BusinessException(ResultCode.FAILED, "地址不能为空");
            }
            if(StringUtils.isEmpty(consumerVo.getPhone())){
                throw new BusinessException(ResultCode.PARAM_ERROR, "联系方式不能为空");
            }
            if(Objects.isNull(consumerVo.getSort())){
                throw new BusinessException(ResultCode.PARAM_ERROR, "排序不能为空");
            }
            Consumer consumer = new Consumer();
            BeanUtils.copyProperties(consumerVo, consumer);
            int res = consumerMapper.insert(consumer);
            if (res > 0) {
                outStockVo.setConsumerId(consumer.getId());
            } else {
                throw new BusinessException(ResultCode.FAILED, "添加物资来源失败，请重试！");
            }
        }

        // 添加出单信息
        // 随机生成出单号
        String OUT_STOCK_NUM = UUID.randomUUID().toString().replace("-", "");
        // 该单的总数
        int itemNumber = 0;
        // 获取商品明细
        List<Object> products = outStockVo.getProducts();
        if (products.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "物资发放商品明细不能为空！");
        }
        for (Object product : products) {
            JSONObject json = JSONUtil.parseObj(product);
            // 发放数量
            Integer productNumber = json.getInt("productNumber");
            // 物资编号
            Integer productId = json.getInt("productId");
            Product dbProduct = productMapper.selectById(productId);
            if (Objects.isNull(dbProduct)) {
                throw new BusinessException(ResultCode.FAILED, "物资编号[ " + productId + " ]的商品找不到，添加入库单失败！");
            }
            if (productNumber <= 0) {
                throw new BusinessException(ResultCode.FAILED, dbProduct.getName() + "发放数量不合法，无法入库！");
            }
            // 校验库存
            ProductStock productStock = productStockMapper.selectOne(Wrappers.lambdaQuery(ProductStock.class).eq(ProductStock::getPNum, dbProduct.getPNum()));
            if (Objects.isNull(productStock)) {
                throw new BusinessException(ResultCode.FAILED, dbProduct.getName() + "物资在库存中不存在，请查正后在入库！");
            }
            if (productNumber > productStock.getStock()) {
                throw new BusinessException(ResultCode.FAILED, dbProduct.getName() + "库存不足，当前剩余库存：" + productStock.getStock());
            }
            itemNumber += productNumber;
            // 入库单明细
            OutStockInfo outStockInfo = new OutStockInfo();
            outStockInfo.setProductNumber(productNumber);
            outStockInfo.setPNum(dbProduct.getPNum());
            outStockInfo.setOutNum(OUT_STOCK_NUM);
            int res = outStockInfoMapper.insert(outStockInfo);
            if (res <= 0) {
                throw new BusinessException(ResultCode.FAILED, dbProduct.getName() + "入库明细添加失败，请重试！");
            }
        }
        OutStock outStock = new OutStock();
        BeanUtils.copyProperties(outStockVo, outStock);
        outStock.setProductNumber(itemNumber);
        LoginUser loginUser = UserUtil.getCurrentLoginUser();
        assert loginUser != null;
        outStock.setOperator(loginUser.getUsername());
        // 入库单号
        outStock.setOutNum(OUT_STOCK_NUM);
        // 设置为待审核状态
        outStock.setStatus(2);
        int res = this.baseMapper.insert(outStock);
        if (res <= 0) {
            throw new BusinessException(ResultCode.FAILED, "[ " + OUT_STOCK_NUM + " ]出库单添加失败，请重试！");
        }
        return Result.ok();
    }

    /**
     * 分页获取出库记录列表
     *
     * @param page       page
     * @param size       size
     * @param outStockVo 数据
     * @return Result
     */
    @Override
    public Result findInStockList(Integer page, Integer size, OutStockVo outStockVo) {
        Page<OutStock> pageInfo = this.baseMapper.selectPage(new Page<>(page, size), buildWrapper(outStockVo));
        return Result.ok().data(toVoPage(pageInfo));
    }

    /**
     * 移入回收站
     *
     * @param id id
     * @return Result
     */
    @Override
    public Result remove(Long id) {
        OutStock outStock = this.baseMapper.selectById(id);
        if (Objects.isNull(outStock)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "发放单不存在，请重试！");
        }
        // 只有status=0,正常的情况下,才可移入回收站
        if(outStock.getStatus() != 0){
            throw new BusinessException(ResultCode.PARAM_ERROR, "发放单状态不正确");
        }
        outStock.setStatus(1);
        int res = this.baseMapper.updateById(outStock);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    /**
     * 获取入库单明细
     *
     * @param id   id
     * @param page page
     * @param size size
     * @return Result
     */
    @Override
    public Result detail(Long id, Integer page, Integer size) {
        OutStockDetailVo outStockDetailVo = new OutStockDetailVo();
        OutStock outStock = this.baseMapper.selectById(id);
        if (Objects.isNull(outStock)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "发放单不存在！");
        }
        BeanUtils.copyProperties(outStock, outStockDetailVo);
        Consumer consumer = consumerMapper.selectById(outStock.getConsumerId());
        if (Objects.isNull(consumer)) {
            throw new BusinessException(ResultCode.FAILED, "物资领取方不存在，或已被删除！");
        }
        ConsumerVo consumerVo = new ConsumerVo();
        BeanUtils.copyProperties(consumer, consumerVo);
        outStockDetailVo.setConsumerVo(consumerVo);
        // 获取发放单号
        String outNum = outStock.getOutNum();
        // 查询该单所有的物资
        Page<OutStockInfo> outStockInfoPage = outStockInfoMapper.selectPage(new Page<>(page, size), Wrappers.lambdaQuery(OutStockInfo.class).eq(OutStockInfo::getOutNum, outNum));
        outStockDetailVo.setTotal(outStockInfoPage.getTotal());

        if (outStockInfoPage.getRecords().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "发放编号为[ " + outNum + " ]的明细找不到，或者已被删除！");
        }
        for (OutStockInfo record : outStockInfoPage.getRecords()) {
            String pNum = record.getPNum();
            // 查出物资
            List<Product> products = productMapper.selectList(Wrappers.lambdaQuery(Product.class).eq(Product::getPNum, pNum));
            if (products.isEmpty()) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "编号为[ " + pNum + " ]的物资找不到，或已被删除！");
            }
            Product product = products.get(0);
            OutStockItemVo outStockItemVo = new OutStockItemVo();
            BeanUtils.copyProperties(product, outStockItemVo);
            outStockItemVo.setCount(record.getProductNumber());
            outStockDetailVo.getItemVos().add(outStockItemVo);
        }
        return Result.ok().data(outStockDetailVo);
    }

    /**
     * 删除入库记录
     *
     * @param id id
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public Result delete(Long id) {
        OutStock outStock = this.baseMapper.selectById(id);
        if (Objects.isNull(outStock)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "发放单不存在，请重试！");
        }
        // 只有为 1 的才是处于回收站状态
        if(outStock.getStatus() != 1 && outStock.getStatus() != 2){
            throw new BusinessException(ResultCode.PARAM_ERROR, "发放单状态不正确，无法删除！");
        }
        int res = this.baseMapper.deleteById(id);
        if (res > 0) {
            String outNum = outStock.getOutNum();
            int res2 = outStockInfoMapper.delete(Wrappers.lambdaQuery(OutStockInfo.class).eq(OutStockInfo::getOutNum, outNum));
            if (res2 >= 0) {
                return Result.ok();
            }
            throw new BusinessException(ResultCode.FAILED, "删除发放单的明细失败！");
        }
        throw new BusinessException(ResultCode.FAILED, "删除发放单失败！");
    }

    /**
     * 入库审核
     *
     * @param id id
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public Result publish(Long id) {
        OutStock outStock = this.baseMapper.selectById(id);
        if (Objects.isNull(outStock)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "发放单不存在，请重试！");
        }
        if (outStock.getStatus() != 2) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "发放单状态错误！");
        }
        Consumer consumer = consumerMapper.selectById(outStock.getConsumerId());
        if (Objects.isNull(consumer)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "发放来源信息错误！");
        }
        // 发放单号
        String outNum = outStock.getOutNum();
        List<OutStockInfo> outStockInfos = outStockInfoMapper.selectList(Wrappers.lambdaQuery(OutStockInfo.class).eq(OutStockInfo::getOutNum, outNum));
        if (outStockInfos.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "发放单的明细缺失，无法进行审操作！");
        }
        for (OutStockInfo outStockInfo : outStockInfos) {
            // 物资编号
            String pNum = outStockInfo.getPNum();
            // 入库物资数量
            Integer productNumber = outStockInfo.getProductNumber();
            List<Product> products = productMapper.selectList(Wrappers.lambdaQuery(Product.class).eq(Product::getPNum, pNum));
            if (products.isEmpty()) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "物资编号为[ " + pNum + " ]的物资不存在！");
            }
            Product product = products.get(0);
            // 如果存在就减少数量
            List<ProductStock> productStocks = productStockMapper.selectList(Wrappers.lambdaQuery(ProductStock.class).eq(ProductStock::getPNum, pNum));
            if (productStocks.isEmpty()) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "[ " + product.getName() + " ]在库存中找不到！");
            }
            // 更新数量
            ProductStock productStock = productStocks.get(0);
            if (productStock.getStock() < productNumber) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "物资[ " + product.getName() + " ]的库存不足！");
            }
            productStock.setStock(productStock.getStock() - productNumber);
            int res = productStockMapper.updateById(productStock);
            if (res <= 0) {
                throw new BusinessException(ResultCode.FAILED, "更新[ " + product.getName() + " ]的库存错误，请重试！");
            }
            // 修改入库单状态
            outStock.setStatus(0);
            this.baseMapper.updateById(outStock);
        }
        return Result.ok();
    }

    /**
     * 从回收站收回数据
     *
     * @param id id
     * @return Result
     */
    @Override
    public Result back(Long id) {
        OutStock outStock = this.baseMapper.selectById(id);
        if (Objects.isNull(outStock)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "发放单不存在，请重试！");
        }
        // 只有为 1 的才是处于回收站状态
        if(outStock.getStatus() != 1){
            throw new BusinessException(ResultCode.PARAM_ERROR, "发放单状态不正确");
        }
        outStock.setStatus(0);
        int res = this.baseMapper.updateById(outStock);
        if (res > 0) {
            return Result.ok();
        }
        throw new BusinessException(ResultCode.FAILED);
    }

    private static LambdaQueryWrapper<OutStock> buildWrapper(OutStockVo outStockVo) {
        LambdaQueryWrapper<OutStock> wrapper = Wrappers.lambdaQuery(OutStock.class);
        if (!StringUtils.isEmpty(outStockVo.getOutNum())) {
            wrapper.like(OutStock::getOutNum, outStockVo.getOutNum());
        }
        if (!Objects.isNull(outStockVo.getType())) {
            wrapper.eq(OutStock::getType, outStockVo.getType());
        }
        if (!Objects.isNull(outStockVo.getStatus())) {
            wrapper.eq(OutStock::getStatus, outStockVo.getStatus());
        }
        wrapper.orderByDesc(OutStock::getCreateTime);
        return wrapper;
    }

    private List<OutStockVo> toVoList(List<OutStock> list) {
        List<OutStockVo> outStockVos = new ArrayList<>(list.size());
        list.forEach(outStock -> {
            OutStockVo outStockVo = new OutStockVo();
            BeanUtils.copyProperties(outStock, outStockVo);
            Consumer consumer = consumerMapper.selectById(outStock.getConsumerId());
            if (!Objects.isNull(consumer)) {
                outStockVo.setName(consumer.getAddress() + "[ " + consumer.getName() + " ]");
                outStockVo.setPhone(consumer.getPhone());
            }
            outStockVos.add(outStockVo);
        });
        return outStockVos;
    }

    private Page<OutStockVo> toVoPage(Page<OutStock> pageInfo) {
        Page<OutStockVo> voPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, voPage);
        voPage.setRecords(toVoList(pageInfo.getRecords()));
        return voPage;
    }
}
