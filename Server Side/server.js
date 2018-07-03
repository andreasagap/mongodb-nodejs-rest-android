const express=require('express');
const bodyParser = require('body-parser');

const app=express();

app.use(bodyParser.urlencoded({extended:true}));
app.use(bodyParser.json())

// define simple route
app.get('/',(req,res)=>{
    res.json({'message': "Welcome"})
});
// Require Costumers routes
require('./app/routes/customer.routes')(app);

app.listen(3000, () => {
    console.log("Server is listening on port 3000");
});

const dbConfig=require('./config/database.config.js');
const mongoose=require('mongoose');

mongoose.Promise=global.Promise;

// Connecting to the database
mongoose.connect(dbConfig.url)
.then(() => {
    console.log("Successfully connected to the database");    
}).catch(err => {
    console.log('Could not connect to the database. Exiting now...');
    process.exit();
});