<!--
  @author qiukangming
  @description 登录日志
  @date 2021/03/15 14:30
-->
<template>
  <div>
    <!-- 面包导航 -->
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>日志管理</el-breadcrumb-item>
      <el-breadcrumb-item>登入日志</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="box-card" id="card">
      <el-form size="mini" :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名查询"></el-input>
        </el-form-item>
        <el-form-item label="IP地址">
          <el-input v-model="searchForm.ip" placeholder="请输入IP查询"></el-input>
        </el-form-item>
        <el-form-item label="登入地址">
          <el-input v-model="searchForm.location" placeholder="请输入登入地址查询"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button icon="el-icon-search" @click="search(1)" type="primary">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button @click="deleteFileOrDirectory" icon="el-icon-delete"
                     :disabled="checked.length === 0">批量删除
          </el-button>
        </el-form-item>
      </el-form>
      <!-- 表格区域 -->
      <template>
        <el-table border size="mini" :data="page.records" style="width: 100%;" @selection-change="checkedChange" :max-height="this.getTableMaxHeight()">
          <el-table-column type="selection" width="55" align="center"></el-table-column>
          <el-table-column prop="id" type="index" label="ID" align="center"></el-table-column>
          <el-table-column prop="username" label="登入用户" align="center"></el-table-column>
          <el-table-column prop="loginTime" label="登入时间" align="center"></el-table-column>
          <el-table-column prop="location" label="登入地点" align="center"></el-table-column>
          <el-table-column prop="ip" label="IP地址" align="center"></el-table-column>
          <el-table-column prop="userSystem" label="操作系统" align="center"></el-table-column>
          <el-table-column prop="userBrowser" label="浏览器" align="center"></el-table-column>
          <el-table-column label="操作" align="center">
            <template slot-scope="scope">
              <el-button type="text" size="mini" icon="el-icon-delete" @click="del(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
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
    </el-card>
  </div>
</template>

<script>
import { getLoginLogList, delLoginLog, batchDelLoginLog } from '@/api/login-log'

export default {
  name: 'Login-Log',
  components: {},
  data () {
    return {
      checked: [], // 选中的值显示
      LoginLogData: [],
      page: {
        records: [],
        total: 0,
        size: 0,
        current: 0,
        orders: []
      },
      searchForm: {
        page: 1,
        size: this.getPageSize(),
        username: '',
        ip: '',
        location: ''
      }
    }
  },
  created () {
    this.getLoginLogList()
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight()
  },
  activated () {
  },
  methods: {
    getLoginLogList (page = this.searchForm.page) {
      const _this = this
      this.searchForm.page = page
      getLoginLogList(this.searchForm).then(res => {
        _this.page = res.data
      })
    },
    // 改变页码
    handleSizeChange (newSize) {
      this.searchForm.size = newSize
      this.getLoginLogList()
    },
    // 翻页
    handleCurrentChange (current) {
      this.searchForm.page = current
      this.getLoginLogList()
    },
    search (page = this.searchForm.page) {
      this.getLoginLogList(page)
    },
    deleteFileOrDirectory () {
      const _this = this
      const ids = _this.checked.map(item => item.id).join() // 获取所有选中行的id组成的字符串，以逗号分隔
      _this.$confirm('确认批量删除这些登入日志吗?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        batchDelLoginLog(ids).then(() => {
          _this.NormalMsg(_this, '批量删除登录日志信息成功！', 'success')
          _this.getLoginLogList()
        }).catch()
      })
    },
    checkedChange (checked) {
      this.checked = checked
    },
    del (id) {
      const _this = this
      _this.$confirm('确认删除该登入日志吗?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delLoginLog(id).then(() => {
          _this.NormalMsg(_this, '删除登录日志信息成功！', 'success')
          _this.getLoginLogList()
        }).catch()
      })
    }
  }
}
</script>

<style scoped>

</style>
