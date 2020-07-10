<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :footer="null"
    @cancel="handleCancel"
   >

    <a-spin :spinning="confirmLoading"  style="height: 382px;margin-left: 55px">
      <a-form :form="form" style="width: 825px;">
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="省份"  class="a-form-item1">
          <a-select v-decorator="[ 'oneAreaName', {}]"  placeholder="请选择省份" class="a-input1" style="cursor: not-allowed" disabled>
            <a-select-option value="" @click="initialOneAreaList">全部</a-select-option>
            <a-select-option v-for="(oneArea,oneAreaindex) in getOneArea" :key="oneAreaindex.toString()" :value="oneArea.id"
                             @click="initialGetTwoAreaList(oneArea.id)" >{{oneArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="城市"  class="a-form-item1">
          <a-select v-decorator="[ 'twoAreaName', {}]" placeholder="请选择城市" class="a-input1" style="cursor: not-allowed" disabled>
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(twoArea,twoAreaindex) in getTwoArea" :key="twoAreaindex.toString()" :value="twoArea.id"
                             @click="initialGetCustomersList(twoArea.id)">{{twoArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="客户名称"  class="a-form-item">
          <a-select v-decorator="[ 'name', {}]" placeholder="请选择客户名称" class="a-input" style="cursor: not-allowed" disabled>
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(customer,customerindex) in getCustomers" :key="customerindex.toString()" :value="customer.id">{{customer.name}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="设备类型" :labelCol="labelCol" :wrapperCol="wrapperCol"  class="a-form-item">
          <a-select v-decorator="[ 'alarmDeviceType', {}]" placeholder="请选择设备类型"  class="a-input" style="cursor: not-allowed" disabled >
            <a-select-option value="1">探测器</a-select-option>
            <a-select-option value="2">控制器</a-select-option>
            <a-select-option value="3">输出模块</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="设备IMEI" class="a-form-item a-formItem1">
          <a-input placeholder="请输入设备IMEI" v-decorator="['alarmDeviceImei',{}]" class="a-input2" readonly/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="设备ADD" class="a-form-item a-formItem1">
          <a-input placeholder="请输入设备ADD" v-decorator="['alarmAddress', {}]" class="a-input2" readonly/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="编号/位置" class="a-form-item">
          <a-input placeholder="请输入设备编号" v-decorator="['deviceNum',{}]" class="a-input" readonly/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="检测目标" class="a-form-item">
          <a-input placeholder="请输入检测目标" v-decorator="['detectionTarget', {}]" class="a-input" readonly/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="浓度" class="a-form-item">
          <a-input placeholder="请输入浓度" v-decorator="[ 'alarmPv', {}]" class="a-input" readonly/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="报警类型" class="a-form-item">
          <a-input placeholder="报警的类型" v-decorator="[ 'tstatus',{}]" class="a-input" readonly/>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="报警时间" class="a-form-item">
          <a-input placeholder="报警的时间" v-decorator="[ 'alarmTime',{}]" class="a-input" readonly/>
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
    name: "AlarmRecordModal",
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
          alarmTime:{rules: [{ required: true, message: '请输入报警时间!' }]},
          alarmType:{rules: [{ required: true, message: '请输入报警类型!' }]},
          customerId:{rules: [{ required: true, message: '请输入客户id!' }]},
          deviceId:{rules: [{ required: true, message: '请输入设备id!' }]},
        },
        url: {
          add: "/monitor/alarmRecord/add",
          edit: "/monitor/alarmRecord/edit",
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
          this.form.setFieldsValue(pick(this.model,'twoAreaName','alarmTime','name','oneAreaName','oneAreaId','twoAreaId','customerId','alarmDeviceType','deviceId','alarmDeviceImei','alarmICCID', 'alarmAddress','deviceNum','detectionTarget','alarmPv','tstatus'))
          //时间格式化
          //this.form.setFieldsValue({alarmTime:this.model.alarmTime?moment(this.model.alarmTime):null})
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
            formData.alarmTime = formData.alarmTime?formData.alarmTime.format('YYYY-MM-DD HH:mm:ss'):null;

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