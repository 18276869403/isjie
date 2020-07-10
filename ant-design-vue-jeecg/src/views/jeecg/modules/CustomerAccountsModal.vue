<template>
  <a-modal
    :title="title"
    :width="930"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel1"
    cancelText="关闭"
    :footer="null"
    v-if="!this.model.id">

    <a-spin :spinning="confirmLoading" style="height: 425px;margin-left: 12px">
      <a-form :form="form" style="margin-left: 15px;">

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="省份" class="aform">
          <a-select v-decorator="[ 'oneAreaId', validatorRules.oneAreaId]"  placeholder="请选择省份">
            <a-select-option value="" @click="initialOneAreaList">全部</a-select-option>
            <a-select-option v-for="(oneArea,oneIndex) in getOneArea" :key="oneIndex.toString()" :value="oneArea.id"
                             @click="initialGetTwoAreaList(oneArea.id)" >{{oneArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="城市" class="aform">
          <a-select v-decorator="[ 'twoAreaId', validatorRules.twoAreaId]" placeholder="请选择城市">
            <a-select-option value="" @click="initialTwoAreaList">全部</a-select-option>
            <a-select-option v-for="(twoArea,twoIndex) in getTwoArea" :key="twoIndex.toString()" :value="twoArea.id"
                             @click="initialGetCustomersList(twoArea.id)" >{{twoArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="客户名称" class="aform">
          <a-select v-decorator="[ 'customerId', validatorRules.customerId]"  placeholder="请选择客户名称">
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(customer,customerIndex) in getCustomers" :key="customerIndex.toString()" :value="customer.id">{{customer.name}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="账户名称" class="aform">
          <a-input placeholder="请输入账户名称" v-decorator="['account', validatorRules.account ]" />
        </a-form-item>
        <template v-if="!model.id">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="账户密码" class="aform">
            <a-input type="password" placeholder="请输入账户密码" v-decorator="['password', validatorRules.password ]" />
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="确认密码" class="aform">
            <a-input type="password" placeholder="请输入确认密码" v-decorator="['confirmpassword', validatorRules.confirmpassword ]" />
          </a-form-item>
        </template>

        <template v-if="!model.id">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="接收报警短信" class="aform" style="margin-left: -1px;" >
            <a-radio-group v-decorator="['shortLetter', validatorRules.shortLetter]" style="margin-left: 10px;" @change="change1">   <!--v-decorator="[ 'shortLetter',
validatorRules.shortLetter]"-->
              <a-radio value="1">接收</a-radio>
              <a-radio value="2">拒绝</a-radio>
            </a-radio-group>
          </a-form-item>
        </template>
        <template v-else>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="接收报警短信" class="aform" >
            <a-radio-group v-decorator="['shortLetter', {}]" style="margin-left: 10px;" @change="change1">   <!--v-decorator="[ 'shortLetter', validatorRules.shortLetter]"-->
              <a-radio :value="1">接收</a-radio>
              <a-radio :value="2">拒绝</a-radio>
            </a-radio-group>
          </a-form-item>
        </template>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="手机号码" class="aform" > <!--v-if="this.shortLetter1==1"-->
          <a-input placeholder="请输入手机号码" v-decorator="['phone', validatorRules.phone]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="状态" class="aform aform1">
          <a-select v-if="!model.id"  v-decorator="[ 'custState', validatorRules.custState]" placeholder="请选择状态">
            <a-select-option value="1">启用</a-select-option>
            <a-select-option value="2">禁用</a-select-option>
          </a-select>
          <a-select v-else v-decorator="[ 'custState', {}]" placeholder="请选择状态">
            <a-select-option :value="1">启用</a-select-option>
            <a-select-option :value="2">禁用</a-select-option>
          </a-select>
        </a-form-item>
        <template v-if="!model.id">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="备注" class="aform aform1">
            <a-textarea placeholder="请输入备注" v-decorator="['remark', {}]" style="height: 75px;"/>
          </a-form-item>
        </template>
        <template v-else>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="备注" class="aform aform1">
            <a-textarea placeholder="请输入备注" v-decorator="['remark', {}]" style="height: 135px;"/>
          </a-form-item>
        </template>
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
    :width="930"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleCancel"
    @cancel="handleCancel1"
    cancelText="关闭"
    v-else
   >

    <a-spin :spinning="confirmLoading" style="height: 400px;margin-left: 12px">
      <a-form :form="form" style="margin-left: 15px;">

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="省份" class="aform">
          <a-select v-decorator="[ 'oneAreaId', validatorRules.oneAreaId]"  placeholder="请选择省份">
            <a-select-option value="" @click="initialOneAreaList">全部</a-select-option>
            <a-select-option v-for="(oneArea,oneIndex) in getOneArea" :key="oneIndex.toString()" :value="oneArea.id"
                             @click="initialGetTwoAreaList(oneArea.id)" >{{oneArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="城市" class="aform">
          <a-select v-decorator="[ 'twoAreaId', validatorRules.twoAreaId]" placeholder="请选择城市">
            <a-select-option value="" @click="initialTwoAreaList">全部</a-select-option>
            <a-select-option v-for="(twoArea,twoIndex) in getTwoArea" :key="twoIndex.toString()" :value="twoArea.id"
                             @click="initialGetCustomersList(twoArea.id)" >{{twoArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="客户名称" class="aform">
          <a-select v-decorator="[ 'customerId', validatorRules.customerId]"  placeholder="请选择客户名称">
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(customer,customerIndex) in getCustomers" :key="customerIndex.toString()" :value="customer.id">{{customer.name}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="账户名称" class="aform">
          <a-input placeholder="请输入账户名称" v-decorator="['account', validatorRules.account ]" />
        </a-form-item>
        <template v-if="!model.id">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="账户密码" class="aform">
            <a-input type="password" placeholder="请输入账户密码" v-decorator="['password', validatorRules.password ]" />
          </a-form-item>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="确认密码" class="aform">
            <a-input type="password" placeholder="请输入确认密码" v-decorator="['confirmpassword', validatorRules.confirmpassword ]" />
          </a-form-item>
        </template>
        <template v-if="!model.id">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="接收报警短信" class="aform" style="margin-left: -1px;" >
            <a-radio-group v-decorator="['shortLetter', validatorRules.shortLetter]" style="margin-left: 10px;" @change="change1">   <!--v-decorator="[ 'shortLetter',
validatorRules.shortLetter]"-->
              <a-radio value="1">接收</a-radio>
              <a-radio value="2">拒绝</a-radio>
            </a-radio-group>
          </a-form-item>
        </template>
        <template v-else>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="接收报警短信" class="aform" >
            <a-radio-group v-decorator="['shortLetter', {}]" style="margin-left: 10px;" @change="change1">   <!--v-decorator="[ 'shortLetter', validatorRules.shortLetter]"-->
              <a-radio :value="1">接收</a-radio>
              <a-radio :value="2">拒绝</a-radio>
            </a-radio-group>
          </a-form-item>
        </template>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="手机号码" class="aform" > <!--v-if="this.shortLetter1==1"-->
          <a-input placeholder="请输入手机号码" v-decorator="['phone', validatorRules.phone]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="状态" class="aform aform1">
          <a-select v-if="!model.id"  v-decorator="[ 'custState', validatorRules.custState]" placeholder="请选择状态">
            <a-select-option value="1">启用</a-select-option>
            <a-select-option value="2">禁用</a-select-option>
          </a-select>
          <a-select v-else v-decorator="[ 'custState', {}]" placeholder="请选择状态">
            <a-select-option :value="1">启用</a-select-option>
            <a-select-option :value="2">禁用</a-select-option>
          </a-select>
        </a-form-item>
        <template v-if="!model.id">
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="备注" class="aform aform1">
            <a-textarea placeholder="请输入备注" v-decorator="['remark', {}]" style="height: 75px;"/>
          </a-form-item>
        </template>
        <template v-else>
          <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="备注" class="aform aform1">
            <a-textarea placeholder="请输入备注" v-decorator="['remark', {}]" style="height: 135px;"/>
          </a-form-item>
        </template>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import {getOneAreaAll,getTwoAreaAll,getCustomersAll,duplicateCheck} from '@/api/api'
  import ARadioGroup from 'ant-design-vue/es/radio/Group'
  import ATextarea from 'ant-design-vue/es/input/TextArea'

  export default {
    name: "CustomerAccountsModal",
    components: { ATextarea, ARadioGroup },
    data () {
      return {
        buttonWidth: 70,
        text: '是否继续添加客户账号?',
        shortLetter1:"",
        title:"操作",
        visible: false,
        model: {},
        getOneArea:[], //获取所有的省份
        getTwoArea:[],//获取所有的城市
        getCustomers:[],//获取所有的客户
        oneid:"",
        twoid:"",
        custid:"",
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
          customerId:{rules: [{ required: true, message: '请选择客户名称!' }]},
          oneAreaId:{rules: [{ required: true, message: '请选择省份!' }]},
          twoAreaId:{rules: [{ required: true, message: '请选择城市!' }]},
          custState:{rules: [{ required: true, message: '请选择状态!' }],initialValue: '1'},
          shortLetter:{rules: [{ required: true, message: '请选择是否接收报警短信!' }],initialValue: '2'},
          account:{ rules: [{
              required: true, message: '请输入账户名称!'
            },
              {
                validator: this.checkCustName,
              }]},
          password:{rules: [{
              required: true,message: '请输入密码!'

            }, {
              validator: this.validateToNextPassword,
            }],},
          confirmpassword:{
            rules: [{
              required: true, message: '请重新输入密码!',
            }, {
              validator: this.compareToFirstPassword,
            }],
          },
          phone:{rules: [{validator: this.validatePhone}]}
        },
        url: {
          add: "/monitor/customerAccounts/add",
          edit: "/monitor/customerAccounts/edit",
        },
      }
    },
    created () {
      this.shortLetter1=2
    },
    methods: {
      //获取所有的省份
      initialGetOneAreaList(){
        this.getOneArea = ['']
        getOneAreaAll().then((res) => {
          if (res.success){
            this.getOneArea = res.result
          } else {
            console.log(res.message)
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
            console.log(res.message)
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
            console.log(res.message)
          }
        })
      },
      //当未选择一、城市时，客户下拉框清空
      initialTwoAreaList(){
        this.getCustomers = ['']
        this.customerId="";
      },
      add (custid) {
        this.shortLetter1=2
        console.log(custid);
        if (custid != null){
          this.one1=custid;
          getAction('/monitor/customers/queryAllByCustId',{customerId:custid}).then((res)=> {
            console.log(res);
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
        let that = this
        that.initialGetOneAreaList()
        if (record.id != null){
          this.shortLetter1=record.shortLetter
          that.initialGetTwoAreaList(record.oneAreaId)
          that.initialGetCustomersList(record.twoAreaId)
        }
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'oneAreaId','twoAreaId','customerId','account','shortLetter','phone','custState','remark'))//,'password','confirmpassword'
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
      checkCustName(rule, value, callback){
        var params = {
          tableName: 'customer_accounts',
          fieldName: 'Account',
          fieldVal: value,
          dataId: this.model.id
        };
        duplicateCheck(params).then((res) => {
          if (res.success){
            callback()
          } else {
            callback("账号已存在!")
          }
        })
      },
      validateToNextPassword  (rule, value, callback) {
        const form = this.form;
        const confirmpassword=form.getFieldValue('confirmpassword');
        if (value && confirmpassword && value !== confirmpassword) {
          callback();
        }
        if (value && this.confirmDirty) {
          form.validateFields(['confirm'], { force: true })
        }
        callback();
      },
      compareToFirstPassword  (rule, value, callback) {
        const form = this.form;
        if (value && value !== form.getFieldValue('password')) {
          callback('两次输入的密码不一样！');
        } else {
          callback()
        }
      },
      validatePhone(rule, value, callback){
        if(!value){
          callback()
        }else{
          //update-begin--Author:kangxiaolin  Date:20190826 for：[05] 手机号不支持199号码段--------------------
          if(new RegExp(/^1[3|4|5|7|8|9][0-9]\d{8}$/).test(value)){
            //update-end--Author:kangxiaolin  Date:20190826 for：[05] 手机号不支持199号码段--------------------
            var params = {
              tableName: 'customer_accounts',
              fieldName: 'Phone',
              fieldVal: value,
              dataId: this.model.id
            };
            duplicateCheck(params).then((res) => {
              if (res.success) {
                callback()
              }else {
                callback('手机号已存在!')
              }
            })
          }else{
            callback("请输入正确格式的手机号码!");
          }
        }
      },
      change1(e){
        this.shortLetter1=e.target.value
      }
    }
  }
</script>

<style lang="less" scoped>
  .aform {
    float: left;
    width: 425px;
  }
  .aform1 {
    width: 1060px;
    margin-left: -158px;
  }

  .querenBnt {
    width: 80px;
    margin-left: 720px;
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