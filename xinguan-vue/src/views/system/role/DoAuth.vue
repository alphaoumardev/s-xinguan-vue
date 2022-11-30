<!--
  @author: qiukangming
  @description: 授权组件
  @date: 2021/01/28 10:57
-->
<template>
  <el-dialog :title="'为 [ ' + params.roleName + ' ] 分配菜单权限'" :visible.sync="grantDialogVisible" @open="onOpen" @close="onClose" :close-on-click-modal="false" width="38%">
    <div class="grid-content bg-purple filter">
      <el-input placeholder="输入关键字进行过滤" v-model="filterText" clearable size="mini"></el-input>
    </div>
    <span>
      <el-tree
        :auto-expand-parent="false"
        default-expand-all
        :data="data"
        show-checkbox
        node-key="id"
        :default-expanded-keys="open"
        :props="defaultProps"
        ref="menuTree"
        :render-content="renderContent"
        highlight-current
        check-strictly
        @check-change="handleCheckChange"
        :filter-node-method="filterNode"
      ></el-tree>
    </span>
    <span slot="footer" class="dialog-footer">
      <el-button @click="onClose" class="el-icon-close" size="mini"> 取消分配</el-button>
      <el-button type="primary" icon="el-icon-setting" @click="authority" size="mini" :loading="btnLoading" :disabled="btnDisabled"> 授权菜单</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { getMenuAndButton } from '@/api/menu'
import { getMenuListByRoleId, assignMenus } from '@/api/role'

export default {
  name: 'DoAuth',
  data () {
    return {
      filterText: '',
      grantDialogVisible: false,
      defaultProps: {
        children: 'children',
        label: 'menuName'
      },
      data: [],
      open: [], // 展开
      btnLoading: false,
      btnDisabled: false
    }
  },
  props: {
    params: {
      type: Object,
      require: true
    },
    dlgVisible: {
      type: Boolean,
      default: false
    },
    roleMenuIds: {
      type: Array
    }
  },
  watch: {
    // 监听 dlgVisible 改变
    dlgVisible () {
      this.grantDialogVisible = this.dlgVisible
    },
    filterText (val) {
      this.$refs.menuTree.filter(val)
    }
  },
  methods: {
    onOpen () {
      // 获取菜单
      getMenuAndButton().then((res) => {
        this.data = res.data.menus
        this.open = res.data.ids
        this.btnDisabled = this.data.length === 0
        // 设置选中项
        this.getMenusByRoleId(this.params.id)
      }).finally(() => {})
    },
    onClose () {
      this.filterText = ''
      this.data = []
      this.open = []
      this.$emit('authChangeShow', false)
    },
    // 过滤节点
    filterNode (value, data) {
      if (!value) return true
      return data.menuName.indexOf(value) !== -1
    },
    handleCheckChange (data, checked, indeterminate) {
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
        </span>
      )
    },
    authority () {
      const _this = this
      // getCheckedKeys 获取选中的节点，getHalfCheckedKeys 获取半选中的节点
      const menuIds = [].concat(_this.$refs.menuTree.getCheckedKeys(), _this.$refs.menuTree.getHalfCheckedKeys())
      assignMenus(_this.params.id, menuIds).then(res => {
        let type = 'error'
        if (res.isSuccess) {
          type = 'success'
        }
        _this.NormalMsg(_this, res.message, type)
        _this.onClose()
      })
    },
    getMenusByRoleId (id) {
      const _this = this
      getMenuListByRoleId(id).then(res => {
        const checked = res.data
        checked.forEach(check => {
          const node = _this.$refs.menuTree.getNode(check)
          if (node.isLeaf) {
            _this.$refs.menuTree.setChecked(check, true, false)
          }
        })
      })
    }
  }
}
</script>

<style lang="less" scoped>
.filter {
  margin-bottom: 10px;
}

/deep/ .el-dialog__body {
  padding: 10px 20px;
}
</style>
