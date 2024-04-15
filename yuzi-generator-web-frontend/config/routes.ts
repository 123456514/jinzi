export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {name: '登录', path: '/user/login', component: './User/Login' },
      { name: '注册账号', path: '/user/register', component: './User/Register' }
    ]
  },
  { path: '/', name: '欢迎', icon: 'smile', component: './Welcome' },

  { path: '/list/page/vo', icon: 'home', component: './Index', name: "主页" },
  { path: '/recharge/list', icon: 'PayCircleOutlined', name: '积分商城', component: './Recharge' },
  { path: '/order/list', name: '我的订单', icon: 'ProfileOutlined', component: './Order/OrderList',},
  {
    path: '/generator/add',
    icon: 'plus',
    component: './Generator/Add',
    name: '创建生成器',
  },
  {
    path: '/generator/update',
    icon: 'plus',
    component: './Generator/Add',
    name: '修改生成器',
    hideInMenu: true,
  },
  {
    path: '/generator/use/:id',
    icon: 'home',
    component: './Generator/Use',
    name: '使用生成器',
    hideInMenu: true,
  },
  {
    path: '/generator/detail/:id',
    icon: 'home',
    component: './Generator/Detail',
    name: '生成器详情',
    hideInMenu: true,
  },
  {
    path: '/test/file',
    icon: 'home',
    component: './Test/File',
    name: '文件上传下载测试',
    hideInMenu: true,
  },
  {
    path: '/admin',
    icon: 'crown',
    name: "管理页",
    access: 'canAdmin',
    routes: [
      { path: '/admin', redirect: '/admin/user' },
      { icon: 'table', path: '/admin/user', component: './Admin/User', name: "用户管理" },
      { icon: 'tools', path: '/admin/generator', component: './Admin/Generator', name: "生成器管理" },
    ],
  },
  {
    path: '/account/center',
    name: '个人中心',
    icon: 'UserOutlined',
    component: './User/UserInfo',
    hideInMenu: true,
  },
  { path: '*', layout: false, component: './404' },
];
