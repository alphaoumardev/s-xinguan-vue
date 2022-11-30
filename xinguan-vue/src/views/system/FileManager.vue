<!--
  @author: qiukangming
  @description:
  @date: 2021/01/27 15:15
-->
<template>
  <div>
    <!-- 面包导航 -->
    <el-breadcrumb separator="/" style="padding-left:10px;padding-bottom:10px;font-size:12px;">
      <el-breadcrumb-item :to="{ path: '/main' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>系统管理</el-breadcrumb-item>
      <el-breadcrumb-item>文件管理</el-breadcrumb-item>
    </el-breadcrumb>
    <!-- 图片列表部分 -->
    <el-card class="card" id="card">
      <!-- 搜索部分 -->
      <el-form size="mini" :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="图片路径">
          <el-input clearable v-model="searchForm.path" placeholder="输入图片路径查询"></el-input>
        </el-form-item>
        <el-form-item label="图片后缀">
          <el-select clearable v-model="searchForm.suffix" placeholder="请选择">
            <el-option
              v-for="item in suffixs"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="search(1)">查询</el-button>
          <el-button type="success" @click="centerDialogVisible = true">上传<i class="el-icon-upload el-icon--right"></i>
          </el-button>
          <el-button icon="el-icon-search" @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
      <!-- 图片展示部分 -->
      <el-row :gutter="20" style="height:100%;" v-if="page.records.length > 0" v-loading="loading"
              element-loading-text="数据加载中...">
        <el-col style="margin-top:10px;" v-for="image in page.records" :key="image.id" :span="6">
          <div class="grid-content bg-purple">
            <el-image
              :alt="image.path"
              :fit="fits"
              :preview-src-list="prevImageList"
              style="width:200px;height:170px"
              :src="image.path"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <div>
              <el-tag
                size="mini"
                effect="dark"
                type="success"
                style="margin-left:20px;"
              >{{ image.width }}px X {{ image.height }}px
              </el-tag>
              <el-popconfirm
                confirm-button-text='必须的'
                cancel-button-text='不删了'
                icon="el-icon-info"
                icon-color="red"
                title="确定删除这个文件吗?"
                @confirm="del(image.name)">
                <el-button style="margin-left:30px;" icon="el-icon-delete" size="mini" type="text" slot="reference">删除
                </el-button>
              </el-popconfirm>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="height:100%;display:flex;justify-content:center;" v-else v-loading="loading">
        <div style="margin-top:10px;">暂无数据</div>
      </el-row>

      <!--分页-->
      <el-pagination
        v-if="page.total > 0"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="page.current"
        :page-sizes="[6, 12, 18, 24]"
        :page-size="page.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="page.total">
      </el-pagination>
      <!-- 上传弹出框 -->
      <el-dialog title="上传图片附件" @close="closeUpload" :visible.sync="centerDialogVisible" width="30%" center>
        <span>
          <el-upload
            style="text-align: center"
            class="upload-demo" drag
            :on-error="onError"
            :on-change="onChange" accept="image/*" :auto-upload="false" :multiple="true" ref="upload" :limit="10"
            :on-exceed="exceed"
            :on-success="handleUploadSuccess"
            :action="uploadUrl">
            <div>
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">
                将文件拖到此处，或 <em>点击上传</em>
              </div>
              <div class="el-upload__tip" slot="tip">只能上传jpg/png文件, 最多支持10张图片一起上传</div>
            </div>
          </el-upload>
        </span>
        <span slot="footer" class="dialog-footer">
          <el-button @click="closeUpload" size="small">取消上传</el-button>
          <el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload">上传文件</el-button>
        </span>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { listFile, deleteFile } from '@/api/file'

export default {
  name: 'FileManager',
  data () {
    return {
      uploadUrl: process.env.VUE_APP_UPLOAD_URL,
      centerDialogVisible: false,
      loading: false,
      fits: 'contain',
      searchForm: {
        mediaType: '',
        suffix: '',
        path: ''
      },
      page: {
        records: [],
        total: 0,
        size: 0,
        current: 0,
        orders: []
      },
      pageNum: 1,
      pageSize: 12,
      // 开启预览文件数组
      prevImageList: [],
      suffixs: [{
        value: '.jpg',
        label: 'jpg/JPG'
      }, {
        value: '.png',
        label: 'png/PNG'
      }, {
        value: '.gif',
        label: 'gif/GIF'
      }]
    }
  },
  methods: {
    handleSizeChange (val) {
      /* 将value赋值给size, 重新查询数据 */
      this.pageSize = val
      this.search()
    },
    handleCurrentChange (val) {
      this.pageNum = val
      this.search()
    },
    search (page = this.pageNum) {
      const _this = this
      _this.loading = true
      listFile(page, this.pageSize, this.searchForm).then(response => {
        _this.page = response.data
        _this.prevImageList = []
        // 加入预览列表
        _this.page.records.forEach(item => {
          _this.prevImageList.push(item.path)
        })
      }).finally(() => {
        _this.loading = false
      })
    },
    reset () {
      this.searchForm = {
        mediaType: '',
        suffix: '',
        path: ''
      }
      this.pageNum = 1
      this.pageSize = this.getPageSize()
      this.search()
    },
    del (fileName) {
      const _this = this
      deleteFile(fileName).then(response => {
        _this.NormalMsg(_this, response.message, 'success')
        _this.search()
      })
    },
    closeUpload () {
      this.centerDialogVisible = false
      this.$refs.upload.clearFiles()
    },
    /**
     * 超出允许上传的时候
     */
    exceed () {
      this.$message.warning('超出允许上传图片的数量')
    },
    handleUploadSuccess (response) {
      if (response.isSuccess) {
        this.NormalMsg(this, '图片附件上传成功！', 'success')
        this.centerDialogVisible = false
        this.search()
      } else {
        this.NormalMsg(this, response.message, 'error')
      }
    },
    /**
     * 点击上传到服务器
     */
    submitUpload () {
      this.$refs.upload.submit()
    },
    onChange (file, ignore) {
      if (file.size / 1024 / 1024 > 1) {
        this.NormalMsg(this, '上传文件大小超过 1 MB', 'error')
        this.$refs.upload.handleRemove(file)
      }
    },
    onError (ignore, file, ignore2) {
      this.NormalMsg(this, '文件 ' + file.name + ' 上传失败！', 'error')
    }
  },
  created () {
    this.search()
  },
  mounted () {
    // 获取屏幕高度
    document.getElementById('card').style.height = this.getWindowHeight()
  }
}
</script>

<style scoped>

</style>
