<template>
  <div class="markdown-editor-wrapper">
    <div ref="editorRef" class="markdown-editor"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import Vditor from 'vditor'
import 'vditor/dist/index.css'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '请输入内容...'
  },
  height: {
    type: Number,
    default: 500
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const editorRef = ref(null)
let vditor = null
let isInitialized = false

const initEditor = () => {
  if (!editorRef.value || isInitialized) return
  
  try {
    vditor = new Vditor(editorRef.value, {
      height: props.height,
      placeholder: props.placeholder,
      mode: 'wysiwyg',
      theme: 'classic',
      icon: 'ant',
      cache: {
        enable: false
      },
      preview: {
        delay: 500,
        hljs: {
          style: 'github',
          lineNumber: true
        }
      },
      toolbar: [
        'headings', 'bold', 'italic', 'strike', 'link', '|',
        'list', 'ordered-list', 'check', '|',
        'quote', 'code', 'inline-code', '|',
        'table', '|',
        'undo', 'redo', '|',
        'fullscreen', 'preview'
      ],
      after() {
        isInitialized = true
        if (props.modelValue) {
          vditor.setValue(props.modelValue)
        }
      },
      input(value) {
        emit('update:modelValue', value)
        emit('change', value)
      }
    })
  } catch (error) {
    console.error('Vditor初始化失败:', error)
  }
}

onMounted(() => {
  nextTick(() => {
    initEditor()
  })
})

watch(() => props.modelValue, (newValue) => {
  if (vditor && isInitialized && vditor.getValue() !== newValue) {
    try {
      vditor.setValue(newValue || '')
    } catch (e) {
      console.error('设置值失败:', e)
    }
  }
})

onUnmounted(() => {
  if (vditor) {
    try {
      vditor.destroy()
    } catch (e) {
      console.error('销毁编辑器失败:', e)
    }
    vditor = null
    isInitialized = false
  }
})

defineExpose({
  getValue: () => vditor ? vditor.getValue() : '',
  setValue: (value) => {
    if (vditor && isInitialized) {
      vditor.setValue(value || '')
    }
  }
})
</script>

<style lang="scss" scoped>
.markdown-editor-wrapper {
  width: 100%;
}

.markdown-editor {
  width: 100%;
  
  :deep(.vditor) {
    border-radius: 8px;
    border: 1px solid #ddd;
    
    .vditor-toolbar {
      border-radius: 8px 8px 0 0;
      background: #f8f9fa;
      border-bottom: 1px solid #ddd;
    }
    
    .vditor-content {
      border-radius: 0 0 8px 8px;
    }
  }
}
</style>
