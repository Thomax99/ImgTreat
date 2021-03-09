<template>
  <div class="Gallery">
    <h3>Images disponibles</h3>
    <button class="Refresh__Button" @click="refresh()">Rafraichir les images</button>
    <ul>
      <li v-for="resp in response" :key="resp">
        <img src="" :id="'img'+ resp.id" @click="callback(resp)"/>
      </li>
    </ul>
    </div>
</template>
<script>

import { callRestServiceGetLstImg, callRestServiceGetImg } from '../http-api'
export default {
  name: 'Gallery',
  props: {
    callback: Function
  },
  data () {
    return {
      response: [],
      imgHeight: 200
    }
  },
  methods: {
    refresh () {
      callRestServiceGetLstImg(this.updateImage, this.callbackError)
    },
    updateOneImage (data) {
      document.querySelector('#img' + data.id + '').setAttribute('src', data.result)
      document.querySelector('#img' + data.id + '').setAttribute('height', this.imgHeight)
      document.querySelector('#img' + data.id + '').setAttribute('width', this.imgHeight * data.width / data.height)
    },
    updateImage (responses) {
      this.response = responses
      for (let i = 0; i < this.response.length; i++) {
        const image = this.response[i]
        const width = image.width
        const height = image.height
        const data = {
          id: image.id,
          width: width,
          height: height
        }
        callRestServiceGetImg(image.id, this.updateOneImage, data, this.callbackError)
      }
    },
    callbackError () {
      this.refresh()
    }
  },
  mounted () {
    this.refresh()
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
