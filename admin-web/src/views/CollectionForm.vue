<template>
  <div class="collection-form">
    <div class="page-header">
      <h2 class="page-title">{{ isEdit ? '编辑藏品' : '新增藏品' }}</h2>
    </div>

    <el-card shadow="hover">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="max-width: 800px;"
      >
        <el-form-item label="藏品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入藏品名称" maxlength="200" />
        </el-form-item>

        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width:100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="朝代" prop="dynasty">
              <el-input v-model="form.dynasty" placeholder="如: 明" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="年代" prop="era">
              <el-input v-model="form.era" placeholder="如: 永乐年间" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="材质" prop="material">
              <el-input v-model="form.material" placeholder="如: 陶瓷" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="尺寸描述" prop="sizeDesc">
          <el-input v-model="form.sizeDesc" placeholder="如: 高45cm，口径12cm" />
        </el-form-item>

        <el-form-item label="出土地点" prop="originPlace">
          <el-input v-model="form.originPlace" placeholder="如: 江西景德镇" />
        </el-form-item>

        <el-form-item label="封面图片" prop="coverImage">
          <el-input v-model="form.coverImage" placeholder="请输入图片URL">
            <template #append>
              <el-button @click="showPreview = !showPreview">预览</el-button>
            </template>
          </el-input>
          <img v-if="showPreview && form.coverImage" :src="form.coverImage" style="max-width:200px;margin-top:8px;border-radius:4px;" />
        </el-form-item>

        <el-form-item label="藏品描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入藏品描述"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="文化意义" prop="culturalSignificance">
          <el-input
            v-model="form.culturalSignificance"
            type="textarea"
            :rows="3"
            placeholder="请输入文化意义"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="on_display">展出中</el-radio>
            <el-radio value="in_storage">库房中</el-radio>
            <el-radio value="maintenance">修复中</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="推荐展示">
          <el-switch v-model="isFeatured" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">
            {{ isEdit ? '保存修改' : '立即创建' }}
          </el-button>
          <el-button size="large" @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCategoryList } from '@/api/category'
import { createCollection, updateCollection } from '@/api/collection'
import request from '@/api/index'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const categories = ref([])
const showPreview = ref(false)

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  id: null,
  name: '',
  categoryId: null,
  dynasty: '',
  era: '',
  material: '',
  sizeDesc: '',
  originPlace: '',
  description: '',
  culturalSignificance: '',
  coverImage: '',
  status: 'on_display',
  isFeatured: 0
})

const isFeatured = computed({
  get: () => form.isFeatured === 1,
  set: (v) => { form.isFeatured = v ? 1 : 0 }
})

const rules = {
  name: [{ required: true, message: '请输入藏品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

// 加载编辑数据
onMounted(async () => {
  try {
    const res = await getCategoryList()
    if (res.code === 200) categories.value = res.data
  } catch (e) { console.error(e) }

  if (isEdit.value) {
    try {
      const res = await request.get(`/collection/detail/${route.params.id}`)
      if (res.code === 200) {
        Object.assign(form, res.data)
      }
    } catch (e) {
      ElMessage.error('加载藏品信息失败')
      router.push('/collection')
    }
  }
})

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const submitData = { ...form }
    let res
    if (isEdit.value) {
      res = await updateCollection(submitData)
    } else {
      delete submitData.id
      res = await createCollection(submitData)
    }
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '保存成功' : '创建成功')
      router.push('/collection')
    }
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.collection-form {
  max-width: 900px;
}
.page-header {
  margin-bottom: 16px;
}
.page-title {
  font-size: 20px;
  color: #1a365d;
}
</style>
