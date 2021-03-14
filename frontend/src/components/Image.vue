<template>
  <!-- To see tiff img, need jimp -->
  <div class="Image">
    <div id = "metaboutons"> 
      <a id = "download" href=""
        download=""><button >Télécharger l'image</button></a>
      <button v-on:click="callback()">Changer l'image</button>
      <button v-show="!onAlgo" v-on:click="callback2()">Supprimer l'image</button>
      <button v-show="onAlgo" v-on:click="resendImg" > Renvoyer l'image sur le serveur </button> <!-- methode a faire -->
    </div>
    <div id ="algoboutons">
      <button v-on:click="callAlgo('luminosity')">Augmenter la luminosité</button>
    </div>

    <section>
        <img id="imgDownloaded" />
    </section>
  </div>
</template>
<script>

import { callRestServicePostImg, callRestServiceIncreaseLuminosity, callRestServiceGetImg, callRestServiceGetInfoImg } from '../http-api'
import Jimp from 'jimp'
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
      type:'',
      name: '',
      height: 0,
      idSelected: -1,
      onAlgo: false,
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
      this.type = data.type
      callRestServiceGetImg(this.idSelected, this.setSrcImg, callbackError, { } )
    },
    setSrcImg (data) {
      //reading tiff img
      if (this.type.subtype === 'tiff') {
        Jimp.read(data.result, function (err, file) {
          if (err) {
            console.log(err)
          } else {
            file.getBase64('image/png', (err, b64) => {
              if (err) {
                console.log(err)
              }
              else {
                document.querySelector('#imgDownloaded').setAttribute('src', b64)
              }
            })
          }
        })
      }
      else {
        document.querySelector('#imgDownloaded').setAttribute('src', data.result)
      }
      document.querySelector('#imgDownloaded').setAttribute('alt', this.name)
      document.querySelector('#download').setAttribute('href', data.result)
      document.querySelector('#download').setAttribute('download', this.name)
      document.querySelector('#imgDownloaded').setAttribute('height', this.imgHeight)
      document.querySelector('#imgDownloaded').setAttribute('width', this.imgHeight * this.width / this.height)
    },
    callAlgo (algotype) {
      this.onAlgo = true
      if (algotype === "luminosity") {
        callRestServiceIncreaseLuminosity(this.idSelected, this.setSrcImg, (e) => { console.log(e)})
      }
    },
    resendImg () {
      const formData = new FormData()
      const data = document.querySelector('#download').getAttribute('href')
      const blob = this.dataURItoBlob(data)
      formData.append('file', blob)
      callRestServicePostImg(formData, null, null)
    },
    dataURItoBlob(dataURI) {
      // Thanks Stoive on stackoverflow :)
      // convert base64/URLEncoded data component to raw binary data held in a string
      var byteString;
      if (dataURI.split(',')[0].indexOf('base64') >= 0)
          byteString = atob(dataURI.split(',')[1]);
      else
          byteString = unescape(dataURI.split(',')[1]);

      // separate out the mime component
      var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

      // write the bytes of the string to a typed array
      var ia = new Uint8Array(byteString.length);
      for (var i = 0; i < byteString.length; i++) {
          ia[i] = byteString.charCodeAt(i);
      }
      return new Blob([ia], {type:mimeString});
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
#metaboutons {
  padding-top: 15px;
  padding-bottom: 15px;
}
</style>
