# 数据库初始化

-- 创建库
create database if not exists my_db;

-- 切换库
use my_db;
delete from user
-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword   varchar(512)                         null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    email          varchar(256)                           null comment '邮箱',
    gender         varchar(10)                            null comment '性别 0-男 1-女',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    balance        bigint       default 30                not null comment '钱包余额,注册送30币',
    invitationCode varchar(256)                           null comment '邀请码',
    status         tinyint      default 0                 not null comment '账号状态（0- 正常 1- 封号）',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_userAccount (userAccount)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 代码生成器表
create table if not exists generator
(
    id                      bigint auto_increment comment 'id' primary key,
    name                    varchar(128)                       null comment '名称',
    description             text                               null comment '描述',
    basePackage             varchar(128)                       null comment '基础包',
    versionControl          bit(1)                             null comment 'git版本控制',
    forcedInteractiveSwitch bit(1)                             null comment '强制交互式开关',
    version                 varchar(128)                       null comment '版本',
    author                  varchar(128)                       null comment '作者',
    tags                    json                               null comment '标签列表（json 数组）',
    picture                 varchar(256)                       null comment '图片',
    fileConfig              json                               null comment '文件配置（json字符串）',
    modelConfig             json                               null comment '模型配置（json字符串）',
    distPath                varchar(256)                               null comment '代码生成器产物路径',
    status                  int      default 0                 not null comment '状态',
    thumbNum                int      default 0                 not null comment '点赞数',
    favourNum               int      default 0                 not null comment '收藏数',
    userId                  bigint                             not null comment '创建用户 id',
    createTime              datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime              datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete                tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '代码生成器' collate = utf8mb4_unicode_ci;

INSERT INTO  generator (id, name, description, basePackage, version, author, tags, picture, fileConfig,
                                     modelConfig, distPath, status, userId)
VALUES (1, 'ACM 模板项目', 'ACM 模板项目生成器', 'com.azhang', '1.0', '小张', '["Java"]',
        'https://pic.yupi.icu/1/_r0_c1851-bf115939332e.jpg', '{}', '{}', null, 0, 1);
INSERT INTO  generator (id, name, description, basePackage, version, author, tags, picture, fileConfig,
                                     modelConfig, distPath, status, userId)
VALUES (2, 'Spring Boot 初始化模板', 'Spring Boot 初始化模板项目生成器', 'com.azhang', '1.0', '小张', '["Java"]',
        'https://pic.yupi.icu/1/_r0_c0726-7e30f8db802a.jpg', '{}', '{}', null, 0, 1);
INSERT INTO  generator (id, name, description, basePackage, version, author, tags, picture, fileConfig,
                                     modelConfig, distPath, status, userId)
VALUES (3, '阿张外卖', '阿张外卖项目生成器', 'com.azhang', '1.0', '小张', '["Java", "前端"]',
        'https://pic.yupi.icu/1/_r1_c0cf7-f8e4bd865b4b.jpg', '{}', '{}', null, 0, 1);
INSERT INTO  generator (id, name, description, basePackage, version, author, tags, picture, fileConfig,
                                     modelConfig, distPath, status, userId)
VALUES (4, '阿张用户中心', '阿张用户中心项目生成器', 'com.azhang', '1.0', '小张', '["Java", "前端"]',
        'https://pic.yupi.icu/1/_r1_c1c15-79cdecf24aed.jpg', '{}', '{}', null, 0, 1);
INSERT INTO generator (id, name, description, basePackage, version, author, tags, picture, fileConfig,
                                     modelConfig, distPath, status, userId)
VALUES (5, '阿张商城', '阿张商城项目生成器', 'com.azhang', '1.0', '小张', '["Java", "前端"]',
        'https://pic.yupi.icu/1/_r1_c0709-8e80689ac1da.jpg', '{}', '{}', null, 0, 1);

-- 代码生成器点赞表（硬删除）
create table if not exists generator_thumb
(
    id          bigint auto_increment comment 'id' primary key,
    generatorId bigint                             not null comment '生成器 id',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (generatorId),
    index idx_userId (userId)
) comment '代码生成器点赞表';

-- 代码生成器收藏表（硬删除）
create table if not exists generator_favour
(
    id          bigint auto_increment comment 'id' primary key,
    generatorId bigint                             not null comment '生成器 id',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (generatorId),
    index idx_userId (userId)
) comment '代码生成器收藏表';

-- 积分表
create table if not exists score
(
    id           bigint auto_increment comment 'id' primary key,
    userId       bigint                   comment '创建用户id',
    scoreTotal   bigint null  comment '总积分' default 0,
    isSign       tinyint    comment '0表示未签到，1表示已签到' default 0,
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '积分表' collate = utf8mb4_unicode_ci;



-- 每日签到表
create table if not exists daily_check_in
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                             not null comment '签到人',
    description varchar(256)                       null comment '描述',
    addPoints   bigint   default 10                not null comment '签到增加积分个数',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '每日签到表';


-- 产品信息
create table if not exists product_info
(
    id             bigint auto_increment comment 'id' primary key,
    name           varchar(256)                           not null comment '产品名称',
    description    varchar(256)                           null comment '产品描述',
    userId         bigint                                 null comment '创建人',
    total          bigint                                 null comment '金额(分)',
    addPoints      bigint       default 0                 not null comment '增加积分个数',
    productType    varchar(256) default 'RECHARGE'        not null comment '产品类型（VIP-会员 RECHARGE-充值,RECHARGEACTIVITY-充值活动）',
    status         tinyint      default 0                 not null comment '商品状态（0- 默认下线 1- 上线）',
    expirationTime datetime                               null comment '过期时间',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除'
)
    comment '产品信息';

-- 产品订单
create table if not exists product_order
(
    id             bigint auto_increment comment 'id' primary key,
    orderNo        varchar(256)                           not null comment '订单号',
    codeUrl        varchar(256)                           null comment '二维码地址',
    userId         bigint                                 not null comment '创建人',
    productId      bigint                                 not null comment '商品id',
    orderName      varchar(256)                           not null comment '商品名称',
    total          bigint                                 not null comment '金额(分)',
    status         varchar(256) default 'NOTPAY'          not null comment '交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）
                                                                              USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)',
    payType        varchar(256) default 'WX'              not null comment '支付方式（默认 WX- 微信 ZFB- 支付宝）',
    productInfo    text                                   null comment '商品信息',
    formData       text                                   null comment '支付宝formData',
    addPoints      bigint       default 0                 not null comment '增加积分个数',
    expirationTime datetime                               null comment '过期时间',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '商品订单';




-- 付款信息
create table if not exists  payment_info
(
    id             bigint auto_increment comment 'id' primary key,
    orderNo        varchar(256)                           null comment '商户订单号',
    transactionId  varchar(256)                           null comment '微信支付订单号',
    tradeType      varchar(256)                           null comment '交易类型',
    tradeState     varchar(256)                           null comment '交易状态(SUCCESS：支付成功 REFUND：转入退款 NOTPAY：未支付 CLOSED：已关闭 REVOKED：已撤销（仅付款码支付会返回）
                                                                              USERPAYING：用户支付中（仅付款码支付会返回）PAYERROR：支付失败（仅付款码支付会返回）)',
    tradeStateDesc varchar(256)                           null comment '交易状态描述',
    successTime    varchar(256)                           null comment '支付完成时间',
    openid         varchar(256)                           null comment '用户标识',
    payerTotal     bigint                                 null comment '用户支付金额',
    currency       varchar(256) default 'CNY'             null comment '货币类型',
    payerCurrency  varchar(256) default 'CNY'             null comment '用户支付币种',
    content        text                                   null comment '接口返回内容',
    total          bigint                                 null comment '总金额(分)',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '付款信息';




INSERT INTO product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695338876708544514, '100坤币', '增加100坤币到钱包', 1691069533871013889, 1, 100, 'RECHARGEACTIVITY', 1, null, '2023-08-26 15:34:20', '2023-08-28 12:58:30', 0);
INSERT INTO product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695773972037839073, '9999坤币', '增加9999坤币到钱包', 1691069533871013889, 699, 9999, 'RECHARGE', 1, '2023-08-28 13:01:34', '2023-08-27 20:35:34', '2023-08-27 20:41:29', 0);
INSERT INTO product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695776766919888897, '1000坤币', '增加1000坤币到钱包', 1691069533871013889, 99, 1000, 'RECHARGE', 1, null, '2023-08-27 20:34:21', '2023-08-27 20:34:21', 0);
INSERT INTO product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695777072030339073, '3000坤币', '增加3000坤币到钱包', 1691069533871013889, 199, 3000, 'RECHARGE', 1, null, '2023-08-27 20:35:34', '2023-08-27 20:41:29', 0);
INSERT INTO product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695777203236556802, '15999坤币', '增加15999坤币到钱包', 1691069533871013889, 888, 15999, 'RECHARGE', 1, null, '2023-08-27 20:36:05', '2023-08-28 13:02:25', 0);
INSERT INTO product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1695778320091631617, '18999坤币', '增加18999坤币到钱包', 1691069533871013889, 999, 18999, 'RECHARGE', 1, null, '2023-08-27 20:40:32', '2023-08-28 13:02:42', 0);
INSERT INTO product_info (id, name, description, userId, total, addPoints, productType, status, expirationTime, createTime, updateTime, isDelete) VALUES (1697087470134259713, '10坤币', '签到获取', 1692848556158709762, 0, 10, 'RECHARGE', 0, null, '2023-08-31 11:22:37', '2023-08-31 11:22:37', 1);