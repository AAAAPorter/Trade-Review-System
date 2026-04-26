<template>
  <el-container class="app-shell">
    <el-aside width="220px" class="sidebar">
      <div class="brand">个人交易复盘</div>
      <el-menu router :default-active="$route.path" class="nav">
        <el-menu-item index="/dashboard">首页仪表盘</el-menu-item>
        <el-menu-item index="/trades">交易记录</el-menu-item>
        <el-menu-item index="/trades/create">新增交易</el-menu-item>
        <el-menu-item index="/mistake-tags">错误标签</el-menu-item>
        <el-menu-item index="/weekly-review">周复盘</el-menu-item>
        <el-menu-item index="/rule-card">纪律卡片</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main class="main">
      <router-view />
    </el-main>
  </el-container>
</template>
