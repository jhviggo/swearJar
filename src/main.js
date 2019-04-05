import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import './registerServiceWorker'
import 'bootstrap-css-only';

import firebase from 'firebase';

var config = {
  apiKey: process.env.VUE_APP_FIREBASE_KEY,
  authDomain: 'swearjar.firebaseapp.com',
  databaseURL: 'https://swearjar-4a26e.firebaseio.com',
  storageBucket: 'swearjar-4a26e.appspot.com',
};

var firebaseApp = firebase.initializeApp(config);

/*
var ref = firebaseApp.database().ref('known_swears');

ref.on("value", function(snapshot) {
  console.log(snapshot.val());
}, function (errorObject) {
  console.log("The read failed: " + errorObject.code);
});
*/

Vue.config.productionTip = false;

Vue.prototype.$db = firebase.database();

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
