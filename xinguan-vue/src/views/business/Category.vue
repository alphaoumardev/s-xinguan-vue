<!--
  @author qiukangming
  @description 物资分类
  @date 2021/03/27 20:19
-->
<template>
  <div>
    <!-- 面包导航 -->
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>物资管理</el-breadcrumb-item>
      <el-breadcrumb-item>物资分类</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card id="card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-button size="mini" type="warning" icon="el-icon-circle-plus-outline" @click="openAdd">添加</el-button>
        </el-col>
      </el-row>
      <!-- 表格部分 -->
      <el-table size="mini" v-loading="loading" :data="page.records" style="width: 100%; margin-top: 15px" row-key="id" border default-expand-all :tree-props="{children: 'children', hasChildren: 'hasChildren'}" :max-height="this.getTableMaxHeight()">
        <el-table-column align="center" label="序号" type="index"></el-table-column>
        <el-table-column align="center" prop="name" label="分类名称" show-overflow-tooltip></el-table-column>
<!--        <el-table-column align="center" prop="sort" label="排序" sortable></el-table-column>-->
        <el-table-column align="center" prop="remark" label="备注" show-overflow-tooltip></el-table-column>
<!--        <el-table-column align="center" prop="createTime" label="创建时间" sortable></el-table-column>-->
        <el-table-column align="center" prop="modifiedTime" label="更新时间" sortable></el-table-column>
        <el-table-column align="center" prop="lev" label="层级" sortable>
          <template slot-scope="scope">
            <el-tag v-if="scope.row.lev === 1">一级分类</el-tag>
            <el-tag type="success" v-else-if="scope.row.lev === 2">二级分类</el-tag>
            <el-tag type="danger" v-else>三级分类</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="template" label="操作">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" icon="el-icon-edit" @click="edit(scope.row.id)"></el-button>
            <el-button type="danger" size="mini" icon="el-icon-delete" @click="del(scope.row.id)"></el-button>
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
      <!-- 添加弹出框 -->
      <el-dialog title="添加分类" :visible.sync="addDialogVisible" @close="addCloseDialog" width="50%">
        <span>
          <el-form size="mini" :model="addRuleForm" :rules="addRules" ref="addRuleFormRef" label-width="100px">
            <el-form-item label="分类名称" prop="name">
              <el-input v-model="addRuleForm.name"></el-input>
            </el-form-item>
            <el-form-item label="父分类" prop="pid">
              <el-cascader @change="selectParentChange" @clear="clearParent" :change-on-select="true" placeholder="选择父级分类(不选则为一级分类, 最高支持三级分类)"
                :options="parentCategorys" clearable filterable style="width:100%" :props="selectProps" v-model="pKeys"></el-cascader>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input type="textarea" v-model="addRuleForm.remark"></el-input>
            </el-form-item>
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="addRuleForm.sort" :min="1" :max="10" label="排序"></el-input-number>
            </el-form-item>
          </el-form>
        </span>
        <span slot="footer" class="dialog-footer">
          <el-button size="mini" @click="addDialogVisible = false">取 消</el-button>
          <el-button size="mini" type="primary" @click="add" :disabled="false" :loading="false">确 定</el-button>
        </span>
      </el-dialog>
      <!-- 编辑弹出框 -->
      <el-dialog title="编辑分类" :visible.sync="editDialogVisible" @close="editCloseDialog" width="50%">
        <span>
          <el-form size="mini" :model="editRuleForm" :rules="addRules" ref="editRuleFormRef" label-width="100px">
            <el-form-item label="分类名称" prop="name">
              <el-input v-model="editRuleForm.name"></el-input>
            </el-form-item>

            <el-form-item label="备注" prop="remark">
              <el-input type="textarea" v-model="editRuleForm.remark"></el-input>
            </el-form-item>
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="editRuleForm.sort" :min="1" :max="10" label="排序"></el-input-number>
            </el-form-item>
          </el-form>
        </span>
        <span slot="footer" class="dialog-footer">
          <el-button @click="editDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="update" :disabled="false" :loading="false">确 定</el-button>
        </span>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { categoryTree, getParentCategoryTree, findById, delProCat, add, update } from '@/api/product-category'

export default {
  name: 'Category',
  components: {},
  data () {
    return {
      addDialogVisible: false,
      categorys: [],
      loading: false,
      pageNum: 1,
      pageSize: this.getPageSize(),
      page: {
        records: [],
        total: 0,
        size: 0,
        current: 0,
        orders: []
      },
      addRuleForm: {
        pid: 0,
        name: '',
        remark: '',
        sort: ''
      },
      addRules: {
        name: [
          { required: true, message: '请输入分类名', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        remark: [
          { required: true, message: '请输入备注信息', trigger: 'blur' }
        ],
        sort: [{ required: true, message: '请输入排序号', trigger: 'blur' }]
      },
      pKeys: [],
      parentCategorys: [],
      selectProps: {
        expandTrigger: 'hover',
        value: 'id',
        label: 'name',
        children: 'children'
      }, // 级联选择器配置项
      editDialogVisible: false,
      editRuleForm: {}
    }
  },
  created () {
    this.getCategoryList()
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight(0.22)
  },
  activated () {
  },
  methods: {
    getCategoryList () {
      categoryTree(this.pageNum, this.pageSize).then(res => {
        this.page = res.data
      }).finally()
    },
    getParentCategoryList () {
      getParentCategoryTree().then(res => {
        this.parentCategorys = res.data
      }).finally()
    },
    // 打开添加
    openAdd () {
      this.getParentCategoryList()
      this.addDialogVisible = true
    },
    edit (id) {
      findById(id).then(res => {
        this.editRuleForm = res.data
        this.editDialogVisible = true
      }).finally()
    },
    del (id) {
      const _this = this
      _this.$confirm('确认删除个分类信息吗?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        delProCat(id).then(() => {
          _this.getCategoryList()
          this.NormalMsg(this, '删除分类成功！', 'success')
        }).finally()
      }).catch(() => {
        _this.NormalMsg(_this, '已取消删除！', 'info')
      })
    },
    // 改变页码
    handleSizeChange (newSize) {
      this.pageSize = newSize
      this.getCategoryList()
    },
    // 翻页
    handleCurrentChange (current) {
      this.pageNum = current
      this.getCategoryList()
    },
    addCloseDialog () {
      this.$refs.addRuleFormRef.clearValidate()
      this.addRuleForm = {}
      this.pKeys = []
    },
    editCloseDialog () {
      this.$refs.editRuleFormRef.clearValidate()
      this.editRuleForm = {}
    },
    // 父级分类中改变
    selectParentChange () {
      const len = this.pKeys.length
      if (len > 0) {
        this.addRuleForm.pid = this.pKeys[len - 1]
      } else {
        this.addRuleForm.pid = 0
      }
    },
    clearParent () {
      this.addRuleForm.pid = 0
    },
    add () {
      this.$refs.addRuleFormRef.validate(valid => {
        if (valid) {
          if (this.addRuleForm.pid == null) {
            this.addRuleForm.pid = 0
          }
          add(this.addRuleForm).then(() => {
            this.getCategoryList()
            this.NormalMsg(this, '添加分类信息成功！', 'success')
            this.addDialogVisible = false
          }).finally()
        }
      })
    },
    update () {
      update(this.editRuleForm.id, this.editRuleForm).then(() => {
        this.getCategoryList()
        this.NormalMsg(this, '更新分类信息成功！', 'success')
        this.editDialogVisible = false
      }).finally()
    }
  }
}
</script>

<style scoped>

</style>
