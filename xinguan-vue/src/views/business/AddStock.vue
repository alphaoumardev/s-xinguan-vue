<!--
  @author qiukangming
  @description 添加入库
  @date 2021/03/23 22:15
-->
<template>
  <div>
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>业务管理</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/business/product/in-stocks' }">物资入库</el-breadcrumb-item>
      <el-breadcrumb-item>添加入库</el-breadcrumb-item>
    </el-breadcrumb>
    <el-row :gutter="20">
      <el-col :span="11">
        <div class="grid-content bg-purple">
          <el-card id="card">
            <el-steps style="margin-bottom: 20px;" :active="stepActive" simple>
              <el-step title="填写" icon="el-icon-edit"></el-step>
              <el-step title="审核" icon="el-icon-postcard"></el-step>
              <el-step title="入库" icon="el-icon-set-up"></el-step>
            </el-steps>
            <el-form size="mini" ref="addRuleFormRef" :rules="addRules" :model="addRuleForm" label-width="80px">
              <el-form-item label="添加方式">
                <el-col :span="24">
                  <div class="grid-content bg-purple">
                    <el-radio border size="mini" @change="existenceChange(existence)" v-model="existence" :label="0">
                      新增来源
                    </el-radio>
                    <el-radio border size="mini" @change="existenceChange(existence)" v-model="existence" :label="1">
                      已知来源
                    </el-radio>
                  </div>
                </el-col>
              </el-form-item>
              <el-form-item v-if="existence === 1" label="物资来源" prop="supplierId">
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <el-select v-if="existence === 1" style="width:100%;" filterable
                               @change="supplerSelectChange(addRuleForm.supplierId)" v-model="addRuleForm.supplierId"
                               placeholder="选择已存在来源">
                      <el-option v-for="item in suppliers" :key="item.id" :label="item.name"
                                 :value="item.id"></el-option>
                    </el-select>
                  </div>
                </el-col>
              </el-form-item>
              <el-form-item label="入库类型" prop="type">
                <el-radio-group v-model="addRuleForm.type">
                  <el-radio :size="'mini'" :label="1">捐赠</el-radio>
                  <el-radio :size="'mini'" :label="2">下拨</el-radio>
                  <el-radio :size="'mini'" :label="3">采购</el-radio>
                  <el-radio :size="'mini'" :label="4">借用</el-radio>
                </el-radio-group>
              </el-form-item>
              <div v-if="existence === 0">
                <el-row>
                  <el-col :span="8">
                    <div class="grid-content bg-purple"></div>
                    <el-form-item label="省份" prop="valueProvince">
                      <el-select v-model="addRuleForm.valueProvince" placeholder="请选择省" @change="changeProvince">
                        <el-option v-for="item in provinceOptions" :key="item.value" :label="item.label"
                                   :value="item.value"></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <div class="grid-content bg-purple-light">
                      <el-form-item label="城市" prop="valueCity">
                        <el-select v-model="addRuleForm.valueCity" placeholder="请选择市" @change="changeCity">
                          <el-option v-for="item in cityOptions" :key="item.value" :label="item.label"
                                     :value="item.value"></el-option>
                        </el-select>
                      </el-form-item>
                    </div>
                  </el-col>
                  <el-col :span="8">
                    <div class="grid-content bg-purple">
                      <el-form-item label="区县" prop="valueOrigin">
                        <el-select v-model="addRuleForm.valueOrigin" placeholder="请选择区" @change="changeOrigin">
                          <el-option v-for="item in originOptions" :key="item.label" :label="item.label"
                                     :value="item.value"></el-option>
                        </el-select>
                      </el-form-item>
                    </div>
                  </el-col>
                </el-row>
                <el-form-item label="具体来源" prop="name">
                  <el-input placeholder="请输入具体来源" v-model="addRuleForm.name"></el-input>
                </el-form-item>
                <el-form-item label="联系人" prop="contact">
                  <el-input placeholder="请输入联系人名称" v-model="addRuleForm.contact"></el-input>
                </el-form-item>

                <el-form-item label="邮箱" prop="email">
                  <el-input placeholder="请输入邮箱地址" v-model="addRuleForm.email"></el-input>
                </el-form-item>
                <el-form-item label="电话" prop="phone">
                  <el-input placeholder="请输入电话号码" v-model="addRuleForm.phone"></el-input>
                </el-form-item>
                <el-form-item label="排序" prop="sort">
                  <el-input-number v-model="addRuleForm.sort" :min="1" :max="10" label="排序"></el-input-number>
                </el-form-item>
              </div>
              <div v-if="existence === 1">
                <el-card class="box-card" style="margin-bottom: 30px;">
                  <div class="text item" style="font-size: 12px;padding: 5px;">
                    详细地址: <span style="margin-left: 200px;color: #999"><el-tag size="mini" type="info">{{ supplierInfo.name }}</el-tag></span>
                  </div>
                  <div class="text item" style="font-size: 12px;padding: 5px;">
                    区县信息: <span style="margin-left: 200px;color: #999"><el-tag size="mini" type="info">{{ supplierInfo.address }}</el-tag></span>
                  </div>
                  <div class="text item" style="font-size: 12px;padding: 5px;">
                    联系方式: <span style="margin-left: 200px;color: #999"><el-tag size="mini" type="info">{{ supplierInfo.phone }}</el-tag></span>
                  </div>
                  <div class="text item" style="font-size: 12px;padding: 5px;">
                    电子邮件: <span style="margin-left: 200px;color: #999"><el-tag size="mini" type="info">{{ supplierInfo.email }}</el-tag></span>
                  </div>
                  <div class="text item" style="font-size: 12px;padding: 5px;">
                    联系人员: <span style="margin-left: 200px;color: #999"><el-tag size="mini" type="info">{{ supplierInfo.contact }}</el-tag></span>
                  </div>
                </el-card>
              </div>
              <el-row>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <el-form-item label="物资明细">
                      <el-button size="mini" plain icon="el-icon-search" @click="drawer = true">入库明细</el-button>
                    </el-form-item>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="grid-content bg-purple">
                    <el-form-item label="物资总类">
                      <el-input-number v-model="itemNum" :disabled="itemNumDisable"></el-input-number>
                    </el-form-item>
                  </div>
                </el-col>
              </el-row>
              <el-form-item label="描述信息" prop="remark">
                <el-input type="textarea" :rows="2" v-model="addRuleForm.remark"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="add">立即创建</el-button>
                <el-button @click="reset">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </div>
      </el-col>
      <el-col :span="13">
        <div class="grid-content bg-purple-light">
          <el-card id="card2">
            <!-- 查询搜索 -->
            <el-form size="mini" :inline="true" :model="queryData" class="demo-form-inline">
              <el-form-item label="分类">
                <el-cascader size="mini" :change-on-select="true" @change="selectChange" v-model="categorykeys" :props="selectProps" :options="catetorys" clearable></el-cascader>
              </el-form-item>
              <el-form-item>
                <el-form-item label="名称">
                  <el-input clearable v-model="queryData.name" placeholder="名称" @clear="search(1)"></el-input>
                </el-form-item>
                <el-button type="primary" @click="search(1)">查询</el-button>
              </el-form-item>
            </el-form>
            <el-alert title="勾选先下方物资后,在左侧表单的明细中添加其入库数量" type="info" show-icon style="margin-bottom:20px;" :closable="false"></el-alert>
            <!-- 入库预选表格 -->
            <el-table style="width: 100%" :data="page.records" :row-key="getRowKey" ref="dataTable" @selection-change="handleSelectionChange" border :max-height="this.getTableMaxHeight(0.45)">
              <el-table-column type="selection" :reserve-selection="true" align="center"></el-table-column>
              <el-table-column prop="imageUrl" label="图片" align="center">
                <template slot-scope="scope">
                  <el-popover placement="right" trigger="hover">
                    <img :src="scope.row.imageUrl" style="height: 200px;width: 200px" alt="alt"/>
                    <img slot="reference" :src="scope.row.imageUrl" alt="alt"
                         style="height: 32px;width: 32px;cursor: pointer"/>
                  </el-popover>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="名称" align="center"></el-table-column>
              <el-table-column prop="pnum" label="商品编号" :show-overflow-tooltip='true' align="center"></el-table-column>
              <el-table-column prop="model" label="型号" align="center"></el-table-column>
              <el-table-column prop="unit" label="单位" align="center"></el-table-column>
            </el-table>
            <!-- 表格分页 -->
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
            <!-- 抽屉 -->
            <el-drawer size="49%" title="入库明细" :visible.sync="drawer" :with-header="false">
              <span>
                <el-button size="mini" style="float: right;margin: 10px;" type="primary" icon="el-icon-remove" :disabled="products.length === 0" @click="removeAllItem">清空</el-button>
                <el-table :data="products" border :max-height="this.getTableMaxHeight(0.15)">
                  <el-table-column prop="name" label="名称" align="center" show-overflow-tooltip></el-table-column>
                  <el-table-column prop="imageUrl" label="图片" align="center">
                    <template slot-scope="scope">
                      <img :src="scope.row.imageUrl" alt="alt" style="width: 50px;height:30px"/>
                    </template>
                  </el-table-column>
                  <el-table-column prop="model" label="型号" align="center"></el-table-column>
                  <el-table-column label="数量" align="center" width="160">
                    <template slot-scope="scope">
                      <el-input-number size="mini" v-model="scope.row.number" :min="1" :max="500" label="描述文字"></el-input-number>
                    </template>
                  </el-table-column>
                  <el-table-column prop="unit" label="单位" align="center"></el-table-column>
                  <el-table-column label="操作" align="center">
                    <template slot-scope="scope">
                      <el-button type="danger" size="mini" plain icon="el-icon-remove"
                                 @click="removeItem(scope.row.id)">移除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </span>
            </el-drawer>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { areaList } from '@/utils/provices'
import { addInStock } from '@/api/in-stock'
import { findProductList } from '@/api/product'
import { categoryTree } from '@/api/product-category'
import { findAll } from '@/api/supplier'

export default {
  name: 'AddStock',
  components: {},
  data () {
    return {
      areaList: areaList,
      stepActive: 1,
      addRules: {
        supplierId: [
          { required: true, message: '请选中物资来源', trigger: 'blur' }
        ],
        type: [{ required: true, message: '请选中入库类型', trigger: 'blur' }],
        remark: [
          { required: true, message: '请输入商品说明备注', trigger: 'blur' },
          { min: 5, max: 50, message: '长度在 5 到 50 个字符', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入来源名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入地址信息', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        email: [{ required: true, validator: this.checkEmail, trigger: 'blur' }],
        valueProvince: [
          { required: true, message: '请输入省份', trigger: 'blur' }
        ],
        valueCity: [{ required: true, message: '请输入城市', trigger: 'blur' }],
        valueOrigin: [
          { required: true, message: '请输入区县', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '请输入排序号', trigger: 'blur' }
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
      }, // 添加验证
      addRuleForm: {
        type: 1,
        supplierId: '',
        valueProvince: '',
        valueCity: '',
        city: '',
        valueOrigin: '',
        origin: '',
        province: '',
        name: '',
        contact: '',
        email: '',
        phone: '',
        sort: '',
        remark: ''
      },
      existence: 0,
      suppliers: [],
      supplierInfo: {
        name: '选择后显示详细地址',
        address: '选择后显示区县信息',
        contact: '选择后显示联系人信息',
        phone: '选择后显示联系方式',
        email: '选择后显示邮箱信息'
      },
      provinceOptions: [], // 省下拉框数据
      cityOptions: [], // 市下拉框数据
      originOptions: [], // 区下拉框数据
      drawer: false,
      itemNum: 0,
      itemNumDisable: true,
      products: [],
      queryDataPageNum: 1,
      queryDataPageSize: this.getPageSize(),
      queryData: {
        name: '',
        categorys: []
      },
      page: {
        records: [],
        total: 0,
        size: 0,
        current: 0,
        orders: []
      },
      categorykeys: [],
      selectProps: {
        expandTrigger: 'hover',
        value: 'id',
        label: 'name',
        children: 'children'
      }, // 级联选择器配置项
      catetorys: []
    }
  },
  created () {
    this.buildProvesData()
    this.getTableData()
    this.getCatetorys()
    this.getSuppliers()
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight()
    document.getElementById('card2').style.height = this.getWindowHeight()
  },
  activated () {
  },
  methods: {
    getTableData (page = this.queryDataPageNum) {
      findProductList(page, this.queryDataPageSize, null, true, this.queryData).then(res => {
        this.page = res.data
      }).finally()
    },
    getCatetorys () {
      categoryTree().then(res => {
        this.catetorys = res.data.records
      }).finally()
    },
    getSuppliers () {
      findAll().then(res => {
        this.suppliers = res.data
      }).finally()
    },
    /**
     * 改变来源
     */
    existenceChange (existence) {
      // 选择已经存在的来源
      if (existence === 1) {
      } else if (existence === 0) {
        this.addRuleForm.supplierId = ''
      }
      this.$refs.addRuleFormRef.clearValidate()
      this.supplierInfo = {
        name: '选择后显示详细地址',
        address: '选择后显示区县信息',
        contact: '选择后显示联系人信息',
        phone: '选择后显示联系方式',
        email: '选择后显示邮箱信息'
      }
    },
    // 改变来源选择
    supplerSelectChange (supplierId) {
      this.supplierInfo = this.suppliers.find(function (x) {
        return x.id === supplierId
      })
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
      this.$forceUpdate()
    },
    add () {
      this.$refs.addRuleFormRef.validate(async valid => {
        if (valid) {
          this.addRuleForm.address = this.addRuleForm.province + '/' + this.addRuleForm.city + '/' + this.addRuleForm.origin
          const car = []
          this.products.forEach(row => {
            if (row.number !== undefined) {
              const item = { productId: row.id, productNumber: row.number }
              car.push(item)
            }
          })
          if (car.length > 0) {
            this.addRuleForm.products = car
          } else {
            return this.$message.warning('入库商品的数量不能为空，请勾选物资后在明细中添加数量！')
          }
          const res = await this.$confirm('此操作将提交入库单, 是否继续?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消提交'
            })
          })
          if (res === 'confirm') {
            addInStock(this.addRuleForm).then(() => {
              this.$confirm('物资入库已进入审核状态，是否继续添加入库信息?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
              }).then(() => {
                this.reset()
              }).catch(() => {
                this.$router.push('/business/product/in-stocks')
              })
            }).catch().finally()
          }
        }
      })
    },
    // 重置信息
    reset () {
      this.addRuleForm = {
        type: 1,
        supplierId: '',
        valueProvince: '',
        valueCity: '',
        city: '',
        valueOrigin: '',
        origin: '',
        province: '',
        name: '',
        contact: '',
        email: '',
        phone: '',
        sort: '',
        remark: ''
      }
      this.$refs.addRuleFormRef.clearValidate()
      this.supplierInfo = {
        name: '选择后显示详细地址',
        address: '选择后显示区县信息',
        contact: '选择后显示联系人信息',
        phone: '选择后显示联系方式',
        email: '选择后显示邮箱信息'
      }
    },
    // 改变页码
    handleSizeChange (newSize) {
      this.queryDataPageSize = newSize
      this.getTableData()
    },
    // 翻页
    handleCurrentChange (current) {
      this.queryDataPageNum = current
      this.getTableData()
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
      this.queryData.categorys = str
    },
    /**
     * 搜索
     */
    search (page = this.queryDataPageNum) {
      this.getTableData(page)
    },
    /**
     *  指定一个key标识这一行的数据
     */
    getRowKey (row) {
      return row.id
    },
    /**
     * val表示选中的表格行数据
     */
    handleSelectionChange (val) {
      this.products = val
      this.itemNum = val.length
    },
    /**
     * 清空所有
     */
    removeAllItem () {
      this.products.forEach(row => {
        this.$refs.dataTable.toggleRowSelection(row, false)
      })
      this.products = []
    },
    /**
     * 移除item从抽屉中
     */
    removeItem (val) {
      this.products.forEach(row => {
        if (row.id === val) {
          this.$refs.dataTable.toggleRowSelection(row, false)
        }
      })
      this.products = this.products.filter(item => item.id !== val)
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

<style scoped lang="less">
</style>
