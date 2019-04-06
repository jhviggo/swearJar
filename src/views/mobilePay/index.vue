<template>
    <div>
        <h1 class="mx-auto">Mobile pay</h1>
        <div class="container">
            <div class="row">
                <h2 class="mx-auto">SwearJar balance</h2>
                <div class="col-12">
                    SwearJar has <span class="font-weight-bold">{{ jarBalance }}</span>DKK in the jar.
                </div>

                <h2 class="mx-auto">Users:</h2>
                <div class="col-12" v-for="user in users">
                    <span class="font-weight-bold">{{ user.name }}</span> has <span class="font-weight-bold">{{ user.balance }}</span> DKK.
                </div>

                <h2 class="mx-auto mt-4">Transactions</h2>
                <div class="col-12" v-for="transaction in transactions">
                    <span class="font-weight-bold">{{ transaction.from }}</span> payed <span class="font-weight-bold">{{ transaction.amount }}</span>DKK for saying <span class="font-weight-bold">{{ transaction.word }}</span>.
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
            jarBalance: 0
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
    }
}
</script>
