<!--
  @author qiukangming
  @description 物资入库
  @date 2021/04/06 20:44
-->
<template>
  <div>
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>健康报备</el-breadcrumb-item>
      <el-breadcrumb-item>打卡记录</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card id="card">
      <!-- 搜索部分 -->
      <el-form size="small" :inline="true" :model="queryData" class="demo-form-inline">
        <el-row>
          <el-col :span="24">
            <el-form-item label="省">
              <el-select v-model="valueProvince" placeholder="选择省" @change="changeProvince">
                <el-option v-for="item in provinceOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="市">
              <el-select v-model="valueCity" placeholder="选择市" @change="changeCity" clearable>
                <el-option v-for="item in cityOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="区/县">
              <el-select v-model="valueOrigin" placeholder="选择区/县" @change="changeOrigin" clearable>
                <el-option v-for="item in originOptions" :key="item.label" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input clearable @clear="search(1)" v-model="queryData.username" placeholder="请打卡人姓名"
                        @keyup.enter.native="search(1)" class="input-with-select"></el-input>
            </el-form-item>
            <el-form-item>
              <el-select clearable v-model="queryData.situation" placeholder="请健康状态">
                <el-option label="健康" :value="0"></el-option>
                <el-option label="有咳嗽发热症状" :value="1"></el-option>
                <el-option label="其他情况" :value="2"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-select clearable v-model="queryData.touch" placeholder="是否接触过疑似人">
                <el-option label="是" :value="1"></el-option>
                <el-option label="否" :value="0"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-select clearable v-model="queryData.passby" placeholder="是否经过湖北地区">
                <el-option label="是" :value="1"></el-option>
                <el-option label="否" :value="0"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-select clearable v-model="queryData.reception" placeholder="是否招待过湖北客户">
                <el-option label="是" :value="1"></el-option>
                <el-option label="否" :value="0"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-date-picker
                @change="rangeChange"
                :clearable="false" v-model="range" type="datetimerange" value-format="yyyy-MM-dd HH:mm:ss"
                :picker-options="pickerOptions" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"
                align="right">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="search(1)">查询</el-button>
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-refresh" type="warning" @click="reset">重置</el-button>
            </el-form-item>
            <el-form-item>
              <el-button @click="exportList" type="button" icon="el-icon-download">导出</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <!-- 表格区域 -->
      <el-table size="mini" v-loading="loading" :data="page.records" border style="width: 100%;" :max-height="this.getTableMaxHeight(0.48)">
        <el-table-column label="序号" type="index" align="center" width="50"></el-table-column>
        <el-table-column label="打卡地址" prop="address" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column label="打卡用户名" prop="username" align="center" width="90"></el-table-column>
        <el-table-column label="健康状况" align="center">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.situation === 0">健康</el-tag>
            <el-tag type="danger" v-else-if="scope.row.situation === 1">有咳嗽发热症状</el-tag>
            <el-tag type="warning" v-else-if="scope.row.situation === 2">其他情况</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="是否接触过疑似人" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" type="danger" effect="plain" v-if="scope.row.touch === 1">是</el-tag>
            <el-tag size="mini" effect="plain" v-if="scope.row.touch === 0">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="是否接经过湖北地区(包括转车)" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" type="danger" effect="plain" v-if="scope.row.passby === 1">是</el-tag>
            <el-tag size="mini" effect="plain" v-if="scope.row.passby === 0">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="是否接待过湖北客户" align="center">
          <template slot-scope="scope">
            <el-tag size="mini" type="danger" effect="plain" v-if="scope.row.reception === 1">是</el-tag>
            <el-tag size="mini" effect="plain" v-if="scope.row.reception === 0">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="打卡时间" sortable align="center" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column label="操作" align="center" width="80">
          <template slot-scope="scope">
            <el-popconfirm @confirm="del(scope.row.id)" title="确定删除吗？">
              <el-button type="text" slot="reference" size="small" icon="el-icon-delete">删除</el-button>
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
    </el-card>
  </div>
</template>

<script>
import { points, delPoint, exportPoints } from '@/api/healthy'
import { areaList } from '@/utils/provices'

export default {
  name: 'Points',
  components: {},
  data () {
    return {
      areaList: areaList,
      valueProvince: null,
      valueCity: null,
      valueOrigin: null,
      provinceOptions: [], // 省列表
      cityOptions: [], // 市下拉框数据
      originOptions: [], // 区下拉框数据
      pageNum: 1,
      sizeNum: this.getPageSize(),
      queryData: {
        province: null,
        city: null,
        origin: null,
        username: null,
        situation: null,
        passby: null,
        reception: null,
        touch: null,
        startTime: null,
        endTime: null
      },
      range: [],
      pickerOptions: {
        shortcuts: [
          {
            text: '今天(此刻)',
            onClick (picker) {
              const end = new Date()
              const start = new Date(new Date(new Date().toLocaleDateString()).getTime())
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '昨天',
            onClick (picker) {
              const end = new Date(new Date(new Date().toLocaleDateString()).getTime())
              const start = new Date(new Date(new Date().toLocaleDateString()).getTime())
              start.setTime(start.getTime() - 3600 * 1000 * 24)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近一周',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近一个月',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近两个月',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 60)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近三个月',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
              picker.$emit('pick', [start, end])
            }
          }
        ]
      },
      loading: false,
      page: {
        records: [],
        total: 0,
        size: 0,
        current: 0,
        orders: []
      }
    }
  },
  created () {
    this.init()
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight()
  },
  activated () {
  },
  methods: {
    init () {
      this.buildProvesData()
      this.getDataList()
    },
    rangeChange (value) {
      if (value !== null) {
        this.queryData.startTime = value[0]
        this.queryData.endTime = value[1]
      } else {
        this.queryData.startTime = value
        this.queryData.endTime = value
      }
    },
    getDataList (page = this.pageNum) {
      points(page, this.sizeNum, this.queryData).then(res => {
        this.page = res.data
      }).catch().finally()
    },
    search (page = this.pageNum) {
      this.getDataList(page)
    },
    reset () {
      this.queryData = {
        province: null,
        city: null,
        origin: null,
        username: null,
        situation: null,
        passby: null,
        reception: null,
        touch: null,
        startTime: null,
        endTime: null
      }
      this.valueProvince = null
      this.valueCity = null
      this.valueOrigin = null
      this.cityOptions = []
      this.originOptions = []
      this.range = []
      this.pageNum = 1
      this.pageSize = this.getPageSize()
      this.getDataList()
    },
    del (id) {
      delPoint(id).then(() => {
        this.getDataList()
        this.NormalMsg(this, '删除该打卡记录成功！', 'success')
      }).catch().finally()
    },
    // 改变页码
    handleSizeChange (newSize) {
      this.sizeNum = newSize
      this.getDataList()
    },
    // 翻页
    handleCurrentChange (current) {
      this.pageNum = current
      this.getDataList()
    },
    exportList () {
      this.$confirm('确定导出打卡记录信息吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        exportPoints(this.queryData).then((res) => {
          this.downloadFile(res, '打卡记录', 'xls')
        })
      }).catch(() => {
        this.NormalMsg(this, '已取消导出', 'info')
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
    },
    changeProvince (val) {
      this.valueCity = ''
      this.queryData.city = ''
      this.cityOptions = []
      this.valueOrigin = ''
      this.queryData.origin = ''
      this.originOptions = []
      this.provinceOptions.forEach((province) => {
        if (val.toString() === province.value) {
          this.queryData.province = province.label
          this.cityOptions = province.children
        }
      })
    },
    changeCity (val) {
      this.valueOrigin = ''
      this.queryData.origin = ''
      this.originOptions = []
      this.cityOptions.forEach((city) => {
        if (val.toString() === city.value) {
          this.queryData.city = city.label
          this.originOptions = city.children
        }
      })
    },
    changeOrigin (val) {
      this.originOptions.forEach((origin) => {
        if (val.toString() === origin.value) {
          this.queryData.origin = origin.label
        }
      })
      this.$forceUpdate()
    }
  }
}
</script>

<style scoped>

</style>
