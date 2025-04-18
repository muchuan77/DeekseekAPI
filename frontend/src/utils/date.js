import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

dayjs.locale('zh-cn')

export const formatDate = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  return dayjs(date).format(format)
}

export const formatRelativeTime = (date) => {
  const now = dayjs()
  const target = dayjs(date)
  const diff = now.diff(target, 'minute')

  if (diff < 1) return '刚刚'
  if (diff < 60) return `${diff}分钟前`
  if (diff < 1440) return `${Math.floor(diff / 60)}小时前`
  if (diff < 43200) return `${Math.floor(diff / 1440)}天前`
  return formatDate(date, 'YYYY-MM-DD')
}

export const formatDuration = (duration) => {
  const hours = Math.floor(duration / 3600)
  const minutes = Math.floor((duration % 3600) / 60)
  const seconds = duration % 60

  if (hours > 0) {
    return `${hours}小时${minutes}分${seconds}秒`
  }
  if (minutes > 0) {
    return `${minutes}分${seconds}秒`
  }
  return `${seconds}秒`
} 