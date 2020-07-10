<template>
  <a-modal
    :title="title"
    :width="460"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">

    <a-spin :spinning="confirmLoading" style="height: 105px">
      <a-form :form="form">
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="省份">
          <a-select  v-decorator="[ 'num', validatorRules.num]" placeholder="请选择省份">
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(oneArea,oneAreaindex) in getOneArea" :key="oneAreaindex.toString()" :value="oneArea.id">{{oneArea.areaName}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="城市">
          <a-input placeholder="请输入城市" v-decorator="['areaName', {}]" />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import {getOneAreaAll} from '@/api/api'

  export default {
    name: "AreaModal",
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
        getOneArea:[], //获取所有的一级区域
        oneid:"",
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
          num:{rules: [{ required: true, message: '请输入省份!' }]},
        },
        url: {
          add: "/monitor/area/add",
          edit: "/monitor/area/edit",
        },
      }
    },
    created () {
      // this.initialGetOneAreaList()
    },
    methods: {
      //获取所有的省份
      initialGetOneAreaList (){
        this.getOneArea = ['']
        getOneAreaAll().then((res) => {
          if (res.success){
            this.getOneArea = res.result
          } else {
            console.log(res.message)
          }
        })
      },
      add (num1) {
        if (num1 != null){
          getAction('/monitor/area/querAreaByNum',{num:num1}).then((res)=> {
            this.oneid = res.result.id;
            this.edit({num:this.oneid});
          })
        }else {
          this.edit({});
        }
      },
      edit (record) {
        let that = this
        that.initialGetOneAreaList ();
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'areaName','num'))
          //时间格式化
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

</style>