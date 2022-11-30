<!--
  @author qiukangming
  @description
  @date 2021/02/24 23:01
-->
<template>
  <el-dialog :title="title" :visible.sync="showDialogVisible" @open="onOpen" @close="dlgClose" width="50%" :close-on-click-modal="false">
    <span>
      <el-form size="mini" ref="menuFormRef" :model="formData" label-width="80px" :rules="formRules">
          <el-form-item label="节点名称" prop="menuName">
            <el-input v-model="formData.menuName"></el-input>
          </el-form-item>
          <el-form-item label="URL">
            <el-input v-model="formData.url" :disabled="isBtn"></el-input>
          </el-form-item>
          <el-form-item label="权限编码">
            <el-input v-model="formData.perms"></el-input>
          </el-form-item>
          <el-form-item label="图标">
            <el-input v-model="formData.icon"></el-input>
          </el-form-item>
          <el-form-item label="是否可用" prop="disabled">
            <template>
              <el-radio v-model="formData.disabled" :label="false">可用</el-radio>
              <el-radio v-model="formData.disabled" :label="true">禁用</el-radio>
            </template>
          </el-form-item>
          <el-form-item label="是否展开" prop="open">
            <template>
              <el-radio v-model="formData.open" :label="1">展开</el-radio>
              <el-radio v-model="formData.open" :label="0">关闭</el-radio>
            </template>
          </el-form-item>
          <el-form-item label="排序" prop="orderNum">
            <el-input-number v-model="formData.orderNum" :min="1" :max="10" label="描述文字"></el-input-number>
          </el-form-item>
          <el-form-item label="类型" prop="type">
            <template>
              <el-radio v-model="formData.type" label="0">菜单</el-radio>
              <el-radio v-model="formData.type" label="1">按钮</el-radio>
            </template>
          </el-form-item>
        </el-form>
    </span>
    <span slot="footer" class="dialog-footer">
      <el-button @click="dlgClose" size="mini">取 消</el-button>
      <el-button type="primary" @click="submit" :disabled="isSubmit" size="mini">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateMenu, submitMenu } from '@/api/menu'

export default {
  name: 'AddOrUpdate',
  components: {},
  props: {
    isEdit: {
      require: true,
      type: Boolean,
      default: false
    },
    title: {
      type: String
    },
    data: {
      type: Object
    },
    showDialog: {
      require: true,
      type: Boolean,
      default: false
    },
    childrenIds: {
      type: Array,
      require: false
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
      // 添加请求表单数据
      formData: {
        parentId: '',
        menuName: '',
        url: '',
        type: '',
        orderNum: '',
        disabled: '',
        open: '',
        perms: ''
      },
      formRules: {
        menuName: [
          { required: true, message: '节点名称不能为空', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
        ],
        disabled: [
          { required: true, message: '节点状态不能为空', trigger: 'blur' }
        ],

        orderNum: [
          { required: true, message: '排序不能为空', trigger: 'blur' }
        ],
        type: [{ required: true, message: '类型不能为空', trigger: 'blur' }],
        open: [{ required: true, message: '是否展开不能为空', trigger: 'blur' }]
      }, // 添加表单验证规则
      isSubmit: false,
      isBtn: false,
      ids: []
    }
  },
  methods: {
    onOpen () {
      if (this.isEdit) {
        this.formData = this.data
        this.ids = this.childrenIds
        this.isBtn = this.formData.type === '1'
      }
    },
    dlgClose () {
      // 清除 rule 数据
      this.$refs.menuFormRef.clearValidate()
      // 清除form数据
      this.formData = {
        parentId: '',
        menuName: '',
        url: '',
        type: '',
        orderNum: '',
        disabled: '',
        open: '',
        perms: ''
      }
      this.ids = []
      this.$emit('changeShow', 'false')
    },
    submit () {
      const func = this.isEdit ? updateMenu : submitMenu
      if (!this.isEdit) {
        // 填充父级ID
        this.formData.parentId = this.data.id
      }
      this.formData.ids = this.ids
      func(this.formData).then(() => {
        const str = this.isEdit ? '修改' : '添加'
        this.NormalMsg(this, str + '菜单/资源信息成功！', 'success')
        this.$emit('dataUpdate')
      }).catch()
    }
  }
}
</script>

<style scoped>

</style>
