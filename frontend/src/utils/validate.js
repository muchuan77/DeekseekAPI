// 邮箱验证
export const validateEmail = (email) => {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(email)
}

// 手机号验证
export const validatePhone = (phone) => {
  const re = /^1[3-9]\d{9}$/
  return re.test(phone)
}

// 密码强度验证
export const validatePassword = (password) => {
  // 至少8位，包含大小写字母和数字
  const re = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/
  return re.test(password)
}

// 用户名验证
export const validateUsername = (username) => {
  // 4-16位字母、数字、下划线
  const re = /^[a-zA-Z0-9_]{4,16}$/
  return re.test(username)
}

// URL验证
export const validateUrl = (url) => {
  const re = /^(https?:\/\/)?([\da-z.-]+)\.([a-z.]{2,6})([/\w .-]*)*\/?$/
  return re.test(url)
}

// 文件大小验证
export const validateFileSize = (file, maxSize) => {
  return file.size <= maxSize
}

// 文件类型验证
export const validateFileType = (file, allowedTypes) => {
  return allowedTypes.includes(file.type)
} 