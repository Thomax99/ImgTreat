<template>
  <div class="Gallery">
    <h3>Images disponibles</h3>
    <button class="Refresh__Button" @click="refresh()">Rafraichir les images</button>
    <ul v-show="showable">
      <li v-for="resp in response" :key="resp">
        <img src="" :id="'img'+ resp.id" @click="callback(resp)"/>
      </li>
    </ul>
    </div>
</template>
<script>

import { callRestServiceGetLstImg, callRestServiceGetImg } from '../http-api'
import Jimp from 'jimp'

export default {
  name: 'Gallery',
  props: {
    callback: Function
  },
  data () {
    return {
      response: [],
      imgHeight: 200,
      showable: false
    }
  },
  methods: {
    refresh () {
      this.showable = false
      callRestServiceGetLstImg(this.updateImage, this.callbackError)
    },
    updateOneImage (data) {
      if (data.type.subtype === 'tiff') {
        Jimp.read(data.result, function (err, file) {
          if (err) {
            console.log(err)
          } else {
            file.getBase64('image/png', (err, b64) => {
              if (err) {
                console.log(err)
              }
              else {
                document.querySelector('#img' + data.id + '').setAttribute('src', b64)
              }
            })
          }
        })
      }
      else {
        document.querySelector('#img' + data.id + '').setAttribute('src', data.result)
      }
      document.querySelector('#img' + data.id + '').setAttribute('height', this.imgHeight)
      document.querySelector('#img' + data.id + '').setAttribute('width', this.imgHeight * data.width / data.height)
      if (data.id === this.response[this.response.length-1].id) {
        // cette instruction est la simplement pour eviter un affichage "image apres image" mais plutot que toutes les images de
        // la galerie s'affichent a peu pres en meme temps (on suppose que les requetes se font a peu pres dans l'ordre ou elles
        // ont ete emises )
        this.showable = true
      }
    },
    updateImage (responses) {
      this.response = responses
      for (let i = 0; i < this.response.length; i++) {
        const image = this.response[i]
        const width = image.width
        const height = image.height
        const type = image.type
        const data = {
          id: image.id,
          width: width,
          height: height,
          type: type
        }
        document.querySelector('#img' + image.id + '').setAttribute('alt', image.id)
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
