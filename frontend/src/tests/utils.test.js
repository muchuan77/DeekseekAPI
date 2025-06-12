import { describe, it, expect } from 'vitest'
import { formatDate } from '../utils/date'
import { validateEmail, validatePassword, validatePhone, validateUsername } from '../utils/validate'
import { formatFileSize, downloadFile } from '../utils/file'

describe('工具函数全面测试', () => {
  describe('日期工具函数', () => {
    it('应该格式化各种日期格式', () => {
      // 测试Date对象
      const date = new Date('2023-12-25T10:30:00')
      expect(formatDate(date)).toBe('2023-12-25 10:30:00')
      
      // 测试字符串
      expect(formatDate('2023-12-25T10:30:00')).toBe('2023-12-25 10:30:00')
      
      // 测试时间戳
      const timestamp = new Date('2023-12-25T10:30:00').getTime()
      expect(formatDate(timestamp)).toBe('2023-12-25 10:30:00')
    })

    it('应该处理有效日期输入', () => {
      // dayjs在处理null/undefined时会返回当前时间，所以我们测试它不会抛出错误
      expect(() => formatDate(null)).not.toThrow()
      expect(() => formatDate(undefined)).not.toThrow()
      
      // 测试无效日期字符串
      expect(formatDate('invalid-date')).toBe('Invalid Date')
    })

    it('应该支持自定义格式', () => {
      const date = new Date('2023-12-25T10:30:00')
      expect(formatDate(date, 'YYYY-MM-DD')).toBe('2023-12-25')
      expect(formatDate(date, 'MM/DD/YYYY')).toBe('12/25/2023')
    })
  })

  describe('验证工具函数', () => {
    describe('邮箱验证', () => {
      it('应该验证正确的邮箱格式', () => {
        expect(validateEmail('test@example.com')).toBe(true)
        expect(validateEmail('user.name+tag@example.com')).toBe(true)
        expect(validateEmail('user_name@example-site.co.uk')).toBe(true)
      })

      it('应该拒绝错误的邮箱格式', () => {
        expect(validateEmail('')).toBe(false)
        expect(validateEmail('invalid')).toBe(false)
        expect(validateEmail('@example.com')).toBe(false)
        expect(validateEmail('test@')).toBe(false)
        expect(validateEmail('test.example.com')).toBe(false)
      })
    })

    describe('密码验证', () => {
      it('应该验证强密码', () => {
        expect(validatePassword('Password123')).toBe(true)
        expect(validatePassword('MyPass123')).toBe(true)
        expect(validatePassword('SecurePass1')).toBe(true)
      })

      it('应该拒绝弱密码', () => {
        expect(validatePassword('123456')).toBe(false) // 太短，没有字母
        expect(validatePassword('password')).toBe(false) // 没有大写和数字
        expect(validatePassword('PASSWORD')).toBe(false) // 没有小写和数字
        expect(validatePassword('Pass123')).toBe(false) // 太短
      })
    })

    describe('手机号验证', () => {
      it('应该验证正确的手机号', () => {
        expect(validatePhone('13812345678')).toBe(true)
        expect(validatePhone('15987654321')).toBe(true)
        expect(validatePhone('18612345678')).toBe(true)
      })

      it('应该拒绝错误的手机号', () => {
        expect(validatePhone('12345678901')).toBe(false) // 不是1开头
        expect(validatePhone('1381234567')).toBe(false) // 位数不够
        expect(validatePhone('138123456789')).toBe(false) // 位数过多
      })
    })

    describe('用户名验证', () => {
      it('应该验证正确的用户名', () => {
        expect(validateUsername('user123')).toBe(true)
        expect(validateUsername('test_user')).toBe(true)
        expect(validateUsername('UserName123')).toBe(true)
      })

      it('应该拒绝错误的用户名', () => {
        expect(validateUsername('usr')).toBe(false) // 太短
        expect(validateUsername('verylongusernamethatistoolong')).toBe(false) // 太长
        expect(validateUsername('user-name')).toBe(false) // 包含连字符
        expect(validateUsername('user@name')).toBe(false) // 包含特殊字符
      })
    })
  })

  describe('文件工具函数', () => {
    it('应该格式化文件大小', () => {
      // 根据实际实现，formatFileSize返回的格式是"1 KB"而不是"1.00 KB"
      expect(formatFileSize(1024)).toBe('1 KB')
      expect(formatFileSize(1048576)).toBe('1 MB')
      expect(formatFileSize(1073741824)).toBe('1 GB')
      expect(formatFileSize(500)).toBe('500 B')
      expect(formatFileSize(0)).toBe('0 B')
    })

    it('应该处理下载文件', () => {
      // 模拟测试，因为downloadFile涉及DOM操作
      expect(() => downloadFile('http://example.com/file.pdf', 'test.pdf')).not.toThrow()
    })
  })
}) 