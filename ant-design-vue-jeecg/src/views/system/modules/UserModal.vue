<template>
  <a-modal
    :title="title"
    :width="764"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :visible="visible"
    style="height: calc(100% - 155px);overflow: auto;padding-bottom: 53px;">
    <a-spin :spinning="confirmLoading" style="calc(100% - 155px);overflow: auto;margin-left: 15px;margin-bottom:-25px ">
      <a-form :form="form">

        <a-form-item label="用户名" :labelCol="labelCol" :wrapperCol="wrapperCol" class="a-form">
          <a-input placeholder="请输入用户名" v-decorator="[ 'username', validatorRules.username]" class="a-input"/>
        </a-form-item>

        <template v-if="!model.id">
          <a-form-item label="密码" :labelCol="labelCol" :wrapperCol="wrapperCol" class="a-form">
            <a-input type="password" placeholder="请输入密码" v-decorator="[ 'password', validatorRules.password]"
                     class="a-input"/>
          </a-form-item>

          <a-form-item label="确认密码" :labelCol="labelCol" :wrapperCol="wrapperCol" class="a-form">
            <a-input type="password" @blur="handleConfirmBlur" placeholder="请重新输入密码"
                     v-decorator="[ 'confirmpassword', validatorRules.confirmpassword]" class="a-input"/>
          </a-form-item>
        </template>

        <template v-if="!model.id">
          <a-form-item label="真实姓名" :labelCol="labelCol" :wrapperCol="wrapperCol" class="a-form">
            <a-input placeholder="请输入真实姓名" v-decorator="[ 'realname', validatorRules.realname]" class="a-input"/>
          </a-form-item>
        </template>
        <template v-else>
          <a-form-item label="真实姓名" :labelCol="labelCol" :wrapperCol="wrapperCol" class="a-form">
            <a-input placeholder="请输入真实姓名" v-decorator="[ 'realname', validatorRules.realname]" class="a-input"/>
          </a-form-item>
        </template>

        <a-form-item label="性别" :labelCol="labelCol" :wrapperCol="wrapperCol" class="a-form">
          <a-select v-decorator="[ 'sex', {}]" placeholder="请选择性别" class="a-input">
            <a-select-option :value="1">男</a-select-option>
            <a-select-option :value="2">女</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="用户角色" :labelCol="labelCol" :wrapperCol="wrapperCol" v-show="!roleDisabled" class="a-form">
          <a-select mode="multiple" placeholder="请选择用户角色" optionFilterProp="children" v-model="selectedRole"
                    class="a-input" >
            <a-select-option v-for="(role,roleindex) in roleList" :key="roleindex.toString()" :value="role.id">
              {{ role.roleName }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="手机号" :labelCol="labelCol" :wrapperCol="wrapperCol" class="a-form">
          <a-input placeholder="请输入手机号" v-decorator="[ 'phone', validatorRules.phone]" class="a-input"/>
        </a-form-item>

        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="状态" class="a-form">
          <a-select v-if="!model.id"  v-decorator="[ 'status', validatorRules.status]" placeholder="请选择状态" class="a-input">
            <a-select-option value="1">正常</a-select-option>
            <a-select-option value="2">冻结</a-select-option>
          </a-select>
          <a-select v-else v-decorator="[ 'status', {}]" placeholder="请选择状态" class="a-input">
            <a-select-option :value="1">正常</a-select-option>
            <a-select-option :value="2">冻结</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import pick from 'lodash.pick'
  import moment from 'moment'
  import Vue from 'vue'
  // 引入搜索部门弹出框的组件
  import departWindow from './DepartWindow'
  import JSelectPosition from '@/components/jeecgbiz/JSelectPosition'
  import { ACCESS_TOKEN } from '@/store/mutation-types'
  import { getAction } from '@/api/manage'
  import { addUser, editUser, queryUserRole, queryall } from '@/api/api'
  import { disabledAuthFilter } from '@/utils/authFilter'
  import { duplicateCheck } from '@/api/api'
  import { checkuser } from '@/api/api'

  export default {
    name: 'UserModal',
    components: {
      departWindow,
      JSelectPosition
    },
    data() {
      return {
        departDisabled: false, //是否是我的部门调用该页面
        roleDisabled: false, //是否是角色维护调用该页面
        modalWidth: 800,
        drawerWidth: 700,
        modaltoggleFlag: true,
        confirmDirty: false,
        selectedDepartKeys: [], //保存用户选择部门id
        checkedDepartKeys: [],
        checkedDepartNames: [], // 保存部门的名称 =>title
        checkedDepartNameString: '', // 保存部门的名称 =>title
        userId:'', //保存用户id
        disableSubmit: false,
        userDepartModel: { userId: '', departIdList: [] }, // 保存SysUserDepart的用户部门中间表数据需要的对象
        dateFormat: 'YYYY-MM-DD',
        validatorRules: {
          username: {
            rules: [{
              required: true, message: '请输入用户名!'
            }, {
              validator: this.validateUsername
            }]
          },
          status:{rules: [{ required: true, message: '请选择状态!' }],initialValue: '1'},
          realname: {
            rules: [{
              required: true, message: '请输入真实姓名!'
            }, {
              // validator: this.validateUsername
            }]
          },

          password: {
            rules: [{
              required: true, message: '请输入密码!'
            }, {
              validator: this.validateUsername
            }]
          },
          password: {
            rules: [{
              required: true,
              pattern: /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[~!@#$%^&*()_+`\-={}:";'<>?,./]).{8,}$/,
              message: '密码由8位数字、大小写字母和特殊符号组成!'
            }]
          },
          confirmpassword: {
            rules: [{
              required: true, message: '请重新输入密码!'
            }, {
              validator: this.compareToFirstPassword
            }]
          },
          realname: { rules: [{ required: true, message: '请输入真实姓名!' }] },
          phone: { rules: [{ validator: this.validatePhone }] },
          email: {
            rules: [{
              required: true, message: '请输入邮箱!'
            }]
          },
          roles: {},
          //  sex:{initialValue:((!this.model.sex)?"": (this.model.sex+""))}
          workNo: {
            rules: [
              { required: true, message: '请输入工号' },
              { validator: this.validateWorkNo }
            ]
          },
          telephone: {
            rules: [
              { pattern: /^0\d{2,3}-[1-9]\d{6,7}$/, message: '请输入正确的座机号码' }
            ]
          }
        },
        title: '操作',
        visible: false,
        model: {},
        roleList: [],
        selectedRole: [],
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        uploadLoading: false,
        confirmLoading: false,
        headers: {},
        form: this.$form.createForm(this),
        picUrl: '',
        url: {
          fileUpload: window._CONFIG['domianURL'] + '/sys/common/upload',
          imgerver: window._CONFIG['domianURL'] + '/sys/common/view',
          userWithDepart: '/sys/user/userDepartList', // 引入为指定用户查看部门信息需要的url
          userId: '/sys/user/generateUserId', // 引入生成添加用户情况下的url
          syncUserByUserName: '/process/extActProcess/doSyncUserByUserName'//同步用户到工作流
        }
      }
    },
    created() {
      const token = Vue.ls.get(ACCESS_TOKEN)
      this.headers = { 'X-Access-Token': token }

    },
    computed: {
      uploadAction: function() {
        return this.url.fileUpload
      }
    },
    methods: {
      isDisabledAuth(code) {
        return disabledAuthFilter(code)
      },
      //窗口最大化切换
      toggleScreen() {
        if (this.modaltoggleFlag) {
          this.modalWidth = window.innerWidth
        } else {
          this.modalWidth = 800
        }
        this.modaltoggleFlag = !this.modaltoggleFlag
      },
      initialRoleList() {
        queryall().then((res) => {
          if (res.success) {
            this.roleList = res.result
          } else {

          }
        })
      },
      loadUserRoles(userid) {
        queryUserRole({ userid: userid }).then((res) => {
          if (res.success) {
            this.selectedRole = res.result
          } else {

          }
        })
      },
      refresh() {
        this.selectedDepartKeys = []
        this.checkedDepartKeys = []
        this.checkedDepartNames = []
        this.checkedDepartNameString = ''
        this.userId = ''
      },
      add() {
        this.picUrl = ''
        this.refresh()
        this.edit({ activitiSync: '1' })
      },
      edit(record) {
        this.resetScreenSize() // 调用此方法,根据屏幕宽度自适应调整抽屉的宽度
        let that = this
        that.initialRoleList()
        that.checkedDepartNameString = ''
        that.form.resetFields()
        if (record.hasOwnProperty('id')) {
          that.loadUserRoles(record.id)
          this.picUrl = 'Has no pic url yet'
        }
        that.userId = record.id
        that.visible = true
        that.model = Object.assign({}, record)
        that.$nextTick(() => {
          that.form.setFieldsValue(pick(this.model, 'username', 'password', 'confirmpassword', 'sex', 'realname', 'phone', 'status'))
        })
        // 调用查询用户对应的部门信息的方法
        that.checkedDepartKeys = []
        that.loadCheckedDeparts()
      },
      //
      loadCheckedDeparts() {
        let that = this
        if (!that.userId) {
          return
        }
        getAction(that.url.userWithDepart, { userId: that.userId }).then((res) => {
          that.checkedDepartNames = []
          if (res.success) {
            for (let i = 0; i < res.result.length; i++) {
              that.checkedDepartNames.push(res.result[i].title)
              this.checkedDepartNameString = this.checkedDepartNames.join(',')
              that.checkedDepartKeys.push(res.result[i].key)
            }
            that.userDepartModel.departIdList = that.checkedDepartKeys
          } else {

          }
        })
      },
      close() {
        this.$emit('close')
        this.visible = false
        this.disableSubmit = false
        this.selectedRole = []
        this.userDepartModel = { userId: '', departIdList: [] }
        this.checkedDepartNames = []
        this.checkedDepartNameString = ''
        this.checkedDepartKeys = []
        this.selectedDepartKeys = []
      },
      moment,
      handleOk() {

        const that = this
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true
            let avatar = that.model.avatar
            if (!values.birthday) {
              values.birthday = ''
            } else {
              values.birthday = values.birthday.format(this.dateFormat)
            }
            let formData = Object.assign(this.model, values)
            formData.avatar = avatar
            formData.selectedroles = this.selectedRole.length > 0 ? this.selectedRole.join(',') : ''
            formData.selecteddeparts = this.userDepartModel.departIdList.length > 0 ? this.userDepartModel.departIdList.join(',') : ''

            // that.addDepartsToUser(that,formData); // 调用根据当前用户添加部门信息的方法
            let obj
            if (!this.model.id) {
              formData.id = this.userId
              obj = addUser(formData)
            } else {
              obj = editUser(formData)
            }
            obj.then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
              } else {
                that.$message.warning(res.message)
              }
            }).finally(() => {
              that.confirmLoading = false
              that.checkedDepartNames = []
              that.userDepartModel.departIdList = { userId: '', departIdList: [] }

              that.close()
            })

          }
        })
      },
      handleCancel() {
        this.close()
      },
      validateToNextPassword(rule, value, callback) {
        const form = this.form
        const confirmpassword = form.getFieldValue('confirmpassword')

        if (value && confirmpassword && value !== confirmpassword) {
          callback('两次输入的密码不一样！')
        }
        if (value && this.confirmDirty) {
          form.validateFields(['confirm'], { force: true })
        }
        callback()
      },
      compareToFirstPassword(rule, value, callback) {
        const form = this.form
        if (value && value !== form.getFieldValue('password')) {
          callback('两次输入的密码不一样！')
        } else {
          callback()
        }
      },
      validatePhone(rule, value, callback) {
        if (!value) {
          callback()
        } else {
          //update-begin--Author:kangxiaolin  Date:20190826 for：[05] 手机号不支持199号码段--------------------
          if (new RegExp(/^1[3|4|5|7|8|9][0-9]\d{8}$/).test(value)) {
            //update-end--Author:kangxiaolin  Date:20190826 for：[05] 手机号不支持199号码段--------------------

            var params = {
              tableName: 'sys_user',
              fieldName: 'phone',
              fieldVal: value,
              dataId: this.userId
            }
            duplicateCheck(params).then((res) => {
              if (res.success) {
                callback()
              } else {
                callback('手机号已存在!')
              }
            })
          } else {
            callback('请输入正确格式的手机号码!')
          }
        }
      },


      validateEmail(rule, value, callback) {

        if (!value) {
          callback()
        } else {
          if (new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/).test(value)) {
            var params = {
              tableName: 'sys_user',
              fieldName: 'email',
              fieldVal: value,
              dataId: this.userId
            }
            duplicateCheck(params).then((res) => {
              if (res.success) {
                callback()
              } else {
                callback('邮箱已存在!')
              }
            })
          } else {
            callback('请输入正确格式的邮箱!')
          }
        }
      },

      validateUsername(rule, value, callback) {
        if(this.userId==null){
          this.userId="";
        }
        var params = {
          name: value,
          id:this.userId
        }
        checkuser(params).then((res) => {
          if (res.success) {
            callback()
          } else {
            callback('用户名已存在!')
          }
        })
      },
      validateWorkNo(rule, value, callback) {
        var params = {
          tableName: 'sys_user',
          fieldName: 'work_no',
          fieldVal: value,
          dataId: this.userId
        }
        duplicateCheck(params).then((res) => {
          if (res.success) {
            callback()
          } else {
            callback('工号已存在!')
          }
        })
      },
      handleConfirmBlur(e) {
        const value = e.target.value
        this.confirmDirty = this.confirmDirty || !!value
      },

      normFile(e) {

        if (Array.isArray(e)) {
          return e
        }
        return e && e.fileList
      },
      beforeUpload: function(file) {
        var fileType = file.type
        if (fileType.indexOf('image') < 0) {
          this.$message.warning('请上传图片')
          return false
        }
        //TODO 验证文件大小
      },
      handleChange(info) {
        this.picUrl = ''
        if (info.file.status === 'uploading') {
          this.uploadLoading = true
          return
        }
        if (info.file.status === 'done') {
          var response = info.file.response
          this.uploadLoading = false

          if (response.success) {
            this.model.avatar = response.message
            this.picUrl = 'Has no pic url yet'
          } else {
            this.$message.warning(response.message)
          }
        }
      },
      getAvatarView() {
        return this.url.imgerver + '/' + this.model.avatar
      },
      // 搜索用户对应的部门API
      onSearch() {
        this.$refs.departWindow.add(this.checkedDepartKeys, this.userId)
      },

      // 获取用户对应部门弹出框提交给返回的数据
      modalFormOk(formData) {
        this.checkedDepartNames = []
        this.selectedDepartKeys = []
        this.checkedDepartNameString = ''
        this.userId = formData.userId
        this.userDepartModel.userId = formData.userId
        for (let i = 0; i < formData.departIdList.length; i++) {
          this.selectedDepartKeys.push(formData.departIdList[i].key)
          this.checkedDepartNames.push(formData.departIdList[i].title)
          this.checkedDepartNameString = this.checkedDepartNames.join(',')
        }
        this.userDepartModel.departIdList = this.selectedDepartKeys
        this.checkedDepartKeys = this.selectedDepartKeys  //更新当前的选择keys
      },
      // 根据屏幕变化,设置抽屉尺寸
      resetScreenSize() {
        let screenWidth = document.body.clientWidth
        if (screenWidth < 500) {
          this.drawerWidth = screenWidth
        } else {
          this.drawerWidth = 700
        }
      }
    }
  }
</script>

<style scoped>
  .avatar-uploader > .ant-upload {
    width: 104px;
    height: 104px;
  }

  .ant-upload-select-picture-card i {
    font-size: 49px;
    color: #999;
  }

  .ant-upload-select-picture-card .ant-upload-text {
    margin-top: 8px;
    color: #666;
  }

  .ant-table-tbody .ant-table-row td {
    padding-top: 10px;
    padding-bottom: 10px;
    margin-left: 10px;
  }
  /*.drawer-bootom-button {*/
  /*  position: absolute;*/
  /*  bottom: -8px;*/
  /*  width: 100%;*/
  /*  border-top: 1px solid #e8e8e8;*/
  /*  padding: 10px 16px;*/
  /*  text-align: right;*/
  /*  left: 0;*/
  /*  background: #fff;*/
  /*  border-radius: 0 0 2px 2px;*/
  /*}*/
  .a-form {
    float: left;
    width: 350px;

  }
  .a-input {
   width: 250px;

  }
</style>