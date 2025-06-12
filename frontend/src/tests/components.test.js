import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'

describe('组件测试', () => {
  describe('基础组件', () => {
    it('应该渲染带有props的组件', () => {
      const TestComponent = {
        template: '<div class="test-component">{{ title }}</div>',
        props: ['title']
      }

      const wrapper = mount(TestComponent, {
        props: { title: 'Test Title' }
      })

      expect(wrapper.text()).toBe('Test Title')
      expect(wrapper.classes()).toContain('test-component')
    })

    it('应该处理用户交互事件', async () => {
      const TestComponent = {
        template: `
          <div>
            <button @click="increment">{{ count }}</button>
          </div>
        `,
        data() {
          return { count: 0 }
        },
        methods: {
          increment() {
            this.count++
          }
        }
      }

      const wrapper = mount(TestComponent)
      expect(wrapper.text()).toBe('0')

      await wrapper.find('button').trigger('click')
      expect(wrapper.text()).toBe('1')

      await wrapper.find('button').trigger('click')
      expect(wrapper.text()).toBe('2')
    })

    it('应该测试计算属性', () => {
      const TestComponent = {
        template: '<div>{{ fullName }}</div>',
        data() {
          return {
            firstName: 'John',
            lastName: 'Doe'
          }
        },
        computed: {
          fullName() {
            return `${this.firstName} ${this.lastName}`
          }
        }
      }

      const wrapper = mount(TestComponent)
      expect(wrapper.text()).toBe('John Doe')
    })

    it('应该测试条件渲染', async () => {
      const TestComponent = {
        template: `
          <div>
            <p v-if="showMessage">{{ message }}</p>
            <button @click="toggleMessage">Toggle</button>
          </div>
        `,
        data() {
          return {
            showMessage: false,
            message: 'Hello World'
          }
        },
        methods: {
          toggleMessage() {
            this.showMessage = !this.showMessage
          }
        }
      }

      const wrapper = mount(TestComponent)
      expect(wrapper.find('p').exists()).toBe(false)

      await wrapper.find('button').trigger('click')
      expect(wrapper.find('p').exists()).toBe(true)
      expect(wrapper.find('p').text()).toBe('Hello World')
    })

    it('应该测试列表渲染', () => {
      const TestComponent = {
        template: `
          <ul>
            <li v-for="item in items" :key="item.id">{{ item.name }}</li>
          </ul>
        `,
        data() {
          return {
            items: [
              { id: 1, name: 'Item 1' },
              { id: 2, name: 'Item 2' },
              { id: 3, name: 'Item 3' }
            ]
          }
        }
      }

      const wrapper = mount(TestComponent)
      const listItems = wrapper.findAll('li')
      
      expect(listItems).toHaveLength(3)
      expect(listItems[0].text()).toBe('Item 1')
      expect(listItems[1].text()).toBe('Item 2')
      expect(listItems[2].text()).toBe('Item 3')
    })
  })
}) 