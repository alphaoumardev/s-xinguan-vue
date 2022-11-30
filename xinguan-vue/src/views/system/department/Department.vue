<!--
  @author: qiukangming
  @description:
  @date: 2021/01/23 22:28
-->
<template>
  <div>
    <!-- 面包导航 -->
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统管理</el-breadcrumb-item>
      <el-breadcrumb-item>部门管理</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 用户列表卡片区 -->
    <el-card class="box-card card" id="card">
      <el-row :gutter="20">
        <el-col :span="4">
          <el-input size="small" clearable v-model="searchForm.deptName" placeholder="请输入部门查询"
                    class="input-with-select"></el-input>
        </el-col>
        <el-col :span="4">
          <el-input size="small" clearable v-model="searchForm.phone" placeholder="请输入电话号码"
                    class="input-with-select"></el-input>
        </el-col>
        <el-col :span="2">
          <el-button size="mini" icon="el-icon-search" type="primary" @click="getDeptList(1)">查询</el-button>
        </el-col>
        <el-col :span="2">
          <el-button size="mini" icon="el-icon-plus" @click="add" type="success">添加</el-button>
        </el-col>
        <el-col :span="2">
          <el-button size="mini" icon="el-icon-download" type="warning" @click="exportDeptList">导出</el-button>
        </el-col>
        <el-col :span="2">
          <el-button size="mini" icon="el-icon-refresh" @click="reset">重置</el-button>
        </el-col>
      </el-row>
      <!-- 表格区域 -->
      <template>
        <el-table border size="mini" v-loading="loading" element-loading-text="数据加载中..." stripe :data="page.records"
                  style="width: 100%;margin-top:20px;" :max-height="this.getTableMaxHeight()">
          <el-table-column align="center" type="index" label="序号" width="50"></el-table-column>
          <el-table-column align="center" prop="name" label="部门名称" width="120"></el-table-column>
          <el-table-column align="center" prop="phone" label="办公电话" width="180"></el-table-column>
          <el-table-column align="center" prop="total" label="人数" sortable width="100">
            <template slot-scope="scope">
              <el-tag effect="plain" v-text="scope.row.total+'人'" size="small"></el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="createTime" label="创建时间" sortable></el-table-column>
          <el-table-column align="center" prop="modifiedTime" label="修改时间" sortable></el-table-column>
          <el-table-column align="center" prop="address" label="地址"></el-table-column>
          <el-table-column align="center" label="操作">
            <template slot-scope="scope">
              <el-button type="text" size="mini" icon="el-icon-edit" @click="edit(scope.row)">编辑</el-button>
              <el-popconfirm
                confirm-button-text='必须的'
                cancel-button-text='不删了'
                icon="el-icon-info"
                icon-color="red"
                title="确定删除该部门吗?"
                @confirm="del(scope.row.id)">
                <el-button type="text" size="mini" icon="el-icon-delete" slot="reference" style="margin-left: 10px">删除
                </el-button>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </template>
      <!--分页-->
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
    </el-card>
    <AddOrUpdate :showDialog="dialogVisible"
                 :data="dialogData"
                 @changeShow="showAddOrUpdate"
                 @dataUpdate="init"
                 :isEdit="isEdit">
    </AddOrUpdate>
  </div>
</template>

<script>
import { getList, delDept, exportDept } from '@/api/departments'
import AddOrUpdate from '@/views/system/department/AddOrUpdate'

export default {
  name: 'Department',
  components: {
    AddOrUpdate
  },
  data () {
    return {
      searchForm: {
        page: 1,
        size: this.getPageSize(),
        deptName: '',
        phone: ''
      },
      page: {
        records: [],
        total: 0,
        size: 0,
        current: 0,
        orders: []
      },
      isEdit: false,
      dialogData: {},
      dialogVisible: false,
      loading: false
    }
  },
  methods: {
    init () {
      this.getDeptList()
    },
    getDeptList (page = this.searchForm.page) {
      this.loading = true
      this.searchForm.page = page
      getList(this.searchForm).then(res => {
        this.page = res.data
      }).finally(() => {
        this.loading = false
      })
    },
    edit (data) {
      this.isEdit = true
      this.dialogData = JSON.parse(JSON.stringify(data))
      this.dialogVisible = true
    },
    del (id) {
      delDept(id).then(res => {
        this.NormalMsg(this, res.message, 'success')
        this.getDeptList()
      })
    },
    // 改变页码
    handleSizeChange (newSize) {
      this.searchForm.size = newSize
      this.getDeptList()
    },
    // 翻页
    handleCurrentChange (current) {
      this.searchForm.page = current
      this.getDeptList()
    },
    // 重置
    reset () {
      this.searchForm = {
        page: 1,
        size: 10,
        deptName: '',
        phone: ''
      }
      this.getDeptList()
    },
    add () {
      this.dialogVisible = true
      this.isEdit = false
    },
    // 监听 子组件弹窗关闭后触发，有子组件调用
    showAddOrUpdate (data) {
      this.dialogVisible = data !== 'false'
    },
    exportDeptList () {
      this.$confirm('确定导出部门信息吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        exportDept(this.searchForm).then((res) => {
          this.downloadFile(res, '部门信息', 'xls')
        })
      }).catch(() => {
        this.NormalMsg(this, '已取消导出', 'info')
      })
    }
  },
  created () {
    this.init()
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight()
  }
}
</script>

<style scoped>

</style>
