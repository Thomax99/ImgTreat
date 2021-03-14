<template>
  <div class="hello">
    <h2 v-show="errorShown"> Erreur : {{ e }} </h2>
    <h1>{{ msg }}</h1>
    <div>
        <div class="container">
            <label>Fichier
              <input type="file" id="file" ref="file" v-on:change="handleFileUpload()"/>
            </label>
              <button v-on:click="submitFile()">Soumettre</button>
        </div>
    </div>
    <Image id ="bigimg" v-show="showImg"  :callback="showGallery" :callback2="deleteImageSelected" ref ="childImg" />
    <Gallery id= "gal" v-show="!showImg" :callback="callImage" ref ="childGallery" />
  </div>
</template>
<script>
import Gallery from '@/components/Gallery.vue'
import Image from '@/components/Image.vue'
import { callRestServicePostImg, callRestServiceDeleteImg } from '../http-api'

export default {
  name: 'Main',
  components: {
    Gallery,
    Image
  },
  props: {
    msg: String
  },
  data () {
    return {
      showImg: false,
      response: [],
      file: '',
      imageDataUrl: '',
      selected: 0,
      errors: [],
      e: '',
      errorShown: false
    }
  },
  methods: {
    handleFileUpload () {
      this.file = this.$refs.file.files[0]
    },
    callbackSubmitFile () {
      this.$refs.childGallery.refresh()
    },
    submitFile () {
      const formData = new FormData()
      console.log(this.file)
      formData.append('file', this.file)
      callRestServicePostImg(formData, this.callbackSubmitFile, this.callbackError)
    },
    deleteImageSelected () {
      this.showGallery()
      callRestServiceDeleteImg(this.selected, this.callbackError)
    },
    showGallery () {
      this.showImg = false
      this.$refs.childGallery.refresh()
    },
    showError () {
      this.errorShown = true
      setTimeout( () => {
        this.errorShown = false
      }, 3000)
    },
    callImage (image) {
      this.selected = image.id
      this.showImg = true
      this.$refs.childImg.setImg(image.id, this.callbackError)
    },
    callbackError (error) {
      this.e = error.message
      this.showGallery()
      this.showError()
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
