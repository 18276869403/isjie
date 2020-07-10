<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :footer="null"
    @cancel="handleCancel"
   >
    
    <a-spin :spinning="confirmLoading"  style="height: 400px;">
      <a-form :form="form" style="margin-left: 12px;">

        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="省份"  class="a-formItem">
          <a-select v-decorator="[ 'oneAreaName', {}]"  placeholder="请选择省份" class="a-input1" style="cursor: not-allowed" disabled>
            <a-select-option value="" @click="initialOneAreaList">全部</a-select-option>
            <a-select-option v-for="(oneArea,oneAreaindex) in getOneArea" :key="oneAreaindex.toString()" :value="oneArea.id"
                             @click="initialGetTwoAreaList(oneArea.id)" >{{oneArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="城市"  class="a-formItem">
          <a-select v-decorator="[ 'twoAreaName', {}]" placeholder="请选择城市" class="a-input1" style="cursor: not-allowed" disabled>
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(twoArea,twoAreaindex) in getTwoArea" :key="twoAreaindex.toString()" :value="twoArea.id"
                             @click="initialGetCustomersList(twoArea.id)">{{twoArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="客户名称"  class="a-formItem">
          <a-select v-decorator="[ 'name', {}]" placeholder="请选择客户名称" class="a-input" style="cursor: not-allowed" disabled>
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(customer,customerindex) in getCustomers" :key="customerindex.toString()" :value="customer.id">{{customer.name}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="项目名称" class="a-formItem" >
          <a-input placeholder="请输入项目名称" v-decorator="['projectName', {} ]"  readonly/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="设备imei" class="a-formItem"    >
          <a-input placeholder="请输入设备imei" v-decorator="['imei', {}]" readonly/>
        </a-form-item>
        <a-form-item label="设备类型" :labelCol="labelCol" :wrapperCol="wrapperCol"  class="a-formItem">
          <a-select v-decorator="[ 'equipType', {}]" placeholder="请选择设备类型"  class="a-input" style="cursor: not-allowed" disabled >
            <a-select-option value="1">探测器</a-select-option>
            <a-select-option value="2">控制器</a-select-option>
            <a-select-option value="3">输出模块</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="设备地址" class="a-formItem">
          <a-input placeholder="请输入设备address" v-decorator="['equipAdd', {}]" readonly/>
        </a-form-item>

        <a-form-item label="发送短信" :labelCol="labelCol" :wrapperCol="wrapperCol"  class="a-formItem">
          <a-select v-decorator="[ 'smsType', {}]" placeholder="发送短信"  class="a-input" style="cursor: not-allowed" disabled >
            <a-select-option value="0">未发送</a-select-option>
            <a-select-option value="1">已发送</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="报警类型" class="a-formItem">
          <a-input placeholder="请输入报警类型" v-decorator="['alarmType', {}]" readonly/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="报警时间" class="a-formItem">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'alarmDate', {}]"  style="cursor: not-allowed;backgrounr:white" disabled />
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="发送时间" class="a-formItem">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'sendDate', {}]"  style="cursor: not-allowed" disabled  />
        </a-form-item>

		
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"
  import {getOneAreaAll,getTwoAreaAll} from '@/api/api'
  import {getCustomersAll} from '@/api/api'
  export default {
    name: "AlarmSmsModal",
    data () {
      return {
        title:"操作",
        visible: false,
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
        },
        url: {
          add: "/monitor/alarmSms/add",
          edit: "/monitor/alarmSms/edit",
        },
      }
    },
    created () {
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
      add (devid) {
        if (devid != null){
          getAction('/monitor/customers/queryAllByCustId',{customerId:devid}).then((res)=> {
            // console.log(res);
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
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'imei','equipType','equipAdd','smsType','alarmType','oneAreaName','twoAreaName','name','projectName'))
		  //时间格式化
          this.form.setFieldsValue({alarmDate:this.model.alarmDate?moment(this.model.alarmDate):null})
          this.form.setFieldsValue({sendDate:this.model.sendDate?moment(this.model.sendDate):null})
        });

      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
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
            formData.alarmDate = formData.alarmDate?formData.alarmDate.format('YYYY-MM-DD HH:mm:ss'):null;
            formData.sendDate = formData.sendDate?formData.sendDate.format('YYYY-MM-DD HH:mm:ss'):null;
            
            console.log(formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })



          }
        })
      },
      handleCancel () {
        this.close()
      },


    }
  }
</script>

<style lang="less" scoped>
  .a-formItem {
    float: left;
    width: 370px;
  }


</style>