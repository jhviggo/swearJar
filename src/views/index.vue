<template>
    <div class="container">
        <div class="row">
            <div class="col-12 d-flex justify-content-center swear-list">
                <ul class="list-unstyled text-left" id="test">
                    <li v-for="(swear, index) in swears">
                        {{ index }} - <span>{{ swear.word }}</span> said <span>{{ swear.count }}</span> times.
                    </li>
                </ul>
            </div>
            <div class="col-12 form-group">
                <div class="d-flex flex-column flex-md-row justify-content-center">
                    <input class="form-control swear-input" id="inpSwear" type="text" v-model="message" placeholder="Add a swear word herer">
                    <button class="btn btn-primary" @click="addSwear">Add swear</button>
                </div>
                <p class="bg-warning">{{ error }}</p>
            </div>
            <div class="col-12">
                <button class="btn" :class="{ 'btn-success': !moneyHasBeenSend, 'btn-warning': moneyHasBeenSend }" @click="sendMoney">{{ btnText }}</button>
            </div>
        </div>
    </div>
</template>

<script>
import Vue from 'vue';
import sendAmount from '@/utils/mobilePay.js';

export default {
    data() {
        return {
            moneyHasBeenSend: false,
            btnText: 'Send money (mobilepay test)',
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
                this.ref.push({ word: this.message, count: 0 });
                this.error = '';
            }
            else if(this.message === '') {
                this.error = 'Please type something.';
            }
            else {
                this.error = 'Swear already exists!'
            }
            this.message = '';
        },
        sendMoney() {
            if(!this.moneyHasBeenSend) {
                sendAmount(15.51);
                this.moneyHasBeenSend = true;
                this.btnText = 'Money has been sent';
            }
        }
    }
}
</script>

<style scoped>
.swear-list {
    overflow-y: hidden;
}

.swear-list ul {
    max-height: 250px;
    overflow-y: scroll;
}

li span {
    font-weight: bold;
}

.swear-input {
    max-width: 25rem;
}

@media(max-width: 770px) {
    .swear-input {
        max-width: unset;
    }
}
</style>
