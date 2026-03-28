<template>
  <div class="app-container">
    <canvas ref="sakuraCanvas" class="sakura-canvas"></canvas>
    <router-view />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const sakuraCanvas = ref(null)
let animationId = null
let petals = []

const createPetal = (canvas) => {
  return {
    x: Math.random() * canvas.width,
    y: Math.random() * canvas.height - canvas.height,
    speedY: Math.random() * 2 + 1,
    speedX: Math.random() * 1 - 0.5,
    size: Math.random() * 5 + 5,
    rotation: Math.random() * 360,
    rotationSpeed: Math.random() * 2 - 1,
    opacity: Math.random() * 0.5 + 0.3
  }
}

const drawPetal = (ctx, p) => {
  ctx.save()
  ctx.translate(p.x, p.y)
  ctx.rotate((p.rotation * Math.PI) / 180)
  ctx.globalAlpha = p.opacity
  
  const gradient = ctx.createRadialGradient(0, 0, 0, 0, 0, p.size)
  gradient.addColorStop(0, 'rgba(255, 143, 177, 0.9)')
  gradient.addColorStop(0.5, 'rgba(255, 182, 193, 0.7)')
  gradient.addColorStop(1, 'rgba(255, 209, 220, 0.3)')
  
  ctx.fillStyle = gradient
  ctx.beginPath()
  
  for (let i = 0; i < 5; i++) {
    const angle = (i * 72 * Math.PI) / 180
    const x = Math.cos(angle) * p.size
    const y = Math.sin(angle) * p.size
    if (i === 0) {
      ctx.moveTo(x, y)
    } else {
      ctx.lineTo(x, y)
    }
  }
  
  ctx.closePath()
  ctx.fill()
  ctx.restore()
}

const animate = (ctx, canvas) => {
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  
  petals.forEach(p => {
    p.y += p.speedY
    p.x += p.speedX + Math.sin(p.y * 0.01) * 0.5
    p.rotation += p.rotationSpeed
    
    if (p.y > canvas.height + 20) {
      p.y = -20
      p.x = Math.random() * canvas.width
    }
    
    drawPetal(ctx, p)
  })
  
  animationId = requestAnimationFrame(() => animate(ctx, canvas))
}

onMounted(() => {
  const canvas = sakuraCanvas.value
  const ctx = canvas.getContext('2d')
  
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
  
  for (let i = 0; i < 50; i++) {
    petals.push(createPetal(canvas))
  }
  
  animate(ctx, canvas)
  
  window.addEventListener('resize', () => {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  })
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
})

// 点击撒花效果
const sparkles = ['✨', '🌸', '⭐', '💫', '🎀', '🍬', '🌟']

const createClickSparkle = (e) => {
  const sparkle = document.createElement('div')
  sparkle.className = 'click-sparkle'
  sparkle.innerHTML = sparkles[Math.floor(Math.random() * sparkles.length)]
  sparkle.style.left = e.clientX + 'px'
  sparkle.style.top = e.clientY + 'px'
  
  // 随机飞散方向
  const angle = Math.random() * Math.PI * 2
  const distance = 50 + Math.random() * 50
  sparkle.style.setProperty('--tx', Math.cos(angle) * distance + 'px')
  sparkle.style.setProperty('--ty', Math.sin(angle) * distance + 'px')
  
  document.body.appendChild(sparkle)
  
  setTimeout(() => {
    sparkle.remove()
  }, 800)
}

onMounted(() => {
  document.addEventListener('click', createClickSparkle)
})

onUnmounted(() => {
  document.removeEventListener('click', createClickSparkle)
})
</script>

<style lang="scss">
.app-container {
  min-height: 100vh;
  background: var(--bg-gradient);
  background-attachment: fixed;
  position: relative;
}

.sakura-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}
</style>
