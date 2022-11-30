<template>
  <div>
    <el-dialog v-bind="$attrs" v-on="$listeners"
               @open="onOpen" @close="onClose"
               :title="isEdit ? (isSelf ? '个人信息-编辑' : '编辑用户') : '添加用户'" :visible.sync="showDialog"
               :close-on-click-modal="false">
      <el-row :gutter="0">
        <el-form ref="UserForm" :model="formData" :rules="rules" size="small" label-width="98px">
          <el-col :span="12">
            <el-form-item label="头像">
              <el-upload
                  accept="image/*"
                  class="avatar-uploader"
                  :action="fileUpload"
                  :auto-upload="true"
                  :headers="headerObject"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload">
                <img v-if="imageUrl" :src="imageUrl" class="avatar" alt="暂无">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
            </el-form-item>
            <el-form-item label="用户名" prop="username">
              <el-input v-model="formData.username" placeholder="请输入用户名(字母或数字)" :maxlength="6" show-word-limit
                        clearable prefix-icon='el-icon-user-solid' :style="{width: '100%'}"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门" prop="departmentId">
              <el-select v-model="formData.departmentId" placeholder="请选择部门"
                         :style="{width: '100%'}">
                <el-option v-for="item in departments" :key="item.deptName" :label="item.deptName"
                           :value="item.id">
                  <span style="float: left">{{ item.deptName }}</span>
                  <span
                      class="el-tag el-tag--success el-tag--mini el-tag--light"
                      style="float: right; color: #8492a6; font-size: 13px">{{ item.deptCount }}</span>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="formData.nickname" placeholder="请输入昵称" clearable prefix-icon='el-icon-user'
                        :style="{width: '100%'}" maxlength="6" show-word-limit></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="formData.sex" size="medium">
                <el-radio v-for="(item, index) in sexOptions" :key="index" :label="item.value"
                          :disabled="item.disabled">{{ item.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" clearable prefix-icon='el-icon-message'
                        :style="{width: '100%'}"></el-input>
            </el-form-item>
            <el-form-item label="手机" prop="phoneNumber">
              <el-input v-model="formData.phoneNumber" placeholder="请输入手机" clearable
                        prefix-icon='el-icon-mobile-phone' :style="{width: '100%'}"></el-input>
            </el-form-item>
            <el-form-item label="生日" prop="birth">
              <el-date-picker v-model="formData.birth" format="yyyy-MM-dd" value-format="yyyy-MM-dd"
                              :style="{width: '100%'}" placeholder="请选择生日" clearable></el-date-picker>
            </el-form-item>
          </el-col>
        </el-form>
      </el-row>
      <div slot="footer">
        <el-button @click="onClose" type="danger" icon="el-icon-close" size="small">取 消</el-button>
        <el-button @click="handelConfirm" type="success" icon="el-icon-upload2" size="small" :disabled="isSubmit">提 交</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { addUser, getUserById, editUser } from '@/api/users'
import { getDeptNameAndCount } from '@/api/departments'

export default {
  name: 'AddOrUpdate',
  data () {
    // 数据
    return {
      formData: {
        username: undefined,
        nickname: undefined,
        email: undefined,
        avatar: '',
        phoneNumber: undefined,
        sex: 2,
        birth: null,
        departmentId: undefined
      },
      rules: {
        username: [{
          required: true,
          message: '请输入用户名',
          trigger: 'blur'
        }],
        departmentId: [{
          required: true,
          message: '请选择部门',
          trigger: 'blur'
        }],
        nickname: [{
          required: true,
          message: '请输入昵称',
          trigger: 'blur'
        }],
        sex: [{
          required: true,
          message: '性别不能为空',
          trigger: 'blur'
        }],
        email: [{
          required: true,
          validator: this.checkEmail,
          trigger: 'blur'
        }],
        phoneNumber: [{
          required: true,
          validator: this.checkPhone,
          trigger: 'blur'
        }],
        birth: [{
          required: true,
          message: '请选择生日',
          trigger: 'blur'
        }]
      },
      departments: [],
      sexOptions: [{ label: '帅哥', value: 0 }, { label: '美女', value: 1 }, { label: '保密', value: 2 }],
      showDialog: false,
      imageUrl: require('@/assets/img/default.jpg'),
      fileUpload: '',
      isSubmit: false,
      headerObject: {
        Authorization: window.sessionStorage.getItem('token')
      } // 图片上传请求头
    }
  },
  props: {
    addOrUpdateVisible: {
      type: Boolean,
      default: false
    },
    userId: {
      type: Number,
      default: 0
    },
    isEdit: {
      type: Boolean,
      default: false
    },
    isSelf: {
      type: Boolean,
      default: false
    }
  },
  computed: {},
  watch: {
    // 监听 addOrUpdateVisible 改变
    addOrUpdateVisible () {
      this.showDialog = this.addOrUpdateVisible
      this.departments = this.$parent.$data.dncList
    }
  },
  methods: {
    onOpen () {
      this.fileUpload = process.env.VUE_APP_UPLOAD_URL
      if (this.isEdit) {
        // 根据userId查找用户信息
        const _this = this
        getUserById(this.userId).then(response => {
          // 获取到用户信息，填充至各个字段的值当中去
          _this.formData = response.data
          if (_this.formData.avatar !== '') {
            _this.imageUrl = _this.formData.avatar
          }
        })
        // 判断是否是自己信息
        if (this.isSelf) {
          getDeptNameAndCount().then(response => {
            this.departments = response.data
          })
        }
      } else {
        // 不是编辑框，将 form 表单信息清除
        this.resetFormData()
      }
    },
    onClose () {
      this.imageUrl = require('@/assets/img/default.jpg')
      // 清除 rule 数据
      this.$refs.UserForm.clearValidate()
      this.$emit('changeShow', 'false')
    },
    handelConfirm () {
      this.$refs.UserForm.validate(valid => {
        // 验证不通过
        if (!valid) return
        const _this = this
        _this.isSubmit = true
        // 判断是新增还是修改
        if (!_this.isEdit) {
          addUser(_this.formData).then(response => {
            _this.RightMessage(_this, '成功', response.message, 'success')
            _this.$emit('update:visible', false)
            _this.$emit('changeShow', 'false')
            _this.$emit('dataUpdate')
          }).finally(() => {
            _this.isSubmit = false
          })
        } else {
          editUser(_this.formData, _this.userId).then(response => {
            // 如果是当前登录用户，需要更新 sessionStorage 的数据
            if (_this.isSelf) {
              window.sessionStorage.setItem('account', JSON.stringify(_this.formData))
              _this.RightMessage(_this, '成功', response.message + '，请刷新页面！', 'success')
            } else {
              _this.RightMessage(_this, '成功', response.message, 'success')
            }
            _this.$emit('update:visible', false)
            _this.$emit('changeShow', 'false')
            _this.$emit('dataUpdate')
          }).finally(() => {
            _this.isSubmit = false
          })
        }
      })
    },
    beforeAvatarUpload (file) {
      const isJPG = file.type === 'image/jpeg'
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    },
    resetFormData () {
      this.formData.username = undefined
      this.formData.nickname = undefined
      this.formData.email = undefined
      this.formData.avatar = ''
      this.formData.phoneNumber = undefined
      this.formData.sex = 2
      this.formData.birth = null
      this.formData.departmentId = undefined
    },
    handleAvatarSuccess (res, file) {
      if (res.code === 200) {
        this.imageUrl = URL.createObjectURL(file.raw)
        this.formData.avatar = res.data
      } else {
        this.$message.error(res.message)
      }
    }
  }
}
</script>
<style>

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
