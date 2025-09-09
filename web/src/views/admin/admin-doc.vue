<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-row :gutter="24">
        <a-col :span="8">
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
              v-if="level1.length > 0"
              :columns="columns"
              :row-key="record => record.id"
              :data-source="level1"
              :loading="loading"
              :pagination="false"
              size="small"
              :defaultExpandAllRows="true"
          >
            <template #name="{ text, record }">
              {{record.sort}} {{text}}
            </template>

            <template v-slot:action="{ text, record }">
              <a-space size="small">
                <a-button type="primary" @click="edit(record)" size="small">
                  编辑
                </a-button>
                <a-popconfirm
                    title="是否确定删除?删除后无法恢复"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="handleDelete(record.id)"
                >
                  <a-button type="primary" size="small">
                    删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleSave()">
                  保存
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-form
              :model="doc"
              layout="vertical"
          >
            <a-form-item>
              <a-input v-model:value="doc.name" placeholder="名称" />
            </a-form-item>

            <a-form-item>
              <a-tree-select
                  v-model:value="doc.parent"
                  style="width: 100%"
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  placeholder="Please select"
                  allow-clear
                  tree-default-expand-all
                  :tree-data="treeSelectData"
                  :fieldNames="{label: 'name', value: 'id'}"
              >
              </a-tree-select>
            </a-form-item>

            <a-form-item>
              <a-input v-model:value="doc.sort" placeholder="顺序" />
            </a-form-item>

            <a-form-item>
              <div id="content"></div>
            </a-form-item>

          </a-form>
        </a-col>
      </a-row>

    </a-layout-content>
  </a-layout>

<!--  <a-modal
      v-model:visible="modalVisible"
      title="文档表单"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >

  </a-modal>-->

</template>



<script>
  import { defineComponent, onMounted, ref} from 'vue';
  import {message} from "ant-design-vue";
  import axios from 'axios';
  import {Tool} from "@/util/tool";
  import {useRoute} from "vue-router";
  import E from 'wangeditor'

  export default defineComponent({
    name:'AdminDoc',
    setup(){
      const route = useRoute();
      const docs = ref();

      const loading=ref(false);


      const columns = [
        {
          title: '名称',
          dataIndex: 'name',
          slots: { customRender: 'name' }
          /*若此处有dataIndex 则渲染的text为dataindex对应的值 若无则text为一行的值*/
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
      level1.value = [];

      /**
       * 数据查询
       **/
      const handleQuery=()=>{
        loading.value=true;
        // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
        level1.value = [];
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
       * 将某节点及其子孙节点全部置为disabled
       */
      const setDisable = (treeSelectData, id) => {
        // console.log(treeSelectData, id);
        // 遍历数组，即遍历某一层节点
        for (let i = 0; i < treeSelectData.length; i++) {
          const node = treeSelectData[i];
          if (node.id === id) {
            // 如果当前节点就是目标节点
            console.log("disabled", node);
            // 将目标节点设置为disabled
            node.disabled = true;

            // 遍历所有子节点，将所有子节点全部都加上disabled
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              for (let j = 0; j < children.length; j++) {
                setDisable(children, children[j].id)
              }
            }
          } else {
            // 如果当前节点不是目标节点，则到其子节点再找找看。
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              setDisable(children, id);
            }
          }
        }
      };




      /**
       * 编辑按钮弹出模态框,文档表单
       */

      const doc=ref({});
      const treeSelectData = ref();
      treeSelectData.value = [];
      const modalVisible = ref(false);
      const modalLoading = ref(false);




      const edit = (record) => {
        modalVisible.value = true;
        doc.value=Tool.copy(record);

        // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
        treeSelectData.value = Tool.copy(level1.value);
        setDisable(treeSelectData.value, record.id);

        // 为选择树添加一个"无"
        treeSelectData.value.unshift({id: 0, name: '无'});

      };
      const add =()=>{
        modalVisible.value = true;
        doc.value={
          ebookId : route.query.ebookId,
        };
        treeSelectData.value = Tool.copy(level1.value);
        // 为选择树添加一个"无"
        treeSelectData.value.unshift({id: 0, name: '无'});

      }
      const handleSave = () => {
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


      const deleteIds = [];
      /**
       * 查找整根树枝
       */
      const getDeleteIds = (treeSelectData, id) => {
        // console.log(treeSelectData, id);
        // 遍历数组，即遍历某一层节点
        for (let i = 0; i < treeSelectData.length; i++) {
          const node = treeSelectData[i];
          if (node.id === id) {
            // 如果当前节点就是目标节点
            console.log("delete", node);
            // 将目标ID放入结果集ids
            // node.disabled = true;
            deleteIds.push(id);

            // 遍历所有子节点
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              for (let j = 0; j < children.length; j++) {
                getDeleteIds(children, children[j].id)
              }
            }
          } else {
            // 如果当前节点不是目标节点，则到其子节点再找找看。
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              getDeleteIds(children, id);
            }
          }
        }
      };




      const handleDelete = (id) => {
        console.log("删除的id是:"+id)
        getDeleteIds(level1.value, id);
        axios.delete("doc/delete/"+deleteIds.join(",")).then((response)=>{
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
        const editor = new E('#content');
        editor.config.zIndex = 0;
        editor.create();
      });

      return{
        //docs,
        columns,
        loading,

        modalVisible,
        modalLoading,
        handleSave,
        edit,
        doc,

        add,
        handleDelete,

        formState,
        handleFinish,

        level1,
        treeSelectData,
        setDisable,

      }

    }
  })
</script>
