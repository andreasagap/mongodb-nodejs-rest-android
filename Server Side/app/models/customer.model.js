const mongoose = require('mongoose');

const CustomerSchema = mongoose.Schema({
    name: String,
    age: Number,
    city: String
}, {
        timestamps: false
    });

module.exports = mongoose.model('Customer', CustomerSchema);
