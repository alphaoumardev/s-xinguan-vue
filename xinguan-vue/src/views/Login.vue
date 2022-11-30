<template>
  <!--登陆表单的容器-->
  <div class="login_container">
    <!--登陆区域-->
    <div class="login_box">
      <!--头像-->
      <div class="avatar_box">
        <img :src="head" alt="">
      </div>
      <!--表单-->
      <div>
        <el-form :model="loginForm" :rules="loginRules" ref="loginForm" label-width="0px" class="login_form">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" @change="getAvatar" type="text" placeholder="请输入用户名称"
                      prefix-icon="el-icon-user-solid"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入登录密码"
                      prefix-icon="el-icon-lock"></el-input>
          </el-form-item>
          <el-form-item prop="verifyCode">
            <div class="verifyCode_box">
              <el-input v-model="loginForm.verifyCode" type="text" placeholder="请输入验证码" prefix-icon="el-icon-mobile"
                        class="verifyCode"></el-input>
              <img :src="verifyCodeImage" alt="" class="verifyCode_img" @click="newVerifyCode">
            </div>
          </el-form-item>
          <el-form-item class="login_btn">
            <el-button type="primary" @click="submitForm('loginForm')">{{ loading ? '登录中...' : '登 录'}}</el-button>
            <el-button @click="resetForm('loginForm')">重 置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { login, getUserAvatar, verifyCode } from '@/api/users'

export default {
  name: 'Login',
  data () {
    return {
      loginForm: {
        username: '',
        password: '',
        verifyCodeId: 'first',
        verifyCode: ''
      },
      verifyCodeImage: null,
      loginRules: {
        username: [
          { required: true, message: '请输入用户名称', trigger: 'blur' },
          { min: 3, max: 16, message: '长度在 3 到 16 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入登录密码', trigger: 'blur' },
          { min: 3, max: 16, message: '长度在 3 到 16 个字符', trigger: 'blur' }
        ],
        verifyCode: [
          { required: true, message: '请输入验证码', trigger: 'blur' }
        ]
      },
      head: require('@/assets/img/default.jpg'), // 加载本地静态图片
      loading: false
    }
  },
  methods: {
    submitForm (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const _this = this
          _this.loading = true
          login(this.loginForm).then(res => {
            // 设置用户信息
            window.sessionStorage.setItem('account', JSON.stringify(res.data.data))
            // 设置 token 信息
            window.sessionStorage.setItem('token', res.data.token)
            _this.$router.push('/main')
            return _this.$message({
              message: '欢迎进入系统',
              type: 'success'
            })
          }).finally(() => {
            _this.loading = false
          })
        }
      })
    },
    resetForm (formName) {
      this.$refs[formName].resetFields()
      this.head = require('../assets/img/default.jpg')
    },
    getAvatar (username) {
      const _this = this
      getUserAvatar(username).then(res => {
        _this.head = res.data
      })
    },
    getVerifyCode (historyId) {
      verifyCode(historyId).then(res => {
        this.loginForm.verifyCodeId = res.data.id
        this.verifyCodeImage = 'data:image/gif;base64,' + res.data.image
      })
    },
    newVerifyCode () {
      this.getVerifyCode(this.loginForm.verifyCodeId)
    }
  },
  mounted () {
    this.getVerifyCode(this.loginForm.verifyCodeId)
  }
}
</script>

<style lang="less" scoped>
.login_container {
  height: 100%;
  //background-color: aquamarine;
  background-size: 100%;
  background-image: url('../../public/background.jpg');
}

.login_box {
  width: 450px;
  height: 380px;
  background-color: #FFFFFF;
  border-radius: 3px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);

  .avatar_box {
    width: 130px;
    height: 130px;
    border: 1px solid #EEEEEE;
    border-radius: 50%;
    padding: 10px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    margin: -65px auto;
    background-color: #FFFFFF;

    img {
      width: 100%;
      height: 100%;
      border-radius: 50%;
      background-color: #EEEEEE;
    }

    img[src=""], img:not([src]) {
      opacity: 0;
      border: none;
      visibility: hidden;
      max-width: none;
    }
  }

  .login_form {
    position: absolute;
    bottom: 0;
    width: 100%;
    padding: 0 20px;
    box-sizing: border-box;

    .login_btn {
      display: flex;
      justify-content: flex-end;
    }

    .verifyCode_box {
      display: flex;

      .verifyCode {
        width: 70%;
        justify-content: left;
      }

      .verifyCode_img {
        padding-left: 5px;
        padding-top: 2px;
        width: 25%;
        height: 35px;
        justify-content: flex-end;
        cursor: pointer;
      }
    }
  }
}
</style>
