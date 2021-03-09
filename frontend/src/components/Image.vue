<template>
  <div class="Image">
    <h1>Image principale</h1>
    <a id = "download" href=""
      download=""><button >Télécharger l'image</button></a>
    <button v-on:click="callback()">Changer l'image</button>
    <button v-on:click="callback2()">Supprimer l'image</button>
    <section>
        <img id="imgDownloaded" />
    </section>
  </div>
</template>
<script>

import { callRestServiceGetImg, callRestServiceGetInfoImg } from '../http-api'

export default {
  name: 'Image',
  components: {
  },
  props: {
    callback: Function,
    callback2: Function,
  },
  data () {
    return {
      imgHeight: 400,
      width: 0,
      name: '',
      height: 0,
      idSelected: -1
    }
  },
  methods: {
    setImg (id, callbackError) {
      this.idSelected = id
      callRestServiceGetInfoImg(id, this.setSizeImg, callbackError)
    },
    setSizeImg (data, callbackError) {
      this.height = data.height
      this.width = data.width
      this.name = data.name
      callRestServiceGetImg(this.idSelected, this.setSrcImg, callbackError, { } )
    },
    setSrcImg (data) {
      document.querySelector('#imgDownloaded').setAttribute('src', data.result)
      document.querySelector('#download').setAttribute('href', data.result)
      document.querySelector('#download').setAttribute('download', this.name)
      document.querySelector('#imgDownloaded').setAttribute('height', this.imgHeight)
      document.querySelector('#imgDownloaded').setAttribute('width', this.imgHeight * this.width / this.height)
    },
    downloadImg () {
      
    }
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
