<!--
  @author: qiukangming
  @description: 角色添加或者编辑弹框组件
  @date: 2021/01/28 10:57
-->
<template>
  <el-dialog :title="isEdit ? '编辑角色信息' : '添加角色信息'"
             :visible.sync="showDialogVisible" @open="onOpen" @close="onClose" :close-on-click-modal="false">
    <el-form ref="formRoleRef" :model="formData" label-width="95px" :rules="formRoles">
      <el-form-item label="角色标识码" prop="roleCode">
        <el-input size="mini" v-model="formData.roleCode" :disabled="isEdit" placeholder="标识码提交后不可修改"></el-input>
      </el-form-item>
      <el-form-item label="角色名称" prop="roleName">
        <el-input size="mini" v-model="formData.roleName"></el-input>
      </el-form-item>
      <el-form-item label="描述信息" prop="remark">
        <el-input size="mini" type="textarea" v-model="formData.remark"></el-input>
      </el-form-item>
      <el-form-item label="是否启用" prop="forbidden">
        <el-radio-group size="mini" v-model="formData.forbidden">
          <el-radio-button label="0">禁用</el-radio-button>
          <el-radio-button label="1">启用</el-radio-button>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <div slot="footer">
      <el-button @click="onClose" type="danger" icon="el-icon-close" size="mini">取 消</el-button>
      <el-button @click="handelConfirm" type="success" icon="el-icon-upload2" size="mini" :disabled="isSubmit">提 交</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { addRole, updateRole } from '@/api/role'

export default {
  name: 'AddOrUpdate',
  props: {
    isEdit: {
      type: Boolean,
      default: false
    },
    data: {
      type: Object
    },
    showDialog: {
      require: true,
      type: Boolean,
      default: false
    }
  },
  watch: {
    // 监听 showDialog 改变
    showDialog () {
      this.showDialogVisible = this.showDialog
    }
  },
  data () {
    return {
      showDialogVisible: false,
      formData: {
        id: '',
        roleName: '',
        roleCode: '',
        remark: '',
        forbidden: null
      },
      formRoles: {
        roleCode: [
          { required: true, message: '请输入角色标识码', trigger: 'blur' },
          { min: 4, max: 12, message: '长度在 4 到 12 个字符', trigger: 'blur' }
        ],
        roleName: [
          { required: true, message: '请输入角色名称', trigger: 'blur' }
        ],
        forbidden: [
          { required: true, message: '请选择角色状态', trigger: 'blur' }
        ]
      },
      isSubmit: false
    }
  },
  methods: {
    onOpen () {
      if (this.isEdit) {
        this.formData = this.data
      }
    },
    onClose () {
      // 清除 rule 数据
      this.$refs.formRoleRef.clearValidate()
      // 清除form数据
      this.formData = {}
      this.$emit('changeShow', 'false')
    },
    handelConfirm () {
      this.$refs.formRoleRef.validate(valid => {
        // 验证不通过
        if (!valid) return
        const _this = this
        _this.isSubmit = true
        const func = _this.isEdit ? updateRole : addRole
        func(_this.formData).then(res => {
          _this.NormalMsg(_this, res.message, 'success')
          _this.onClose()
          _this.$emit('dataUpdate')
        }).finally(() => {
          _this.isSubmit = false
        })
      })
    }
  }
}
</script>

<style scoped>

</style>
