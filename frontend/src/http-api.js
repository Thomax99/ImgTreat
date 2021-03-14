import axios from 'axios'

/**
 * Obtain the list of the images on the servor
 * @param {*} callback the function called when there is no problem. This function take in parameter just the list of images
 * @param {*} callbackError the function called in case of error. This function take in parameter just the error
 */
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

/**
 * Obtain an image on the servor
 * @param {*} id the id of the image that you would like
 * @param {*} callback the function called when there is no problem. This function take in parameter an object in which the response is as .result
 * @param {*} data the object which is going to be augmented by the result of the request and passed to the callback function
 * @param {*} callbackError the function called when there is a problem. This function take in parameter just the error
 */
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

/**
 * Delete an image on the servor
 * @param {*} id the id of the image that you want delete
 * @param {*} callbackError the function called when there is an error. This function take in parameter just the error
 */
function callRestServiceDeleteImg (id, callbackError) {
  const url = '/images/'+id
  axios.delete(url).catch ((e) => {
    callbackError(e)
  })
}
/**
 * Post an image on te servor
 * @param {*} data the data corresponding to the image that you would like post
 * @param {*} callback The function called where there is no problem. This function doesn't take any parameter
 * @param {*} callbackError The function called where there is a problem. This function take the error on parameter
 */
function callRestServicePostImg (data, callback, callbackError) {
  console.log(data)
  axios.post('/images',
    data
  ).then(function () {
    if (callback != null) {
        callback()
    }
  }).catch ((e) => {
    if (callbackError != null) {
      callbackError(e)
    }
  })
}

/**
 * Get the info of an image
 * @param {*} id The id of the image that you want the infos
 * @param {*} callback the function called when there is no problem. This function take in parameter an object corresponding to the info
 * And a function which is going to be called when there is error
 * @param {*} callbackError A function called in case of error
 */
function callRestServiceGetInfoImg (id, callback, callbackError) {
  const url = '/images/infos/'+id
  axios.get(url, {}
  ).then((response) => {
    callback(response.data, callbackError)
  }).catch((e) => {
    callbackError(e)
  })
}

/**
 * Increase the luminosity of an image
 * @param {*} id The id of the image that you like to increase the luminosity
 * @param {*} callback The function when there is no problem. THis function take in parameter an object in which there a result field corresponding
 * To the data image back
 * @param {*} callbackError The function called in case of error. This function take in parameter the type of the error
 */
function callRestServiceIncreaseLuminosity (id, callback, callbackError) {
  const value = window.prompt("Valeur d'augmentation : ")
  const url = '/images/'+id+'?algorithm=increaseLuminosity&p1=gain=' + value+'&p2=Y'
  axios.get(url, { responseType: 'blob' })
  .then((response) => {
    var reader = new window.FileReader()
    reader.readAsDataURL(response.data)
    reader.onload = function () {
      callback(reader) // this is something like really uggly thing
    }
  }).catch ((e) => {
      callbackError(e)
    })
}

export { callRestServiceIncreaseLuminosity, callRestServiceGetInfoImg, callRestServicePostImg, callRestServiceDeleteImg, callRestServiceGetLstImg, callRestServiceGetImg }
