<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form
            layout="inline"
            :model="formState"
            @finish="handleFinish"
        >
          <a-form-item>
            <a-input v-model:value="formState.loginName" placeholder="查询的用户">
            </a-input>
          </a-form-item>

          <a-form-item>
            <a-button
                type="primary"
                html-type="submit"
                :disabled="formState.name === '' "
            >
              查询
            </a-button>
          </a-form-item>

          <a-form-item>
            <a-button type="primary" @click="add" >
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="users"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >


        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="是否确定删除?删除后无法恢复"
                ok-text="是"
                cancel-text="否"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="primary">
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <a-modal
      v-model:visible="modalVisible"
      title="用户表单"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form
        :model="user"
        :label-col="{ span: 8 }"
        :wrapper-col="{ span: 16 }"
    >
      <a-form-item label="登陆名">
        <a-input v-model:value="user.loginName" :disabled="!!user.id"/>
      </a-form-item>
      <a-form-item label="昵称">
        <a-input v-model:value="user.name" />
      </a-form-item>
      <a-form-item label="密码" v-show="!user.id">
        <a-input v-model:value="user.password"/>
      </a-form-item>

    </a-form>
  </a-modal>

</template>



<script>
  import { defineComponent, onMounted, ref} from 'vue';
  import {message} from "ant-design-vue";
  import axios from 'axios';
  import {Tool} from "@/util/tool";
  import md5 from "js-md5";


  export default defineComponent({
    name:'AdminUser',
    setup(){
      const users = ref();
      const pagination = ref({
        current: 1,
        pageSize: 10,
        total: 0
      });
      const loading=ref(false);

      const columns = [
        {
          title: '登录名',
          dataIndex: 'loginName'
        },
        {
          title: '昵称',
          dataIndex: 'name'
        },
        {
          title: '密码',
          dataIndex: 'password'
        },
        {
          title: 'Action',
          key: 'action',
          slots: { customRender: 'action' }
        }
      ];

      /**
       * 数据查询
       **/
      const handleQuery=(params)=>{
        loading.value=true;
        axios.get("/user/list",{
          params:{
            page : params.page,
            size : params.size,
            loginName : params.loginName,
          }
        }).then((response)=>{
          loading.value=false;
          const data=response.data;
          if(data.success){
            users.value=data.content.list;

            //重置分页按钮
            pagination.value.current=params.page;
            pagination.value.total=data.content.total;
          }
          else{
            message.error(data.message);
          }

        });
      };

      /**
       * 表格点击页码时触发
       */
      const handleTableChange = (pagination) => {
        console.log("看看自带的分页参数都有啥：" + pagination);
        handleQuery({
          page: pagination.current,
          size: pagination.pageSize
        });
      };


      /**
       * 编辑按钮弹出模态框,用户表单
       */
      // -------- 表单 ---------

      const user=ref({});
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      const edit = (record) => {
        modalVisible.value = true;
        user.value=Tool.copy(record);
      };
      const add =()=>{
        modalVisible.value = true;
        user.value={};
      }
      const handleModalOk = () => {
        modalLoading.value = true;

        user.value.password = md5(user.value.password);

        axios.post("user/save",user.value).then((response)=>{
          const data=response.data;
          modalLoading.value = false;
          if(data.success){
            modalVisible.value = false;
          }
          else{
            message.error(data.message);
          }
        });

        //重新查询列表
        handleQuery({
          page : pagination.value.current,
          size : pagination.value.pageSize,
        });

      };




      const handleDelete = (id) => {
        console.log("删除的id是:"+id)
        axios.delete("user/delete/"+id).then((response)=>{
          const data=response.data;
          if(data.success){
            //重新查询列表
            handleQuery({
              page : pagination.value.current,
              size : pagination.value.pageSize,
            });
          }
        });

      }

      /**
       * 根据书名进行查询
       */
      const formState = ref({
        loginName:'',
      });
      const handleFinish = values => {
        handleQuery({
          page : 1,
          size : pagination.value.pageSize,
          loginName : formState.value.loginName,
        })
        console.log(values, formState);
      };



      onMounted(()=>{
        handleQuery({
          page: 1,
          size: pagination.value.pageSize,
        });
      });

      return{
        users,
        pagination,
        columns,
        loading,
        handleTableChange,

        modalVisible,
        modalLoading,
        handleModalOk,
        edit,
        user,

        add,
        handleDelete,

        formState,
        handleFinish,

      }

    }
  })
</script>
