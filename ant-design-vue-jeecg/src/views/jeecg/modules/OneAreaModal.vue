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
      <a-form :form="form" style="height: 165px;margin-left: -5px;">
        <a-form-item
          label="省份"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol">
          <a-input v-decorator="[ 'oneAreaName', validatorRules.oneAreaName]"
                   placeholder="请输入省份名" class="a-input"></a-input>
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
        </a-form-item>-->
        <a-form-item label="备注"
                     :labelCol="labelCol"
                     :wrapperCol="wrapperCol">
          <a-textarea style="left: 10px;"  v-decorator="['remark']" rows="4" placeholder="请输入备注"/>
        </a-form-item>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>

  import { httpAction,getAction } from '@/api/manage'
  import pick from 'lodash.pick'

  export default {
    name: "OneAreaModal",
    components: {
    },
    data () {
      return {
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
        confirmLoading: false,
        validatorRules:{
          delFlag:{rules: [{ required: true, message: '请选择状态!' }],initialValue: '0'},
          oneAreaName:{ rules: [{ required: true, message: '请输入省份!' }] },
          remark:{},
        },
        url: {
          add: "/monitor/oneArea/add",
          edit: "/monitor/oneArea/edit",
        }

      }
    },
    created () {
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'oneAreaName','remark'))
        })
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
        this.form.setFieldsValue(pick(row,'oneAreaName','remark'))
      },


    }
  }
</script>

<style lang="less" scoped>
  .a-form-item {
    margin-left: 10px;
  }
  .a-input {
    margin-left: 10px;
  }
</style>