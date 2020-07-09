<template>
  <a-modal
    :title="title"
    :width="840"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel1"
    cancelText="关闭"
    :footer="null"
    style=" position: absolute;top: 40%;left: 40%;margin-top: -270px;margin-left:-195px;">

    <a-spin :spinning="confirmLoading" style="height: 170px;margin-left: 75px">
      <a-form :form="form" style="width: 825px;">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="省份" class="a-form-item1">
          <a-select v-decorator="[ 'oneAreaId', validatorRules.oneAreaId]" placeholder="请选择省份" class="a-input1">
            <a-select-option value="" @click="initialOneAreaList">全部</a-select-option>
            <a-select-option v-for="(oneArea,oneAreaindex) in getOneArea" :key="oneAreaindex.toString()" :value="oneArea.id"
                             @click="initialGetTwoAreaList(oneArea.id)">{{oneArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item :labelCol="labelCol"
                     :wrapperCol="wrapperCol"
                     label="城市" class="a-form-item1">
          <a-select v-decorator="[ 'twoAreaId', validatorRules.twoAreaId]" placeholder="请选择城市" class="a-input1">
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(twoArea,twoAreaindex) in getTwoArea" :key="twoAreaindex.toString()" :value="twoArea.id"
                             @click="initialGetCustomersList(twoArea.id)">{{twoArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item :labelCol="labelCol"
                     :wrapperCol="wrapperCol"
                     label="客户名称" class="a-form-item">
          <a-select v-decorator="[ 'customerId', validatorRules.customerId]" placeholder="请选择客户名称" class="a-input">
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(customer,customerindex) in getCustomers" :key="customerindex.toString()" :value="customer.id">{{customer.name}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item :labelCol="labelCol"
                     :wrapperCol="wrapperCol"
                     label="区域名称" class="a-form-item2">
          <a-input placeholder="区域名称" v-decorator="['areaName', validatorRules.areaName]" class="a-input2" />
        </a-form-item>

      </a-form>
    </a-spin>
    <div style="margin-bottom: 25px;">
      <div class="querenBnt">
        <a-button @click="handleCancel1">关闭</a-button>
      </div>
      <div class="querenBnt1">
        <a-popconfirm placement="topLeft" okText="是" cancelText="否" @confirm="confirm" @cancel="handleCancel">
          <template slot="title">
            <p>{{text}}</p>
          </template>
          <a-button style="background-color: #1890ff;color:white;">确认</a-button>
        </a-popconfirm>
      </div>
    </div> 
    </a-modal>
</template>

<script>
  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import {getOneAreaAll,getTwoAreaAll} from '@/api/api'
  import {getCustomersAll} from '@/api/api'
  export default {
    name: "DeviceAreaModal",
    data () {
      return {
        title:"操作",
        visible: false,
        buttonWidth: 70,
        text: '是否继续添加客户区域?',
        model: {},
        oneid:"",
        twoid:"",
        custid:"",
        getOneArea:[], //获取所有的一级区域
        getTwoArea:[],//获取所有的二级区域
        getCustomers:[],//获取所有的客户
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
          oneAreaId:{rules: [{ required: true, message: '请选择省份!' }]},
          twoAreaId:{rules: [{ required: true, message: '请选择城市!' }]},
          customerId:{rules: [{ required: true, message: '请选择客户名称!' }]},
          deviceType:{rules: [{ required: true, message: '请选择设备类型!' }]},
          deviceImei:{rules: [{ required: true, message: '请输入设备IMEI!' }]},
          iccid:{rules: [{ required: true, message: '请输入ICCID!' }]},
          addressNumber:{rules: [{ required: true, message: '请输入设备ADD!' }]},
          statusType: { rules: [{ required: true, message: '请选择设备状态!' }], initialValue: '正常' },
          areaName: { rules: [{ required: true, message: '请输入区域名称!' }]},
        },
        url: {
          add: "/monitor/deviceArea/add",
          edit: "/monitor/deviceArea/edit",
        },
      }
    },
    created () {
      /*  this.initialGetOneAreaList()*/
    },
    methods: {
      //获取所有的省份
      initialGetOneAreaList(){
        this.getOneArea = ['']
        getOneAreaAll().then((res) => {
          if (res.success){
            this.getOneArea = res.result
          } else {
            /*   console.log(res.message)*/
          }
        })
      },
      //获取所有的城市
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
      //当未选择一级区域时，二级区域下拉框清空
      initialOneAreaList(){
        this.getTwoArea = ['']
        this.model.twoAreaId="";
      },
      //当未选择省份时，城市、客户下拉框清空
      initialOneAreaList(){
        this.getTwoArea = ['']
        this.model.twoAreaId="";
        this.getCustomers = ['']
        this.model.customerId="";
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
        this.model.customerId="";
      },

      add () {
          this.edit({});
      },
      customerAdd(record) {
        if (record) {
          if (record.oneAreaId) {
            record.oneAreaId = record.oneAreaId * 1;
          }
          if (record.twoAreaId) {
            record.twoAreaId = record.twoAreaId * 1;
          }
          if (record.customerId) {
            record.customerId = record.customerId * 1;
          }
          let that = this;
          that.initialGetOneAreaList();
          that.initialGetTwoAreaList(record.oneAreaId);
          that.initialGetCustomersList(record.twoAreaId)
          this.form.resetFields();
          this.model = Object.assign({}, record);
          this.visible = true;
          this.$nextTick(() => {
            this.form.setFieldsValue(pick(this.model, 'oneAreaId', 'twoAreaId'
              , 'customerId', 'areaName'
            ));
            //时间格式化
          });
        } else {
          this.edit({});
        }
      },
      edit (record) {
        let that=this;
        that.initialGetOneAreaList();
        if (record.id != null){
          that.initialGetTwoAreaList(record.oneAreaId)
          that.initialGetCustomersList(record.twoAreaId)
        }
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model, 'oneAreaId', 'twoAreaId'
            , 'customerId', 'areaName'
          ));
          //时间格式化
        });
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
            console.log("34343",formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if (res.success) {
                that.$message.success("操作成功");
                that.$emit('ok');
                this.edit({
                  oneAreaId: this.model.oneAreaId,
                  twoAreaId: this.model.twoAreaId,
                  customerId: this.model.customerId
                });
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
            console.log("9999",formData)
            console.log(httpurl);
            httpAction(httpurl, formData, method).then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                console.log("123456")
                that.$emit('ok')
              } else {
                console.log("666666")
                that.$message.warning(res.message)
              }
            }).finally(() => {
              that.confirmLoading = false
              this.close();
            })
          }
        })
      },
    }
  }
</script>

<style lang="less" scoped>
  .a-form-item {
    width: 360px;
    float: left;
    margin-top: -12px;
  }
  .a-input {
    width: 220px;
    margin-top: -15px;
  }
  .a-form-item1{
    width: 360px;
    float: left;

  }
  .querenBnt {
    width: 80px;
    margin-left:644px;
    border-radius:5px;
    float: left;
  }
  .querenBnt1 {
    /*width: 170px;*/
    /*margin-left: 420px;*/
    border-radius:5px;
    float: left;
  }
  .a-formItem1{
    width: 868px;
    margin-left: -106px;

    white-space: pre-line;
  }
  .a-input1 {
    width: 220px;
    margin-top: -15px;

  }
  .a-form-item2{
    width: 580px;
    float: left;
    margin-top: -13px;
    width: 880px;
    margin-left: -109px;

  }
  .a-input2 {
    width: 580px;

  }
  .a-form-remark{
    width: 868px;
    float: left;
    margin-left: -106px;
    margin-top: -9px;
    height: 90px;
  }
  .a-input-remark{
    height: 100px;
  }
  .form-name{
    margin-left: -107px;
    width: 874px;
  }
</style>
