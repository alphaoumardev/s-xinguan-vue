<!--
  @author qiukangming
  @description 出库记录
  @date 2021/03/28 11:45
-->
<template>
  <div>
    <!-- 面包导航 -->
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>物资管理</el-breadcrumb-item>
      <el-breadcrumb-item>出库记录</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 卡片部分-->
    <el-card id="card">
      <!--搜索部分-->
      <el-form size="mini" :inline="true" :model="queryMap" class="demo-form-inline">
        <el-form-item label="发放单号">
          <el-input v-model="queryMap.outNum" placeholder="发放单号"></el-input>
        </el-form-item>
        <el-form-item label="发放类型">
          <el-select v-model="queryMap.type" placeholder="发放类型">
            <el-option label="全部类型" value=""></el-option>
            <el-option label="政府领取" value="0"></el-option>
            <el-option label="医院领取" value="1"></el-option>
            <el-option label="小区领取" value="2"></el-option>
            <el-option label="个人领取" value="3"></el-option>
            <el-option label="其他领取" value="4"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-select v-model="queryMap.status" placeholder="请选择状态">
            <el-option label="已发放" :value="0"></el-option>
            <el-option label="回收站" :value="1"></el-option>
            <el-option label="待审核" :value="2"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="search(1)">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>

        <el-form-item>
          <router-link to="/business/product/publish">
            <el-button type="success">发放物资</el-button>
          </router-link>
        </el-form-item>
      </el-form>
      <el-table size="mini" border :data="page.records" style="width: 100%" :max-height="this.getTableMaxHeight()">
        <el-table-column align="center" label="ID" prop="id" width="50"></el-table-column>
        <el-table-column align="center" prop="outNum" show-overflow-tooltip label="发放单号"></el-table-column>
        <el-table-column align="center" prop="type" label="发放类型" width="90">
          <template slot-scope="scope">
            <el-tag effect="plain" size="mini" type="success" v-if="scope.row.type === 0">政府领取</el-tag>
            <el-tag effect="plain" size="mini" type="danger" v-else-if="scope.row.type === 1">医院领取</el-tag>
            <el-tag effect="plain" size="mini" type="warning" v-else-if="scope.row.type === 2">小区领取</el-tag>
            <el-tag effect="plain" size="mini" type="info" v-else-if="scope.row.type === 3">个人领取</el-tag>
            <el-tag effect="plain" size="mini" v-else-if="scope.row.type === 4">其他领取</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="priority" label="紧急程度" width="180">
          <template slot-scope="scope">
            <el-rate disabled v-model="scope.row.priority" show-text :texts="['不急','常规','紧急','特急','超急']"></el-rate>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="name" label="发放地点" show-overflow-tooltip></el-table-column>
        <el-table-column align="center" prop="productNumber" label="总数" width="50"></el-table-column>
        <el-table-column align="center" prop="phone" label="联系方式"></el-table-column>
        <el-table-column align="center" prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag size="mini" type="danger" effect="plain" v-if="scope.row.status === 1">已回收</el-tag>
            <el-tag size="mini" effect="plain" v-if="scope.row.status === 0">已发放</el-tag>
            <el-tag size="mini" effect="plain" type="warning" v-if="scope.row.status === 2">审核中</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="operator" label="操作员"></el-table-column>
        <el-table-column align="center" prop="createTime" label="发放时间" show-overflow-tooltip></el-table-column>
        <el-table-column align="center" label="操作" width="180">
          <template slot-scope="scope">
            <el-button icon="el-icon-view" type="text" size="small" @click="detail(scope.row.id, scope.row.remark)">明细</el-button>
            <span v-if="scope.row.status === 0">
              <el-popconfirm @confirm="remove(scope.row.id)" style="margin-left: 10px;" confirmButtonText='好的' cancelButtonText='不用了' icon="el-icon-info" iconColor="red" title="确定移入回收站吗？">
                <el-button type="text" slot="reference" size="mini" icon="el-icon-s-operation">回收站</el-button>
              </el-popconfirm>
            </span>
            <span v-if="scope.row.status === 1">
              <el-popconfirm @confirm="back(scope.row.id)" style="margin-left:10px;" confirmButtonText='好的' cancelButtonText='不用了' icon="el-icon-info" iconColor="green" title="确定恢复数据吗？">
                <el-button type="text" slot="reference" size="mini" icon="el-icon-s-operation">还原</el-button>
              </el-popconfirm>
              <el-popconfirm style="margin-left:10px;" @confirm="del(scope.row.id)" title="确定删除吗？">
                <el-button type="text" slot="reference" size="small" icon="el-icon-delete">删除</el-button>
              </el-popconfirm>
            </span>
            <span v-if="scope.row.status === 2">
              <el-popconfirm style="margin-left:10px;" @confirm="publish(scope.row.id)" title="审核通过后库存将更新,是否通过？">
                <el-button type="text" slot="reference" size="small" icon="el-icon-refresh">通过</el-button>
              </el-popconfirm>
              <el-popconfirm style="margin-left:10px;" @confirm="del(scope.row.id)" title="这确定删除吗？">
                <el-button type="text" slot="reference" size="small" icon="el-icon-delete">删除</el-button>
              </el-popconfirm>
            </span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="page.total > 0"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="page.current"
        :page-sizes="[5, 10, 20, 30]"
        :page-size="page.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="page.total">
      </el-pagination>
      <!-- 发放明细 -->
      <el-dialog title="发放明细" :visible.sync="dialogVisible" @close="closeDetail" width="65%">
        <el-card class="box-card" style="margin-bottom: 10px;">
          <div class="text item">
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="grid-content bg-purple">
                  <span style="font-size: 11px;color: #303030;">省区市:</span>&nbsp;
                  <el-tag size="mini">{{ consumer.address }}</el-tag>
                </div>
              </el-col>
              <el-col :span="10">
                <div class="grid-content bg-purple">
                  <span style="font-size: 11px;color: #303030;">具体位置:</span> &nbsp;
                  <el-tag size="mini">{{ consumer.name }}</el-tag>
                </div>
              </el-col>
              <el-col :span="4">
                <div class="grid-content bg-purple">
                  <span style="font-size: 11px;color: #303030;">联系人:</span> &nbsp;
                  <el-tag size="mini">{{ consumer.contact }}</el-tag>
                </div>
              </el-col>
              <el-col :span="4">
                <div class="grid-content bg-purple">
                  <span style="font-size: 11px;color: #303030;">电话:</span>&nbsp;
                  <el-tag size="mini">{{ consumer.phone }}</el-tag>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="20" style="margin-top: 10px">
              <el-col :span="24">
                <span style="font-size: 11px;color: #303030;">备注信息:</span> &nbsp;
                <el-tag size="mini">{{ remark }}</el-tag>
              </el-col>
            </el-row>
          </div>
        </el-card>
        <el-steps simple v-if="pStatus === 0" style="margin-left: 10px;margin-bottom: 5px;" :space="200" :active="3" finish-status="success">
          <el-step title="提交发放单"></el-step>
          <el-step title="审核发放单"></el-step>
          <el-step title="成功发放"></el-step>
        </el-steps>
        <el-steps simple v-if="pStatus === 2" style="margin-left: 10px;margin-bottom: 5px;" :space="200" :active="2" finish-status="success">
          <el-step title="提交发放单"></el-step>
          <el-step title="审核发放单"></el-step>
          <el-step title="成功发放"></el-step>
        </el-steps>
        <div :style="{ height: pStatus !== 1 ? '350px' : '400px' }">
          <el-table size="mini" border :data="detailTable" style="width: 100%" :height="pStatus !== 1 ? '320' : '370'" width="70%">
            <el-table-column align="center" prop="name" label="名称" show-overflow-tooltip></el-table-column>
            <el-table-column align="center" show-overflow-tooltip prop="pnum" label="商品编号"></el-table-column>
            <el-table-column align="center" prop="model" label="规格"></el-table-column>
            <el-table-column align="center" prop="imageUrl" label="图片">
              <template slot-scope="scope">
                <img :src="scope.row.imageUrl" alt="alt" style="width: 30px;height:30px"/>
              </template>
            </el-table-column>
            <el-table-column align="center" prop="count" label="数量"></el-table-column>
            <el-table-column align="center" prop="unit" label="单位"></el-table-column>
          </el-table>
          <el-pagination
            v-if="detailTotal > 0"
            @current-change="handleDetailCurrentChange"
            :current-page="pageNumDetail"
            :page-sizes="[5]"
            :page-size="pageSizeDetail"
            layout="total, sizes, prev, pager, next, jumper"
            :total="detailTotal">
          </el-pagination>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { findOutStockList, getOutStockDetailList, remove, backRecord, delOutStock, publishRecord } from '@/api/out-stock'

export default {
  name: 'ProductOutStock',
  components: {},
  data () {
    return {
      queryMap: {
        outNum: ''
      },
      pageNum: 1,
      pageSize: this.getPageSize(),
      page: {
        records: [],
        total: 0,
        size: 0,
        current: 0,
        orders: []
      },
      dialogVisible: false,
      consumer: {},
      detailId: null,
      remark: '',
      pStatus: null,
      detailTable: [],
      detailTotal: 0,
      pageNumDetail: 1,
      pageSizeDetail: 5
    }
  },
  created () {
    this.getOutStockData()
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight()
  },
  activated () {
  },
  methods: {
    getOutStockData (page = this.pageNum) {
      findOutStockList(page, this.pageSize, this.queryMap).then(res => {
        this.page = res.data
      }).finally()
    },
    search (page = this.pageNum) {
      this.getOutStockData(page)
    },
    /**
     * 重置查询表单
     */
    reset () {
      this.queryMap = {}
      this.pageNum = 1
      this.pageSize = this.getPageSize()
      this.getOutStockData()
    },
    detail (id, remark) {
      this.detailId = id
      getOutStockDetailList(this.detailId, this.pageNumDetail, this.pageSizeDetail).then(res => {
        this.detailTable = res.data.itemVos
        this.detailTotal = res.data.total
        this.consumer = res.data.consumerVo
        this.remark = remark
        this.pStatus = res.data.status
        this.dialogVisible = true
      }).finally()
    },
    remove (id) {
      remove(id).then(() => {
        this.getOutStockData()
        this.NormalMsg(this, '移入回收站成功！', 'success')
      }).finally()
    },
    back (id) {
      backRecord(id).then(() => {
        this.getOutStockData()
        this.NormalMsg(this, '还原成功！', 'success')
      }).finally()
    },
    del (id) {
      delOutStock(id).then(() => {
        this.getOutStockData()
        this.NormalMsg(this, '删除该出库记录成功！', 'success')
      }).finally()
    },
    publish (id) {
      publishRecord(id).then(() => {
        this.getOutStockData()
        this.NormalMsg(this, '出库已审核通过！', 'success')
      }).finally()
    },
    // 改变页码
    handleSizeChange (newSize) {
      this.pageSize = newSize
      this.getOutStockData()
    },
    // 翻页
    handleCurrentChange (current) {
      this.pageNum = current
      this.getOutStockData()
    },
    closeDetail () {},
    // 翻页
    handleDetailCurrentChange (current) {
      this.pageNumDetail = current
      this.detail(this.detailId, this.remark)
    }
  }
}
</script>

<style scoped>

</style>
