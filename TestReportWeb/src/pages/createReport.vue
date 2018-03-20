<template>
<div >

  <h1 class="title">创建测试报告</h1>
    <div class="uploadReport">
      <table >
        <td>

          <el-upload
            class="upload-demo"
            action="/api/uploadTestReport"
            :on-change="handleChange"
            :on-success="uploadSuccess"
            :file-list="fileList3">
            <el-button size="small" type="primary">点击上传测试报告</el-button>
            <div slot="tip" class="el-upload__tip">只能上传html文件</div>
          </el-upload>
        </td>
      </table>
    </div>
    <div class="" style="mergin-top: 50px;">
      <table border="1">
        <thead>
          <tr class="menu">
            <th style="width:100px">测试报告名称</th>
            <th>
              <a>测试过程</a>
            </th>
          </tr>
        </thead>

        <tbody>
        <tr>
          <td>
              <div id="reportNameDiv" style="font-size:10px;">此名称与上传的测试报告名称相同，上传后自动显示</div>
          </td>
          <td>
              <div>
                  <quill-editor ref="myTextEditor"
                                v-model="content"
                                :options="editorOption"
                                @blur="onEditorBlur($event)"
                                @focus="onEditorFocus($event)"
                                @ready="onEditorReady($event)">
                  </quill-editor>
              </div>
          </td>

        </tr>
      </tbody>
      </table>
      <table style="margin-left:70%">
        <td>
          <el-button type="primary" v-on:click="saveTestProcess">保存</el-button>
        </td>
        <td>
          <el-button type="success"  v-on:click="previewTestProcess" plain>预览</el-button>
        </td>
        <td>
          <el-button type="danger" v-on:click="deleteContent">清空</el-button>
        </td>
      </table>
  </div>
</div>


</template>
<script>
import Vue from 'vue'
import axios from 'axios'
import VueAxios from 'vue-axios'
import VueQuillEditor from 'vue-quill-editor'
import qs from 'qs';


import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'

Vue.use(VueQuillEditor)

// editor option example:
export default {
  data () {
    return {
      content: '<h3>请在这里输入你的测试过程，可以是图片和文字相结合的形式</h3>',
      editorOption: {
       // something config
     },
     fileList3: [],
     reportFileName:''
    }
  },
  // 如果需要手动控制数据同步，父组件需要显式地处理changed事件
  methods: {
    onEditorBlur(editor) {
      console.log('editor blur!', editor)
    },
    onEditorFocus(editor) {
      console.log('editor focus!', editor)
    },
    onEditorReady(editor) {
      console.log('editor ready!', editor)
    },
    onEditorChange({ editor, html, text }) {
      // console.log('editor change!', editor, html, text)
      this.content = html;
    },
    deleteContent({ editor, html, text }){
      var con = confirm("确定删除所有内容么?");
      if(con == true){
        this.content = html;

        alert("删除成功！");
      }
    },
    //保存测试过程函数
    saveTestProcess(editor){
      if('' == this.reportFileName){
        alert("请先上传测试报告！");
        return;
      };
      //获取用户上传的测试报告的ID
       var htmlReportId = this.reportFileName.split(".")[0].split("_")[1];
      // 取出用户输出的内容，html格式的
      var userContentHtml = this.$refs.myTextEditor._content;
      console.log(userContentHtml);
      if("undefined" == typeof(userContentHtml)){
          alert("请输入你的测试过程后再保存！");
          return;
      }

      axios.post('/api/saveTestProcess',{
        processHtml: userContentHtml,
        reportId:htmlReportId
      }
    ).then((response) => {
        alert(response.data);
      })
    },
    // 预览测试报告
    previewTestProcess(editor){
      if('' == this.reportFileName){
        alert("请先上传测试报告！");
        return;
      };
      //获取用户上传的测试报告的ID
      var htmlReportId = this.reportFileName.split(".")[0].split("_")[1];
      // 取出用户输出的内容，html格式的
      var userContentHtml = this.$refs.myTextEditor._content;
      console.log(userContentHtml);
      if("undefined" == typeof(userContentHtml)){
          alert("请输入你的测试过程后再预览！");
          return;
      }

      axios.post('/api/saveTestProcess',{
        processHtml: userContentHtml,
        reportId:htmlReportId
      }
    ).then((response) => {
        if("保存失败" == response.data){
          alert("预览失败，发生错误！")
        };
      });
      // 获取报告服务器地址
      var _this = this;
      axios.get('/api/getPreviewHtmlService',{
        params: { 'reportId': htmlReportId }
      }).then((response)=>{
        // console.log(response.data);
        window.open(response.data);
      });

    },
    handleChange(file, fileList) {
      //显示同时上传几个文件
        this.fileList3 = fileList.slice(-1);
      },
      // 上传成功后回调
      uploadSuccess(response, file, fileList){
        // console.log(response);
        this.reportFileName = file.name;
        document.getElementById("reportNameDiv").innerHTML=response;
      }
  },
  // if you need to get the current editor object, you can find the editor object like this, the $ref object is a ref attribute corresponding to the dom redefined
  // 如果你需要得到当前的editor对象来做一些事情，你可以像下面这样定义一个方法属性来获取当前的editor对象，实际上这里的$refs对应的是当前组件内所有关联了ref属性的组件元素对象
  computed: {
    editor() {
      return this.$refs.myTextEditor.quillEditor
    }
  },
  mounted() {
    // you can use current editor object to do something(editor methods)
    // console.log('this is my editor', this.editor)
    // this.editor to do something...
  }
}


</script>
<style>

.title{
		width: 100%;
		text-align: center;
		color: #5a5050;
		background:#7ba1db;
		margin-top: 30px;
		font-size: 30px;
		font-family: "arial black";

}

.menu{
  background:-webkit-linear-gradient(top, #7fc2ea, #0391e6);
  border:0px solid white;
  border-radius: 6px;
  color: white;

}


.uploadReport{
  margin-left:0%;
  margin-top: 50px;
}


.publicButton{
  /* margin-left:0%; */
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
  cursor: pointer;
}

</style>
