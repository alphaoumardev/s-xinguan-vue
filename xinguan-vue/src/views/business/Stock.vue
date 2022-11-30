<!--
  @author qiukangming
  @description 库存维护页面
  @date 2021/03/28 18:26
-->
<template>
  <div>
    <!-- 面包导航 -->
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>物资管理</el-breadcrumb-item>
      <el-breadcrumb-item>库存维护</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card id="card">
      <el-row :gutter="20">
        <el-col :span="13">
          <div class="grid-content bg-purple-dark">
            <el-card>
              <!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
              <div id="tianxing" style="width: 100%;"></div>
            </el-card>
            <el-card style="margin-top:10px;">
              <!-- 库存饼图 -->
              <div id="bingtu" style="width: 100%;"></div>
            </el-card>
          </div>
        </el-col>
        <el-col :span="11">
          <div class="grid-content bg-purple-dark">
            <el-card id="cardTable">
              <el-form size="mini" :inline="true" :model="queryMap" class="demo-form-inline">
                <el-form-item>
                  <el-cascader placeholder="请选择分类查询" :change-on-select="true" @change="selectChange" v-model="categorykeys" :props="searchSelectProps" :options="catetorys" clearable></el-cascader>
                </el-form-item>
                <el-form-item>
                  <el-input clearable @clear="search(1)" v-model="queryMap.name" placeholder="物资名称"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button size="mini" type="primary" @click="search(1)" icon="el-icon-search">查询</el-button>
                  <el-button size="mini" type="success" @click="reset" icon="el-icon-refresh">重置</el-button>
                </el-form-item>
              </el-form>
              <el-table size="mini" border :data="page.records" style="width: 100%" :max-height="this.getTableMaxHeight(0.445)">
                <el-table-column prop="imageUrl" label="图片" align="center">
                  <template slot-scope="scope">
                    <el-popover placement="right"  trigger="hover">
                      <img :src="scope.row.imageUrl"  style="height: 200px;width: 200px" alt="alt"/>
                      <img slot="reference" :src="scope.row.imageUrl" alt="alt" style="height: 21px;width: 21px;cursor: pointer">
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column align="center" prop="name" label="名称" show-overflow-tooltip></el-table-column>
                <el-table-column align="center" prop="model" label="规格"></el-table-column>
                <el-table-column align="center" prop="stock" label="库存">
                  <template slot-scope="scope">
                    <el-tag size="mini">{{scope.row.stock}}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column align="center" prop="unit" label="单位"></el-table-column>
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
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { findProductStocks, findAllProductStocks } from '@/api/product'
import echarts from 'echarts'
import { categoryTree } from '@/api/product-category'

export default {
  name: 'Stock',
  components: {},
  data () {
    return {
      queryMap: {},
      categorykeys: [],
      searchSelectProps: {
        expandTrigger: 'hover',
        value: 'id',
        label: 'name',
        children: 'children',
        checkStrictly: true
      }, // 级联搜索选择器配置项
      catetorys: [], // 类别选择
      categorys: '',
      pageNum: 1,
      pageSize: this.getPageSize(),
      page: {
        records: [],
        total: 0,
        size: 0,
        current: 0,
        orders: []
      },
      xData: [],
      yData: [],
      legendData: [], // 饼图存放物资名称
      selected: {}, // 存放选择的数据
      seriesData: [{ name: '', value: '' }] // 饼图数据
    }
  },
  created () {
    this.getProductStockList()
    this.getCatetorys()
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight()
    document.getElementById('cardTable').style.height = this.getWindowHeight(0.26)
    document.getElementById('tianxing').style.height = this.getWindowHeight(0.679)
    document.getElementById('bingtu').style.height = this.getWindowHeight(0.68)
  },
  activated () {
  },
  methods: {
    getProductStockList (page = this.pageNum) {
      findProductStocks(page, this.pageSize, this.categorys, this.queryMap).then(res => {
        this.page = res.data
        this.xData = []
        this.yData = []
        const $this = this
        // 构建表格条形统计图的数据
        this.page.records.forEach(function (e) {
          $this.xData.push(e.name)
          $this.yData.push(e.stock)
        })
        // 重新绘制表格
        this.draw()
        // 饼图
        this.getAllProductStocksData()
      }).catch().finally()
    },
    getAllProductStocksData () {
      findAllProductStocks(this.categorys, this.queryMap).then(res => {
        this.legendData = []
        this.selected = {}
        this.seriesData = []
        const $this = this
        // 构建饼图的数据对象
        res.data.forEach(function (e) {
          $this.legendData.push(e.name)
          $this.seriesData.push({ name: e.name, value: e.stock })
        })
        // 重新绘制饼图
        this.drawRound()
      }).catch().finally()
    },
    getCatetorys () {
      categoryTree().then(res => {
        this.catetorys = res.data.records
      }).catch().finally()
    },
    reset () {
      this.queryMap = {}
      this.categorykeys = []
      this.categorys = ''
      this.pageNum = 1
      this.pageSize = this.getPageSize()
      this.getProductStockList()
    },
    /**
     * 绘制条形统计图
     */
    draw () {
      const myChart = echarts.init(document.getElementById('tianxing'))
      // 指定图表的配置项和数据
      const option = {
        title: {
          text: '库存条形图'
        },
        toolbox: {
          show: true,
          feature: {
            dataZoom: {
              yAxisIndex: 'none'
            },
            dataView: { readOnly: false }, // 缩放
            magicType: { type: ['bar'] }, // 折线  直方图切换
            restore: {}, // 重置
            saveAsImage: {} // 导出图片
          }
        },
        tooltip: {},
        legend: {
          data: ['库存量']
        },
        xAxis: {
          data: this.xData
        },
        yAxis: {},
        series: [
          {
            // name: '库存量',
            type: 'bar',
            showBackground: true,
            data: this.yData,
            itemStyle: {
              normal: {
                // 然后根据所以取得不同的值，这样就实现了，颜色变化
                color: function (params) {
                  // build a color map as your need.
                  const colorList = [
                    '#0780cf', '#fa6d1d', '#ac2026', '#701866', '#d22e8d',
                    '#FE8463', '#a195c5', '#FAD860', '#F3A43B', '#127a93',
                    '#D7504B', '#0000CD', '#797326', '#808080', '#63b2ee'
                  ]
                  return colorList[params.dataIndex]
                }
                // 以下为是否显示，显示位置和显示格式的设置了
                // label: {
                //   show: false,
                //   position: 'top',
                //   formatter: '{b}\n{c}'
                // }
              }
            }
          }
        ]
      }
      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option)
    },
    /**
     * 绘制饼图
     */
    drawRound () {
      const myChart = echarts.init(document.getElementById('bingtu'))
      const option = {
        title: {
          text: '库存占比图',
          left: 'left'
        },
        toolbox: {
          show: true,
          feature: {
            saveAsImage: {} // 导出图片
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b} : {c} ({d}%)'
        },
        legend: {
          type: 'scroll',
          orient: 'vertical',
          right: 10,
          top: 20,
          bottom: 20,
          data: this.legendData,
          selected: this.selected
        },
        series: [
          {
            // name: '物资名称',
            type: 'pie',
            radius: '55%',
            center: ['40%', '50%'],
            data: this.seriesData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
      myChart.setOption(option)
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
      this.categorys = str
    },
    search (page = this.pageNum) {
      this.getProductStockList(page)
    },
    // 改变页码
    handleSizeChange (newSize) {
      this.pageSize = newSize
      this.getProductStockList()
    },
    // 翻页
    handleCurrentChange (current) {
      this.pageNum = current
      this.getProductStockList()
    }
  }
}
</script>

<style scoped>

</style>
