<!--
  @author qiukangming
  @description 物资去处
  @date 2021/03/27 15:48
-->
<template>
  <div>
    <!-- 面包导航 -->
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>物资流向</el-breadcrumb-item>
      <el-breadcrumb-item>物资去处</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card id="card">
      <el-form size="mini" :inline="true" :model="queryMap" class="demo-form-inline">
        <el-form-item label="省市区县">
          <el-input v-model="queryMap.address" clearable @clear="search(1)" placeholder="省市区县"></el-input>
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="queryMap.contact" clearable @clear="search(1)" placeholder="联系人"></el-input>
        </el-form-item>
        <el-form-item label="具体地点">
          <el-input clearable v-model="queryMap.name" placeholder="请具体地点查询" @clear="search(1)" class="input-with-el-select"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="search(1)">查询</el-button>
          <el-button type="success" icon="el-icon-circle-plus-outline" @click="openAdd">添加</el-button>
        </el-form-item>
      </el-form>
      <!-- 表格区域 -->
      <el-table border size="mini" v-loading="loading" stripe :data="page.records" style="width: 100%;" :max-height="this.getTableMaxHeight()">
        <el-table-column align="center" prop="id" type="index" label="ID"></el-table-column>
        <el-table-column align="center" label="物资去向地址">
          <el-table-column align="center" prop="address" label="省份">
            <template slot-scope="scope">
              <span v-text="scope.row.address.split('/')[0]"></span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="address" label="市">
            <template slot-scope="scope">
              <span v-text="scope.row.address.split('/')[1]"></span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="address" label="区县">
            <template slot-scope="scope">
              <span v-text="scope.row.address.split('/')[2]"></span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="name" label="地址" show-overflow-tooltip></el-table-column>
        </el-table-column>
        <el-table-column align="center" prop="contact" label="联系人"></el-table-column>
        <el-table-column align="center" prop="phone" label="电话"></el-table-column>
        <el-table-column align="center" prop="modifiedTime" label="更新时间" show-overflow-tooltip></el-table-column>
<!--        <el-table-column align="center" prop="sort" label="排序" width="50"></el-table-column>-->
        <el-table-column align="center" label="操作">
          <template slot-scope="scope">
            <el-button type="text" size="mini" icon="el-icon-edit" @click="edit(scope.row.id)">编辑</el-button>
            <el-popconfirm style="margin-left:10px;" @confirm="del(scope.row.id)" title="确定删除吗？">
              <el-button type="text" slot="reference" size="mini" icon="el-icon-delete">删除</el-button>
            </el-popconfirm>
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
      <!-- 系别添加弹出框 -->
      <el-dialog title="添加物资去处" :visible.sync="addDialogVisible" @close="closeAddDialog">
        <span>
          <el-form size="mini" :model="addRuleForm" :rules="addRules" ref="addRuleFormRef" label-width="100px">
            <el-row>
              <el-col :span="8">
                <div class="grid-content bg-purple"></div>
                <el-form-item label="省份" prop="valueProvince">
                  <el-select v-model="addRuleForm.valueProvince" placeholder="请选择省" @change="changeProvince">
                    <el-option v-for="item in provinceOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <div class="grid-content bg-purple-light">
                  <el-form-item label="城市" prop="valueCity">
                    <el-select v-model="addRuleForm.valueCity" placeholder="请选择市" @change="changeCity">
                      <el-option v-for="item in cityOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                  </el-form-item>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="grid-content bg-purple">
                  <el-form-item label="区县" prop="valueOrigin">
                    <el-select v-model="addRuleForm.valueOrigin" placeholder="请选择区" @change="changeOrigin">
                      <el-option v-for="item in originOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                    </el-select>
                  </el-form-item>
                </div>
              </el-col>
            </el-row>
            <el-form-item label="联系人" prop="contact">
              <el-input v-model="addRuleForm.contact"></el-input>
            </el-form-item>

            <el-form-item label="详细去处" prop="name">
              <el-input type="textarea" v-model="addRuleForm.name"></el-input>
            </el-form-item>

            <el-form-item label="电话" prop="phone">
              <el-input v-model="addRuleForm.phone"></el-input>
            </el-form-item>

            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="addRuleForm.sort" :min="1" :max="10" label="排序"></el-input-number>
            </el-form-item>
          </el-form>
        </span>
        <span slot="footer" class="dialog-footer">
          <el-button size="mini" @click="addDialogVisible = false">取 消</el-button>
          <el-button size="mini" type="primary" @click="add">确 定</el-button>
        </span>
      </el-dialog>

      <!-- 系别编辑弹出框 -->
      <el-dialog title="更新物资去处" :visible.sync="editDialogVisible" @close="closeEditDialog">
        <span>
          <el-form size="mini" :model="editRuleForm" :rules="addRules" ref="editRuleFormRef" label-width="100px" class="demo-ruleForm">
            <el-form-item label="省市区县" prop="address">
              <el-input v-model="editRuleForm.address" disabled></el-input>
            </el-form-item>
            <el-form-item label="详细去处" prop="name">
              <el-input type="textarea" v-model="editRuleForm.name"></el-input>
            </el-form-item>
            <el-form-item label="电话" prop="phone">
              <el-input v-model="editRuleForm.phone"></el-input>
            </el-form-item>
            <el-form-item label="联系人" prop="contact">
              <el-input v-model="editRuleForm.contact"></el-input>
            </el-form-item>
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="editRuleForm.sort" :min="1" :max="10" label="排序"></el-input-number>
            </el-form-item>
          </el-form>
        </span>
        <span slot="footer" class="dialog-footer">
          <el-button size="mini" @click="editDialogVisible = false">取 消</el-button>
          <el-button size="mini" type="primary" @click="update">确 定</el-button>
        </span>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { areaList } from '@/utils/provices'
import { findConsumerList, add, deleteConsumer, findById, update } from '@/api/consumer'

export default {
  name: 'ThingsGo',
  components: {},
  data () {
    return {
      areaList: areaList,
      queryMap: {
        address: '',
        contact: '',
        name: ''
      },
      loading: false,
      thingsData: [],
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
      addRuleForm: {
        valueProvince: '',
        valueCity: '',
        valueOrigin: '',
        province: '',
        city: '',
        origin: ''
      },
      addRules: {
        name: [
          { required: true, message: '请输入物资去处名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入地址信息', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        sort: [{ required: true, message: '请输入排序号', trigger: 'blur' }],
        valueProvince: [
          { required: true, message: '请输入省份', trigger: 'blur' }
        ],
        valueCity: [{ required: true, message: '请输入城市', trigger: 'blur' }],
        valueOrigin: [
          { required: true, message: '请输入区县', trigger: 'blur' }
        ],
        contact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
        phone: [
          {
            required: true,
            message: '请输入联系方式',
            validator: this.checkPhone,
            trigger: 'blur'
          }
        ]
      },
      provinceOptions: [], // 省下拉框数据
      cityOptions: [], // 市下拉框数据
      originOptions: [], // 区下拉框数据
      editRuleForm: {},
      editDialogVisible: false
    }
  },
  created () {
    this.buildProvesData()
    this.getConsumerList()
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight()
  },
  activated () {
  },
  methods: {
    getConsumerList (page = this.pageNum) {
      findConsumerList(page, this.pageSize, this.queryMap).then(res => {
        this.page = res.data
      }).catch().finally()
    },
    search (page = this.pageNum) {
      this.getConsumerList(page)
    },
    openAdd () {
      this.addDialogVisible = true
    },
    edit (id) {
      findById(id).then(res => {
        this.editRuleForm = res.data
        this.editDialogVisible = true
      }).catch().finally()
    },
    del (id) {
      deleteConsumer(id).then(() => {
        this.NormalMsg(this, '删除该物资去处成功！', 'success')
        this.getConsumerList()
      }).catch().finally()
    },
    // 改变页码
    handleSizeChange (newSize) {
      this.pageSize = newSize
      this.getConsumerList()
    },
    // 翻页
    handleCurrentChange (current) {
      this.pageNum = current
      this.getConsumerList()
    },
    // 关闭弹出框
    closeAddDialog () {
      this.$refs.addRuleFormRef.clearValidate()
      this.cityOptions = []
      this.originOptions = []
      this.addRuleForm = {}
    },
    // 选择省
    changeProvince (val) {
      this.addRuleForm.valueCity = ''
      this.addRuleForm.valueOrigin = ''
      this.cityOptions = []
      this.originOptions = []
      this.provinceOptions.forEach((province) => {
        if (val.toString() === province.value) {
          this.addRuleForm.province = province.label
          this.cityOptions = province.children
        }
      })
    },
    // 选择市
    changeCity (val) {
      this.addRuleForm.valueOrigin = ''
      this.originOptions = []
      this.cityOptions.forEach((city) => {
        if (val.toString() === city.value) {
          this.addRuleForm.city = city.label
          this.originOptions = city.children
        }
      })
    },
    // 选择区
    changeOrigin (val) {
      this.originOptions.forEach((origin) => {
        if (val.toString() === origin.value) {
          this.addRuleForm.origin = origin.label
        }
      })
      // 数据层次太多 还没来得及渲染数据
      this.$forceUpdate()
    },
    add () {
      this.$refs.addRuleFormRef.validate(valid => {
        if (valid) {
          this.addRuleForm.address = this.addRuleForm.province + '/' + this.addRuleForm.city + '/' + this.addRuleForm.origin
          add(this.addRuleForm).then(() => {
            this.NormalMsg(this, '物资去处添加成功！', 'success')
            this.addRuleForm = {}
            this.getConsumerList()
            this.addDialogVisible = false
          }).catch().finally()
        }
      })
    },
    // 关闭弹出框
    closeEditDialog () {
      this.$refs.editRuleFormRef.clearValidate()
      this.editRuleForm = {}
    },
    update () {
      this.$refs.editRuleFormRef.validate(valid => {
        if (valid) {
          update(this.editRuleForm.id, this.editRuleForm).then(() => {
            this.NormalMsg(this, '物资去处更新成功！', 'success')
            this.editRuleForm = {}
            this.getConsumerList()
            this.editDialogVisible = false
          }).catch().finally()
        }
      })
    },
    /**
     * 构造省市县下拉框数据
     */
    buildProvesData () {
      // 构造省数据
      const provinces = this.areaList.province_list
      for (const value in provinces) {
        this.provinceOptions.push({
          value: value,
          label: provinces[value],
          children: []
        })
      }

      // 构造市数据
      const cities = this.areaList.city_list
      this.provinceOptions.forEach(province => {
        for (const city in cities) {
          if (province.value.substr(0, 2) === city.substr(0, 2)) {
            province.children.push({
              value: city,
              label: cities[city],
              children: []
            })
          }
        }
      })

      // 构造区县数据
      const counties = this.areaList.county_list
      this.provinceOptions.forEach(province => {
        province.children.forEach(city => {
          for (const county in counties) {
            if (city.value.substr(0, 4) === county.substr(0, 4)) {
              city.children.push({
                value: county,
                label: counties[county],
                children: []
              })
            }
          }
        })
      })
    }
  }
}
</script>

<style scoped>

</style>
