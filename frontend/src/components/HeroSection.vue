<template>
  <div class="hero" ref="heroRef">
    <div class="hero-bg" :style="{ transform: `translateY(${scrollY * 0.3}px)` }"></div>
    <canvas ref="sakuraCanvas" class="sakura-canvas"></canvas>
    
    <div class="hero-content">
      <h1 class="hero-title">Hello! 欢迎来到hyのblog！</h1>
      <p class="hero-subtitle">感谢您的到来，让我们共同开启美好时光！</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const heroRef = ref(null)
const sakuraCanvas = ref(null)
const scrollY = ref(0)

let animationId = null
let particles = []

class SakuraParticle {
  constructor(canvas) {
    this.canvas = canvas
    this.reset()
  }
  
  reset() {
    this.x = Math.random() * this.canvas.width
    this.y = Math.random() * -this.canvas.height
    this.size = Math.random() * 12 + 6
    this.speedY = Math.random() * 1.5 + 0.5
    this.speedX = Math.random() * 0.8 - 0.4
    this.rotation = Math.random() * 360
    this.rotationSpeed = Math.random() * 3 - 1.5
    this.opacity = Math.random() * 0.4 + 0.4
    this.swing = Math.random() * 2
    this.swingSpeed = Math.random() * 0.02 + 0.01
  }
  
  update() {
    this.y += this.speedY
    this.swing += this.swingSpeed
    this.x += this.speedX + Math.sin(this.swing) * 0.8
    this.rotation += this.rotationSpeed
    
    if (this.y > this.canvas.height + 50) {
      this.reset()
    }
  }
  
  draw(ctx) {
    ctx.save()
    ctx.translate(this.x, this.y)
    ctx.rotate(this.rotation * Math.PI / 180)
    ctx.globalAlpha = this.opacity
    
    const gradient = ctx.createRadialGradient(0, 0, 0, 0, 0, this.size)
    gradient.addColorStop(0, 'rgba(255, 182, 193, 0.9)')
    gradient.addColorStop(0.5, 'rgba(255, 143, 177, 0.8)')
    gradient.addColorStop(1, 'rgba(255, 209, 220, 0.6)')
    
    ctx.fillStyle = gradient
    ctx.beginPath()
    ctx.ellipse(0, 0, this.size, this.size / 2.5, 0, 0, Math.PI * 2)
    ctx.fill()
    
    ctx.beginPath()
    ctx.ellipse(-this.size * 0.3, -this.size * 0.1, this.size * 0.4, this.size * 0.2, -0.3, 0, Math.PI * 2)
    ctx.fillStyle = 'rgba(255, 230, 235, 0.5)'
    ctx.fill()
    
    ctx.restore()
  }
}

const initSakura = () => {
  const canvas = sakuraCanvas.value
  if (!canvas) return
  
  const ctx = canvas.getContext('2d')
  
  const resize = () => {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }
  
  resize()
  window.addEventListener('resize', resize)
  
  particles = []
  for (let i = 0; i < 40; i++) {
    particles.push(new SakuraParticle(canvas))
  }
  
  const animate = () => {
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    
    particles.forEach(particle => {
      particle.update()
      particle.draw(ctx)
    })
    
    animationId = requestAnimationFrame(animate)
  }
  
  animate()
}

const handleScroll = () => {
  scrollY.value = window.scrollY
}

onMounted(() => {
  initSakura()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style lang="scss" scoped>
.hero {
  height: 100vh;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 120%;
  background-image: url('/images/背景.png');
  background-size: cover;
  background-position: center;
  z-index: 0;
}

.sakura-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  pointer-events: none;
}

.hero-content {
  position: relative;
  z-index: 2;
  text-align: center;
  padding: 0 20px;
}

.hero-title {
  font-size: clamp(2rem, 5vw, 4rem);
  color: #FF5C8A;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(255, 255, 255, 0.8), 0 0 30px rgba(255, 143, 177, 0.5);
  animation: fadeInUp 1.5s ease;
}

.hero-subtitle {
  font-size: clamp(1rem, 2vw, 1.5rem);
  color: #555;
  margin-bottom: 40px;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
  animation: fadeInUp 1.5s ease 0.3s both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
