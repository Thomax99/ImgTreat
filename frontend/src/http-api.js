import axios from 'axios'

function callRestServiceGetLstImg (callback, callbackError) {
  axios
    .get('images')
    .then((response) => {
    // JSON responses are automatically parsed.
      callback(response.data)
    })
    .catch((e) => {
      callbackError(e)
    })
}

function callRestServiceGetImg (id, callback, data, callbackError) {
  const url = '/images/'+id
  axios.get(url, { responseType: 'blob' })
    .then((response) => {
      var reader = new window.FileReader()
      reader.readAsDataURL(response.data)
      reader.onload = function () {
        data.result = reader.result
        callback(data)
      }
    }).catch ((e) => {
        callbackError(e)
      })
}

function callRestServiceDeleteImg (id, callbackError) {
  const url = '/images/'+id
  axios.delete(url).catch ((e) => {
    callbackError(e)
  })
}

function callRestServicePostImg (data, callback, callbackError) {
  axios.post('/images',
    data
  ).then(function () {
    callback()
  }).catch ((e) => {
    callbackError(e)
  })
}

function callRestServiceGetInfoImg (id, callback, callbackError) {
  const url = '/images/infos/'+id
  axios.get(url, {}
  ).then((response) => {
    callback(response.data, callbackError)
  }).catch((e) => {
    callbackError(e)
  })
}

export { callRestServiceGetInfoImg, callRestServicePostImg, callRestServiceDeleteImg, callRestServiceGetLstImg, callRestServiceGetImg }
