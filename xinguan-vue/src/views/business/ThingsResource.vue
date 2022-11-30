<!--
  @author qiukangming
  @description 物资资料
  @date 2021/03/27 18:19
-->
<template>
  <div>
    <!-- 面包导航 -->
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>物资管理</el-breadcrumb-item>
      <el-breadcrumb-item>物资资料</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card id="card">
<!--      <el-container style="margin-bottom:20px;">-->
<!--        <el-alert show-icon title="友情提示:  商品的分类只支持三级分类" type="warning"></el-alert>-->
<!--      </el-container>-->
      <el-row :gutter="10">
        <el-col :span="6">
          <el-cascader size="mini" placeholder="请选择分类查询" :change-on-select="true" @change="selectChange" style="width: 250px"
            v-model="categorykeys" :props="searchSelectProps" :options="cateories" clearable>
          </el-cascader>
        </el-col>
        <el-col :span="4">
          <el-input clearable size="mini" v-model="queryMap.name" placeholder="请输入物资名称查询" @clear="search(1)" class="input-with-select"></el-input>
        </el-col>
        <el-col :span="4">
          <el-input clearable size="mini" v-model="queryMap.pNum" placeholder="请输入物资编号查询" @clear="search(1)" class="input-with-select"></el-input>
        </el-col>
        <el-col :span="3">
          <template>
            <el-select size="mini" v-model="queryMap.status" clearable @click="search(1)" placeholder="请选择状态">
              <el-option label="正常" :value="0"></el-option>
              <el-option label="回收站" :value="1"></el-option>
              <el-option label="待审核" :value="2"></el-option>
            </el-select>
          </template>
        </el-col>
        <el-col :span="7">
          <el-button size="mini" type="primary" icon="el-icon-search" @click="search(1)">查找</el-button>
          <el-button size="mini" icon="el-icon-refresh-right" type="warning" @click="resetForm">重置</el-button>
          <el-button size="mini" type="success" icon="el-icon-circle-plus-outline" @click="openAdd">添加</el-button>
        </el-col>
      </el-row>
      <!-- 表格区域 -->
      <el-table size="mini" v-loading="loading" border stripe :data="page.records" style="width: 100%;margin-top:20px;" :max-height="this.getTableMaxHeight()">
<!--        <el-table-column align="center" prop="id" type="index" label="ID"></el-table-column>-->
        <el-table-column align="center" prop="pnum" :show-overflow-tooltip='true' label="物资编号"></el-table-column>
        <el-table-column align="center" prop="name" label="物资名称" show-overflow-tooltip></el-table-column>
        <el-table-column align="center" prop="imageUrl" label="图片">
          <template slot-scope="scope">
            <el-popover placement="right" trigger="hover">
              <img :src="scope.row.imageUrl" style="height: 200px;width: 200px" alt="alt"/>
              <img slot="reference" :src="scope.row.imageUrl" alt="alt" style="height: 38px;width: 38px;cursor: pointer">
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column align="center" label="物资规格">
          <template slot-scope="scope">
            <el-tag type="success" size="mini" closable v-text="scope.row.model"></el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="unit" label="单位"></el-table-column>
        <el-table-column align="center" prop="remark" label="备注" show-overflow-tooltip></el-table-column>
        <el-table-column align="center" prop="status" label="状态">
          <template slot-scope="scope">
            <el-tag size="mini" type="danger" effect="plain" v-if="scope.row.status === 1">回收</el-tag>
            <el-tag size="mini" effect="plain" v-if="scope.row.status === 0">正常</el-tag>
            <el-tag size="mini" effect="plain" type="warning" v-if="scope.row.status === 2">审核中</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="modifiedTime" label="更新时间" show-overflow-tooltip sortable></el-table-column>
        <el-table-column label="操作" align="center" width="140">
          <template slot-scope="scope">
            <span v-if="scope.row.status === 1">
              <el-popconfirm @confirm="back(scope.row.id)" confirmButtonText='好的' cancelButtonText='不用了' icon="el-icon-info" iconColor="green" title="确定恢复吗？">
                <el-button type="text" size="mini" slot="reference" icon="el-icon-back">恢复</el-button>
              </el-popconfirm>
              <el-popconfirm @confirm="del(scope.row.id)" confirmButtonText='好的' cancelButtonText='不用了' icon="el-icon-info" iconColor="green" title="确定删除吗？">
                <el-button style="margin-left:10px;" slot="reference" type="text" size="mini" icon="el-icon-delete">删除</el-button>
              </el-popconfirm>
            </span>
            <span v-if="scope.row.status === 0">
              <el-button type="text" size="mini" icon="el-icon-edit" @click="edit(scope.row.id)">编辑</el-button>
              <el-popconfirm @confirm="remove(scope.row.id)" confirmButtonText='好的' cancelButtonText='不用了' icon="el-icon-info" iconColor="red" title="确定移入回收站吗？">
                <el-button style="margin-left:10px;" type="text" slot="reference" size="mini" icon="el-icon-s-operation">回收站</el-button>
              </el-popconfirm>
              </span>
            <span v-if="scope.row.status === 2">
              <el-popconfirm @confirm="publish(scope.row.id)" confirmButtonText='是的' cancelButtonText='不用了' icon="el-icon-info" iconColor="blue" title="确定审核通过吗？">
                <el-button type="text" slot="reference" size="mini" icon="el-icon-s-operation">通过</el-button>
              </el-popconfirm>
              <el-popconfirm @confirm="del(scope.row.id)" confirmButtonText='好的' cancelButtonText='不用了' icon="el-icon-info" iconColor="green" title="确定删除吗？">
                <el-button style="margin-left:10px;" slot="reference" type="text" size="mini" icon="el-icon-delete">删除</el-button>
              </el-popconfirm>
            </span>
          </template>
        </el-table-column>
      </el-table>
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

      <!-- 物资添加弹出框 -->
      <el-dialog title="添加物资" :visible.sync="addDialogVisible" @close="closeAddDialog">
        <span>
          <el-form size="mini" :model="addRuleForm" :rules="addRules" ref="addRuleFormRef" label-width="100px" class="demo-ruleForm">
            <el-form-item label="物资名称" prop="name">
              <el-input v-model="addRuleForm.name"></el-input>
            </el-form-item>
            <el-form-item label="物资图片" prop="imageUrl" style="height: 180px">
              <!-- 图片上传部分 -->
              <el-upload
                style="height: 145px; width: 145px"
                class="avatar-uploader"
                :action="fileUpload"
                list-type="picture-card"
                :limit="limitCount"
                :auto-upload="true"
                :show-file-list="false"
                accept="image/*"
                :on-success="handleSuccess"
                :headers="headerObject"
                ref="upload"
                :on-preview="handlePictureCardPreview">
                <img v-if="addImageUrl" :src="addImageUrl" class="avatar" alt="暂无">
                <i v-else class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
            <el-row>
              <el-col :span="12">
                <div class="grid-content bg-purple-light">
                  <el-form-item label="物资规格" prop="model">
                    <el-input v-model="addRuleForm.model"></el-input>
                  </el-form-item>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="grid-content bg-purple">
                  <el-form-item label="分类" prop="categoryKeys">
                    <el-cascader :options="cateories" clearable filterable :props="selectProps" v-model="addRuleForm.categoryKeys" placeholder="至少需要选择三级分类"></el-cascader>
                  </el-form-item>
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <div class="grid-content bg-purple">
                  <el-form-item label="单位" prop="unit">
                    <el-input v-model="addRuleForm.unit"></el-input>
                  </el-form-item>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="grid-content bg-purple-light">
                  <el-form-item label="排序" prop="sort">
                    <el-input-number v-model="addRuleForm.sort" :min="1" :max="10" label="排序"></el-input-number>
                  </el-form-item>
                </div>
              </el-col>
            </el-row>
            <el-form-item label="备注信息" prop="remark">
              <el-input type="textarea" v-model="addRuleForm.remark"></el-input>
            </el-form-item>
          </el-form>
        </span>
        <span slot="footer" class="dialog-footer">
          <el-button size="mini" @click="addDialogVisible = false">取 消</el-button>
          <el-button size="mini" type="primary" @click="add" :disabled="false" :loading="false">确 定</el-button>
        </span>
      </el-dialog>

      <!-- 物资编辑弹出框 -->
      <el-dialog title="更新物资" :visible.sync="editDialogVisible" @close="closeEditDialog">
        <span>
          <el-form size="mini" :model="editRuleForm" :rules="addRules" ref="editRuleFormRef" label-width="100px" class="demo-ruleForm">
            <el-form-item label="物资名称" prop="name">
              <el-input v-model="editRuleForm.name"></el-input>
            </el-form-item>
            <el-form-item label="物资图片" prop="imageUrl" style="height: 180px">
              <!-- 图片上传部分 -->
              <el-upload
                class="avatar-uploader"
                :action="fileUpload"
                list-type="picture-card"
                :limit="limitCount"
                :auto-upload="true"
                :show-file-list="false"
                accept="image/*"
                :on-success="handleEditSuccess"
                :headers="headerObject"
                ref="upload2"
                :on-preview="handlePictureCardPreview">
                <img v-if="editImageUrl" :src="editImageUrl" class="avatar" alt="暂无">
                <i v-else class="el-icon-plus"></i>
              </el-upload>
            </el-form-item>
            <el-row>
              <el-col :span="12">
                <el-form-item label="物资规格" prop="model">
                  <el-input v-model="editRuleForm.model"></el-input>
                </el-form-item>
              </el-col>

              <el-col :span="12">
                <div class="grid-content bg-purple">
                  <el-form-item label="分类" prop="categoryKeys">
                    <el-cascader style="width: 250px" :options="cateories" clearable filterable :props="selectProps" v-model="editRuleForm.categoryKeys"></el-cascader>
                  </el-form-item>
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <div class="grid-content bg-purple">
                  <el-form-item label="单位" prop="unit">
                    <el-input v-model="editRuleForm.unit"></el-input>
                  </el-form-item>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="grid-content bg-purple-light">
                  <el-form-item label="排序" prop="sort">
                    <el-input-number v-model="editRuleForm.sort" :min="1" :max="10" label="排序"></el-input-number>
                  </el-form-item>
                </div>
              </el-col>
            </el-row>
            <el-form-item label="备注信息" prop="remark">
              <el-input type="textarea" v-model="editRuleForm.remark"></el-input>
            </el-form-item>
          </el-form>
        </span>
        <span slot="footer" class="dialog-footer">
          <el-button size="mini" @click="editDialogVisible = false">取 消</el-button>
          <el-button size="mini" type="primary" @click="update" :disabled="false" :loading="false">确 定</el-button>
        </span>
      </el-dialog>
      <!-- 图片预览 -->
      <el-dialog :visible.sync="dialogVisible">
        <img width="100%" :src="dialogImageUrl" alt/>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { findProductList, backRecord, remove, delProduct, findById, update, publishRecord, add } from '@/api/product'
import { categoryTree } from '@/api/product-category'

export default {
  name: 'ThingsResource',
  components: {},
  data () {
    return {
      categorykeys: [], // 搜索类别
      searchSelectProps: {
        expandTrigger: 'hover',
        value: 'id',
        label: 'name',
        children: 'children',
        checkStrictly: true
      }, // 级联搜索选择器配置项
      selectProps: {
        expandTrigger: 'hover',
        value: 'id',
        label: 'name',
        children: 'children',
        checkStrictly: false
      }, // 级联选择器配置项
      cateories: [], // 类别选择
      queryMap: {},
      pageNum: 1,
      pageSize: this.getPageSize(),
      page: {
        records: [],
        total: 0,
        size: 0,
        current: 0,
        orders: []
      },
      addDialogVisible: false,
      editDialogVisible: false,
      loading: false,
      productData: [],
      addImageUrl: '',
      addRuleForm: {},
      headerObject: {
        Authorization: window.sessionStorage.getItem('token')
      }, // 图片上传请求头
      fileUpload: process.env.VUE_APP_UPLOAD_URL,
      addRules: {
        name: [
          { required: true, message: '请输入物资名称', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        unit: [
          { required: true, message: '请输入物资单位', trigger: 'blur' },
          { min: 1, max: 10, message: '长度在 1 到 10 个字符', trigger: 'blur' }
        ],
        model: [
          { required: true, message: '请输入物资规格', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        remark: [
          { required: true, message: '请输入物资说明备注', trigger: 'blur' },
          { min: 10, message: '长度大于 10 个字符', trigger: 'blur' }
        ],
        categorys: [
          { required: true, message: '请输入物资分类', trigger: 'blur' }
        ],
        sort: [{ required: true, message: '请输入排序信息', trigger: 'blur' }],
        categoryKeys: [
          { required: true, message: '请选择物资分类', trigger: 'blur' }
        ]
      },
      limitCount: 1,
      editRuleForm: {}, // 修改表单数据
      editImageUrl: '',
      imgFilesList: [],
      dialogVisible: false,
      dialogImageUrl: null
    }
  },
  created () {
    this.getCatetorys()
    this.getTableData()
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight()
  },
  activated () {
  },
  methods: {
    getCatetorys () {
      categoryTree().then(res => {
        this.cateories = res.data.records
      }).catch().finally()
    },
    getTableData (page = this.pageNum) {
      findProductList(page, this.pageSize, this.queryMap.categorys, false, this.queryMap).then(res => {
        this.page = res.data
      }).catch().finally()
    },
    /**
     * 分类搜索改变时
     */
    selectChange () {
      let str = ''
      for (let i = 0; i < this.categorykeys.length; i++) {
        str += this.categorykeys[i] + ','
      }
      str = str.substring(0, str.length - 1)
      this.queryMap.categorys = str
    },
    /**
     * 搜索物资
     */
    search (page = this.pageSize) {
      this.getTableData(page)
    },
    // 重置查询表单
    resetForm () {
      this.queryMap = {
        name: '',
        categoryId: '',
        supplier: '',
        status: null
      }
      this.categorykeys = []
      this.pageNum = 1
      this.pageSize = this.getPageSize()
      this.getTableData()
    },
    /**
     * 打开添加物资弹框
     */
    openAdd () {
      this.addDialogVisible = true
    },
    back (id) {
      backRecord(id).then(() => {
        this.getTableData()
        this.NormalMsg(this, '数据已从回收站中恢复！', 'success')
      }).catch().finally()
    },
    del (id) {
      delProduct(id).then(() => {
        this.getTableData()
        this.NormalMsg(this, '数据已成功删除！', 'success')
      }).catch().finally()
    },
    edit (id) {
      findById(id).then(res => {
        this.editRuleForm = res.data
        this.editImageUrl = res.data.imageUrl
        const array = []
        array.push(res.data.oneCategoryId)
        array.push(res.data.twoCategoryId)
        array.push(res.data.threeCategoryId)
        this.editRuleForm.categoryKeys = array
        this.editDialogVisible = true
      }).catch().finally()
    },
    remove (id) {
      remove(id).then(() => {
        this.getTableData()
        this.NormalMsg(this, '数据已移入回收站！', 'success')
      }).catch().finally()
    },
    publish (id) {
      publishRecord(id).then(() => {
        this.getTableData()
        this.NormalMsg(this, '数据已审核通过！', 'success')
      }).catch().finally()
    },
    // 改变页码
    handleSizeChange (newSize) {
      this.pageSize = newSize
      this.getTableData()
    },
    // 翻页
    handleCurrentChange (current) {
      this.pageNum = current
      this.getTableData()
    },
    closeAddDialog () {
      this.$refs.addRuleFormRef.clearValidate()
      this.addRuleForm = {}
      this.addImageUrl = null
      this.$refs.upload.clearFiles()
    },
    handleSuccess (res, file) {
      if (res.code === 200) {
        this.addImageUrl = URL.createObjectURL(file.raw)
        this.addRuleForm.imageUrl = res.data
      } else {
        this.$message.error(res.message)
      }
    },
    handleEditSuccess (res, file) {
      if (res.code === 200) {
        this.editImageUrl = URL.createObjectURL(file.raw)
        this.editRuleForm.imageUrl = res.data
      } else {
        this.$message.error(res.message)
      }
    },
    handlePictureCardPreview (file) {
      const isJPG = file.type === 'image/jpeg'
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isJPG) {
        this.$message.error('上传图片只能是 JPG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    },
    add () {
      this.$refs.addRuleFormRef.validate(valid => {
        if (valid) {
          add(this.addRuleForm).then(() => {
            this.getTableData()
            this.addRuleForm = {}
            this.NormalMsg(this, '物资信息添加成功！', 'success')
            this.addDialogVisible = false
          }).catch().finally()
        }
      })
    },
    // 关闭弹出框
    closeEditDialog () {
      this.$refs.editRuleFormRef.clearValidate()
      this.editRuleForm = {}
      this.$refs.upload2.clearFiles()
      this.imgFilesList = []
    },
    update () {
      this.$refs.editRuleFormRef.validate(valid => {
        if (valid) {
          update(this.editRuleForm.id, this.editRuleForm).then(() => {
            this.getTableData()
            this.editRuleForm = {}
            this.NormalMsg(this, '更新该物资信息成功！', 'success')
            this.editDialogVisible = false
          }).catch().finally()
        }
      })
    }
  }
}
</script>

<style scoped>
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
