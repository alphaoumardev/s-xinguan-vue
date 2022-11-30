<!--
  @author qiukangming
  @description 物资入库
  @date 2021/03/23 22:22
-->
<template>
  <div>
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>物资管理</el-breadcrumb-item>
      <el-breadcrumb-item>物资入库</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card id="card">
      <!-- 搜索部分 -->
      <el-form size="small" :inline="true" :model="queryData" class="demo-form-inline">
        <el-form-item label="类型">
          <el-select clearable v-model="queryData.type" placeholder="请选择入库类型">
            <el-option label="捐赠" value="1"></el-option>
            <el-option label="下拨" value="2"></el-option>
            <el-option label="采购" value="3"></el-option>
            <el-option label="借用" value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="单号">
          <el-input clearable @clear="search(1)" v-model="queryData.inNum" placeholder="请输入入库单查询"
                    @keyup.enter.native="search(1)" class="input-with-select"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select clearable v-model="queryData.status" placeholder="请选择状态">
            <el-option label="已入库" :value="0"></el-option>
            <el-option label="回收站" :value="1"></el-option>
            <el-option label="待审核" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker
            @change="rangeChange"
            :clearable="false" v-model="range" type="datetimerange" value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="pickerOptions" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"
            align="right">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="search(1)">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button icon="el-icon-refresh" type="warning" @click="clearTime">重置</el-button>
        </el-form-item>
        <el-form-item>
          <router-link to="/business/product/add-stocks">
            <el-button type="success" icon="el-icon-plus">入库</el-button>
          </router-link>
        </el-form-item>
        <el-form-item>
          <el-button @click="exportList" type="button" icon="el-icon-download">导出</el-button>
        </el-form-item>
      </el-form>
      <!-- 表格区域 -->
      <el-table size="mini" v-loading="loading" :data="page.records" border style="width: 100%;" :max-height="this.getTableMaxHeight()">
        <el-table-column label="序号" prop="id" align="center" width="50"></el-table-column>
        <el-table-column prop="inNum" show-overflow-tooltip label="入库单号" align="center"></el-table-column>
        <el-table-column label="物资类型" align="center">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.type === 1">捐赠</el-tag>
            <el-tag v-else-if="scope.row.type === 2">下拨</el-tag>
            <el-tag type="danger" v-else-if="scope.row.type === 3">采购</el-tag>
              <el-tag type="warning" v-else>退货入库</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="productNumber" label="数量" sortable align="center"></el-table-column>
        <el-table-column prop="phone" label="联系方式" align="center"></el-table-column>
        <el-table-column label="状态" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" type="danger" effect="plain" v-if="scope.row.status === 1">回收</el-tag>
            <el-tag size="mini" effect="plain" v-if="scope.row.status === 0">已入库</el-tag>
            <el-tag size="mini" effect="plain" type="warning" v-if="scope.row.status === 2">审核中</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作员" align="center"></el-table-column>
        <el-table-column prop="supplierName" label="物资提供方" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="入库时间" sortable align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column label="操作" align="center" width="200">
          <template slot-scope="scope">
            <el-button icon="el-icon-view" @click="detail(scope.row.id, scope.row.remark)" type="text" size="small">明细</el-button>
            <!-- 给操作员使用的按钮-->
            <span v-if="scope.row.status === 0">
               <el-popconfirm
                 @confirm="remove(scope.row.id)" style="margin-left: 20px;"
                 confirmButtonText='好的' cancelButtonText='不用了' icon="el-icon-info"
                 iconColor="red" title="确定移入回收站吗？">
                <el-button type="text" slot="reference" size="mini" icon="el-icon-s-operation">回收站</el-button>
              </el-popconfirm>
            </span>
            <!-- 给操作员使用的按钮(回收站)-->
            <span v-if="scope.row.status === 1">
               <el-popconfirm @confirm="back(scope.row.id)"
                 style="margin-left:10px;" confirmButtonText='好的' cancelButtonText='不用了'
                 icon="el-icon-info" iconColor="green" title="确定恢复数据吗？">
                 <el-button type="text" slot="reference" size="mini" icon="el-icon-s-operation">还原</el-button>
               </el-popconfirm>
               <el-popconfirm style="margin-left:20px;" @confirm="del(scope.row.id)" title="确定删除吗？">
                 <el-button type="text" slot="reference" size="small" icon="el-icon-delete">删除</el-button>
               </el-popconfirm>
            </span>
            <!-- 给审核员使用的按钮-->
            <span v-if="scope.row.status === 2">
              <el-popconfirm style="margin-left:20px;" @confirm="publish(scope.row.id)" title="审核通过后库存将更新,是否通过">
                <el-button type="text" slot="reference" size="small" icon="el-icon-refresh">通过</el-button>
              </el-popconfirm>
              <el-popconfirm style="margin-left:20px;" @confirm="del(scope.row.id)" title="确定删除吗？">
                <el-button type="text" slot="reference" size="small" icon="el-icon-delete">删除</el-button>
              </el-popconfirm>
            </span>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
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
      <!-- 入库明细 -->
      <el-dialog title="入库明细" :visible.sync="dialogVisible" @close="closeDetail" width="65%">
        <!-- 来源信息 -->
        <el-card style="margin-bottom: 10px;">
          <div class="text item">
            <el-row :gutter="20">
              <el-col :span="6">
                <div class="grid-content bg-purple">
                  <span style="font-size: 11px;color: #303030;">省区市:</span> &nbsp;
                  <el-tag size="mini">{{ supplier.address }}</el-tag>
                </div>
              </el-col>
              <el-col :span="10">
                <div class="grid-content bg-purple">
                  <span style="font-size: 11px;color: #303030;">具体位置:</span> &nbsp;
                  <el-tag size="mini">{{ supplier.name }}</el-tag>
                </div>
              </el-col>
              <el-col :span="4">
                <div class="grid-content bg-purple">
                  <span style="font-size: 11px;color: #303030;">联系人:</span> &nbsp;
                  <el-tag size="mini">{{ supplier.contact }}</el-tag>
                </div>
              </el-col>
              <el-col :span="4">
                <div class="grid-content bg-purple">
                  <span style="font-size: 11px;color: #303030;">电话:</span>&nbsp;
                  <el-tag size="mini">{{ supplier.phone }}</el-tag>
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
        <!-- 步骤条 -->
        <el-steps simple v-if="pStatus === 0" style="margin-left: 10px;margin-bottom: 5px;" :space="200" :active="3" finish-status="success">
          <el-step title="提交入库单"></el-step>
          <el-step title="审核入库单"></el-step>
          <el-step title="进入库存"></el-step>
        </el-steps>
        <el-steps simple v-if="pStatus === 2" style="margin-left: 10px;margin-bottom: 5px;" :space="200" :active="2" finish-status="success">
          <el-step title="提交入库单"></el-step>
          <el-step title="审核入库单"></el-step>
          <el-step title="进入库存"></el-step>
        </el-steps>
        <div :style="{ height: pStatus !== 1 ? '350px' : '400px' }">
          <el-table border :data="detailTable" style="width: 100%" :height="pStatus !== 1 ? '320' : '370'" width="70%">
            <el-table-column prop="name" label="名称" align="center" show-overflow-tooltip></el-table-column>
            <el-table-column show-overflow-tooltip prop="pnum" label="商品编号" align="center"></el-table-column>
            <el-table-column prop="model" label="规格" align="center"></el-table-column>
            <el-table-column prop="imageUrl" label="图片" align="center" width="150px" padding="0px">
              <template slot-scope="scope">
                <img :src="scope.row.imageUrl" alt="alt" style="width: 30px;height:30px"/>
              </template>
            </el-table-column>
            <el-table-column prop="count" label="数量" align="center"></el-table-column>
            <el-table-column prop="unit" label="单位" align="center"></el-table-column>
          </el-table>
          <!--明细分页-->
          <el-pagination
            v-if="detailTotal > 0"
            @current-change="handleDetailCurrentChange"
            :current-page="detailPageNum"
            :page-sizes="[5]"
            :page-size="detailPageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="detailTotal">
          </el-pagination>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { getInStockList, exportInStock, getInStockDetailList, remove, backRecord, publishRecord, delInStock } from '@/api/in-stock'

export default {
  name: 'InStock',
  components: {},
  data () {
    return {
      pageNum: 1,
      sizeNum: this.getPageSize(),
      queryData: {
        type: null,
        inNum: null,
        status: null,
        startTime: null,
        endTime: null
      },
      range: [],
      pickerOptions: {
        shortcuts: [
          {
            text: '今天(此刻)',
            onClick (picker) {
              const end = new Date()
              const start = new Date(new Date(new Date().toLocaleDateString()).getTime())
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '昨天',
            onClick (picker) {
              const end = new Date(new Date(new Date().toLocaleDateString()).getTime())
              const start = new Date(new Date(new Date().toLocaleDateString()).getTime())
              start.setTime(start.getTime() - 3600 * 1000 * 24)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近一周',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近一个月',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近两个月',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 60)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近三个月',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
              picker.$emit('pick', [start, end])
            }
          }
        ]
      },
      loading: false,
      page: {
        records: [],
        total: 0,
        size: 0,
        current: 0,
        orders: []
      },
      // 弹框部分控制变量
      dialogVisible: false,
      supplier: {
        address: '',
        name: '',
        contact: '',
        phone: ''
      },
      // 明细备注信息
      remark: '',
      detailId: null,
      pStatus: null, // 步骤flag
      detailTable: [],
      detailPageNum: 1,
      detailPageSize: 5,
      detailTotal: 0
    }
  },
  created () {
    this.init()
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight()
  },
  activated () {
  },
  methods: {
    init () {
      this.getDataList()
      this.getCatetorys()
    },
    rangeChange (value) {
      if (value !== null) {
        this.queryData.startTime = value[0]
        this.queryData.endTime = value[1]
      } else {
        this.queryData.startTime = value
        this.queryData.endTime = value
      }
    },
    getDataList (page = this.pageNum) {
      getInStockList(page, this.sizeNum, this.queryData).then(res => {
        this.page = res.data
      }).catch().finally()
    },
    getCatetorys () {
    },
    search (page = this.pageNum) {
      this.getDataList(page)
    },
    clearTime () {
      this.queryData = { type: null, inNum: null, status: null, startTime: null, endTime: null }
      this.range = []
      this.pageNum = 1
      this.sizeNum = this.getPageSize()
      this.getDataList()
    },
    detail (id, remark) {
      this.detailId = id
      getInStockDetailList(id, this.detailPageNum, this.detailPageSize).then(res => {
        this.detailTable = res.data.itemVos
        this.detailTotal = res.data.total
        this.supplier = res.data.supplierVo
        this.remark = remark
        this.pStatus = res.data.status
        this.dialogVisible = true
      }).finally()
    },
    remove (id) {
      remove(id).then(() => {
        this.getDataList()
        this.NormalMsg(this, '移入回收站成功！', 'success')
      }).finally()
    },
    back (id) {
      backRecord(id).then(() => {
        this.getDataList()
        this.NormalMsg(this, '还原成功！', 'success')
      }).finally()
    },
    del (id) {
      delInStock(id).then(() => {
        this.getDataList()
        this.NormalMsg(this, '删除该入库记录成功！', 'success')
      }).finally()
    },
    publish (id) {
      publishRecord(id).then(() => {
        this.getDataList()
        this.NormalMsg(this, '入库已审核通过！', 'success')
      }).finally()
    },
    // 改变页码
    handleSizeChange (newSize) {
      this.sizeNum = newSize
      this.getDataList()
    },
    // 翻页
    handleCurrentChange (current) {
      this.pageNum = current
      this.getDataList()
    },
    // 翻页
    handleDetailCurrentChange (current) {
      this.detailPageNum = current
      this.detail(this.detailId, this.remark)
    },
    closeDetail () {
      this.detailPageNum = 1
    },
    exportList () {
      this.$confirm('确定导出入库记录信息吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        exportInStock(this.queryData).then((res) => {
          this.downloadFile(res, '入库记录', 'xls')
        }).finally(() => {})
      }).catch(() => {
        this.NormalMsg(this, '已取消导出', 'info')
      })
    }
  }
}
</script>

<style scoped>

</style>
