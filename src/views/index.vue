<template>
    <div class="container">
        <div class="row">
            <div class="col-12">
                <h1 class="mx-auto">AUHack19! SwearJar</h1>
            </div>
            <div class="col-12 d-flex justify-content-center">
                <ul class="list-unstyled text-left" id="test">
                    <li v-for="(swear, index) in swears">
                        {{ index }} - {{ swear }}
                    </li>
                </ul>
            </div>
            <div class="col-12">
                <label for="inpSwear">Add new swear</label>
                <input id="inpSwear" v-model="message">
                <button @click="addSwear">Add swear</button>
                <p class="bg-warning">{{ error }}</p>
            </div>
        </div>
    </div>
</template>

<script>
import Vue from 'vue';

export default {
    data() {
        return {
            message: '',
            error: '',
            swears: [],
            counter: 0,
            ref: Vue.prototype.$db.ref('known_swears')
        }
    },
    created() {
        const _self = this;

        this.ref.on("value", function(snapshot) {
            _self.swears = [];
            Object.keys(snapshot.val()).forEach(item => _self.swears.push(snapshot.val()[item]));
        }, function (errorObject) {
            console.log("The read failed: " + errorObject.code);
        });
    },
    methods: {
        addSwear(event) {
            if(this.message && !this.swears.includes(this.message)) {
                this.ref.push(this.message);
                this.error = '';
            }
            else if(this.message === '') {
                this.error = 'Please type something.';
            }
            else {
                this.error = 'Swear already exists!'
            }
            this.message = '';
        }
    }
}
</script>
