<template>
  <div id="app">
    <h1 class="text-left pl-3 header-text" @click="$router.push('/')">SwearJar</h1>
    <router-view/>
  </div>
</template>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  background-color: #efefef;
  height: 100vh;
}

.header-text {
  background-color: darkgray;
  cursor: pointer;
  color: white;
  box-shadow: 0 -5px 25px 5px black;
}
</style>
