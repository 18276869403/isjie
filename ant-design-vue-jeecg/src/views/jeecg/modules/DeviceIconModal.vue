<template>
  <a-modal
    :title="title"
    :width="500"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    style="height: 450px;position: absolute;top: 35%;left: 45%;margin-top: -210px;margin-left:-195px;"
  >

    <a-spin :spinning="confirmLoading" style="height: 369px;">
      <a-form :form="form" style="margin-left: 20px;">

        <a-form-item label="设备类型" :labelCol="labelCol" :wrapperCol="wrapperCol"  class="form-item">
          <a-select v-decorator="[ 'deviceType', {}]" placeholder="请选择设备类型"  class="a-input">
            <a-select-option value="探测器">探测器</a-select-option>
            <a-select-option value="控制器">控制器</a-select-option>
            <a-select-option value="输出模块">输出模块</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="图标类型" :labelCol="labelCol" :wrapperCol="wrapperCol"  class="form-item">
          <a-select v-decorator="[ 'iconType', {}]" placeholder="请选择图标类型" :aria-readonly="true" class="a-input">
            <a-select-option value="正常">正常</a-select-option>
            <a-select-option value="维护">维护</a-select-option>
            <a-select-option value="低/高警">报警</a-select-option>
            <a-select-option value="故障">故障</a-select-option>
            <a-select-option value="通讯故障">通讯故障</a-select-option>
          </a-select>
        </a-form-item>

        <!--  <a-form-item
            :labelCol="labelCol"
            :wrapperCol="wrapperCol"
            label="图标类型" class="form-item">
            <a-input placeholder="请输入图标类型" v-decorator="['iconType', {}]" class="input"/>
          </a-form-item>
  -->
        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="图标" class="a-form-item2" style="margin-left: 4px" >
          <a-upload
            list-type="picture-card"
            class="avatar-uploader"
            :showUploadList="false"
            :action="uploadAction"
            :data="{'isup':1}"
            :headers="headers"
            :beforeUpload="beforeUpload"
            @change="handleChange" >
            <img v-if="picUrl" :src="getAvatarView()" alt="图标" style="height:104px;max-width:300px;"  />
            <div v-else>
              <a-icon :type="uploadLoading ? 'loading' : 'plus'" />
              <div class="ant-upload-text" >上传</div>
            </div>
          </a-upload>
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="备注" class="form-item">
          <a-textarea :rows="4" placeholder="请输入备注" v-decorator="['remark', {}]" class="a-input-remark"/>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from "moment"
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import Vue from 'vue'

  export default {
    name: "DeviceIconModal",
    data () {
      return {
        title:"操作",
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
        uploadLoading:false,
        headers:{},
        confirmLoading: false,
        form: this.$form.createForm(this),
        picUrl: '',
        validatorRules:{
        },
        url: {
          add: "/monitor/deviceIcon/add",
          edit: "/monitor/deviceIcon/edit",
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
      add () {
        this.picUrl = "";
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'deviceType','iconType','remark'))
          //时间格式化
        });
        this.picUrl = this.model.iconPic;
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
            let iconPic = that.model.iconPic;
            let formData = Object.assign(this.model, values);
            formData.iconPic = iconPic;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
              method = 'put';
            }
            /* console.log(formData)*/
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
          /*   console.log(response);*/
          if(response.success){
            this.model.iconPic = response.message;
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
  .avatar-uploader > .ant-upload {
    width:104px;
    height:104px;
    margin-left: 23px;
  }
  .ant-upload-select-picture-card i {
    font-size: 49px;
    color: #999;
  }
  .ant-upload-select-picture-card .ant-upload-text {
    margin-top: 8px;
    color: #666;
  }
  .a-input-remark{
    width: 550px;
    margin-left: 0px;
    height: 80px;
  }
  .input{
    margin-left: 6px;
  }
  .form-item{
    margin-left: 6px;
  }
</style>