import { mount } from '@vue/test-utils'
import Main from '@/components/Main.vue'

describe('Main.vue', () => {
  it('renders props.msg when passed', () => {
    const msg = 'new message'
    const wrapper = mount(Main, {
      props: { msg }
    })
    expect(wrapper.text()).toMatch(msg)
  })
})
