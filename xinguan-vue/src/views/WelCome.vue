<template>
  <div>
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>欢迎</el-breadcrumb-item>
    </el-breadcrumb>
    <el-row :gutter="15">
      <!-- 左边部分 -->
      <el-col :span="13">
        <!-- 用户信息表格 -->
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>当前用户信息</span>
          </div>
          <el-row :gutter="5">
            <el-col :span="4">
              <el-avatar
                style="margin-top: 10px"
                shape="square"
                :size="80"
                :src="userInfo.avatar"
              ></el-avatar>
            </el-col>
            <el-col :span="20">
              <el-table :data="tableInfo" border>
                <el-table-column align="center" prop="username" label="用户账号"></el-table-column>
                <el-table-column align="center" prop="nickname" label="用户昵称"></el-table-column>
                <el-table-column align="center" prop="deptName" label="所属部门"></el-table-column>
                <el-table-column align="center" prop="roles" label="用户角色" show-overflow-tooltip></el-table-column>
              </el-table>
            </el-col>
          </el-row>
        </el-card>
        <!-- 功能列表 -->
        <el-row style="margin-top:10px;">
          <el-card style="height:135px;">
            <el-col :span="6">
              <div class="grid-content bg-purple" style="text-align: center">
                <router-link to="/business/product/list">
                  <img
                    src="@/assets/img/1.svg" alt
                    width="68"
                    style="cursor: pointer;"
                  />
                </router-link>
                <div style="font-size:12px;margin-top:5px;">物资资料</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="grid-content bg-purple-light" style="text-align: center">
                <router-link to="/business/product/stocks">
                  <img
                    src="@/assets/img/2.svg" alt
                    width="68"
                    style="cursor: pointer"
                  />
                </router-link>
                <div style="font-size:12px;margin-top:5px;">物资库存</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="grid-content bg-purple-light" style="text-align: center">
                <router-link to="/business/product/add-stocks">
                  <img
                    src="@/assets/img/3.svg"
                    alt
                    width="68"
                    style="cursor: pointer;margin-left:20px;"
                  />
                </router-link>
                <div style="font-size:12px;margin-top:5px;">物资入库</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="grid-content bg-purple" style="text-align: center">
                <router-link to="/business/product/publish">
                  <img
                    src="@/assets/img/4.svg"
                    alt
                    width="68"
                    style="cursor: pointer;"
                  />
                </router-link>
                <div style="font-size:12px;margin-top:5px;">物资发放</div>
              </div>
            </el-col>
          </el-card>
        </el-row>
        <el-card style="margin-top: 10px">
          <!-- 用户登入报表 -->
          <div id="loginReport" style="width: 100%"></div>
        </el-card>
      </el-col>
      <!-- 右边部分 -->
      <el-col :span="11">
        <div class="grid-content bg-purple">
          <el-card id="card">
            <el-carousel height="180px" :interval="4000" type="card">
              <el-carousel-item v-for="(item, index) in images" :key="index">
                <img :src="item.url" alt="alt"/>
              </el-carousel-item>
            </el-carousel>
            <el-divider>疫情数据</el-divider>
            <el-tabs type="border-card">
              <el-tab-pane label="疫情概览">
                <el-table size="mini" border v-loading="loading" :data="info" style="width: 100%" :max-height="this.getTableMaxHeight(0.63)">
                  <el-table-column align="center" prop="name" label="名称" width="100"></el-table-column>
                  <el-table-column align="center" prop="value" label="数量" width="90">
                    <template slot-scope="scope">
                      <el-tag effect="plain" size="mini">{{scope.row.value}}人</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column align="center" prop="yesterday" label="较昨日" width="90">
                    <template slot-scope="scope">
                      <el-tag effect="plain" size="mini" v-if="scope.row.yesterday>0" type="danger">{{scope.row.yesterday}} 人</el-tag>
                      <el-tag effect="plain" size="mini" v-else type="success">{{scope.row.yesterday}}人</el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column align="center" prop="name" label="时间"><span style="font-size: 11px;">{{times}}</span></el-table-column>
                </el-table>
              </el-tab-pane>
              <el-tab-pane label="TOP10城市" >
                <el-table size="mini" border :data="top10" style="width: 100%;" :max-height="this.getTableMaxHeight(0.63)">
                  <el-table-column prop="name" label="城市名称" align="center"></el-table-column>
                  <el-table-column prop="ename" label="City Name" align="center"></el-table-column>
                  <el-table-column prop="jwsrNum" label="确诊人数" align="center">
                    <template slot-scope="scope">
                      <el-tag v-text="scope.row.jwsrNum+'人'" size="mini" type="danger" effect="plain"></el-tag>
                    </template>
                  </el-table-column>
                </el-table>
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import echarts from 'echarts'
import { loginReport } from '@/api/login-log'
import jsonp from 'jsonp'

export default {
  name: 'WelCome',
  components: {},
  data () {
    return {
      xData: [],
      yData: [],
      myData: [],
      userInfo: {},
      tableInfo: [{
        username: '--',
        nickname: '--',
        deptName: '--',
        roles: '--'
      }],
      images: [{
        url: require('../assets/img/slid1.png')
      }, {
        url: require('../assets/img/slid2.png')
      }, {
        url: require('../assets/img/slid3.png')
      }],
      // 疫情数据
      loading: false,
      info: [],
      times: '',
      top10: []
    }
  },
  created () {
    this.userInfo = JSON.parse(window.sessionStorage.getItem('account'))
    this.tableInfo[0].username = this.userInfo.username
    this.tableInfo[0].nickname = this.userInfo.nickname
    this.tableInfo[0].deptName = this.userInfo.deptName
    this.tableInfo[0].roles = this.userInfo.roles
    this.getData()
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight()
    document.getElementById('loginReport').style.height = this.getWindowHeight(0.655)
    this.getLoginReport()
  },
  activated () {
  },
  methods: {
    /**
     * 绘制登入报表
     */
    draw () {
      const myChart = echarts.init(document.getElementById('loginReport'))
      // 指定图表的配置项和数据
      const option = {
        title: {
          text: '用户登入统计'
        },
        toolbox: {
          show: true,
          feature: {
            dataZoom: {
              yAxisIndex: 'none'
            },
            dataView: { readOnly: false }, //  缩放
            magicType: { type: ['bar', 'line'] }, // 折线 直方图切换
            restore: {}, // 重置
            saveAsImage: {} // 导出图片
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#eee'
            }
          }
        },
        legend: {
          type: 'plain',
          data: ['全部', '我']
        },
        xAxis: {
          data: this.xData
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '全部',
            data: this.yData,
            type: 'bar',
            showBackground: true,
            animationDuration: 1500,
            animationEasing: 'cubicInOut',
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(30, 144, 255，0.5)'
            }
          },
          {
            name: '我',
            data: this.myData,
            type: 'bar',
            showBackground: true,
            animationDuration: 2000,
            animationEasing: 'cubicInOut'
          }
        ]
      }
      // 使用刚指定的配置项和数据显示图表。
      myChart.setOption(option)
    },
    /**
     * 获取登录报表数据
     */
    getLoginReport () {
      loginReport(this.userInfo.username).then(res => {
        const $this = this
        this.xData = []
        this.yData = []
        this.myData = []
        res.data.all.forEach(e1 => {
          $this.xData.push(e1.days)
          $this.yData.push(e1.count)
        })
        for (let i = 0; i < this.xData.length; i++) {
          let count = 0
          for (let j = 0; j < res.data.me.length; j++) {
            if ($this.xData[i] === res.data.me[j].days) {
              count = res.data.me[j].count
              break
            } else {
              count = 0
            }
          }
          $this.myData.push(count)
        }
        this.draw()
      }).finally(() => {
      })
    },
    getData () {
      jsonp('https://interface.sina.cn/news/wap/fymap2020_data.d.json?_=1580892522427', {}, (error, data) => {
        if (!error) {
          this.buildTable(data)
        }
      })
    },
    /**
     * 构建表格数据:每日新增....
     */
    buildTable (res) {
      const data = res.data
      const data1 = {
        name: '现存确诊',
        value: data.econNum,
        yesterday: data.add_daily.addecon_new
      }
      const data2 = {
        name: '累计境外输入',
        value: data.jwsrNum,
        yesterday: data.add_daily.addjwsr
      }
      const data3 = {
        name: '现无症状',
        value: data.asymptomNum,
        yesterday: data.add_daily.addasymptom
      }
      const data4 = {
        name: '现存确诊重症',
        value: data.heconNum,
        yesterday: data.add_daily.addhecon_new
      }
      const data5 = {
        name: '累计确诊',
        value: data.gntotal,
        yesterday: data.add_daily.addcon_new
      }
      const data6 = {
        name: '累计死亡',
        value: data.deathtotal,
        yesterday: data.add_daily.adddeath_new
      }
      const data7 = {
        name: '累计治愈',
        value: data.curetotal,
        yesterday: data.add_daily.addcure_new
      }
      const data8 = {
        name: '现存疑似',
        value: data.sustotal,
        yesterday: data.add_daily.wjw_addsus_new
      }
      this.times = data.times
      this.top10 = data.jwsrTop

      this.info.push(data1)
      this.info.push(data2)
      this.info.push(data3)
      this.info.push(data4)
      this.info.push(data5)
      this.info.push(data6)
      this.info.push(data7)
      this.info.push(data8)
      this.loading = false
    }
  }
}
</script>

<style scoped lang="less">
.el-carousel__item h3 {
  color: #475669;
  font-size: 14px;
  opacity: 0.75;
  line-height: 200px;
  margin: 0;
}
.el-carousel__item > img {margin: 0; padding: 0; width: 100%; height: 100%; }
</style>
