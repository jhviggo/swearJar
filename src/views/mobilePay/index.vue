<template>
    <div>
        <h1 class="mx-auto">Mobile pay</h1>
        <div class="container">
            <div class="row">
                <h2 class="mx-auto">Users:</h2>
                <div class="col-12" v-for="user in users">
                    <span class="font-weight-bold">{{ user.name }}</span> has <span class="font-weight-bold">{{ user.balance }}</span> DKK left in their account.
                </div>
                <div class="col-12 pt-3">
                    <button v-if="!userCreationOpen" class="btn btn-primary" @click="openUserCreation()">Add new user</button>
                    <div v-else class="form-group mx-auto py-4 creation">
                        <input id="userName" v-model="userName" class="form-control mx-auto input-field mb-1" placeholder="User's name">
                        <input id="userBalance" v-model="userBalance" class="form-control mx-auto input-field" placeholder="User's balance">
                        <p v-if="creationWarning" class="bg-warning m-0">Please enter your information</p>
                        <button class="btn btn-primary mr-1 mt-3" @click="addNewUser()">Add user</button>
                        <button class="btn btn-danger mt-3" @click="userCreationOpen = false">Cancel</button>
                    </div>
                </div>

                <h2 class="mx-auto col-12 mt-4 d">Transactions</h2>
                <div class="col-auto mx-auto hidden-container">
                    <div class="px-5 bg-white transaction-container">
                        <div v-for="transaction in transactions">
                            <span class="font-weight-bold">{{ transaction.from }}</span> payed <span class="font-weight-bold">{{ transaction.amount }}</span> DKK for saying <span class="font-weight-bold">{{ transaction.word }}</span>.
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import Vue from 'vue';

export default {
    data() {
        return {
            jarRef: Vue.prototype.$db.ref('jar'),
            usersRef: Vue.prototype.$db.ref('users'),
            jarRef: Vue.prototype.$db.ref('jar'),
            transactionsRef: Vue.prototype.$db.ref('transactions'),
            users: [],
            transactions: [],
            jarBalance: 0,
            userCreationOpen: false,
            userName: '',
            userBalance: '',
            creationWarning: false
        }
    },
    created() {
        const _self = this;

        /* adding jar balance */
        this.jarRef.on("value", function(snapshot) {
            _self.jarBalance = snapshot.val().balance;
        }, function (errorObject) {
            console.log("The read failed: " + errorObject.code);
        });

        /* adding users to array of users */
        this.usersRef.on("value", function(snapshot) {
            _self.users = [];
            Object.keys(snapshot.val()).forEach(item => _self.users.push(snapshot.val()[item]));
        }, function (errorObject) {
            console.log("The read failed: " + errorObject.code);
        });

        /* adding transactions to array of transactions */
        this.transactionsRef.on("value", function(snapshot) {
            _self.transactions = [];
            Object.keys(snapshot.val()).forEach(item => _self.transactions.push(snapshot.val()[item]));
        }, function (errorObject) {
            console.log("The read failed: " + errorObject.code);
        });
    },
    methods: {
        openUserCreation() {
            this.userCreationOpen = true;
            this.creationWarning = false;
        },
        addNewUser() {
            if (!this.userName || !this.userBalance || !/\d+\.\d{2}/.test(this.userBalance)) {
                this.creationWarning = true;
                return;
            }

            this.usersRef.push({
                name: this.userName,
                balance: Number(this.userBalance)
            });
            this.userCreationOpen = false;
            this.creationWarning = false;
        }
    }
}
</script>

<style>
.input-field {
    max-width: 20rem;
}

.creation {
    border: 1px solid gray;
    -webkit-border-radius: 1rem;
    -moz-border-radius: 1rem;
    border-radius: 1rem;
    width: 25rem;
}

.transaction-container {
    max-height: 15rem;
    overflow-y: scroll;
}

.hidden-container {
    overflow-y: hidden;
}
</style>
