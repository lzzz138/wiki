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
            <a-input v-model:value="formState.name" placeholder="查询的书名">
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
          :data-source="categorys"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover"  alt="avatar" style="width: 100px; height: 100px;" />
        </template>

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
      title="分类表单"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form
        :model="category"
        :label-col="{ span: 8 }"
        :wrapper-col="{ span: 16 }"
    >
      <a-form-item label="名称">
        <a-input v-model:value="category.name" />
      </a-form-item>

      <a-form-item label="父分类">
        <a-input v-model:value="category.parent" />
      </a-form-item>

      <a-form-item label="顺序">
        <a-input v-model:value="category.sort" />
      </a-form-item>



    </a-form>
  </a-modal>

</template>



<script>
  import { defineComponent, onMounted, ref} from 'vue';
  import {message} from "ant-design-vue";
  import axios from 'axios';
  import {Tool} from "@/util/tool";

  export default defineComponent({
    name:'AdminCategory',
    setup(){
      const categorys = ref();
      const pagination = ref({
        current: 1,
        pageSize: 10,
        total: 0
      });
      const loading=ref(false);

      const columns = [
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '父分类',
          dataIndex: 'parent'
        },
        {
          title: '顺序',
          dataIndex: 'sort'
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
        axios.get("/category/list",{
          params:{
            page : params.page,
            size : params.size,
            name : params.name,
          }
        }).then((response)=>{
          loading.value=false;
          const data=response.data;
          if(data.success){
            categorys.value=data.content.list;

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
       * 编辑按钮弹出模态框,分类表单
       */
      const category=ref({});
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      const edit = (record) => {
        modalVisible.value = true;
        category.value=Tool.copy(record);
      };
      const add =()=>{
        modalVisible.value = true;
        category.value={};
      }
      const handleModalOk = () => {
        modalLoading.value = true;

        axios.post("category/save",category.value).then((response)=>{
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
        axios.delete("category/delete/"+id).then((response)=>{
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
        name:'',
      });
      const handleFinish = values => {
        handleQuery({
          page : 1,
          size : pagination.value.pageSize,
          name : formState.value.name,
        })
        console.log(values, formState);
      };



      onMounted(()=>{
        handleQuery({
          page : 1,
          size : pagination.value.pageSize,
        });
      });

      return{
        categorys,
        pagination,
        columns,
        loading,
        handleTableChange,

        modalVisible,
        modalLoading,
        handleModalOk,
        edit,
        category,

        add,
        handleDelete,

        formState,
        handleFinish,

      }

    }
  })
</script>
