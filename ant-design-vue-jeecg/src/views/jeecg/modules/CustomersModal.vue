<template>
  <a-modal
    :title="title"
    :width="820"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel1"
    cancelText="关闭"
    :footer="null"
    v-if="!this.model.id"
    style="height: 450px;position: absolute;top: 40%;left: 40%;margin-top: -230px;margin-left:-255px;">

    <a-spin :spinning="confirmLoading" style="height: 515px;">
      <a-form :form="form" style="margin-left: 32px;">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="省份" class="a-formItem">
          <a-select v-decorator="[ 'oneAreaId', validatorRules.oneAreaId]"  placeholder="请选择省份">
            <a-select-option value="" @click="initialOneAreaList">全部</a-select-option>
            <a-select-option v-for="(oneArea,oneAreaindex) in getOneArea" :key="oneAreaindex.toString()" :value="oneArea.id"
                             @click="initialGetTwoAreaList(oneArea.id)" >{{oneArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="城市" class="a-formItem">
          <a-select v-decorator="[ 'twoAreaId', validatorRules.twoAreaId]" placeholder="请选择城市">
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(twoArea,twoAreaindex) in getTwoArea" :key="twoAreaindex.toString()" :value="twoArea.id">{{twoArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="客户名称" class="a-formItem">
          <a-input placeholder="请输入客户名称" v-decorator="['name', validatorRules.name ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="项目名称" class="a-formItem">
          <a-input placeholder="请输入项目名称" v-decorator="['projectName', validatorRules.projectName ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="经度" class="a-formItem">
          <a-input placeholder="请输入经度" v-decorator="[ 'longitude', validatorRules.longitude]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="纬度" class="a-formItem">
          <a-input placeholder="请输入纬度" v-decorator="[ 'latitude', validatorRules.latitude] "/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="详细地址" class="a-formItem a-formItem1">
          <a-input placeholder="请输入客户详细地址" v-decorator="['detailedAddress', {}]" />
        </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="客户logo" class="a-formItem">
            <a-icon type="close"  @click="qxkh()" style="margin-left: 150px"/>
            <a-upload
              list-type="picture-card"
              class="avatar-uploader"
              :showUploadList="false"
              :action="uploadAction"
              :data="{'isup':1}"
              :headers="headers"
              :beforeUpload="beforeUpload"
              @change="handleChange">
              <img v-if="picUrl" :src="getAvatarView()" alt="客户logo" style="height:104px;max-width:300px;" />
              <div v-else>
                <a-icon :type="uploadLoading ? 'loading' : 'plus'" />
                <div class="ant-upload-text" >上传</div>
              </div>
            </a-upload>
          </a-form-item>

        <!--客户标注logo-->
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="标注logo" class="a-formItem"  >
          <a-icon type="close"  @click="qxbz()" style="margin-left: 150px"/>
          <a-upload
            list-type="picture-card"
            class="avatar-uploader"
            :showUploadList="false"
            :action="uploadActionbz"
            :data="{'isup':1}"
            :headers="headersbz"
            :beforeUpload="beforeUploadbz"
            @change="handleChangebz" >
            <img v-if="bzpicUrl" :src="getAvatarViewbz()" alt="标注logo" style="height:104px;max-width:300px;"  />
            <div v-else>
              <a-icon :type="uploadLoading ? 'loading' : 'plus'" />
              <div class="ant-upload-text" >上传</div>
            </div>
          </a-upload>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="web端名称" class="a-formItem a-formItem1">
          <a-input placeholder="请输入web端显示的名称" v-decorator="['webName', {}]" />
        </a-form-item>

      </a-form>
    </a-spin>
    <div style="margin-bottom: 25px;">
      <div class="querenBnt">
        <a-button @click="handleCancel1">关闭</a-button>
      </div>
      <div class="querenBnt1">
        <a-button @click="confirm" style="background-color: #1890ff;color:white;">确认</a-button>
      </div>
      <!--div class="querenBnt1">
    <a-popconfirm placement="topLeft" okText="是" cancelText="否" @confirm="confirm" @cancel="handleCancel">
      <template slot="title">
        <p>{{text}}</p>
      </template>
      <a-button style="background-color: #1890ff;color:white;" >确认</a-button>
    </a-popconfirm>
  </div>-->
    </div>
  </a-modal>

  <a-modal
    :title="title"
    :width="820"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleCancel"
    @cancel="handleCancel1"
    cancelText="关闭"
    v-else
    style="height: 450px;position: absolute;top: 40%;left: 40%;margin-top: -230px;margin-left:-255px;">
    <a-spin :spinning="confirmLoading" style="height: 490px;">
      <a-form :form="form" style="margin-left: 32px;">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="省份" class="a-formItem">
          <a-select v-decorator="[ 'oneAreaId', validatorRules.oneAreaId]"  placeholder="请选择省份">
            <a-select-option value="" @click="initialOneAreaList">全部</a-select-option>
            <a-select-option v-for="(oneArea,oneAreaindex) in getOneArea" :key="oneAreaindex.toString()" :value="oneArea.id"
                             @click="initialGetTwoAreaList(oneArea.id)" >{{oneArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="城市" class="a-formItem">
          <a-select v-decorator="[ 'twoAreaId', validatorRules.twoAreaId]" placeholder="请选择城市">
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(twoArea,twoAreaindex) in getTwoArea" :key="twoAreaindex.toString()" :value="twoArea.id">{{twoArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="客户名称" class="a-formItem">
          <a-input placeholder="请输入客户名称" v-decorator="['name', validatorRules.name ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="项目名称" class="a-formItem">
          <a-input placeholder="请输入项目名称" v-decorator="['projectName', validatorRules.projectName ]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="经度" class="a-formItem">
          <a-input placeholder="请输入经度" v-decorator="[ 'longitude', validatorRules.longitude]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="纬度" class="a-formItem">
          <a-input placeholder="请输入纬度" v-decorator="[ 'latitude', validatorRules.latitude] "/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="详细地址" class="a-formItem a-formItem1">
          <a-input placeholder="请输入客户详细地址" v-decorator="['detailedAddress', {}]" />
        </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="客户logo" class="a-formItem">
            <a-icon type="close"  @click="qxkh()" style="margin-left: 150px"/>
            <a-upload
              list-type="picture-card"
              class="avatar-uploader"
              :showUploadList="false"
              :action="uploadAction"
              :data="{'isup':1}"
              :headers="headers"
              :beforeUpload="beforeUpload"
              @change="handleChange">
              <img v-if="picUrl" :src="getAvatarView()" alt="客户logo" style="height:104px;max-width:300px;" />
              <div v-else>
                <a-icon :type="uploadLoading ? 'loading' : 'plus'" />
                <div class="ant-upload-text" >上传</div>
              </div>
            </a-upload>
          </a-form-item>

        <!--客户标注logo-->
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="标注logo" class="a-formItem"  >
          <a-icon type="close"  @click="qxbz()" style="margin-left: 150px"/>
          <a-upload
            list-type="picture-card"
            class="avatar-uploader"
            :showUploadList="false"
            :action="uploadActionbz"
            :data="{'isup':1}"
            :headers="headersbz"
            :beforeUpload="beforeUploadbz"
            @change="handleChangebz" >
            <img v-if="bzpicUrl" :src="getAvatarViewbz()" alt="标注logo" style="height:104px;max-width:300px;"  />
            <div v-else>
              <a-icon :type="uploadLoading ? 'loading' : 'plus'" />
              <div class="ant-upload-text" >上传</div>
            </div>
          </a-upload>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="web端名称" class="a-formItem a-formItem1">
          <a-input placeholder="请输入web端显示的名称" v-decorator="['webName', {}]" />
        </a-form-item>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import Vue from 'vue'
  import {getOneAreaAll,getTwoAreaAll} from '@/api/api'

  export default {
    name: "CustomersModal",
    data () {
      return {
        buttonWidth: 70,
        text: '是否继续添加客户账号?',
        title:"操作",
        visible: false,
        model: {},
        getOneArea:[], //获取所有的省份
        getTwoArea:[],//获取所有的城市
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        uploadLoading:false,
        headers:{},
        headersbz:{},
        confirmLoading: false,
        form: this.$form.createForm(this),
        picUrl: '',
        bzpicUrl:'',
        validatorRules:{
          name:{rules: [{ required: true, message: '请输入客户名称!' }]},
          projectName:{
						// rules: [{ required: true, message: '请输入项目名称!' }],
					},
          oneAreaId:{rules: [{ required: true, message: '请选择省份!' }]},
          twoAreaId:{rules: [{ required: true, message: '请选择城市!' }]},
          longitude:{rules: [{ required: true, message: '请输入经度!' }]},
          latitude:{rules: [{ required: true, message: '请输入纬度!' }]},
          webName:{rules: [{ required: true, message: '请输入web端显示的名称!' }]},
        },
        url: {
          add: "/monitor/customers/add",
          edit: "/monitor/customers/edit",
          fileUpload: window._CONFIG['domianURL']+"/sys/common/upload",
          imgerver: window._CONFIG['domianURL']+"/sys/common/view",
        },
      }
    },
    created () {
      const token = Vue.ls.get(ACCESS_TOKEN);
      this.headers = {"X-Access-Token":token}
      this.headersbz = {"X-Access-Token":token}
      this.initialGetOneAreaList()
    },
    computed:{
      uploadAction:function () {
        return this.url.fileUpload;
      },
      uploadActionbz:function () {
        return this.url.fileUpload;
      }
    },
    methods: {
      qxkh(){
        this.picUrl = "";
        this.model.custLogo="";
      },
      qxbz(){
        this.bzpicUrl = "";
        this.model.biaoZhuLogo="";
      },
      add () {
        this.picUrl = "";
        this.bzpicUrl="";
        this.edit({});
      },
      edit (record) {
        let that = this
        console.log(record);
        that.initialGetOneAreaList()
        if (record.id != null){
          that.initialGetTwoAreaList(record.oneAreaId)
        }
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'oneAreaId','twoAreaId','name','projectName','detailedAddress','longitude','latitude','webName'))
          //时间格式化
        });
        this.picUrl = this.model.custLogo;
        this.bzpicUrl=this.model.biaoZhuLogo;
      },
      //获取所有的省份
      initialGetOneAreaList(){
        this.getOneArea = ['']
        getOneAreaAll().then((res) => {
          if (res.success){
            this.getOneArea = res.result
          } else {
            /*  console.log(res.message)*/
          }
        })
      },
      //获取所有的二级区
      initialGetTwoAreaList(id){
        this.getTwoArea = ['']
        getTwoAreaAll({id:id}).then((res) => {
          if (res.success){
            this.getTwoArea = res.result
          } else {
            /* console.log(res.message)*/
          }
        })
      },
      //当未选择省份时，城市下拉框清空
      initialOneAreaList(){
        this.getTwoArea = ['']
        this.model.twoAreaId="";
      },

      close () {
        this.$emit('close');
        this.visible = false;
      },
      confirm () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
              method = 'put';
            }
            let formData = Object.assign(this.model, values);
            //时间格式化
            console.log(formData)
            httpAction(httpurl,formData,method).then((res)=>{
                if(res.success){
                  that.$message.success(res.message);
                  that.$emit('ok');
                  this.edit({oneAreaId:this.model.oneAreaId,twoAreaId: this.model.twoAreaId,customerId: this.model.customerId});
                }else{
                  that.$message.warning(res.message);
                }

              }
            ).finally(() => {

              that.confirmLoading = false;

            })

          }
        })
      },
      handleCancel1(){
        console.log("111111111111111111111")
        this.close();
      },
      handleCancel() {
        const that = this
        // 触发表单验证
        this.form.validateFields((err, values) => {

          if (!err) {
            that.confirmLoading = true
            let httpurl = ''
            let method = ''
            if (!this.model.id) {
              httpurl += this.url.add
              method = 'post'
              //initialTwoAreaList=['']
            } else {
              httpurl += this.url.edit
              method = 'put'
            }
            let formData = Object.assign(this.model, values)

            //时间格式化
            console.log(formData)
            httpAction(httpurl, formData, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
              } else {
                that.$message.warning(res.message)
              }
            }).finally(() => {
              that.confirmLoading = false
              this.close();
            })
          }
        })
      },
      beforeUpload: function(file){
        var fileType = file.type;
        if(fileType.indexOf('image')<0){
          this.$message.warning('请上传图片');
          return false;
        }
        //TODO 验证文件大小
      },
      //客户标注图上传
      beforeUploadbz: function(file){
        var fileType = file.type;
        if(fileType.indexOf('image')<0){
          this.$message.warning('请上传图片');
          return false;
        }
        //TODO 验证文件大小
      },
      handleChange (info) {
        this.picUrl = "";
        if (info.file.status === 'uploading') {
          this.uploadLoading = true;
          return
        }
        if (info.file.status === 'done') {
          var response = info.file.response;
          this.uploadLoading = false;
          if(response.success){
            this.model.custLogo = response.message;
            this.picUrl = response.message;
          }
          else{
            this.$message.warning(response.message);
          }
        }
      },
      //客户标注图上传
      handleChangebz (info) {
        this.bzpicUrl = "";
        if (info.file.status === 'uploading') {
          this.uploadLoading = true;
          return
        }
        if (info.file.status === 'done') {
          var response = info.file.response;
          this.uploadLoading = false;
          /*  console.log(response);*/
          if(response.success){
            this.model.biaoZhuLogo = response.message;
            this.bzpicUrl = response.message;
          }else{
            this.$message.warning(response.message);
          }
        }
      },
      getAvatarView(){
        return this.url.imgerver +"/"+ this.picUrl;
      },
      getAvatarViewbz(){
        return this.url.imgerver +"/"+ this.bzpicUrl;
      },

    }
  }
</script>

<style>
  .a-formItem {
    float: left;
    width: 370px;
  }
  .a-formItem1 {
    width: 925px;
    margin-left: -115px;
  }
  .avatar-uploader > .ant-upload {
    margin-left: 2px;

  }
  .querenBnt {
    width: 80px;
    margin-left: 620px;
    border-radius:5px;
    float: left;
  }
  .querenBnt1 {
    /*width: 170px;*/
    /*margin-left: 420px;*/
    border-radius:5px;
    float: left;
  }
  .ant-upload-select-picture-card i {
    font-size: 49px;
    color: #999;
  }
  .ant-upload-select-picture-card .ant-upload-text {
    margin-top: 8px;
    color: #666;
  }
</style>