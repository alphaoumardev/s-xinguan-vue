<!--
  @author qiukangming
  @description 分配角色对话框
  @date 2021/03/06 09:41
-->
<template>
  <el-dialog center :title="'为 [ ' + params.username + ' ] 分配角色'" :visible.sync="assignDialogVisible" @open="onOpen" @close="onClose" :close-on-click-modal="false" width="60%">
    <span class="content">
      <template>
        <el-transfer filterable :titles="titles" :button-texts="buttonTexts" v-model="value" :data="roles"></el-transfer>
      </template>
      <div>
        <el-button @click="onClose" class="el-icon-close" size="mini"> 取消分配</el-button>
        <el-button type="primary" size="mini" @click="doAssignRoles" class="el-icon-check" :loading="btnLoading" :disabled="btnLoading"> 分配角色</el-button>
      </div>
    </span>
  </el-dialog>
</template>

<script>
import { getAllRoles } from '@/api/role'
import { assignRoles } from '@/api/users'

export default {
  name: 'AuthRole',
  components: {},
  props: {
    params: {
      type: Object,
      require: true
    },
    dlgVisible: {
      type: Boolean,
      default: false
    },
    userRoleIds: {
      type: Array
    }
  },
  watch: {
    // 监听 dlgVisible 改变
    dlgVisible () {
      this.assignDialogVisible = this.dlgVisible
    }
  },
  data () {
    return {
      assignDialogVisible: false,
      titles: ['未拥有的角色', '已拥有的角色'],
      buttonTexts: ['到左边', '到右边'],
      value: [],
      roles: [],
      btnLoading: false
    }
  },
  created () {
  },
  mounted () {
  },
  activated () {
  },
  methods: {
    doAssignRoles () {
      const _this = this
      _this.btnLoading = true
      assignRoles(_this.params.id, _this.value).then(res => {
        let type = 'error'
        if (res.isSuccess) {
          type = 'success'
        }
        _this.NormalMsg(_this, res.message, type)
        _this.onClose()
      }).finally(() => {})
      _this.btnLoading = false
    },
    onOpen () {
      const _this = this
      _this.value = []
      _this.roles = []
      // 获取所有角色列表
      getAllRoles().then(res => {
        _this.value = _this.userRoleIds
        res.data.forEach(role => {
          _this.roles.push({
            key: role.id,
            label: role.roleName,
            disabled: role.forbidden === 0
          })
        })
      })
    },
    onClose () {
      this.value = []
      this.roles = []
      this.$emit('authChangeShow', false)
    }
  }
}
</script>

<style lang="less" scoped>

.content {
  text-align: center;
  div:nth-child(2) {
    margin-top: 30px;
  }
}
/deep/ .el-transfer-panel__item > .el-checkbox__input {
  left: 35px;
}

/deep/ .el-transfer-panel__header > .el-checkbox > .el-checkbox__label > span {
  display: inline;
  right: 5px;
}

</style>
