import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '@/views/Login'
import config from '@/utils/config'
import { MessageBox } from 'element-ui'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      title: '新冠物资系统-登录'
    }
  },
  {
    path: '/main',
    name: 'Main',
    component: () => import('@/views/Main'),
    redirect: '/system/welcome',
    children: [
      {
        path: '/system/welcome',
        name: 'Welcome',
        component: () => import('@/views/WelCome'), // 系统欢迎页
        meta: {
          title: '新冠物资管理系统'
        }
      },
      {
        path: '/system/users',
        name: 'Users',
        component: () => import('@/views/system/user/Manager'),
        meta: {
          title: '用户管理'
        }
      },
      {
        path: '/healthy/yq-map',
        name: 'ChinaMap',
        component: () => import('@/views/healthy/ChinaMap'),
        meta: {
          title: '全国疫情地图'
        }
      },
      {
        path: '/healthy/point',
        name: 'HealthPoint',
        component: () => import('@/views/healthy/HealthPoint'),
        meta: {
          title: '健康打卡'
        }
      },
      {
        path: '/healthy/points',
        name: 'Points',
        component: () => import('@/views/healthy/Points'),
        meta: {
          title: '打卡记录'
        }
      },
      {
        path: '/system/icons',
        name: 'Icon',
        component: () => import('@/views/system/Icon'),
        meta: {
          title: '图标管理'
        }
      },
      {
        path: '/system/departments',
        name: 'Department',
        component: () => import('@/views/system/department/Department'),
        meta: {
          title: '部门管理'
        }
      },
      {
        path: '/system/attachments',
        name: 'FileManager',
        component: () => import('@/views/system/FileManager'),
        meta: {
          title: '文件管理'
        }
      },
      {
        path: '/system/roles',
        name: 'Role',
        component: () => import('@/views/system/role/Role'),
        meta: {
          title: '角色管理'
        }
      },
      {
        path: '/system/menus',
        name: 'MenuManager',
        component: () => import('@/views/system/menu-auth/MenuManager'),
        meta: {
          title: '菜单权限'
        }
      },
      {
        path: '/system/login-log',
        name: 'Login-Log',
        component: () => import('@/views/log/Login-Log'),
        meta: {
          title: '系统登录日志'
        }
      },
      {
        path: '/business/product/in-stocks',
        name: 'InStock',
        component: () => import('@/views/business/InStock'),
        meta: {
          title: '物资入库'
        }
      },
      {
        path: '/business/product/add-stocks',
        name: 'AddStock',
        component: () => import('@/views/business/AddStock'),
        meta: {
          title: '添加入库'
        }
      },
      {
        path: '/business/product/consumers',
        name: 'ThingsGo',
        component: () => import('@/views/business/ThingsGo'),
        meta: {
          title: '物资去向'
        }
      },
      {
        path: '/business/product/list',
        name: 'ThingsResource',
        component: () => import('@/views/business/ThingsResource'),
        meta: {
          title: '物资资料'
        }
      },
      {
        path: '/business/product/categories',
        name: 'Category',
        component: () => import('@/views/business/Category'),
        meta: {
          title: '物资类别'
        }
      },
      {
        path: '/business/product/suppliers',
        name: 'Supplier',
        component: () => import('@/views/business/Supplier'),
        meta: {
          title: '物资来源'
        }
      },
      {
        path: '/business/product/out-stocks',
        name: 'ProductOutStocks',
        component: () => import('@/views/business/OutStock'),
        meta: {
          title: '物资发放记录'
        }
      },
      {
        path: '/business/product/publish',
        name: 'Publish',
        component: () => import('@/views/business/Publish'),
        meta: {
          title: '发放物资'
        }
      },
      {
        path: '/business/product/stocks',
        name: 'Stocks',
        component: () => import('@/views/business/Stock'),
        meta: {
          title: '库存维护'
        }
      },
      {
        path: '/monitor/logs',
        name: 'SystemLog',
        component: () => import('@/views/monitor/SystemLog'),
        meta: {
          title: '系统日志'
        }
      },
      {
        path: '/monitor/druid',
        name: 'Druid',
        component: () => import('@/views/monitor/Druid'),
        meta: {
          title: 'SQL监控'
        }
      },
      {
        path: '/monitor/swagger-ui',
        name: 'SwaggerUI',
        component: () => import('@/views/monitor/SwaggerUI'),
        meta: {
          title: '接口版本一'
        }
      },
      {
        path: '/monitor/doc-ui',
        name: 'DocUI',
        component: () => import('@/views/monitor/DocUI'),
        meta: {
          title: '接口版本二'
        }
      },
      {
        path: '/common/404',
        name: 'PageNotFound',
        component: () => import('@/views/common/PageNotFound'),
        meta: {
          title: '请求页面不存在'
        }
      }
    ],
    meta: {
      title: '新冠物资管理系统'
    }
  }
]

const router = new VueRouter({
  routes,
  mode: 'history'
})

// const whiteList = ['/login'] // 不重定向白名单
router.beforeEach((to, from, next) => {
  // 导航守卫
  // 1、如果访问不存在的系统菜单，直接导航 404 页面
  // 2、否则是访问系统已有的菜单，接着就判断一个大的，是否登录
  // 3、如果登录了，token !== null，如果访问 /login 页面，就直接跳转到主页面，如果访问其他的就进行下面的逻辑
  // 4、如果没有登录 token === null，访问除 /login 之外的任何页面，都就跳转到 /login 页面
  if (to.matched.length === 0) {
    config.IS404 = true
    window.sessionStorage.removeItem('activePath')
    next('/common/404')
  } else {
    // 获取 token，标识是否登录
    const token = window.sessionStorage.getItem('token')
    if (token) {
      if (to.path === '/login') {
        next('/system/welcome')
        return
      }
      // 获取可跳转的 url
      const urls = JSON.parse(window.sessionStorage.getItem('urls'))
      if (urls !== undefined && urls !== null) {
        if (urls.indexOf(to.path) < 0) {
          config.IS404 = true
          window.sessionStorage.removeItem('activePath')
          next('/common/404')
          return
        }
      }
      if (to.meta.title) {
        document.title = to.meta.title
      }
      if (to.path === '/system/welcome') {
        window.sessionStorage.removeItem('activePath')
      }
      config.IS404 = (to.path === '/common/404' && from.path === '/')
      next()
    } else {
      if (to.path !== '/login') {
        MessageBox.confirm('登录过期，是否重新登录？', '系统提示', {
          type: 'warning'
        }).then(() => {
          next('/login')
        })
      } else {
        next()
      }
    }
  }
})

export default router
