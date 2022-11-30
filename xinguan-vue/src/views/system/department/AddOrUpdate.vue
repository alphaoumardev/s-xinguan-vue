<!--
  @author: qiukangming
  @description:
  @date: 2021/01/25 13:55
-->
<template>
  <el-dialog :title="isEdit ? '编辑部门信息' : '添加部门信息'"
             :visible.sync="showDialogVisible"
             @open="onOpen" @close="onClose" :close-on-click-modal="false">
    <el-form
        :model="formData" :rules="formRules" ref="deptForm" label-width="100px" class="demo-ruleForm">
      <el-form-item label="部门名称" prop="name">
        <el-input v-model="formData.name"></el-input>
      </el-form-item>
      <el-form-item label="办公电话" prop="phone">
        <el-input v-model="formData.phone"></el-input>
      </el-form-item>
      <el-form-item label="办公地址" prop="address">
        <el-input type="textarea" v-model="formData.address"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer">
      <el-button @click="onClose" type="danger" icon="el-icon-close" size="small">取 消</el-button>
      <el-button @click="handelConfirm" type="success" icon="el-icon-upload2" size="small" :disabled="isSubmit">提 交</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { addDepartment, updateDepartment } from '@/api/departments'

export default {
  name: 'AddOrUpdate',
  props: {
    isEdit: {
      require: true,
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
  data () {
    return {
      formData: {
        id: undefined,
        name: '',
        phone: '',
        address: ''
      },
      formRules: {
        name: [
          { required: true, message: '请输入部门名称', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入办公地址', trigger: 'blur' },
          { min: 4, max: 12, message: '长度在 4 到 12 个字符', trigger: 'blur' }
        ],
        phone: [
          {
            required: true,
            validator: this.checkPhone,
            trigger: 'blur'
          }
        ]
      },
      showDialogVisible: false,
      loading: true,
      isSubmit: false
    }
  },
  watch: {
    // 监听 showDialog 改变
    showDialog () {
      this.showDialogVisible = this.showDialog
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
      this.$refs.deptForm.clearValidate()
      // 清除form数据
      this.formData = {}
      this.$emit('changeShow', 'false')
    },
    handelConfirm () {
      this.$refs.deptForm.validate(valid => {
        // 验证不通过
        if (!valid) return
        const _this = this
        _this.isSubmit = true
        const func = _this.isEdit ? updateDepartment : addDepartment
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
