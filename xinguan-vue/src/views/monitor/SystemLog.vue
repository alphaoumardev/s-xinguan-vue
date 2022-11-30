<!--
  @author qiukangming
  @description 系统日志
  @date 2021/03/29 16:40
-->
<template>
  <div>
    <!-- 面包导航 -->
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>日志管理</el-breadcrumb-item>
      <el-breadcrumb-item>系统日志</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="box-card" id="card">
      <el-form :inline="true" size="mini" :model="queryMap" class="demo-form-inline">
        <el-form-item label="操作人">
          <el-input @keyup.enter.native="search(1)" clearable @clear="search(1)" v-model="queryMap.username" placeholder="操作人"></el-input>
        </el-form-item>
        <el-form-item label="ip地址">
          <el-input @keyup.enter.native="search(1)" clearable @clear="search(1)" v-model="queryMap.ip" placeholder="ip地址"></el-input>
        </el-form-item>
        <el-form-item label="操作位置">
          <el-input @keyup.enter.native="search(1)" clearable @clear="search(1)" v-model="queryMap.location" placeholder="操作位置"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search(1)" icon="el-icon-search">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button @click="deleteFileOrDirectory(sels)" :disabled="sels.length === 0" class="el-icon-delete">批量删除</el-button>
        </el-form-item>
      </el-form>
      <!-- 表格区域 -->
      <template>
        <el-table size="mini" border stripe :data="page.records" style="width: 100%;" @selection-change="selsChange" :max-height="this.getTableMaxHeight(0.36)">
          <el-table-column align="center" type="selection"></el-table-column>
          <el-table-column align="center" prop="operation" label="操作" width="150"></el-table-column>
          <el-table-column align="center" show-overflow-tooltip prop="method" label="方法"></el-table-column>
          <el-table-column align="center" show-overflow-tooltip prop="result" label="结果"></el-table-column>
          <el-table-column align="center" show-overflow-tooltip label="参数">
            <template slot-scope="scope">
              <span>{{ scope.row.params }}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="time" label="耗时" sortable>
            <template slot-scope="scope">
              <el-tag v-if="scope.row.time >= 2000" size="mini" type="danger">{{ scope.row.time + '毫秒' }}</el-tag>
              <el-tag size="mini" v-else-if="scope.row.time >= 1000 && scope.row.time < 2000">{{ scope.row.time + '毫秒' }}</el-tag>
              <el-tag v-else type="success" size="mini">{{ scope.row.time + '毫秒' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="location" show-overflow-tooltip label="操作地点"></el-table-column>
          <el-table-column align="center" prop="ip" label="IP地址"></el-table-column>
          <el-table-column align="center" prop="username" label="操作人"></el-table-column>
          <el-table-column align="center" prop="createTime" show-overflow-tooltip label="时间" sortable></el-table-column>
          <el-table-column align="center" label="操作" fixed="right">
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
import { getLogList, delLog, batchDelLog } from '@/api/system-log'

export default {
  name: 'SystemLog',
  components: {},
  data () {
    return {
      queryMap: {},
      sels: [],
      pageNum: 1,
      pageSize: this.getPageSize(),
      page: {
        records: [],
        total: 0,
        size: 0,
        current: 0,
        orders: []
      }
    }
  },
  created () {
    this.search()
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight(0.22)
  },
  activated () {
  },
  methods: {
    search (page = this.pageNum) {
      getLogList(page, this.pageSize, this.queryMap).then(res => {
        this.page = res.data
      }).catch().finally()
    },
    deleteFileOrDirectory (results) {
      const _this = this
      _this.$confirm('批量删除这些系统操作日志, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const ids = results.map(item => {
          return item.id
        })
        batchDelLog(ids).then(() => {
          _this.NormalMsg(_this, '批量删除系统操作日志成功！', 'success')
          _this.search()
        }).catch().finally()
      }).catch(() => {
        _this.$message({
          type: 'info',
          message: '已取消批量删除'
        })
      })
    },
    selsChange (sels) {
      this.sels = sels
    },
    del (id) {
      const _this = this
      _this.$confirm('删除此系统操作日志, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delLog(id).then(() => {
          _this.NormalMsg(_this, '删除系统操作日志成功！', 'success')
          _this.search()
        }).catch().finally()
      }).catch(() => {
        _this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    // 改变页码
    handleSizeChange (newSize) {
      this.pageSize = newSize
      this.search()
    },
    // 翻页
    handleCurrentChange (current) {
      this.pageNum = current
      this.search()
    }
  }
}
</script>

<style scoped>

</style>
