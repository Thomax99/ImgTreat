import { shallowMount } from '@vue/test-utils'
import Gallery from '@/components/Gallery.vue'

function callback () {
    console.log('hello, world')
}

describe('Gallery.vue', () => {
  it('call callback when click on image', () => {
    const wrapper = shallowMount(Gallery, {
      data: () => ({ response: [ {id: 0} ] }),
      props: { callback: callback }
    })
    const spy = jest.spyOn(console, 'log')
    const img = wrapper.find('#img0');
    img.trigger('click')
    expect(spy).toBeCalled();
  })
  it('contains 3 img when the size of response is 3', () => {
    const wrapper = shallowMount(Gallery, {
      data: () => ({ response: [ {id: 0}, {id: 1}, {id: 2} ] }),
      props: { callback: callback }
    })
    expect(wrapper.find('#img0').exists()).toBe(true)
    expect(wrapper.find('#img1').exists()).toBe(true)
    expect(wrapper.find('#img2').exists()).toBe(true)
    expect(wrapper.find('#img3').exists()).toBe(false)
  })

})
