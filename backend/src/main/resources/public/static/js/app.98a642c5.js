(function(e){function t(t){for(var r,o,a=t[0],l=t[1],u=t[2],s=0,f=[];s<a.length;s++)o=a[s],Object.prototype.hasOwnProperty.call(i,o)&&i[o]&&f.push(i[o][0]),i[o]=0;for(r in l)Object.prototype.hasOwnProperty.call(l,r)&&(e[r]=l[r]);d&&d(t);while(f.length)f.shift()();return c.push.apply(c,u||[]),n()}function n(){for(var e,t=0;t<c.length;t++){for(var n=c[t],r=!0,o=1;o<n.length;o++){var l=n[o];0!==i[l]&&(r=!1)}r&&(c.splice(t--,1),e=a(a.s=n[0]))}return e}var r={},i={app:0},c=[];function o(e){return a.p+"static/js/"+({about:"about"}[e]||e)+"."+{about:"f985bbdf"}[e]+".js"}function a(t){if(r[t])return r[t].exports;var n=r[t]={i:t,l:!1,exports:{}};return e[t].call(n.exports,n,n.exports,a),n.l=!0,n.exports}a.e=function(e){var t=[],n=i[e];if(0!==n)if(n)t.push(n[2]);else{var r=new Promise((function(t,r){n=i[e]=[t,r]}));t.push(n[2]=r);var c,l=document.createElement("script");l.charset="utf-8",l.timeout=120,a.nc&&l.setAttribute("nonce",a.nc),l.src=o(e);var u=new Error;c=function(t){l.onerror=l.onload=null,clearTimeout(s);var n=i[e];if(0!==n){if(n){var r=t&&("load"===t.type?"missing":t.type),c=t&&t.target&&t.target.src;u.message="Loading chunk "+e+" failed.\n("+r+": "+c+")",u.name="ChunkLoadError",u.type=r,u.request=c,n[1](u)}i[e]=void 0}};var s=setTimeout((function(){c({type:"timeout",target:l})}),12e4);l.onerror=l.onload=c,document.head.appendChild(l)}return Promise.all(t)},a.m=e,a.c=r,a.d=function(e,t,n){a.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:n})},a.r=function(e){"undefined"!==typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},a.t=function(e,t){if(1&t&&(e=a(e)),8&t)return e;if(4&t&&"object"===typeof e&&e&&e.__esModule)return e;var n=Object.create(null);if(a.r(n),Object.defineProperty(n,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var r in e)a.d(n,r,function(t){return e[t]}.bind(null,r));return n},a.n=function(e){var t=e&&e.__esModule?function(){return e["default"]}:function(){return e};return a.d(t,"a",t),t},a.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},a.p="/",a.oe=function(e){throw console.error(e),e};var l=window["webpackJsonp"]=window["webpackJsonp"]||[],u=l.push.bind(l);l.push=t,l=l.slice();for(var s=0;s<l.length;s++)t(l[s]);var d=u;c.push([0,"chunk-vendors"]),n()})({0:function(e,t,n){e.exports=n("56d7")},"12e3":function(e,t,n){"use strict";n("5a2f")},"1c4b":function(e,t,n){},"417c":function(e,t,n){"use strict";n("db65")},4248:function(e,t,n){"use strict";n("1c4b")},"48a7":function(e,t,n){"use strict";n("a0cf")},"56d7":function(e,t,n){"use strict";n.r(t);n("e260"),n("e6cf"),n("cca6"),n("a79d");var r=n("7a23");function i(e,t){var n=Object(r["v"])("router-view");return Object(r["o"])(),Object(r["d"])("div",null,[Object(r["f"])(n)])}n("48a7");const c={};c.render=i;var o=c,a=(n("d3b7"),n("3ca3"),n("ddb0"),n("6c02")),l={class:"home"};function u(e,t,n,i,c,o){var a=Object(r["v"])("Main");return Object(r["o"])(),Object(r["d"])("div",l,[Object(r["f"])(a,{msg:"Application de traitement d'images"})])}var s=Object(r["A"])("data-v-5f66bfc4");Object(r["r"])("data-v-5f66bfc4");var d={class:"hello"},f={class:"container"},b=Object(r["e"])("Fichier ");Object(r["p"])();var h=s((function(e,t,n,i,c,o){var a=Object(r["v"])("Image"),l=Object(r["v"])("Gallery");return Object(r["o"])(),Object(r["d"])("div",d,[Object(r["f"])("h1",null,Object(r["x"])(n.msg),1),Object(r["f"])("div",null,[Object(r["f"])("div",f,[Object(r["f"])("label",null,[b,Object(r["f"])("input",{type:"file",id:"file",ref:"file",onChange:t[1]||(t[1]=function(e){return o.handleFileUpload()})},null,544)]),Object(r["f"])("button",{onClick:t[2]||(t[2]=function(e){return o.submitFile()})},"Soumettre")])]),c.showImg?(Object(r["o"])(),Object(r["d"])(a,{key:0,id:"bigimg",callback:o.showGallery,callback2:o.deleteImageSelected,ref:"childImg"},null,8,["callback","callback2"])):(Object(r["o"])(),Object(r["d"])(l,{key:1,id:"gal",callback:o.callImage,ref:"childGallery"},null,8,["callback"]))])})),m=Object(r["A"])("data-v-4344f8ea");Object(r["r"])("data-v-4344f8ea");var g={class:"Gallery"},p=Object(r["f"])("h3",null,"Images disponibles",-1);Object(r["p"])();var v=m((function(e,t,n,i,c,o){return Object(r["o"])(),Object(r["d"])("div",g,[p,Object(r["f"])("button",{class:"Refresh__Button",onClick:t[1]||(t[1]=function(e){return o.refresh()})},"Rafraichir les images"),Object(r["f"])("ul",null,[(Object(r["o"])(!0),Object(r["d"])(r["a"],null,Object(r["u"])(c.response,(function(e){return Object(r["o"])(),Object(r["d"])("li",{key:e},[Object(r["f"])("img",{src:"",id:"img"+e.id,onClick:function(t){return n.callback(e)}},null,8,["id","onClick"])])})),128))])])})),O=n("bc3a"),j=n.n(O);function y(e,t){j.a.get("images").then((function(e){t(e.data)})).catch((function(t){e.push(t)}))}function w(e,t,n){var r="/images/"+e;j.a.get(r,{responseType:"blob"}).then((function(e){var r=new window.FileReader;r.readAsDataURL(e.data),r.onload=function(){n.result=r.result,t(n)}}))}function I(e){var t="/images/"+e;j.a.delete(t)}function k(e,t){j.a.post("/images",e).then((function(){t()}))}var S={name:"Gallery",props:{callback:Function},data:function(){return{response:[],errors:[],imgHeight:200}},methods:{refresh:function(){y(this.errors,this.updateImage)},updateOneImage:function(e){document.querySelector("#img"+e.id).setAttribute("src",e.result),document.querySelector("#img"+e.id).setAttribute("height",this.imgHeight),document.querySelector("#img"+e.id).setAttribute("width",this.imgHeight*e.width/e.height)},updateImage:function(e){console.log(e),this.response=e;for(var t=0;t<this.response.length;t++){var n=this.response[t],r=n.width,i=n.height,c={id:n.id,width:r,height:i};w(n.id,this.updateOneImage,c)}}},mounted:function(){this.refresh()}};n("4248");S.render=v,S.__scopeId="data-v-4344f8ea";var _=S,A=Object(r["A"])("data-v-3080f29d");Object(r["r"])("data-v-3080f29d");var F={class:"Image"},C=Object(r["f"])("h1",null,"Image principale",-1),G=Object(r["f"])("section",null,[Object(r["f"])("img",{id:"imgDownloaded"})],-1);Object(r["p"])();var P=A((function(e,t,n,i,c,o){return Object(r["o"])(),Object(r["d"])("div",F,[C,Object(r["f"])("button",{onClick:t[1]||(t[1]=function(e){return n.callback()})},"Changer l'image"),Object(r["f"])("button",{onClick:t[2]||(t[2]=function(e){return n.callback2()})},"Supprimer l'image"),G])})),D={name:"Image",components:{},props:{callback:Function,callback2:Function},data:function(){return{imgHeight:400}},methods:{setImg:function(e){this.idSelected=e.id,w(e.id,this.setSrcImg,{width:e.width,height:e.height})},setSrcImg:function(e){document.querySelector("#imgDownloaded").setAttribute("src",e.result),document.querySelector("#imgDownloaded").setAttribute("height",this.imgHeight),document.querySelector("#imgDownloaded").setAttribute("width",this.imgHeight*e.width/e.height)}}};n("12e3");D.render=P,D.__scopeId="data-v-3080f29d";var H=D,q={name:"Main",components:{Gallery:_,Image:H},props:{msg:String},data:function(){return{showImg:!1,response:[],file:"",imageDataUrl:"",selected:0,errors:[]}},computed:{image:function(){return this.imageDataUrl}},methods:{handleFileUpload:function(){this.file=this.$refs.file.files[0]},callbackSubmitFile:function(){this.$refs.childGallery.refresh()},submitFile:function(){var e=new FormData;e.append("file",this.file),k(e,this.callbackSubmitFile)},deleteImageSelected:function(){this.showGallery(),I(this.selected)},showGallery:function(){this.showImg=!1},callImage:function(e){this.selected=e.id,this.showImg=!0;while(void 0===this.$refs.childImg)console.log(this.$refs.childImg);this.$refs.childImg.setImg(e)}}};n("417c");q.render=h,q.__scopeId="data-v-5f66bfc4";var x=q,M={name:"Home",components:{Main:x}};M.render=u;var T=M,U=[{path:"/",name:"Home",component:T},{path:"/about",name:"About",component:function(){return n.e("about").then(n.bind(null,"f820"))}}],$=Object(a["a"])({history:Object(a["b"])("/"),routes:U}),R=$;Object(r["c"])(o).use(R).mount("#app")},"5a2f":function(e,t,n){},a0cf:function(e,t,n){},db65:function(e,t,n){}});
//# sourceMappingURL=app.98a642c5.js.map