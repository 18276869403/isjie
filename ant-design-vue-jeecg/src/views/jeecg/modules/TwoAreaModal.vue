<template>
  <a-modal
    :title="title"
    :width="500"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-spin :spinning="confirmLoading">
      <a-form :form="form" style="height: 105px;margin-left: 25px;">


        <!--        <a-popconfirm v-if="this.$route.query.oneid==null">-->
        <a-form-item label="选择省份" :labelCol="labelCol" :wrapperCol="wrapperCol" class="a-form-item">
          <a-select v-decorator="[ 'oneAreaId', validatorRules.oneAreaId]" placeholder="请选择省份" >
            <a-select-option  v-for="(one,index) in oneList"
                              :key="index.toString()"
                              :value="one.id"
                              @click="initialTwoAreaList()" >
              {{ one.oneAreaName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <!--        </a-popconfirm>-->

        <a-form-item
          label="城市"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'twoAreaName', validatorRules.twoAreaName]"
                   placeholder="请输入城市名"
                   class="a-input">
          </a-input>
        </a-form-item>
       <!-- <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="状态" class="a-form" >
          <a-select v-if="!model.id"  v-decorator="[ 'delFlag', validatorRules.delFlag]" placeholder="请选择状态" class="a-input">
            <a-select-option value="0">正常</a-select-option>
            <a-select-option value="1">禁用</a-select-option>
          </a-select>
          <a-select v-else v-decorator="[ 'delFlag', {}]" placeholder="请选择状态" class="a-input">
            <a-select-option :value="0">正常</a-select-option>
            <a-select-option :value="1">禁用</a-select-option>
          </a-select>
        </a-form-item>
-->
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import { getAction } from '../../../api/manage'
  import { NeedGetoneall } from '@/api/api'
  export default {
    name: "TwoAreaModal",
    components: {
    },
    data () {

      return {
        // oneAreaId: '',
        // oneAreaName: '',
        oneList: [],
        form: this.$form.createForm(this),
        title:"操作",
        width:800,
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        max: '',
        confirmLoading: false,
        validatorRules: {
          oneAreaId: { rules: [{ required: true, message: '请选择省份!'}] },
          twoAreaName: { rules: [{ required: true, message: '请输入城市!' }] },
          /*delFlag: { rules: [{ required: true, message: '请输入状态，0正常，1禁用' }],initialValue: '0' }*/
        },

        url: {
          add: "/monitor/twoArea/add",
          edit: "/monitor/twoArea/edit",
          // queryMixSort: '/shijie/twoArea/queryMixSort'
        }
      }
    },
    // created () {
    //   //this.initialTwoAreaList("");
    //   //this.getOneArea();
    //
    // },
    methods: {
      initialTwoAreaList() {
      },
      add(oneareaid) {
        if(oneareaid!=""){
          getAction("/monitor/oneArea/getoneall", { oneAreaId: oneareaid}).then((res) => {
            this.max = res
          })
        }
        this.edit({})
      },
      edit (record) {

        let that = this
        that.initialOneList()
        this.form.resetFields()
        this.model = Object.assign({}, record)
        this.visible = true
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'oneAreaId','twoAreaName'))
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      initialOneList() {
        var url = "/monitor/oneArea/getoneall"
        getAction(url).then((ones) =>{
          if(ones.success){


            this.oneList = ones.result

          }else{

          }
        })
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
      popupCallback(row){
        this.form.setFieldsValue(pick(row,'oneAreaId','twoAreaName'))
      },
      oneAreaList(){
        var url = "/monitor/oneArea/getoneall"
        getAction(url).then((ones) =>{
          if(ones.success){
            // this.oneAreaId = this.$route.query.oneid;
            this.oneList = ones.result;
          }else{
          }
        })
      },
      // getOneArea(oneAreaId){
      //   if(this.$route.query.oneid!=null){
      //     var url = "/shijie/oneArea/queryById"
      //     getAction(url,{id: this.$route.query.oneid}).then((res) =>{
      //       if(res.success){
      //         console.log("请求成功---------------")
      //         this.oneAreaId=this.$route.query.oneid;
      //         this.oneAreaName=res.result.oneAreaName;
      //         console.log("onearea是：  "+res.result);
      //       }else{
      //         console.log("请求失败---------------")
      //       }
      //     })
      //   }else{}
      // }
    }
  }
</script>