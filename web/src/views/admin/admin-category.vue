<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-button type="primary" @click="handleQuery()" >
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
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
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
        <template v-slot:action="{text, record}">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="删除不可恢复，确认删除?"
                ok-text="Yes"
                cancel-text="No"
                @confirm="handleDelete(record.id)"
                @cancel="cancel"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>

          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <a-modal
    title="分类"
    v-model:visible="modalVisible"
    :confim-loading="modalLoading"
    @ok="handleModalOk"
    >
    <a-form :model="category" :label-col="{span : 6}">
      <a-form-item label="名称">
        <a-input v-model:value="category.name" />
      </a-form-item>
      <a-form-item label="父分类">
        <a-select
            ref="select"
            v-model:value="category.parent"
        >
          <a-select-option value="0">无</a-select-option>
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="category.id === c.id">
            {{c.name}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="category.sort" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script lang="ts">
 import {defineComponent, onMounted, ref} from 'vue';
 import axios from 'axios';
 import {message} from 'ant-design-vue'
 import {Tool} from "@/util/tool"
 export default defineComponent({
   name: 'AdminCategory',
   setup(){
     const param = ref();
     param.value = {};
     const categorys = ref();
     const loading = ref(false);
     const columns = [
       {
         title: '名称',
         dataIndex: 'name'
       },
       {
         title: '父分类',
         key:'parent',
         dataIndex: 'parent'
       },
       {
         title: '顺序',
         dataIndex: 'sort'
       },
       {
         title:'Action',
         key:'action',
         slots: {customRender: 'action'}
       }
     ];
     const level1 = ref();
     /**
      * 数据查询
      * @param params
      */
     const handleQuery = ()=>{
       loading.value = true;
       axios.get("/category/all").then((response)=>{
         loading.value = false;
         const data = response.data;
         if(data.success){
           categorys.value = data.content;
           level1.value = [];
           level1.value = Tool.array2Tree(categorys.value,0);
           console.log("树形结构:",level1);
         }else {
           message.error(data.message)
         }
       });
     };

     const category = ref({});
     const modalVisible = ref(false);
     const modalLoading = ref(false);
     const handleModalOk = () =>{
       modalLoading.value = true;
       axios.post("/category/save", category.value).then((response)=>{
         modalLoading.value = false;
         const data = response.data; //data = commonResp
         if(data.success){
           modalVisible.value = false;
           //重新加载列表
           handleQuery();
         }else{
           message.error(data.message);
         }
       });
     };
     /**
      * 编辑
      * @param record
      */
     const edit = (record: any)=>{
       modalVisible.value = true;
       category.value = Tool.copy(record);
     }
     /**
      * 新增
      * @param record
      */
     const add = ()=>{
       modalVisible.value = true;
       category.value = {}
     }
     const handleDelete = (id: number)=>{
       axios.delete("/category/delete/"+id).then((response)=>{
         const data = response.data; //data = commonResp
         if(data.success){
           //重新加载列表
           handleQuery()
         }
       });
     }
     onMounted(()=>{
       handleQuery();
     });
     return {
       param,
       //categorys,
       level1,
       columns,
       loading,

       edit,
       add,
       handleDelete,
       category,
       modalVisible,
       modalLoading,
       handleModalOk,
       handleQuery
     }
   }
 })
</script>

