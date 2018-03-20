<template>

<div>
  <div class="title">欢迎使用OCR测试报告管理平台</div>
  <div>
    <button id='doCreateExport' class="createReport" v-on:click="doCreate" style="cursor: pointer">创建报告</button>
      <!-- <el-button type="primary" v-on:click="doCreate">创建报告</el-button> -->
  </div>
	<div class="inputSearchAndButton">
		<table>
      <tbody>
			<tr>
				<!-- <td><input id="reportNameId" type="text" class="reportName" placeholder="请输入测试报告名称"/></td> -->
        <td><el-input id="reportNameId"  placeholder="请输入测试报告名称"></el-input></td>
				<!-- <td><button class="searchButton"  style="cursor: pointer" v-on:click="doSearch">搜索</button></td> -->
        <td><el-button type="primary" icon="el-icon-search" v-on:click="doSearch">搜索</el-button></td>
			</tr>
    </tbody>
		</table>
	</div>

  		<!--测试报告表格-->
  		<div class="tableDetail report">
  			<table border="1">
  				<!--表头-->
  				<thead>

  					<tr class="menu">
  						<th>报告序号</th>
  						<th>报告名称</th>
              <th>测试结论</th>
              <th>QA</th>
              <th>RD</th>
  						<th>创建时间</th>
              <th>测试轮数</th>
              <th>icafeID</th>
  					</tr>
  				</thead>
  				<!--表内容-->
  				<tbody>
  					<!--tr 代表行   动态表格渲染，根据服务端返回的数据-->
  					<tr v-for="info in tableInfo">
  						<td>{{info.id}}</td>
              <td>
                <!-- 动态的给href属性赋值 -->
      						<a :href="serverHost+info.finalReportPath">
                    <!-- <a :href="this.global.SERVERHOST+info.finalReportPath"> -->
      							{{info.reportName}}
      						</a>
              </td>
              <td>{{info.testConclusion}}</td>
              <td>{{info.qAName}}</td>
              <td>{{info.rDName}}</td>
  						<td>{{info.reportWriteTime}}</td>
              <td>{{info.testCount}}</td>
              <td>{{info.reportId}}</td>
  					</tr>
  				</tbody>
  			</table>
         <div class="description">
           <!-- 分页 -->
            <el-pagination
                background
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="currentPage"
                :page-sizes="[10, 20, 30, 40]"
                :page-size="pagesize"
                :total="totalpages">
            </el-pagination>
          </div>
  		</div>
  	</div>

</div>
</template>

<script>
import Vue from 'vue'
import axios from 'axios'
import VueAxios from 'vue-axios'

Vue.use(VueAxios, axios)



export default {


  data(){
    return{
      tableInfo:[],
      serverHost:"",
      pagesize:20,
      currentPage:1,
      totalpages:100
    }
  },
mounted() {
  this.getAllTablesInfo();
  this.getServerHost();
  this.getTotalPages();
},
  methods: {
    getTotalPages(){
      axios.get('/api/getTotalPages').then((response)=>{
        this.totalpages=response.data
      })
    },
    // 获取所有测试报告信息
    getAllTablesInfo() {
      var _this = this;
      axios.get('/api/getReports',{
        params:{
          numbersOfEveryPage:this.pagesize,
          targetPage:this.currentPage
        }
      }).then((response) => {
        // console.log(response.data);
        _this.tableInfo = response.data.tableInfo;
        // console.log(_this.tableInfo);

      })
    },
    // 每页显示多少条
    handleSizeChange(val){
      this.pagesize = val;
      // alert("handleSizeChange:"+val+"----"+this.pagesize);
      this.getAllTablesInfo();
    },
    // 当页面改动时触发
    handleCurrentChange(val){
      this.currentPage = val;
      // alert("handleCurrentChange:"+val+"----"+this.currentPage);
      this.getAllTablesInfo();
    },
    // 获取存放html格式测试报告的服务器地址
    getServerHost(){
      var _this = this;
      axios.get('/api/getReportHtmlService').then((response)=>{
        // console.log(response.data);
        _this.serverHost = response.data;
        // this.global.SERVERHOST = response.data;
      })
    },
    // 页面跳转到创建测试报告页
    doCreate() { //
      this.$router.push({
        path: '/createReport'
      })
    },
    //点击搜索按钮后，渲染页面
    doSearch (event){
      var _this = this;
      axios.get('/api/getReportForName',{
        params:{
          reportName: document.getElementById("reportNameId").value
        }

      }).then((response) => {
        _this.tableInfo = response.data.report;
        // console.log(_this.tableInfo);
      })
    }
  }
}

</script>



<style>
/*全局基本样式不用管*/
{
	margin:0;
	padding:0;
	list-style: none;
	text-decoration: none;
	margin-top: 0px;
}
/*欢迎使用OCR测试报告系统样式*/
.title{
		width: 100%;
		text-align: center;
		color: #5a5050;
		background:#7ba1db;
		margin-top: 30px;
		font-size: 30px;
		font-family: "arial black";

}
/*输入框和按钮样式*/
.inputSearchAndButton{
	/* margin-top: 50px; */
	margin-left:5%;
}
.inputSearchAndButton table tr td button{
  border:1px solid white;
	padding:8px 25px;
	/*background:-webkit-linear-gradient(top, #acce3c, #9cc401);*/
	background:-webkit-linear-gradient(top, #7ba1db, #7ba1db);
	border:none;
	border-radius: 4px;
	color: white;
	-webkit-box-shadow: 0 1px 3px rgba(0,0,0,0.6);
	text-shadow: 0 -1px 1px rgba(0,0,0,0.25);
	outline: none;
}
.reportName{
  padding: 8px ;
}
.createReport{
  margin-top: 50px;
  margin-left:80%;
  border:1px solid white;
	padding:8px 25px;
	/*background:-webkit-linear-gradient(top, #acce3c, #9cc401);*/
	background:-webkit-linear-gradient(top, #7ba1db, #7ba1db);
	border:none;
	border-radius: 4px;
	color: white;
	-webkit-box-shadow: 0 1px 3px rgba(0,0,0,0.6);
	text-shadow: 0 -1px 1px rgba(0,0,0,0.25);
	outline: none;
}


/*装表格的大框子的样式*/
.tableDetail{
	width: 90%;/*表格宽 可调整*/
	margin: 0% auto;/*使表格居中 20%代表距离上边的高度*/
}
/*表格本身样式*/
.tableDetail table{
	width: 100%;
	text-align: center;
}
/*单元格的背景色*/
.tableDetail table td{
	padding: 14px;/*调节表格单元格之间的内间距*/
	/*background: #def3ca;*/
	background:#feeff1;
	border:2px solid white;
	border-radius: 6px;
	color: #5a5050;
	font-size: 12px;

}

.tableDetail table td button:first-child{
	margin-right: 8px;
}

/*标题单元格的背景色*/
.tableDetail table th{
	text-align: center;
	padding: 10px;/*调节表头单元格之间的内间距*/
	/*background:-webkit-linear-gradient(top, #91e304, #6ba93d);*//* Chrome */
	background:-webkit-linear-gradient(top, #fb96a3, #fd2e4a);/* Chrome */
	border:2px solid white;
	border-radius: 6px;
	color: white;
}


/*测试报告表格样式*/
.tableDetail.report table th{
	background:-webkit-linear-gradient(top, #7fc2ea, #0391e6);/* Chrome */
}
.tableDetail.report table td{
	background:#e8f4fb;
}
.tableDetail.report table td button{
	background:-webkit-linear-gradient(top, #98c9e6, #36a3e3);
}



</style>
