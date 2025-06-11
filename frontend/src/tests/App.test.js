import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'

// 基础的测试，确保测试环境正常工作
describe('基础测试', () => {
  it('应该能够运行测试', () => {
    expect(true).toBe(true)
  })

  it('应该能够执行简单的计算', () => {
    const sum = 1 + 1
    expect(sum).toBe(2)
  })

  it('应该能够测试字符串', () => {
    const message = 'Hello World'
    expect(message).toContain('World')
  })
})

// Vue 组件测试示例
describe('Vue 组件测试', () => {
  it('应该能够渲染简单组件', () => {
    const TestComponent = {
      template: '<div>{{ message }}</div>',
      data() {
        return {
          message: 'Hello Test'
        }
      }
    }

    const wrapper = mount(TestComponent)
    expect(wrapper.text()).toContain('Hello Test')
  })
}) 