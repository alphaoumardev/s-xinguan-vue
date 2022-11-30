<!--
  @author: qiukangming
  @description: 角色管理，用于角色授权，角色修改
  @date: 2021/01/28 09:56
-->
<template>
  <div>
    <!-- 面包导航 -->
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统管理</el-breadcrumb-item>
      <el-breadcrumb-item>角色管理</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 卡片主体 -->
    <el-card class="card" id="card">
      <!-- 上面搜索工具栏 -->
      <el-row :gutter="20">
        <el-col :span="4">
          <el-input clearable size="mini" placeholder="请输入角色名查询" v-model="searchForm.roleName"></el-input>
        </el-col>
        <el-col :span="2">
          <el-button size="mini" type="primary" icon="el-icon-search" @click="findRoleList(1)">查询</el-button>
        </el-col>
        <el-col :span="2">
          <el-button size="mini" type="success" icon="el-icon-plus" @click="addRole">新增</el-button>
        </el-col>
        <el-col :span="2">
          <el-button size="mini" icon="el-icon-download" type="warning" @click="exportRoleList">导出</el-button>
        </el-col>
      </el-row>
      <!-- 表格区域 -->
      <template>
        <el-table v-loading="loading" element-loading-text="数据加载中..." :data="page.records" border
                  style="width: 100%;margin-top:20px;" :max-height="this.getTableMaxHeight()" size="mini">
          <el-table-column width="60px" align="center" type="index" label="序号"></el-table-column>
          <el-table-column prop="roleCode" align="center" label="角色标识码" width="180"></el-table-column>
          <el-table-column prop="roleName" align="center" label="角色名" width="180"></el-table-column>
          <el-table-column prop="createTime" align="center" label="创建时间" width="150"></el-table-column>
          <el-table-column prop="forbidden" align="center" label="是否启用" width="100">
            <template slot-scope="scope">
              <el-switch :inactive-value="0" :active-value="1" v-model="scope.row.forbidden" active-color="#13ce66"
                         @change="updateRoleStatusClick(scope.row, scope.row.forbidden)"></el-switch>
            </template>
          </el-table-column>
          <el-table-column prop="remark" align="center" label="备注"></el-table-column>
          <el-table-column fixed="right" align="center" label="操作" width="200">
            <template slot-scope="scope">
              <el-button @click="grantAuthority(scope.row.id, scope.row.roleName)" type="text" icon="el-icon-present" size="small">授权
              </el-button>
              <el-button @click="edit(scope.row)" type="text" icon="el-icon-edit" size="small">编辑</el-button>
              <el-popconfirm
                confirm-button-text='必须的'
                cancel-button-text='不删了'
                icon="el-icon-info"
                icon-color="red"
                title="确定删除该角色吗?"
                @confirm="del(scope.row.id)">
                <el-button type="text" size="small" icon="el-icon-delete" slot="reference" style="margin-left: 10px">删除</el-button>
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
    <AddOrUpdate
      :showDialog="dialogVisible"
      :data="dialogData"
      @changeShow="showAddOrUpdate"
      @dataUpdate="init"
      :isEdit="isEdit">
    </AddOrUpdate>
    <!--角色分配菜单弹框-->
    <DoAuth
      :params="params"
      :dlgVisible="authDlgVisible"
      @authChangeShow="changeAuthVisible"
    >
    </DoAuth>
  </div>
</template>

<script>
import { getRoleList, delRole, exportRole, updateRoleStatus } from '@/api/role'
import AddOrUpdate from '@/views/system/role/AddOrUpdate'
import DoAuth from '@/views/system/role/DoAuth'

export default {
  name: 'Role',
  components: { AddOrUpdate, DoAuth },
  data () {
    return {
      // 控制添加/编辑角色 弹框
      dialogVisible: false,
      searchForm: {
        page: 1,
        size: this.getPageSize(),
        roleName: ''
      },
      page: {
        records: [],
        total: 0,
        size: 0,
        current: 0,
        orders: []
      },
      pageNum: '1',
      pageSize: '10',
      loading: false,
      isEdit: false,
      dialogData: {},
      authDlgVisible: false,
      params: {
        id: null,
        roleName: ''
      }
    }
  },
  methods: {
    init () {
      this.findRoleList()
    },
    findRoleList (page = this.searchForm.page) {
      this.loading = true
      this.searchForm.page = page
      getRoleList(this.searchForm).then(res => {
        this.page = res.data
      }).finally(() => {
        this.loading = false
      })
    },
    exportRoleList () {
      this.$confirm('确定导出角色列表吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        exportRole(this.searchForm).then((res) => {
          this.downloadFile(res, '角色信息', 'xls')
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消导出'
        })
      })
    },
    grantAuthority (id, name) {
      // 根据 ID 查询出权限树信息，渲染
      this.params.id = id
      this.params.roleName = name
      this.authDlgVisible = true
    },
    addRole () {
      this.dialogVisible = true
      this.isEdit = false
    },
    edit (data) {
      this.isEdit = true
      this.dialogData = JSON.parse(JSON.stringify(data))
      this.dialogVisible = true
    },
    del (id) {
      delRole(id).then(response => {
        this.NormalMsg(this, response.message, 'success')
        this.findRoleList()
      })
    },
    handleSizeChange (val) {
      /* 将value赋值给size, 重新查询数据 */
      this.searchForm.size = val
      this.findRoleList()
    },
    handleCurrentChange (val) {
      this.searchForm.page = val
      this.findRoleList()
    },
    showAddOrUpdate (data) {
      this.dialogVisible = data !== 'false'
    },
    updateRoleStatusClick (row, status) {
      const _this = this
      updateRoleStatus(row.id, status).then(response => {
        _this.NormalMsg(_this, response.message, !status ? 'warning' : 'success')
      }).catch(() => {
        if (status) {
          row.forbidden = 0
        } else {
          row.forbidden = 1
        }
      })
    },
    changeAuthVisible (value) {
      this.authDlgVisible = value
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
