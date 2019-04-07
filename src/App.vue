<template>
  <div id="app">
    <div class="container-fluid header-text">
      <div class="row">
        <div class="col-auto">
          <h1 class="text-left pl-3" @click="$router.push('/')">SwearJar</h1>
        </div>
        <div class="col">
          <div class="row justify-content-center">
            <router-link to="/" class="p-3 link">Home</router-link>
            <router-link to="/users" class="p-3 link">Users</router-link>
          </div>
        </div>
      </div>
    </div>
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

.link {
  color: black;
  border-left: 1px solid gray;
}

.link:last-child {
  border-right: 1px solid gray;
}

.link:hover {
  color: black;
  text-decoration: none;
  background-color: lightgray;
}
</style>
