<template>
  <el-container class="main_container">
    <!--头部布局-->
    <el-header class="el-header">
      <!--login、项目名-->
      <div class="left_box">
        <img src="../assets/img/head.png" alt="">
      </div>
      <!--登录用户头像-->
      <div class="right_box">
        <el-dropdown>
          <div style="vertical-align: middle; cursor: pointer">
            <img :src="account.avatar" alt=""/>&nbsp;
            <span>{{ account.username }}</span>
          </div>
          <!--下拉菜单-->
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item icon="el-icon-info" @click.native="addOrUpdateVisible = true">个人信息</el-dropdown-item>
            <el-dropdown-item @click.native="chgPwd = true" icon="el-icon-lock">修改密码</el-dropdown-item>
            <el-dropdown-item @click.native="_getSystemAuthorInfo" icon="el-icon-s-custom">联系我们</el-dropdown-item>
            <el-dropdown-item @click.native="logout(1)" icon="el-icon-switch-button">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>
    <el-container class="el-container">
      <el-aside :width="isCollapse ? '64px' : '200px'" class="el-aside">
        <!--展开收起导航栏-->
        <div class="toggle_box" @click="toggleCollapse">|||</div>
        <el-menu :default-active="activePath" class="el-menu-vertical-demo"
                 background-color="#001529"
                 text-color="#fff"
                 active-text-color="#ffd04b"
                 :collapse="isCollapse"
                 :collapse-transition="false"
                 :unique-opened="true"
                 :router="true"
        >
          <menuTree :menuList="MenuList"></menuTree>
        </el-menu>
      </el-aside>
      <el-main :class="is404 ? 'el-main2' : 'el-main'">
        <!--路由视图-->
        <router-view/>
      </el-main>
    </el-container>
    <!--弹框部分-->
    <el-dialog title="修改密码" :visible.sync="chgPwd" width="30%" @closed="pwdDialogClosed" :close-on-click-modal="false">
      <el-form :model="pwdForm" status-icon :rules="rules" ref="pwdForm" label-width="95px">
        <el-form-item label="旧密码" prop="oldPwd">
          <el-input type="password" v-model="pwdForm.oldPwd" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPwd">
          <el-input type="password" v-model="pwdForm.newPwd" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="checkNewPwd">
          <el-input type="password" v-model="pwdForm.checkNewPwd" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item style="text-align: right; margin-bottom: 0">
          <el-button type="primary" @click="submitForm('pwdForm')">提交</el-button>
          <el-button @click="resetForm('pwdForm')">重置</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <!--用户添加/修改模态框 :userData="isEdit === true ? userData : null"-->
    <AddOrUpdate :addOrUpdateVisible="addOrUpdateVisible"
                 :isEdit="true"
                 :userId="account.id"
                 :isSelf="true"
                 @changeShow="showAddOrUpdate"
                 ref="addOrUpdateRef"
    ></AddOrUpdate>
  </el-container>
</template>

<script>
import MenuTree from '@/components/MenuTree'
import AddOrUpdate from '@/views/system/user/AddOrUpdate'
import { resetSelfPwd } from '@/api/users'
import { getMenus } from '@/api/menu'
import { getSystemAuthorInfo } from '@/api/common'
import config from '@/utils/config'

export default {
  name: 'Main',
  data () {
    return {
      config,
      isCollapse: false,
      activePath: '',
      MenuList: [],
      account: {},
      chgPwd: false,
      // 控制新增编辑弹窗的显示与隐藏
      addOrUpdateVisible: false,
      pwdForm: {
        oldPwd: '',
        newPwd: '',
        checkNewPwd: ''
      },
      rules: {
        oldPwd: [
          { required: true, message: '请输入旧密码', trigger: 'blur' }
        ],
        newPwd: [
          { required: true, message: '请输入新密码', trigger: 'blur' }
        ],
        checkNewPwd: [{
          required: true, message: '请再次输入新密码', trigger: 'blur'
        }]
      },
      is404: false
    }
  },
  created () {
    this.init()
  },
  watch: {
    $route: function (to, from) {
      this.is404 = to.path === '/common/404'
      const flg = from.path === '/common/404'
      if (flg) {
        this.activePath = to.path
        window.sessionStorage.setItem('activePath', this.activePath)
      }
    }
  },
  components: {
    MenuTree, AddOrUpdate
  },
  methods: {
    init () {
      this.account = JSON.parse(window.sessionStorage.getItem('account'))
      if (!this.account) {
        this.account = {
          id: 0,
          avatar: '',
          username: ''
        }
      }
      this.MenuList = []
      if (this.config.IS404) {
        this.is404 = true
      }
      getMenus().then((res) => {
        this.MenuList = res.data.menus
        // 将可以操作的 url 存入 sessionStorage
        window.sessionStorage.setItem('urls', JSON.stringify(res.data.urls))
        const path = window.sessionStorage.getItem('activePath')
        if (path && path !== '') {
          this.activePath = path
        }
      }).finally(() => {})
    },
    toggleCollapse () {
      this.isCollapse = !this.isCollapse
    },
    logout (type) {
      if (type === 1) {
        this.$confirm('确认退出系统吗?', '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          window.sessionStorage.clear()
          this.$router.push('/login')
        })
      } else {
        window.sessionStorage.clear()
        this.RightMessage(this, '登录提示', '您已成功修改密码，请重新登录！', 'info')
        this.$router.push('/login')
      }
    },
    submitForm (formName) {
      const _this = this
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // 就简单的前端比较一下确认密码
          if (_this.pwdForm.newPwd !== _this.pwdForm.checkNewPwd) {
            _this.NormalMsg(_this, '确认密码与新密码不一致！', 'error')
            return false
          }
          _this.pwdForm.id = _this.account.id
          resetSelfPwd(_this.pwdForm).then(() => {
            _this.NormalMsg(_this, '成功修改密码', 'success')
            // 删除浏览器存储的当前登录用户，提示重新登录
            _this.logout(0)
          })
        }
      })
    },
    resetForm (formName) {
      this.$refs[formName].resetFields()
    },
    pwdDialogClosed () {
      this.pwdForm = {
        oldPwd: '',
        newPwd: ''
      }
    },
    showAddOrUpdate (data) {
      this.addOrUpdateVisible = data !== 'false'
    },
    _getSystemAuthorInfo () {
      getSystemAuthorInfo().then(res => {
        const h = this.$createElement
        this.$msgbox({
          title: '联系我们',
          message: h('p', null, [
            h('div', null, [
              h('span', { style: 'font-weight: bold' }, '作者：'),
              h('span', null, res.data.author)
            ]),
            h('div', null, [
              h('span', { style: 'font-weight: bold' }, '电话：'),
              h('span', null, res.data.phone)
            ]),
            h('div', null, [
              h('span', { style: 'font-weight: bold' }, 'QQ：'),
              h('span', null, res.data.qq)
            ]),
            h('div', null, [
              h('span', { style: 'font-weight: bold' }, '微信：'),
              h('span', null, res.data.wechat)
            ])
          ]),
          showCancelButton: false,
          showConfirmButton: false,
          center: true,
          dangerouslyUseHTMLString: true
        }).then(ignore => {}).catch(ignore => {})
      })
    }
  }
}
</script>

<style lang="less" scoped>
/*设置整个容器的高度*/
.main_container {
  height: 100%;
}

/*头部布局*/
.el-header {
  background-color: #001529;
  display: flex;
  justify-content: space-between;
  padding-left: 0;
  color: #FFFFFF;
  align-items: center;
  font-size: 20px;
  /*左边logo和标题*/

  .left_box {
    display: flex;
    align-items: center;
    /*logo*/

    img {
      width: 35%;
      height: 60px;
      margin: 0 10px;
    }

    /*标题*/

    span {
      margin-left: 15px;
    }
  }

  /*右边的登录头像*/

  .right_box {
    .el-dropdown > div {
      img {
        width: 35px;
        height: 35px;
        border-radius: 50%;
        background-color: #FFFFFF;
        background-size: contain;
        vertical-align: middle;
      }

      span {
        font-size: 18px;
        color: white;
        text-align: center;
      }
    }
  }
}

/*侧边栏*/
.el-aside {
  background-color: #001529;

  .el-menu {
    border-right: none;
  }

  /*展开/收起*/

  .toggle_box {
    background-color: #001529;
    font-size: 15px;
    font-weight: bold;
    line-height: 24px;
    color: #FFFFFF;
    letter-spacing: 0.2em;
    text-align: center;
    cursor: pointer;

  }
}

/*内容主体*/
/deep/ .el-main {
  background-color: #E9EEF3;
}

/deep/ .el-main2 {
  background-color: #E9EEF3;
  padding: 0;
}

/*下拉菜单的样式*/
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}
</style>
