<!--
  @author qiukangming
  @description
  @date 2021/02/19 16:25
-->
<template>
  <div>
    <!-- 面包导航 -->
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统管理</el-breadcrumb-item>
      <el-breadcrumb-item>菜单权限</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 主体部分 -->
    <el-card class="box-card card" id="card">
      <div class="block">
        <!-- 节点过滤 -->
        <el-row>
          <el-col :span="12">
            <div class="grid-content bg-purple">
              <el-input placeholder="输入关键字进行过滤" v-model="searchForm.filterText" clearable size="mini"></el-input>
            </div>
          </el-col>
          <el-col :span="3">
            <div class="grid-content bg-purple-light">
              <el-button
                size="mini"
                type="primary"
                icon="el-icon-plus"
                style="margin-left:20px;"
                @click="dialogShow"
              >添加根目录
              </el-button>
            </div>
          </el-col>
          <el-col :span="2">
            <el-button
              size="mini"
              style="margin-left:20px;"
              icon="el-icon-download"
              @click="exportMenuList"
            >导出
            </el-button>
          </el-col>
        </el-row>

        <p>菜单权限树</p>
        <el-tree
          :data="data"
          :auto-expand-parent="false"
          node-key="id"
          show-checkbox
          :default-expanded-keys="open"
          :expand-on-click-node="false"
          :render-content="renderContent"
          :props="defaultProps"
          highlight-current
          :filter-node-method="filterNode"
          ref="tree"
        ></el-tree>
      </div>
    </el-card>
    <!--弹框部分-->
    <AddOrUpdate :showDialog = "dialogVisible"
                 :title="dlgTitle"
                 :data="dialogData"
                 :children-ids="ids"
                 @changeShow="showAddOrUpdate"
                 @dataUpdate="dataUpdate"
                 :isEdit="isEdit">
    </AddOrUpdate>
  </div>
</template>

<script>
import AddOrUpdate from '@/views/system/menu-auth/AddOrUpdate'
import { getMenuAndButton, deleteMenu, exportMenu } from '@/api/menu'

export default {
  name: 'MenuManager',
  components: {
    AddOrUpdate
  },
  data () {
    const data = []
    return {
      btnLoading: false,
      btnDisabled: false,
      dialogVisible: false,
      isEdit: false,
      dlgTitle: '',
      dialogData: {},
      loading: true,
      open: [], // 展开节点
      searchForm: {
        filterText: '' // 节点过滤文本
      },
      editForm: {}, // 编辑节点表单数据
      pNode: {}, // 父节点
      data: JSON.parse(JSON.stringify(data)),
      defaultProps: {
        children: 'children',
        label: 'menuName'
      },
      ids: []
    }
  },
  watch: {
    filterText (val) {
      this.$refs.tree.filter(val)
    }
  },
  created () {
    this.init()
  },
  mounted () {
    const $this = this
    // 获取屏幕高度
    document.getElementById('card').style.height = $this.getWindowHeight()
  },
  activated () {
  },
  methods: {
    dialogShow () {
      this.dlgTitle = '当前添加菜单根路径[系统跟路径]'
      this.dialogVisible = true
      this.isEdit = false
    },
    // 过滤节点
    filterNode (value, data) {
      if (!value) return true
      return data.menuName.indexOf(value) !== -1
    },
    renderContent (h, { node, data }) {
      return (
        <span class="custom-tree-node">
          <span>
            <i class={data.icon}> </i>&nbsp;&nbsp;&nbsp;{node.label}
            {node.data.type === '0' ? <el-tag style='margin-left:20px;' effect='plain' size='mini'>菜单</el-tag>
              : <el-tag style='margin-left:20px;' type='warning' effect='plain' size='mini'>权限 [{node.data.perms}]</el-tag>
            }
          </span>
          <span>
            <el-button size="mini" type="text" on-click={() => { this.addOrEdit(true, data) }}>
              <i class="el-icon-edit"> </i>&nbsp;编辑
            </el-button>
            <el-button size="mini" type="text" on-click={() => { this.addOrEdit(false, data) }}>
              <i class="el-icon-plus"> </i>&nbsp;增加
            </el-button>
            <el-button size="mini" type="text" on-click={() => this.delNode(data.id)}>
              <i class="el-icon-delete"> </i>&nbsp;删除
            </el-button>
          </span>
        </span>
      )
    },
    addOrEdit (isEdit, data) {
      if (!isEdit && data.type === '1') {
        this.NormalMsg(this, '权限按钮不能添加子项', 'warning')
        return
      }
      if (isEdit) {
        this.dlgTitle = '当前编辑资源名称[' + data.menuName + ']'
        // 如果点击的是菜单，获取其子项id列表
        if (data.type === '0') {
          const ids = []
          this.getChildrenIds(data, ids)
          this.ids = ids
        } else if (data.type === '1') {
          this.ids = []
        }
      } else {
        this.dlgTitle = '当前添加菜单根路径[' + data.menuName + ']'
      }
      this.isEdit = isEdit
      this.dialogData = { ...data }
      this.dialogVisible = true
    },
    delNode (id) {
      const _this = this
      _this.$confirm('确认删除该系统菜单/按钮资源吗?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteMenu(id).then(() => {
          _this.NormalMsg(_this, '删除菜单/资源信息成功！', 'success')
          _this.init()
        }).catch()
      })
    },
    init () {
      getMenuAndButton().then((res) => {
        this.data = res.data.menus
        this.open = res.data.ids
      }).finally(() => {})
    },
    // 监听 子组件弹窗关闭后触发，有子组件调用
    showAddOrUpdate (data) {
      this.dialogVisible = data !== 'false'
    },
    dataUpdate () {
      this.init()
      this.dialogVisible = false
    },
    getChildrenIds (data, ids) {
      ids.push(data.id)
      if (data.children.length === 0) {
        return
      }
      for (let i = 0; i < data.children.length; i++) {
        this.getChildrenIds(data.children[i], ids)
      }
    },
    exportMenuList () {
      this.$confirm('确定导出菜单/资源列表吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        exportMenu(this.searchForm).then((res) => {
          this.downloadFile(res, '菜单信息', 'xls')
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消导出'
        })
      })
    }
  }
}
</script>

<style>
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
</style>
