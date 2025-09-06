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
            <a-button
                type="primary"
                html-type="submit"
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
          :data-source="level1"
          :loading="loading"
          :pagination="false"
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
      title="文档表单"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form
        :model="doc"
        :label-col="{ span: 8 }"
        :wrapper-col="{ span: 16 }"
    >
      <a-form-item label="名称">
        <a-input v-model:value="doc.name" />
      </a-form-item>

      <a-form-item label="父文档">
        <a-select
            ref="select"
            v-model:value="doc.parent"
        >
          <a-select-option value="0">无</a-select-option>
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="c.id === doc.id">
            {{c.name}}
          </a-select-option>

        </a-select>
      </a-form-item>

      <a-form-item label="顺序">
        <a-input v-model:value="doc.sort" />
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
    name:'AdminDoc',
    setup(){
      const docs = ref();

      const loading=ref(false);

      const columns = [
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '父文档',
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
       * 一级文档树，children属性就是二级文档
       * [{
       *   id: "",
       *   name: "",
       *   children: [{
       *     id: "",
       *     name: "",
       *   }]
       * }]
       */
      const level1 = ref(); // 一级文档树，children属性就是二级文档



      /**
       * 数据查询
       **/
      const handleQuery=()=>{
        loading.value=true;
        axios.get("/doc/all").then((response)=>{
          loading.value=false;
          const data=response.data;
          if(data.success){
            docs.value=data.content;

            console.log("原始数组",docs.value);
            level1.value = [];
            level1.value = Tool.array2Tree(docs.value,0);
            console.log("树形结构",level1.value);
          }
          else{
            message.error(data.message);
          }

        });
      };




      /**
       * 编辑按钮弹出模态框,文档表单
       */
      const doc=ref({});
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      const edit = (record) => {
        modalVisible.value = true;
        doc.value=Tool.copy(record);
      };
      const add =()=>{
        modalVisible.value = true;
        doc.value={};
      }
      const handleModalOk = () => {
        modalLoading.value = true;

        axios.post("doc/save",doc.value).then((response)=>{
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
        handleQuery();

      };

      const handleDelete = (id) => {
        console.log("删除的id是:"+id)
        axios.delete("doc/delete/"+id).then((response)=>{
          const data=response.data;
          if(data.success){
            //重新查询列表
            handleQuery();
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
        handleQuery()
        console.log(values, formState);
      };



      onMounted(()=>{
        handleQuery();
      });

      return{
        //docs,
        columns,
        loading,

        modalVisible,
        modalLoading,
        handleModalOk,
        edit,
        doc,

        add,
        handleDelete,

        formState,
        handleFinish,

        level1,

      }

    }
  })
</script>
