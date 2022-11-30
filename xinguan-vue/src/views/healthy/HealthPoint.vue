 <!--
  @author qiukangming
  @description 健康打卡页面
  @date 2021/03/23 21:11
-->
<template>
  <div>
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>健康报备</el-breadcrumb-item>
      <el-breadcrumb-item>每日健康打卡</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card id="card">
      <el-alert :closable="false" style="margin-bottom: 20px;" v-if="reported" show-icon :title="reportedTitle"
                type="success"></el-alert>
      <h5>基本信息
        <div style="float: right;margin-bottom: 10px;vertical-align: center;">
          <el-button @click="openHistory" size="small" type="warning">签到记录</el-button>
          <el-button @click="reset" size="small" v-if="!reported">重置</el-button>
        </div>
      </h5>
      <!-- 历史打卡数据列表 -->
      <el-drawer :with-header="false" :visible.sync="drawer" :direction="direction">
        <el-card v-for="item in historyPage.records" :key="item.id" style="margin: 10px;">
          <div class="text item">
            <ul>
              <li>打卡人:{{ loginUser.username }}</li>
              <li>打卡位置:{{ item.address }}</li>
              <li>打卡时间:{{ item.createTime }}</li>
              <li>健康状况:
                <div style="float: right">
                  <el-tag v-if="item.situation===0" size="small" effect="dark" type="success">健康</el-tag>
                  <el-tag v-else-if="item.situation===1" size="small" effect="dark" type="danger">发热</el-tag>
                  <el-tag v-else-if="item.situation===2" size="small" effect="dark" type="warning">其他情况</el-tag>
                </div>
              </li>
            </ul>
          </div>
        </el-card>
        <el-pagination
          v-if="historyPage.total > 0"
          style="width: 100%; padding: 10px 15px"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="historyPage.current"
          :page-sizes="[5, 10, 20, 30]"
          :page-size="historyPage.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="historyPage.total">
        </el-pagination>
      </el-drawer>
      <!-- 分割线 -->
      <el-divider></el-divider>
      <!-- 打卡提交表单 -->
      <el-form size="small" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="grid-content bg-purple">
              <el-form-item label="打卡人">
                <el-input v-model="loginUser.nickname" disabled></el-input>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="grid-content bg-purple">
              <div class="grid-content bg-purple">
                <el-form-item label="打卡时间">
                  <el-date-picker
                    disabled
                    v-model="nowTime"
                    type="datetime"
                    placeholder="选择日期时间">
                  </el-date-picker>
                </el-form-item>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="grid-content bg-purple">
              <div class="grid-content bg-purple">
                <el-form-item label="所属部门">
                  <el-input v-model="loginUser.deptName" disabled></el-input>
                </el-form-item>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-form>
      <!-- 标题 -->
      <h5 style="margin: 0;padding: 0;">健康打卡</h5>
      <!-- 分割线 -->
      <el-divider></el-divider>
      <el-form size="small" :inline="true" :label-position="'top'" :model="pointUserInfo" :rules="rules"
               ref="ruleFormRef" label-width="100px" class="demo-ruleForm">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="grid-content bg-purple">
              <el-form-item label="省份" prop="valueProvince">
                <el-select :disabled="reported" v-model="pointUserInfo.valueProvince" placeholder="请选择省"
                           @change="changeProvince">
                  <el-option v-for="item in provinceOptions" :key="item.value" :label="item.label"
                             :value="item.value"></el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="grid-content bg-purple">
              <el-form-item label="城市" prop="valueCity">
                <el-select :disabled="reported" v-model="pointUserInfo.valueCity" placeholder="请选择市"
                           @change="changeCity">
                  <el-option v-for="item in cityOptions" :key="item.value" :label="item.label"
                             :value="item.value"></el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="grid-content bg-purple">
              <el-form-item label="区县" prop="valueOrigin">
                <el-select :disabled="reported" v-model="pointUserInfo.valueOrigin" placeholder="请选择区"
                           @change="changeOrigin">
                  <el-option v-for="item in originOptions" :key="item.label" :label="item.label"
                             :value="item.value"></el-option>
                </el-select>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="grid-content bg-purple">
              <el-form-item v-if="reported" label="打卡位置" prop="valueOrigin">
                <el-tag size="small">{{ pointUserInfo.address }}</el-tag>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="grid-content bg-purple">
              <el-form-item label="目前健康状况" prop="situation">
                <el-radio-group :disabled="reported" v-model="pointUserInfo.situation">
                  <el-radio :label="0">健康</el-radio>
                  <el-radio :label="1">有咳嗽发热症状</el-radio>
                  <el-radio :label="2">其他情况</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="grid-content bg-purple">
              <el-form-item label="接触的人是否有疑似症状?  (冠状病毒检测结果为阳性或尚在等待检测结构)" prop="touch">
                <el-radio-group :disabled="reported" v-model="pointUserInfo.touch">
                  <el-radio :label="1">是</el-radio>
                  <el-radio :label="0">否</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="grid-content bg-purple">
              <el-form-item label="自2021年1月1日起,是否在湖北省停留过 （包括转车路过)" prop="passby">
                <el-radio-group :disabled="reported" v-model="pointUserInfo.passby">
                  <el-radio :label="1">是</el-radio>
                  <el-radio :label="0">否</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="grid-content bg-purple">
              <el-form-item label="自2021年1月1日起,是否有接待过来自湖北的客户,亲戚或朋友)" prop="reception">
                <el-radio-group :disabled="reported" v-model="pointUserInfo.reception">
                  <el-radio :label="1">是</el-radio>
                  <el-radio :label="0">否</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24" style="margin-left: 30%">
            <el-button style="margin-top: 5px; width: 150px" type="primary" size="small" @click="onSubmit" v-if="!reported">立即打卡
            </el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { areaList } from '@/utils/provices'
import { isReport, report, history } from '@/api/healthy'

export default {
  name: 'HealthPoint',
  components: {},
  data () {
    return {
      timer: null,
      areaList: areaList,
      // 是否打卡，默认为false
      reported: false,
      reportedTitle: '今日已成功打卡,勤洗手,戴口罩,不群聚,打卡时间:[#]',
      drawer: false,
      direction: 'rtl',
      pageNum: 1,
      pageSize: this.getPageSize(),
      historyPage: {
        records: [],
        total: 0,
        size: 0,
        current: 0,
        orders: []
      },
      loginUser: JSON.parse(window.sessionStorage.getItem('account')),
      // 打卡时间
      nowTime: new Date(),
      pointUserInfo: {
        situation: '',
        touch: '',
        passby: '',
        reception: '',
        valueProvince: '',
        valueCity: '',
        valueOrigin: '',
        address: ''
      },
      rules: {
        situation: [
          { required: true, message: '请选择当前情况', trigger: 'blur' }
        ],
        touch: [
          { required: true, message: '请选择是否接触', trigger: 'blur' }
        ],
        passby: [
          { required: true, message: '请选择是否路过', trigger: 'blur' }
        ],
        reception: [
          { required: true, message: '请选择是否招待', trigger: 'blur' }
        ],
        valueProvince: [
          { required: true, message: '请输入省份', trigger: 'blur' }
        ],
        valueCity: [{ required: true, message: '请输入城市', trigger: 'blur' }],
        valueOrigin: [
          { required: true, message: '请输入区县', trigger: 'blur' }
        ]
      },
      provinceOptions: [], // 省列表
      cityOptions: [], // 市下拉框数据
      originOptions: [], // 区下拉框数据
      repNotify: null
    }
  },
  created () {
    this.buildProvesData()
    // 查看是否打卡
    this.isReportShow()
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight()
  },
  activated () {
  },
  methods: {
    openHistory () {
      this.getHistoryList()
      this.drawer = true
    },
    onSubmit () {
      const _this = this
      _this.$refs.ruleFormRef.validate(async vaild => {
        if (vaild) {
          _this.pointUserInfo.address = _this.pointUserInfo.valueProvince + '/' + _this.pointUserInfo.valueCity + '/' + _this.pointUserInfo.valueOrigin
          report(_this.pointUserInfo).then(() => {
            _this.reported = true
            _this.pointUserInfo.createTime = _this.nowTime
            this.reportedTitle = this.reportedTitle.replace('#', this.dateUtils.localDateTime(_this.nowTime.getTime(), '-'))
            // 清除定时器
            clearInterval(this.timer)
            _this.NormalMsg(_this, '今日健康打卡成功！', 'success')
          }).catch().finally()
        }
      })
    },
    reset () {
    },
    handleSizeChange (val) {
      this.pageSize = val
      this.getHistoryList()
    },
    handleCurrentChange (val) {
      this.pageNum = val
      this.getHistoryList()
    },
    changeProvince (val) {
      this.pointUserInfo.valueCity = ''
      this.pointUserInfo.valueOrigin = ''
      this.cityOptions = []
      this.originOptions = []
      this.provinceOptions.forEach((province) => {
        if (val.toString() === province.value) {
          this.pointUserInfo.valueProvince = province.label
          this.cityOptions = province.children
        }
      })
    },
    changeCity (val) {
      this.pointUserInfo.valueOrigin = ''
      this.originOptions = []
      this.cityOptions.forEach((city) => {
        if (val.toString() === city.value) {
          this.pointUserInfo.valueCity = city.label
          this.originOptions = city.children
        }
      })
    },
    changeOrigin (val) {
      this.originOptions.forEach((origin) => {
        if (val.toString() === origin.value) {
          this.pointUserInfo.valueOrigin = origin.label
        }
      })
      this.$forceUpdate()
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
    isReportShow () {
      isReport().then(res => {
        if (res.data) {
          this.pointUserInfo = res.data
          this.reportedTitle = this.reportedTitle.replace('#', res.data.createTime).replace('T', ' ')
          this.nowTime = this.pointUserInfo.createTime
          const strs = this.pointUserInfo.address.split('/')
          this.pointUserInfo.valueProvince = strs[0]
          this.pointUserInfo.valueCity = strs[1]
          this.pointUserInfo.valueOrigin = strs[2]
          this.reported = true
        } else {
          this.reported = false
          this.repNotify = this.$notify.warning({
            title: '温馨提示',
            message: '您今日还未打卡,别忘记了哟~',
            duration: 15000
          })
        }
        if (!this.reported) {
          this.timer = setInterval(() => {
            this.nowTime = new Date()
          }, 1000)
        }
      }).catch().finally()
    },
    getHistoryList () {
      history(this.pageNum, this.pageSize).then(res => {
        this.historyPage = res.data
        this.historyPage.records.forEach(item => {
          item.createTime = item.createTime.replace('T', ' ')
        })
      }).catch().finally()
    }
  },
  beforeDestroy () {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  destroyed () {
    // 关闭页面时，如果弹框未消失，则删除
    if (this.repNotify) {
      this.repNotify.close()
    }
  }
}
</script>

<style scoped>

</style>
