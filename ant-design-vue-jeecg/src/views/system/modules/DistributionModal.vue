<template>
  <a-drawer
    :title="title"
    width=500
    placement="right"
    :closable="true"
    @close="close"
    :visible="visible"
    style="height: calc(100% - 55px);overflow: auto;padding-bottom: 53px;">

    <a-tree
      checkable
      @expand="onExpand"
      @check="onCheck"
      :expandedKeys="expandedKeys"
      :autoExpandParent="autoExpandParent"
      v-model="checkedKeys"
      @select="onSelect"
      :selectedKeys="selectedKeys"
      :treeData="treeData"
    />

    <div class="drawer-bootom-button">
      <a-dropdown style="float: left" :trigger="['click']" placement="topCenter">
        <a-menu slot="overlay">
          <a-menu-item key="3" @click="checkALL">全部勾选</a-menu-item>
          <a-menu-item key="4" @click="cancelCheckALL">取消全选</a-menu-item>
        </a-menu>

        <a-button>
          树操作 <a-icon type="up" />
        </a-button>
      </a-dropdown>
      <a-popconfirm title="确定放弃编辑？" @confirm="close" okText="确定" cancelText="取消">
        <a-button style="margin-right: .8rem">取消</a-button>
      </a-popconfirm>
      <a-button @click="handleSubmit" type="primary" :loading="loading" >提交</a-button>
    </div>


  </a-drawer>
</template>

<script>
  import { httpAction,getAction } from '@/api/manage'
  import { postAction } from '@/api/manage'
  import pick from 'lodash.pick'

  export default {

    name: "DistributionModal",
    data () {
      return {
        expandedKeys: [],  //展开的key
        autoExpandParent: true,
        checkedKeys: [],   //能查到的受过权数据
        selectedKeys: [],                   //选中的数据
        treeData:[],           //所有的数据
        defaultExpandAll: true,
        allTreeKeys: [],
        childrenKey: [],
        title:"操作",
        visible: false,
        loading: false,
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
        form: this.$form.createForm(this),
        validatorRules:{
        },
        url: {
          querySelect: "/monitor/area/findCustomerByUid",
          save: "/monitor/area/updateCustomersArea",
          queryAll:"/monitor/distribution/queryAll",
          findAllAid:"/monitor/area/findAllAid",
          // expands:"/monitor/area/findAllCid",
        },
      }
    },
    created () {

    },
    watch: {
      checkedKeys(val) {

        this.checkedKeys = val

      },
    },
    methods: {
      handleSubmit() {
        let that = this;
        //提交代码，保存
        let arr = []
        this.checkedKeys.forEach((item)=>{
          //if(item>1000){
            arr.push(item)
          //}
        })
        let area = {
          useId: this.userId,
          keys: arr,
        }
        postAction(this.url.save,area).then((res) => {
          if(res.success){
            that.$message.success(res.message);
            that.loading = false;
            that.close();
          }else {
            that.$message.error(res.message);
            that.loading = false;
            that.close();
          }
        },)},

      checkALL() {
        //全选 查询一级菜单
        getAction(this.url.findAllAid).then((res) => {
          this.checkedKeys= res.result
        })

      },
      cancelCheckALL() {
        // 取消全选 赋值为空集合
        this.checkedKeys = []
      },
      settings(id){
        this.userId = id;

        this.init();
        this.queryAllTree();
      },
      init() {
        var params = {userId:this.userId};//查询条件
        getAction(this.url.querySelect,{uid:this.userId}).then((res) => {
          this.checkedKeys = res.result;

          getAction(this.url.queryAll, params).then((res) => {
            if (res.success) {
              this.queryAllTree();
            } else {
              this.edit({ userId: this.userId });
            }
          })
        })
      },
      hasArea(row, id) {
        if (row.children) {
          // 区域
          for (var a = 0; a < row.children.length; a++) {
            var cr = row.children[a];
            if (cr.children) {
              // 客户
              for (var b = 0; b < cr.children.length; b++) {
                var crc = cr.children[b];
                if (crc.id + "" == id && crc.children) {
                  // 区域验证
                  for (var c = 0; c < crc.children.length; c++) {
                    var nothing = true;
                    this.checkedKeys.forEach(cd => {
                      if (cd + "" == crc.children[c].customerId + "") {
                        nothing = false;
                      }
                    });
                    return nothing;
                  }
                }
              };
            }
          }
        }
        return false;
      },
      queryAllTree(){
        getAction('/monitor/area/queryAllTree').then((res) => {
          if (res != null) {
            this.treeData = res.result;

            var checkedKeys = this.checkedKeys;
            for (var a = 0; a < checkedKeys.length; a++) {
              res.result.forEach(row => {
                // 如果不存在，移除选中
                if (this.hasArea(row, checkedKeys[a])) {
                  checkedKeys.splice(a, 1);
                }
              });
            }

            checkedKeys = this.checkedKeys;

            this.edit (res.result);
          }else{
            this.edit({userId:this.userId});
          }
        })
      },
      onExpand (expandedKeys) {
        this.expandedKeys = expandedKeys
        this.autoExpandParent = false
      },
      onCheck (checkedKeys) {
        checkedKeys.forEach((item)=>{
        })
        this.checkedKeys = checkedKeys
      },
      onSelect (selectedKeys, info) {
        this.selectedKeys = selectedKeys
      },
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        this.$nextTick(() => {
          this.form.setFieldsValue(pick(this.model,'userId','customerId'))
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

<style lang="scss" scoped>
  .drawer-bootom-button {
    position: absolute;
    bottom: 0;
    width: 100%;
    border-top: 1px solid #e8e8e8;
    padding: 10px 16px;
    text-align: right;
    left: 0;
    background: #fff;
    border-radius: 0 0 2px 2px;
  }

</style>
