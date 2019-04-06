<template>
  <div id="app">
    <h1 class="mx-auto header-text">AUHack19! SwearJar</h1>
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
  color: white;
  box-shadow: 0 -5px 25px 5px black;
}
</style>
