<template>
  <div>
    <!--用户管理面包屑-->
    <el-breadcrumb separator="/" style="padding-left: 10px;padding-bottom: 10px;font-size: 12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统管理</el-breadcrumb-item>
      <el-breadcrumb-item>用户管理</el-breadcrumb-item>
    </el-breadcrumb>
    <!--用户列表卡片-->
    <el-card class="box-card card" id="card">
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item size="mini" label="部门" label-width="70px" style="font-weight: bold">
          <el-select v-model="searchForm.departmentId" placeholder="请选择">
            <el-option
              v-for="item in dncList"
              :key="item.deptName"
              :label="item.deptName"
              :value="item.id">
              <span style="float: left">{{ item.deptName }}</span>
              <span
                class="el-tag el-tag--success el-tag--mini el-tag--light"
                style="float: right; color: #8492a6; font-size: 13px">{{ item.deptCount }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item size="mini" label="用户名" label-width="70px" style="font-weight: bold">
          <el-input clearable v-model="searchForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item size="mini" label="邮箱" label-width="70px" style="font-weight: bold">
          <el-input clearable v-model="searchForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item size="mini" label="用户状态" label-width="70px" style="font-weight: bold">
          <el-radio v-model="searchForm.forbidden" label="0">禁用</el-radio>
          <el-radio v-model="searchForm.forbidden" label="1">启用</el-radio>
          <el-radio v-model="searchForm.forbidden" label="">不限</el-radio>
        </el-form-item>
        <el-form-item size="mini" label="性别" label-width="70px" style="font-weight: bold">
          <el-radio v-model="searchForm.sex" label="0">帅哥</el-radio>
          <el-radio v-model="searchForm.sex" label="1">美女</el-radio>
          <el-radio v-model="searchForm.sex" label="2">保密</el-radio>
          <el-radio v-model="searchForm.sex" label="">全部</el-radio>
        </el-form-item>
        <el-form-item size="mini" label="昵称" label-width="70px" style="font-weight: bold">
          <el-input clearable v-model="searchForm.nikeName" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item size="mini" style="margin-left: 10px;">
          <el-button icon="el-icon-refresh" @click="resetForm">重置</el-button>
          <el-button icon="el-icon-search" @click="onSubmit(1)" type="primary">查询</el-button>
          <el-button icon="el-icon-plus" @click="show" type="success">添加</el-button>
          <el-button icon="el-icon-download" type="warning" @click="exportUserList">导出</el-button>
        </el-form-item>
      </el-form>
      <!--表格内容显示区域-->
      <el-table
        :max-height="this.getTableMaxHeight()"
        v-loading="loading" element-loading-text="数据加载中..."
        :data="page.records"
        border
        size="mini"
        style="width: 100%;">
        <el-table-column
          width="60px"
          align="center"
          type="index"
          label="序号">
        </el-table-column>
        <el-table-column
          width="80px"
          align="center"
          prop="username"
          label="用户名">
        </el-table-column>
        <el-table-column
          width="100px"
          align="center"
          prop="nickname"
          label="昵称">
        </el-table-column>
        <el-table-column
          width="60px"
          align="center"
          prop="sex"
          label="性别">
          <template slot-scope="scope">
            <span class="el-tag el-tag--success el-tag--mini el-tag--light">{{ scope.row.sex }}</span>
          </template>
        </el-table-column>
        <el-table-column
          width="100px"
          align="center"
          sortable
          prop="deptName"
          label="所属部门">
        </el-table-column>
        <el-table-column
          width="110px"
          align="center"
          sortable
          prop="birth"
          label="出生年月">
        </el-table-column>
        <el-table-column
          align="center"
          prop="email"
          label="用户邮箱">
        </el-table-column>
        <el-table-column
          width="110px"
          align="center"
          prop="phoneNumber"
          label="电话号码">
        </el-table-column>
        <el-table-column
          width="70px"
          align="center"
          prop="forbidden"
          label="是否禁用">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.forbidden"
              active-color="#13ce66"
              @change="updateUserStatusClick(scope.row, scope.row.forbidden)">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="操作">
          <template slot-scope="scope">
            <el-tooltip effect="dark" content="编辑用户" placement="top" :enterable="false">
              <el-button @click="show(scope.row.id, true)" size="mini" type="primary" icon="el-icon-edit"
                         circle></el-button>
            </el-tooltip>
            <el-tooltip effect="dark" content="分配角色" placement="top" :enterable="false">
              <el-button @click="assignRole(scope.row.id, scope.row.nickname)" size="mini" type="info" icon="el-icon-s-custom" circle></el-button>
            </el-tooltip>
            <el-tooltip effect="dark" content="重置密码" placement="top" :enterable="false">
              <el-button @click="resetPassword(scope.row.id)" size="mini" type="warning" icon="el-icon-lock"
                         circle></el-button>
            </el-tooltip>
            <el-tooltip effect="dark" content="删除用户" placement="top" :enterable="false">
              <el-button @click="deleteAccount(scope.row.id)" size="mini" type="danger" icon="el-icon-delete"
                         circle></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
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
    <!--用户添加/修改模态框 :userData="isEdit === true ? userData : null"-->
    <AddOrUpdate :addOrUpdateVisible="addOrUpdateVisible"
                 :isEdit="isEdit"
                 :userId="userId"
                 @dataUpdate="init"
                 @changeShow="showAddOrUpdate"
                 ref="addOrUpdateRef">
    </AddOrUpdate>
    <!--用户授予角色弹框-->
    <AuthRole :dlgVisible="authDlgVisible"
              :params="params"
              :userRoleIds="userRoleIds"
              @authChangeShow="authDlgChangeShow"
              ref="authRolesRef">
    </AuthRole>
  </div>
</template>

<script>
import { getUsersPage, searchUserListPage, updateUserStatus, resetPwd, exportList, deleteUser, getRoleIdsByUserId } from '@/api/users'
import { getDeptNameAndCount } from '@/api/departments'
import AddOrUpdate from './AddOrUpdate'
import AuthRole from './AuthRole'

export default {
  name: 'Users',
  data () {
    return {
      searchForm: {
        departmentId: undefined,
        username: '',
        email: '',
        sex: '',
        nikeName: '',
        forbidden: ''
      },
      page: {
        records: [],
        total: 0,
        size: 0,
        current: 0,
        orders: []
      },
      pageNum: 1,
      pageSize: this.getPageSize(),
      dncList: [{
        id: undefined,
        deptName: '',
        deptCount: ''
      }],
      // 控制新增编辑弹窗的显示与隐藏
      addOrUpdateVisible: false,
      isEdit: false,
      userId: 0,
      loading: false,
      userRoleIds: [],
      authDlgVisible: false,
      params: {
        id: null,
        username: ''
      }
    }
  },
  components: {
    AddOrUpdate, AuthRole
  },
  created () {
    this.init()
  },
  methods: {
    init () {
      const _this = this
      _this.loading = true
      getDeptNameAndCount().then(response => {
        _this.dncList = response.data
        /* 创建组件的时候加载页面信息 */
        getUsersPage(_this.pageNum, _this.pageSize).then(response => {
          _this.page = response.data
        })
      }).finally(() => {
        _this.loading = false
      })
    },
    resetPassword (id) {
      this.$confirm('立即重置该用户密码, 是否继续?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const _this = this
        resetPwd(id).then(response => {
          _this.RightMessage(_this, '成功', response.message, 'success')
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消重置密码'
        })
      })
    },
    onSubmit (page = this.pageNum) {
      const _this = this
      searchUserListPage(this.searchForm, page, this.pageSize).then(response => {
        _this.page = response.data
      })
    },
    handleSizeChange (val) {
      /* 将value赋值给size, 重新查询数据 */
      this.pageSize = val
      this.onSubmit()
    },
    handleCurrentChange (val) {
      this.pageNum = val
      this.onSubmit()
    },
    updateUserStatusClick (row, status) {
      const _this = this
      updateUserStatus(row.id, status).then(response => {
        _this.NormalMsg(_this, response.message, !status ? 'success' : 'warning')
      }).catch(() => {
        row.forbidden = !status
      })
    },
    resetForm () {
      this.searchForm.departmentId = ''
      this.searchForm.username = ''
      this.searchForm.email = ''
      this.searchForm.sex = ''
      this.searchForm.nikeName = ''
      this.searchForm.forbidden = ''
      this.pageNum = 1
      this.pageSize = this.getPageSize()
      const _this = this
      getUsersPage(this.pageNum, this.pageSize).then(response => {
        _this.page = response.data
      })
    },
    // 按钮点击事件 显示新增编辑弹窗组件
    show (id, edit) {
      this.addOrUpdateVisible = true
      if (edit) {
        // 如果是编辑框，将绑定的数据传递给子组件
        this.userId = id
      }
      this.isEdit = edit
    },
    // 监听 子组件弹窗关闭后触发，有子组件调用
    showAddOrUpdate (data) {
      this.addOrUpdateVisible = data !== 'false'
    },
    exportUserList () {
      // 导出数据
      this.$confirm('确定导出用户列表吗?', '温馨提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        exportList(this.searchForm).then(res => {
          this.downloadFile(res, '用户信息', 'xls')
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消导出'
        })
      })
    },
    deleteAccount (id) {
      this.$confirm('是否删除该账户信息?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const _this = this
        deleteUser(id).then(response => {
          _this.init()
          _this.RightMessage(_this, '成功', response.message, 'success')
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除账户'
        })
      })
    },
    assignRole (id, name) {
      const _this = this
      _this.userRoleIds = []
      _this.params = {
        id: null,
        username: ''
      }
      getRoleIdsByUserId(id).then(res => {
        _this.userRoleIds = res.data
        _this.params.id = id
        _this.params.username = name
        // 打开角色授予弹框
        _this.authDlgVisible = true
      })
    },
    authDlgChangeShow (value) {
      this.authDlgVisible = value
    }
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight()
  }
}
</script>

<style scoped>
</style>
