<template>
  <div v-if="globalLoading" class="loading-mask"><div class="spinner"></div></div>

  <div v-if="!adminUser.id" class="login-container">
    <div class="login-wrapper">
      <div class="login-poster">
        <div class="poster-content">
          <div class="brand">
            <el-icon size="40" color="#fff"><Reading /></el-icon>
            <span class="brand-text">Library Cloud</span>
          </div>
          <h2 class="slogan">智慧图书馆<br>资产管理平台</h2>
          <div class="feature-tags">
            <span>资产定值</span>
            <span>借阅追踪</span>
            <span>营收报表</span>
          </div>
        </div>
      </div>
      <div class="login-form-side">
        <div class="form-header">
          <h3>管理员登录</h3>
          <p>请输入您的管理账号访问后台</p>
        </div>
        <el-form @submit.prevent size="large" class="real-form">
          <el-form-item><el-input v-model="loginForm.username" placeholder="账号 (admin)" :prefix-icon="User" /></el-form-item>
          <el-form-item><el-input v-model="loginForm.password" type="password" placeholder="密码 (123456)" :prefix-icon="Lock" show-password @keyup.enter="handleLogin"/></el-form-item>
          <el-button type="primary" class="login-btn" :loading="loggingIn" @click="handleLogin">立即登录</el-button>
          <div class="form-footer"><span>© 2026 Library System v6.5 Price Edition</span></div>
        </el-form>
      </div>
    </div>
  </div>

  <div v-else class="layout">
    <aside class="side-menu">
      <div class="logo-box"><el-icon><Management /></el-icon> Library Cloud</div>
      <el-menu :default-active="activeMenu" background-color="#001529" text-color="#a6adb4" active-text-color="#1890ff" @select="handleMenuChange">
        <el-menu-item index="1"><el-icon><DataLine /></el-icon>资产大屏</el-menu-item>
        <el-menu-item index="2"><el-icon><Notebook /></el-icon>图书资产</el-menu-item>
        <el-menu-item index="3"><el-icon><User /></el-icon>读者名册</el-menu-item>
      </el-menu>
    </aside>

    <main class="main-body">
      <header class="top-bar">
        <div class="page-title">{{ pageTitles[activeMenu] }}</div>
        <div class="user-info">
          <el-avatar size="small" style="background:#1890ff">A</el-avatar>
          <span class="ml-2">管理员 {{adminUser.username}}</span>
          <el-button link type="danger" class="ml-4" @click="logout">退出</el-button>
        </div>
      </header>

      <div class="content-area">
        <div v-if="activeMenu === '1'" class="dashboard animate-in">
          
          <el-row :gutter="20" style="margin-bottom: 20px;">
            <el-col :span="6" v-for="(s, index) in stats" :key="index">
              <div class="stat-box">
                <div class="val" :style="{color: s.color}">{{ s.value }}</div>
                <div class="lbl">{{ s.label }}</div>
              </div>
            </el-col>
          </el-row>

          <el-row :gutter="20" style="margin-bottom: 20px;">
            <el-col :span="16"><el-card shadow="never" class="chart-card"><template #header><b>📊 借阅与营收趋势</b></template><div id="lineChart" style="height: 350px; width: 100%;"></div></el-card></el-col>
            <el-col :span="8"><el-card shadow="never" class="chart-card"><template #header><b>🍩 资产分类占比</b></template><div id="pieChart" style="height: 350px; width: 100%;"></div></el-card></el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-card shadow="never" class="log-card">
                <template #header><div class="flex-between"><b>🔔 资产公告</b> <el-tag size="small">内部</el-tag></div></template>
                <div class="notice-list">
                  <div class="notice-item">💰 本月新购图书资产共计 12,500 元。</div>
                  <div class="notice-item">⚠️ 盘点发现《百年孤独》库存异常，请核查。</div>
                  <div class="notice-item">📚 系统已支持图书定价与赔偿计算功能。</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card shadow="never" class="log-card">
                <template #header><div class="flex-between"><b>⚡ 实时操作日志</b> <span class="live-dot"></span></div></template>
                <div class="log-list">
                  <div v-for="(log, i) in liveLogs" :key="i" class="log-item">
                    <span class="log-time">{{log.time}}</span>
                    <span class="log-content">{{log.content}}</span>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <div v-if="activeMenu === '2'" class="animate-in">
          <div class="toolbar">
            <el-input v-model="searchKey" placeholder="搜书名/ISBN..." style="width: 250px" clearable :prefix-icon="Search"/>
            <el-select v-model="filterCat" placeholder="筛选分类" clearable style="margin-left:10px; width:140px"><el-option v-for="c in categories" :key="c" :label="c" :value="c"/></el-select>
            <div style="flex:1"></div>
            <el-button type="primary" color="#10B981" :icon="Plus" @click="openAddBookDialog">新书采购入库</el-button>
          </div>
          <el-table :data="paginatedBooks" border stripe height="calc(100vh - 240px)">
            <el-table-column prop="id" label="ID" width="70" sortable />
            <el-table-column prop="title" label="书名" min-width="180"><template #default="{row}"><b>{{ row.title }}</b></template></el-table-column>
            
            <el-table-column prop="price" label="单价" width="100" sortable>
              <template #default="{row}">
                <span style="color:#f5222d; font-weight:bold;">￥{{ row.price }}</span>
              </template>
            </el-table-column>

            <el-table-column prop="category" label="分类" width="100"><template #default="{row}"><el-tag effect="light" :type="getCatColor(row.category)">{{row.category}}</el-tag></template></el-table-column>
            <el-table-column prop="author" label="作者" width="140" />
            <el-table-column prop="stock" label="库存" width="80"><template #default="{row}"><span :class="row.stock>0?'text-green-600':'text-red-500'">{{row.stock}}</span></template></el-table-column>
            <el-table-column label="资产操作" width="120" align="center"><template #default="{row}"><el-button size="small" type="primary" plain :disabled="row.stock<=0" @click="handleBorrow(row)">借出</el-button></template></el-table-column>
          </el-table>
          <div class="pagination-bar"><el-pagination background layout="total, prev, pager, next" :total="filteredBooks.length" v-model:current-page="currentPage" :page-size="pageSize" /></div>
        </div>

        <div v-if="activeMenu === '3'" class="animate-in">
          <el-card shadow="never">
            <template #header>
              <div class="flex-between">
                <b>👥 读者借阅监控</b>
                <el-tag type="info">共 {{ userList.length }} 位</el-tag>
              </div>
            </template>
            <el-table :data="userListWithCount" stripe height="calc(100vh - 200px)" highlight-current-row>
              <el-table-column prop="id" label="ID" width="80" sortable align="center" />
              <el-table-column prop="username" label="姓名" min-width="120">
                <template #default="{row}">
                   <div class="flex items-center">
                     <el-avatar :size="32" style="background:#1890ff; margin-right:10px;">{{row.username[0]}}</el-avatar>
                     <span style="font-weight:bold; color:#333;">{{row.username}}</span>
                   </div>
                </template>
              </el-table-column>
              <el-table-column prop="role" label="身份" width="100">
                <template #default="{row}"><el-tag :type="row.role==='ADMIN'?'warning':'info'">{{row.role==='ADMIN'?'管理员':'读者'}}</el-tag></template>
              </el-table-column>
              <el-table-column label="在借数量" width="150" align="center" sortable prop="borrowCount">
                <template #default="{row}">
                  <el-tag v-if="row.borrowCount > 0" type="danger" effect="dark" round>{{ row.borrowCount }} 本</el-tag>
                  <span v-else style="color:#ccc; font-size:12px;">无</span>
                </template>
              </el-table-column>
              <el-table-column label="详情" width="120" align="center">
                <template #default="{row}">
                   <el-button :type="row.borrowCount > 0 ? 'primary' : ''" :plain="row.borrowCount > 0" size="small" :disabled="row.borrowCount === 0" @click="checkUserBorrows(row)">查看</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </div>
    </main>

    <el-dialog v-model="borrowDialog.visible" title="📚 办理借阅" width="400px">
      <p>正在借出：<b>{{borrowDialog.book?.title}}</b></p>
      <p style="color:#f5222d; font-size:12px; margin-top:5px;">资产价值：￥{{borrowDialog.book?.price}}</p>
      <div class="mt-4">
        <p class="text-gray-500 mb-2">读者ID：</p>
        <el-input v-model="borrowDialog.userId" placeholder="请输入读者ID" />
      </div>
      <template #footer><el-button @click="borrowDialog.visible=false">取消</el-button><el-button type="primary" @click="confirmBorrow">确认</el-button></template>
    </el-dialog>

    <el-dialog v-model="addDialogVisible" title="✨ 新书采购入库" width="500px">
      <el-form :model="newBook" label-width="80px">
        <el-form-item label="图书名称"><el-input v-model="newBook.title" /></el-form-item>
        <el-form-item label="作者"><el-input v-model="newBook.author" /></el-form-item>
        <el-form-item label="ISBN"><el-input v-model="newBook.isbn" /></el-form-item>
        <el-form-item label="采购单价"><el-input-number v-model="newBook.price" :precision="2" :step="0.1" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="所属分类"><el-select v-model="newBook.category" class="w-full"><el-option v-for="c in categories" :key="c" :label="c" :value="c" /></el-select></el-form-item>
        <el-form-item label="初始库存"><el-input-number v-model="newBook.stock" :min="1" :max="999" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="addDialogVisible = false">取消</el-button><el-button type="primary" @click="submitAddBook">入库</el-button></template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" :title="'📑 ' + currentUserDetailName + ' 的借阅清单'" width="750px">
      <el-table :data="currentUserBorrows" border stripe v-loading="detailLoading">
        <el-table-column label="图书信息" min-width="180">
          <template #default="{row}">
            <div style="font-weight:bold; color:#333;">{{row.bookTitle}}</div>
            <div style="font-size:12px; color:#999;">ISBN: {{row.isbn}}</div>
          </template>
        </el-table-column>
        <el-table-column label="借阅时间" width="110"><template #default="{row}"><span style="font-size:12px">{{ formatDate(row.borrowTime) }}</span></template></el-table-column>
        <el-table-column label="状态" width="120" align="center">
          <template #default="{row}">
            <el-tag v-if="isOverdue(row.borrowTime)" type="danger" effect="dark">已逾期</el-tag>
            <el-tag v-else type="success" effect="plain">正常</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="{row}"><el-button :type="isOverdue(row.borrowTime) ? 'danger' : 'success'" size="small" @click="handleReturn(row)">{{ isOverdue(row.borrowTime) ? '赔偿归还' : '归还' }}</el-button></template>
        </el-table-column>
      </el-table>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import { User, Lock, Management, DataLine, Notebook, Search, Plus } from '@element-plus/icons-vue'

const API = 'http://localhost:8080/api'
const globalLoading = ref(true)
const loggingIn = ref(false)
const adminUser = ref({})
const loginForm = reactive({username:'', password:''})
const activeMenu = ref('1')
const pageTitles = {'1':'全馆资产数据', '2':'图书资产库房', '3':'读者借阅名册'}

const bookList = ref([])
const userList = ref([])
const userListWithCount = ref([]) 
const searchKey = ref('')
const filterCat = ref('')
const categories = ['计算机','文学','历史','科学','哲学','艺术','经管']
const currentPage = ref(1)
const pageSize = ref(12)

const borrowDialog = reactive({ visible: false, book: null, userId: '' })
const addDialogVisible = ref(false)
const newBook = reactive({ title:'', author:'', isbn:'', category:'计算机', stock:10, price: 59.00 })
const detailDialogVisible = ref(false)
const currentUserBorrows = ref([])
const currentUserDetailName = ref('')
const detailLoading = ref(false)

const liveLogs = ref([
  {time:'10:23', content:'读者 张伟 归还了 《算法导论》'},
  {time:'10:25', content:'采购入库 《Spring Boot实战》 10本 (价值￥890)'},
  {time:'10:28', content:'读者 李娜 借阅了 《百年孤独》'},
  {time:'10:32', content:'系统 完成了资产自动盘点'},
  {time:'10:45', content:'读者 王强 借阅了 《人类简史》'}
])

let lineChartInstance = null
let pieChartInstance = null

onMounted(() => setTimeout(()=>globalLoading.value=false, 600))

const handleMenuChange = (index) => {
  activeMenu.value = index
  if (index === '1') nextTick(() => initCharts())
}

const filteredBooks = computed(() => {
  return bookList.value.filter(b => {
    const k = searchKey.value.toLowerCase()
    const matchK = !k || b.title.toLowerCase().includes(k) || b.author.toLowerCase().includes(k)
    const matchC = !filterCat.value || b.category === filterCat.value
    return matchK && matchC
  })
})
const paginatedBooks = computed(() => {
  const start = (currentPage.value-1)*pageSize.value
  return filteredBooks.value.slice(start, start+pageSize.value)
})

// ★★★ 核心：动态计算总资产 ★★★
const totalAssetValue = computed(() => {
  if (bookList.value.length === 0) return 0;
  // 计算所有书的总价 (价格 * 库存)
  const total = bookList.value.reduce((sum, book) => sum + (book.price * book.stock), 0);
  return (total / 10000).toFixed(2) + ' 万'; // 转换为“万”元
})

const stats = computed(() => [
  { label:'馆藏总数', value: bookList.value.length, color: '#1890ff' },
  { label:'总资产值', value: '￥' + totalAssetValue.value, color: '#cf1322' }, // 这里的颜色改为了红色的金钱色
  { label:'库存充足', value: bookList.value.filter(b=>b.stock>10).length, color: '#52c41a' },
  { label:'紧缺图书', value: bookList.value.filter(b=>b.stock<5).length, color: '#faad14' }
])

const handleLogin = async () => {
  if(!loginForm.username) return ElMessage.warning('请输入账号')
  loggingIn.value = true
  try {
    const res = await axios.post(`${API}/users/login`, loginForm)
    adminUser.value = res.data
    ElMessage.success('登录成功')
    loadData()
  } catch(e) { ElMessage.error(e.response?.data?.message || '登录失败') }
  finally { loggingIn.value = false }
}
const logout = () => adminUser.value = {}

const loadData = async () => {
  const [bRes, uRes] = await Promise.all([axios.get(`${API}/books`), axios.get(`${API}/users`)])
  bookList.value = bRes.data
  const users = uRes.data
  userList.value = users
  
  const promises = users.map(u => axios.get(`${API}/borrow/user/${u.id}`))
  const results = await Promise.all(promises)
  
  userListWithCount.value = users.map((u, i) => ({
    ...u,
    borrowCount: results[i].data.length
  }))

  if(activeMenu.value==='1') nextTick(initCharts)
}

const handleBorrow = (book) => { borrowDialog.book = book; borrowDialog.userId = ''; borrowDialog.visible = true }
const confirmBorrow = async () => {
  if(!borrowDialog.userId) return ElMessage.warning('请输入读者ID')
  try {
    const res = await axios.post(`${API}/borrow?userId=${borrowDialog.userId}&bookId=${borrowDialog.book.id}`)
    if(res.data==='借阅成功') { ElMessage.success('办理成功'); borrowDialog.visible = false; loadData() } else ElMessage.error(res.data)
  } catch { ElMessage.error('失败') }
}

const openAddBookDialog = () => { Object.assign(newBook, { title:'', author:'', isbn:'', category:'计算机', stock:10, price: 59.9 }); addDialogVisible.value = true }
const submitAddBook = async () => {
  if(!newBook.title) return ElMessage.warning('请补全信息')
  try { await axios.post(`${API}/books`, newBook); ElMessage.success('入库成功'); addDialogVisible.value = false; loadData() } catch { ElMessage.error('入库失败') }
}

const checkUserBorrows = async (user) => {
  currentUserDetailName.value = user.username; detailDialogVisible.value = true; detailLoading.value = true; currentUserBorrows.value = []
  try { const res = await axios.get(`${API}/borrow/user/${user.id}`); currentUserBorrows.value = res.data } catch(e) { ElMessage.error('获取详情失败') } finally { detailLoading.value = false }
}

const handleReturn = (row) => {
  ElMessageBox.confirm(`确认归还图书《${row.bookTitle}》吗？`, '归还确认', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'success' }).then(async () => {
    try {
      const res = await axios.post(`${API}/borrow/return?borrowId=${row.borrowId}`)
      if (res.data === '还书成功') { ElMessage.success('归还成功'); const user = userList.value.find(u => u.username === currentUserDetailName.value); if (user) checkUserBorrows(user); loadData() } else { ElMessage.error(res.data) }
    } catch (e) { ElMessage.error('操作失败') }
  }).catch(() => {})
}

const getCatColor = (c) => { return {'计算机':'', '文学':'success', '历史':'warning', '科学':'danger', '经管':'info'}[c] || '' }
const formatDate = (str) => { if(!str) return ''; return new Date(str).toLocaleDateString() }
const isOverdue = (dateStr) => {
  const diff = Math.abs(new Date() - new Date(dateStr));
  return Math.ceil(diff / (1000 * 60 * 60 * 24)) > 30;
}

const initCharts = async () => {
  const lineDom = document.getElementById('lineChart'); const pieDom = document.getElementById('pieChart');
  if(!lineDom || !pieDom) return;
  if(lineChartInstance) lineChartInstance.dispose(); if(pieChartInstance) pieChartInstance.dispose();
  lineChartInstance = echarts.init(lineDom); pieChartInstance = echarts.init(pieDom);
  try {
    const [tRes, cRes] = await Promise.all([axios.get(`${API}/stats/traffic`), axios.get(`${API}/stats/categories`)])
    lineChartInstance.setOption({ tooltip:{trigger:'axis'}, xAxis:{type:'category', data:tRes.data.map(i=>i.date)}, yAxis:{type:'value'}, series:[{data:tRes.data.map(i=>i.count), type:'line', smooth:true, areaStyle:{}, color:'#1890ff'}] })
    pieChartInstance.setOption({ tooltip:{trigger:'item'}, legend:{orient:'vertical', left:'left'}, series:[{type:'pie', radius:['40%', '70%'], data: cRes.data, itemStyle: { borderRadius: 5, borderColor: '#fff', borderWidth: 2 }}] })
  } catch{}
}
</script>

<style>
/* Reset */
body { margin: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif; background: #f0f2f5; color: #333; }
.w-full { width: 100%; } .ml-2 { margin-left: 0.5rem; } .ml-4 { margin-left: 1rem; }
.flex { display: flex; } .items-center { align-items: center; } .flex-between { display: flex; justify-content: space-between; align-items: center; }
.text-green-600 { color: #52c41a; font-weight: bold; } .text-red-500 { color: #f5222d; }

/* Login Page */
.login-container { height: 100vh; display: flex; justify-content: center; align-items: center; background: #f0f2f5; }
.login-wrapper { display: flex; width: 900px; height: 550px; background: #fff; box-shadow: 0 0 20px rgba(0,0,0,0.05); border-radius: 8px; overflow: hidden; }
.login-poster { flex: 1; background: linear-gradient(135deg, #001529 0%, #003a8c 100%); display: flex; align-items: center; justify-content: center; color: #fff; position: relative; overflow: hidden; }
.poster-content { position: relative; z-index: 2; padding: 40px; }
.brand { display: flex; align-items: center; gap: 10px; margin-bottom: 20px; }
.brand-text { font-size: 24px; font-weight: bold; letter-spacing: 1px; }
.slogan { font-size: 32px; font-weight: 300; line-height: 1.4; margin-bottom: 40px; }
.feature-tags span { display: inline-block; padding: 5px 12px; border: 1px solid rgba(255,255,255,0.3); border-radius: 20px; margin-right: 10px; font-size: 12px; }
.login-form-side { flex: 1; padding: 60px; display: flex; flex-direction: column; justify-content: center; background: #fff; }
.form-header h3 { font-size: 24px; color: #1890ff; margin-bottom: 10px; }
.form-header p { color: #999; margin-bottom: 30px; }
.real-form .el-input__wrapper { box-shadow: none; border-bottom: 1px solid #d9d9d9; border-radius: 0; padding-left: 0; }
.real-form .el-input__wrapper:hover { border-bottom-color: #1890ff; }
.login-btn { width: 100%; height: 48px; font-size: 16px; margin-top: 20px; background: #1890ff; border: none; }
.login-btn:hover { background: #40a9ff; }
.form-footer { margin-top: auto; text-align: center; color: #ccc; font-size: 12px; }

/* Layout */
.layout { display: flex; height: 100vh; overflow: hidden; }
.side-menu { width: 220px; background: #001529; display: flex; flex-direction: column; box-shadow: 2px 0 6px rgba(0,21,41,0.35); z-index: 10; }
.logo-box { height: 64px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 18px; font-weight: bold; background: #002140; gap: 8px; }
.main-body { flex: 1; display: flex; flex-direction: column; background: #f0f2f5; }
.top-bar { height: 64px; background: #fff; border-bottom: 1px solid #e8e8e8; display: flex; justify-content: space-between; align-items: center; padding: 0 24px; box-shadow: 0 1px 4px rgba(0,21,41,0.08); }
.page-title { font-size: 18px; font-weight: 500; color: #000; }
.content-area { flex: 1; padding: 24px; overflow-y: auto; }

/* Dashboard */
.stat-box { background: #fff; padding: 24px; border-radius: 2px; text-align: center; cursor: pointer; transition: all 0.3s; }
.stat-box:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.15); }
.stat-box .val { font-size: 30px; font-weight: bold; line-height: 1.2; }
.stat-box .lbl { color: #8c8c8c; font-size: 14px; margin-top: 8px; }
.chart-card { border-radius: 2px; }
.log-card { height: 300px; overflow: hidden; }
.notice-list .notice-item { padding: 8px 0; border-bottom: 1px dashed #eee; font-size: 14px; color: #666; }
.log-list { height: 200px; overflow-y: auto; }
.log-item { display: flex; padding: 8px 0; font-size: 13px; }
.log-time { color: #999; margin-right: 10px; width: 40px; }
.log-content { color: #333; }
.live-dot { width: 8px; height: 8px; background: #52c41a; border-radius: 50%; display: inline-block; animation: pulse 1.5s infinite; }
@keyframes pulse { 0% { box-shadow: 0 0 0 0 rgba(82, 196, 26, 0.4); } 70% { box-shadow: 0 0 0 6px rgba(82, 196, 26, 0); } 100% { box-shadow: 0 0 0 0 rgba(82, 196, 26, 0); } }

.toolbar { background: #fff; padding: 16px; margin-bottom: 16px; display: flex; align-items: center; border-radius: 2px; }
.pagination-bar { background: #fff; padding: 12px; margin-top: 16px; display: flex; justify-content: flex-end; }
.animate-in { animation: fadeIn 0.4s ease-out; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
::-webkit-scrollbar { width: 6px; height: 6px; }
::-webkit-scrollbar-thumb { background: #ccc; border-radius: 3px; }
</style>