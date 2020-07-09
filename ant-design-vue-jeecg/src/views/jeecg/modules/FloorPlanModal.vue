<template>
  <a-modal
    :title="title"
    :width="770"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel1"
    cancelText="关闭"
    :footer="null"
    v-if="!this.model.id"
    style="height: 450px;position: absolute;top: 40%;left: 40%;margin-top: -190px;margin-left:-235px;">

    <a-spin :spinning="confirmLoading" style="height: 445px;width: 755px;margin-left: 12px">
      <a-form :form="form">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="省份"   class="a-form">
          <a-select v-decorator="[ 'oneAreaId', validatorRules.oneAreaId]"  placeholder="请选择省份"   class="a-input">
            <a-select-option value="" @click="initialOneAreaList">全部</a-select-option>
            <a-select-option v-for="(oneArea,oneAreaindex) in getOneArea" :key="oneAreaindex.toString()" :value="oneArea.id"
                             @click="initialGetTwoAreaList(oneArea.id)" >{{oneArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="城市"   class="a-form">
          <a-select v-decorator="[ 'twoAreaId', validatorRules.twoAreaId]" placeholder="请选择城市"  class="a-input">
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(twoArea,twoAreaindex) in getTwoArea" :key="twoAreaindex.toString()" :value="twoArea.id"
                             @click="initialGetCustomersList(twoArea.id)">{{twoArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="客户名称"  class="a-form">
          <a-select v-decorator="[ 'customerId', validatorRules.customerId]" placeholder="请选择客户名称"  class="a-input">
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(customer,customerindex) in getCustomers" :key="customerindex.toString()" :value="customer.id">{{customer.name}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="楼层名"  class="a-form">
          <a-input placeholder="请输入楼层名" v-decorator="['floorName', validatorRules.floorName ]"  class="a-input"/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="序号"  class="a-form">
          <a-input placeholder="请输入序号" v-decorator="['backup1', validatorRules.backup1 ]"  class="a-input"/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="平面图" class="a-formItem">
          <a-icon type="close"  @click="qxkh()" style="margin-left: 150px"/>
          <a-upload
            list-type="picture-card"
            class="avatar-uploader"
            :showUploadList="false"
            :action="uploadAction"
            :data="{'isup':1}"
            :headers="headers"
            :beforeUpload="beforeUpload"
            @change="handleChange"
            v-decorator="['custLogo']">
            <img v-if="picUrl" :src="getAvatarView()" alt="平面图" style="height:104px;max-width:300px;" />
            <div v-else>
              <a-icon :type="uploadLoading ? 'loading' : 'plus'" />
              <div class="ant-upload-text" >上传</div>
            </div>
          </a-upload>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="备注" class="a-form-remark">
          <a-textarea placeholder="请输入备注" v-decorator="['remark', {}]" class="a-input-remark"/>
        </a-form-item>


      </a-form>
    </a-spin>

    <div style="margin-bottom: 25px;">
      <div class="querenBnt">
        <a-button  @click="handleCancel1">关闭</a-button >
      </div>
      <div class="querenBnt1">
        <a-popconfirm placement="topLeft" okText="是" cancelText="否" @confirm="confirm" @cancel="handleCancel">
          <template slot="title">
            <p>{{text}}</p>
          </template>
          <a-button style="background-color: #1890ff;color:white;" >确认</a-button>
        </a-popconfirm>
      </div>
    </div>
  </a-modal>

  <a-modal
    :title="title"
    :width="770"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleCancel"
    @cancel="handleCancel1"
    cancelText="关闭"
    v-else
    style="height: 450px;position: absolute;top: 40%;left: 40%;margin-top: -190px;margin-left:-235px;">

    <a-spin :spinning="confirmLoading" style="height: 428px;width: 755px;margin-left: 12px">
      <a-form :form="form">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="省份"   class="a-form">
          <a-select v-decorator="[ 'oneAreaId', validatorRules.oneAreaId]"  placeholder="请选择省份"   class="a-input">
            <a-select-option value="" @click="initialOneAreaList">全部</a-select-option>
            <a-select-option v-for="(oneArea,oneAreaindex) in getOneArea" :key="oneAreaindex.toString()" :value="oneArea.id"
                             @click="initialGetTwoAreaList(oneArea.id)" >{{oneArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="城市"   class="a-form">
          <a-select v-decorator="[ 'twoAreaId', validatorRules.twoAreaId]" placeholder="请选择城市"  class="a-input">
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(twoArea,twoAreaindex) in getTwoArea" :key="twoAreaindex.toString()" :value="twoArea.id"
                             @click="initialGetCustomersList(twoArea.id)">{{twoArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="客户名称"  class="a-form">
          <a-select v-decorator="[ 'customerId', validatorRules.customerId]" placeholder="请选择客户名称"  class="a-input">
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(customer,customerindex) in getCustomers" :key="customerindex.toString()" :value="customer.id">{{customer.name}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="楼层名"  class="a-form">
          <a-input placeholder="请输入楼层名" v-decorator="['floorName', validatorRules.floorName ]"  class="a-input"/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="序号"  class="a-form">
          <a-input placeholder="请输入序号" v-decorator="['backup1', validatorRules.backup1 ]"  class="a-input"/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="平面图" class="a-formItem">
          <a-icon type="close"  @click="qxkh()" style="margin-left: 150px"/>
          <a-upload
            list-type="picture-card"
            class="avatar-uploader"
            :showUploadList="false"
            :action="uploadAction"
            :data="{'isup':1}"
            :headers="headers"
            :beforeUpload="beforeUpload"
            @change="handleChange"
            v-decorator="['custLogo']">
            <img v-if="picUrl" :src="getAvatarView()" alt="平面图" style="height:104px;max-width:300px;" />
            <div v-else>
              <a-icon :type="uploadLoading ? 'loading' : 'plus'" />
              <div class="ant-upload-text" >上传</div>
            </div>
          </a-upload>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="备注" class="a-form-remark">
          <a-textarea placeholder="请输入备注" v-decorator="['remark', {}]" class="a-input-remark"/>
        </a-form-item>


      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import Vue from 'vue'
  import {getOneAreaAll,getTwoAreaAll,getCustomersAll} from '@/api/api'
  export default {
    name: "FloorPlanModal",
    data () {
      return {
        title:"操作",
        visible: false,
        buttonWidth: 70,
        text: '是否继续添加楼层图?',
        model: {},
        getOneArea:[], //获取所有的一级区域
        getTwoArea:[],//获取所有的二级区域
        getCustomers:[],//获取所有的客户
        oneid:"",
        twoid:"",
        custid:"",
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
        confirmLoading: false,
        form: this.$form.createForm(this),
        picUrl: '',
        validatorRules:{
          oneAreaId:{rules: [{ required: true, message: '请选择省份!' }]},
          twoAreaId:{rules: [{ required: true, message: '请选择城市!' }]},
          customerId:{rules: [{ required: true, message: '请选择客户名称!' }]},
          floorName:{rules: [{ required: true, message: '请输入楼层名!' }]},
          backup1:{rules: [{ required: true, message: '请输入序号!' }]},

        },
        url: {
          add: "/monitor/floorPlan/add",
          edit: "/monitor/floorPlan/edit",
          fileUpload: window._CONFIG['domianURL']+"/sys/common/upload",
          imgerver: window._CONFIG['domianURL']+"/sys/common/view",
        },
      }
    },
    created () {
      const token = Vue.ls.get(ACCESS_TOKEN);
      this.headers = {"X-Access-Token":token}
    },
    computed:{
      uploadAction:function () {
        return this.url.fileUpload;
      }
    },
    methods: {
      qxkh(){
        this.picUrl = "";
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
      //获取所有的城市
      initialGetTwoAreaList(id){
        this.getTwoArea = ['']
        getTwoAreaAll({id:id}).then((res) => {
          if (res.success){
            this.getTwoArea = res.result
            console.log("456")
          } else {
            /* console.log(res.message)*/
          }
        })
      },
      //当未选择省份时，城市、客户下拉框清空
      initialOneAreaList(){
        this.getTwoArea = ['']
        this.twoAreaId="";
        this.getCustomers = ['']
        this.customerId="";
      },
      //获取所有的客户
      initialGetCustomersList(id){
        this.getCustomers = ['']
        getCustomersAll({id:id}).then((res) => {
          if (res.success){
            this.getCustomers = res.result
          } else {
            /* console.log(res.message)*/
          }
        })
      },
      //当未选择一、城市时，客户下拉框清空
      initialTwoAreaList(){
        this.getCustomers = ['']
        this.customerId="";
      },
      add (floorid) {
        this.picUrl = "";
        if (floorid != null){
          getAction('/monitor/customers/queryAllByCustId',{customerId:floorid}).then((res)=> {
            /*  console.log(res);*/
            this.oneid = res.result.oneAreaId;
            this.twoid = res.result.twoAreaId;
            this.custid = res.result.id;
            this.initialGetTwoAreaList(this.oneid)
            this.initialGetCustomersList(this.twoid)
            // console.log(this.oneid,this.twoid,this.custid);
            this.edit({oneAreaId:this.oneid,twoAreaId: this.twoid,customerId: this.custid});
          })
        }else {
          this.edit({});
        }
      },
      edit (record) {
        console.log(record)
        let that=this;
        that.initialGetOneAreaList();
        if (record.id != null){
          console.log(record.oneAreaId,record.twoAreaId)
          that.initialGetTwoAreaList(record.oneAreaId)
          that.initialGetCustomersList(record.twoAreaId)
        }
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'oneAreaId','twoAreaId','customerId','floorName','backup1','planPic','remark',))
          //时间格式化
        });
        this.picUrl = this.model.planPic;
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
            }).finally(() => {
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
                this.edit({oneAreaId:this.model.oneAreaId,twoAreaId: this.model.twoAreaId,customerId: this.model.customerId});
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
      handleChange (info) {
        this.picUrl = "";
        if (info.file.status === 'uploading') {
          this.uploadLoading = true;
          return
        }
        if (info.file.status === 'done') {
          var response = info.file.response;
          this.uploadLoading = false;
          /* console.log(response);*/
          if(response.success){
            this.model.planPic = response.message;
            this.picUrl = response.message;
          }else{
            this.$message.warning(response.message);
          }
        }
      },
      getAvatarView(){
        return this.url.imgerver +"/"+ this.picUrl;
      },


    }
  }
</script>

<style lang="less" scoped>
  .avatar-uploader >.ant-upload {


  }
  .ant-upload-select-picture-card i {
    font-size: 49px;
    color: #999;
  }
  .ant-upload-select-picture-card .ant-upload-text {
    margin-top: 8px;
    color: #666;
  }
  .a-formItem {
    float: left;
    width: 370px;

  }
  .a-form{
    width: 358px;
    float: left;
    margin-left: 2px;
  }
  .a-input {
    width: 250px;
  }
  .a-form-item2{
    float: left;
  }
  .a-form-remark{
    width: 918px;
    float: left;
    margin-left: -116px;
    margin-top: -9px;
    height: 110px;

  }
  .a-input-remark{
    height: 100px;
  }
  .querenBnt {
    width: 80px;
    margin-left: 570px;
    border-radius:5px;
    float: left;
  }
  .querenBnt1 {
    /*width: 170px;*/
    /*margin-left: 420px;*/
    border-radius:5px;
    float: left;
  }
</style>