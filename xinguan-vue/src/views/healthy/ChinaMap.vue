<!--
  @author: qiukangming
  @description: 全国疫情地图
  @date: 2021/01/22 22:17
-->
<template>
  <div class="main">
    <!--用户管理面包屑-->
    <el-breadcrumb separator="/" style="padding-left: 10px;padding-bottom: 10px;font-size: 12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>健康报备</el-breadcrumb-item>
      <el-breadcrumb-item>全国疫情</el-breadcrumb-item>
    </el-breadcrumb>
    <!--echarts 容器-->
    <div id="chart1"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

// 使用 npm install echarts 默认版本是 5.0.1 但是没有 map 模块，所以手动写成 4.7.0 然后再 npm install一下
import 'echarts/map/js/china'
import { getYqData } from '@/api/healthy'
const option = {
  title: {
    text: '全国疫情地图',
    show: false,
    // x轴居中
    x: 'center',
    textStyle: {
      color: '#97e73c'
    }
  },
  tooltip: {
    // 鼠标移入时的提示信息，一定要知道的是它和series数据是平级的
    tigger: 'item',
    // formatter:'地区: {@name}<br/>感染人口: {@value}<br/>当日新增: {@add}'
    formatter: (params) => {
      return '地区: ' + params.data.name + '<br/>' + '感染人口: ' + params.data.value + '<br/>' + '当日新增: ' + params.data.add
    }
  },
  series: [
    // series配置出来了以后才会有地图显示  所以series是关键
    {
      // 图表类型
      type: 'map',
      // 中国地图
      map: 'china',
      // 数据
      data: [],
      label: {
        // 设置地图每一个角落的名字
        show: true,
        color: 'red'
      },
      itemStyle: {
        borderColor: 'blue'
      },
      emphasis: {
        label: {
          color: '#fff',
          fontSize: '10px'
        },
        itemStyle: {
          areaColor: 'green'
        }
      }
    }
  ],
  roam: true,
  scaleLimit: {
    max: 5,
    min: 0.5
  },
  visualMap: {
    // 设置类型为分段类型
    type: 'piecewise',
    show: true,
    // 设置不用的段位
    pieces: [
      // 不指定 max，表示 max 为无限大（Infinity）
      { min: 10000 },
      { min: 1000, max: 9999 },
      { min: 100, max: 999 },
      { min: 10, max: 99 },
      { min: 1, max: 9 },
      { value: 0 }
    ],
    inRange: {
      // 范围所对应的颜色
      color: ['#fff', '#ffaa85', '#660208']
    }
  }
}
export default {
  name: 'ChinaMap',
  mounted () {
    this.init()
    // 这个是生命周期函数
    // 初始化表格
    this.mycharts = echarts.init(document.getElementById('chart1'))
  },
  data () {
    return {
      mycharts: '',
      loading: true
    }
  },
  methods: {
    init () {
      const _this = this
      getYqData().then(res => {
        // res.data.data 中国数据，累计确诊人数
        // res.data.today 中国数据，累计新增确诊人数
        const list = []
        const a = res.data.data
        const b = res.data.today
        for (let i = 0; i < a.length; i++) {
          list.push({
            name: a[i].name,
            value: a[i].value,
            add: b[i].value
          })
        }
        option.series[0].data = list
        _this.mycharts.setOption(option)
      })
    }
  }
}
</script>

<style lang="less" scoped>
.main{
  height: 100%;
}
#chart1{
  width: 100%;
  height: 95%;
  margin-top: 20px;
}
</style>
