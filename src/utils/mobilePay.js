import axios from 'axios';

const instance = axios.create({
    baseUrl: 'https://api.sandbox.mobilepay.dk/',
    timeout: 10000,
    headers: {
        'x-ibm-client-id': '1c0cd3ff-1143-476b-b136-efe9b1f5ecf3',
        'x-ibm-client-secret': process.env.VUE_APP_MOBILE_PAY_KEY
    }
});

export default function sendAmount(amount) {
    instance.post('https://api.sandbox.mobilepay.dk/bindings-restapi/api/v1/payments/payout-bankaccount', {
        'merchantId': 'd314f902-9b51-41f0-90da-948b952efa85',
        'merchantBinding': '000000-9988',
        'receiverRegNumber': '3098',
        'receiverAccountNumber': '3100460793',
        'amount': amount
    })
}
