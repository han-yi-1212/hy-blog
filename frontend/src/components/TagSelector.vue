<template>
  <div class="tag-selector">
    <label class="selector-label">文章标签</label>
    <div class="selected-tags">
      <el-tag
        v-for="tag in selectedTags"
        :key="tag.id"
        closable
        @close="removeTag(tag)"
        class="selected-tag"
      >
        {{ tag.name }}
      </el-tag>
      <el-input
        v-if="inputVisible"
        ref="InputRef"
        v-model="inputValue"
        class="tag-input"
        size="small"
        @keyup.enter="handleInputConfirm"
        @blur="handleInputConfirm"
      />
      <el-button v-else class="button-new-tag" size="small" @click="showInput">
        + 添加标签
      </el-button>
    </div>
    <div class="existing-tags">
      <span class="existing-label">推荐标签：</span>
      <span
        v-for="tag in availableTags"
        :key="tag.id"
        class="existing-tag"
        @click="selectTag(tag)"
      >
        {{ tag.name }}
      </span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { tagApi } from '@/api/tag'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue'])

const allTags = ref([])
const inputVisible = ref(false)
const inputValue = ref('')
const InputRef = ref(null)

const selectedTags = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const availableTags = computed(() => {
  return allTags.value.filter(tag => 
    !selectedTags.value.some(selected => selected.id === tag.id)
  )
})

const fetchTags = async () => {
  try {
    const res = await tagApi.list()
    if (res.code === 200) {
      allTags.value = res.data
    }
  } catch (error) {
    console.error('获取标签列表失败:', error)
  }
}

const selectTag = (tag) => {
  if (!selectedTags.value.some(t => t.id === tag.id)) {
    selectedTags.value = [...selectedTags.value, tag]
  }
}

const removeTag = (tag) => {
  selectedTags.value = selectedTags.value.filter(t => t.id !== tag.id)
}

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    InputRef.value?.input?.focus()
  })
}

const handleInputConfirm = async () => {
  if (inputValue.value) {
    // 检查是否已存在
    const existing = allTags.value.find(t => t.name === inputValue.value)
    if (existing) {
      selectTag(existing)
    } else {
      // 创建新标签
      try {
        const res = await tagApi.create(inputValue.value)
        if (res.code === 200) {
          allTags.value.push(res.data)
          selectTag(res.data)
        }
      } catch (error) {
        console.error('创建标签失败:', error)
      }
    }
  }
  inputVisible.value = false
  inputValue.value = ''
}

onMounted(() => {
  fetchTags()
})
</script>

<style lang="scss" scoped>
.tag-selector {
  .selector-label {
    display: block;
    color: var(--text-color);
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 8px;
  }
  
  .selected-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 12px;
    
    .selected-tag {
      background: rgba(255, 143, 177, 0.2);
      border-color: rgba(255, 143, 177, 0.3);
      color: var(--text-color);
    }
    
    .tag-input {
      width: 100px;
    }
    
    .button-new-tag {
      border-style: dashed;
    }
  }
  
  .existing-tags {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;
    
    .existing-label {
      font-size: 13px;
      color: var(--text-secondary);
    }
    
    .existing-tag {
      padding: 4px 10px;
      background: rgba(255, 143, 177, 0.1);
      border-radius: 12px;
      font-size: 13px;
      color: var(--text-color);
      cursor: pointer;
      transition: all 0.3s ease;
      
      &:hover {
        background: rgba(255, 143, 177, 0.2);
      }
    }
  }
}
</style>
