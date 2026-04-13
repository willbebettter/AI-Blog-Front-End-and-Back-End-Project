const MAX_AVATAR_SIZE = 500 * 1024
const MAX_COVER_SIZE = 2 * 1024 * 1024
const AVATAR_MAX_WIDTH = 200
const AVATAR_MAX_HEIGHT = 200
const COVER_MAX_WIDTH = 800
const COVER_MAX_HEIGHT = 600

export function compressImage(file, maxWidth, maxHeight, quality = 0.7) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => {
        const canvas = document.createElement('canvas')
        let width = img.width
        let height = img.height

        if (width > maxWidth) {
          height = (height * maxWidth) / width
          width = maxWidth
        }
        if (height > maxHeight) {
          width = (width * maxHeight) / height
          height = maxHeight
        }

        canvas.width = width
        canvas.height = height
        const ctx = canvas.getContext('2d')
        ctx.drawImage(img, 0, 0, width, height)

        const dataUrl = canvas.toDataURL('image/jpeg', quality)
        resolve(dataUrl)
      }
      img.onerror = () => reject(new Error('图片加载失败'))
      img.src = e.target.result
    }
    reader.onerror = () => reject(new Error('文件读取失败'))
    reader.readAsDataURL(file)
  })
}

export function validateImageSize(file, maxSize) {
  if (file.size > maxSize) {
    const maxMB = (maxSize / 1024 / 1024).toFixed(1)
    const fileMB = (file.size / 1024 / 1024).toFixed(1)
    throw new Error(`图片大小 ${fileMB}MB 超过限制 ${maxMB}MB`)
  }
}

export async function processAvatar(file) {
  validateImageSize(file, MAX_AVATAR_SIZE + 1 * 1024 * 1024)
  return compressImage(file, AVATAR_MAX_WIDTH, AVATAR_MAX_HEIGHT, 0.6)
}

export async function processCoverImage(file) {
  validateImageSize(file, MAX_COVER_SIZE + 2 * 1024 * 1024)
  return compressImage(file, COVER_MAX_WIDTH, COVER_MAX_HEIGHT, 0.7)
}
