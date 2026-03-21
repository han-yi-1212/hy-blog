from PIL import Image

# 打开图片
img = Image.open('e:/自主博客/frontend/public/images/logo.jpg')

# 转换为RGBA模式
img = img.convert('RGBA')

# 获取像素数据
pixels = img.load()

# 遍历所有像素，将白色背景改为透明
width, height = img.size
for x in range(width):
    for y in range(height):
        r, g, b, a = pixels[x, y]
        # 判断是否为白色或接近白色
        if r > 200 and g > 200 and b > 200:
            pixels[x, y] = (255, 255, 255, 0)

# 保存为PNG格式（支持透明）
img.save('e:/自主博客/frontend/public/images/logo_transparent.png')

print('Logo背景已改为透明，保存为 logo_transparent.png')
