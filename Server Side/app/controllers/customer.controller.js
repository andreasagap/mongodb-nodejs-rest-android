const Customer = require('../models/customer.model');

// Create
exports.create = (req, res) => {
    if (!req.body.name) {
        return res.status(400).send({
            message: "Customer's name is empty"
        });
    }

    const c = new Customer({
        name: req.body.name ,
        age: req.body.age || 0,
        city: req.body.city || "No city"
    });

    c.save().then(data => {
        res.send(data);
    }).catch(err => {
        res.status(500).send({
            message: err.message || "Error"
        });
    });
};

// Retrieve all
exports.findAll = (req, res) => {
    Customer.find().then(customers => {
        res.send(customers);
    }).catch(err => {
        res.status(500).send({
            message: err.message || "Error"
        });
    });
};

// Find one by id
exports.findOne = (req, res) => {
    Customer.findById(req.params.customerId).then(customer=>{
      if(!customer){
          return res.status(404).send({
              message: "Not fount customer with id: "+req.params.customerId
          });
      }
      res.send(customer);
  }).catch(err=>{
    return res.status(500).send({
        message:"Error"
    });
  });
};

// Update one by id
exports.update = (req, res) => {
    if(!req.body.name){
        return res.status(400).send({
            message:"Customer's name is empty"
        });
    }    
    Customer.findByIdAndUpdate(req.params.customerId,{
        name: req.body.name ,
        age: req.body.age,
        city: req.body.city
    },{new:true})
    .then(customer=>{
        if(!customer){
            return res.status(404).send({
                message:"Error"
            });
        }
        res.send(customer);
    }).catch(err=>{
        return res.status(500).send({
            message:"Error"
        });
    });
};

// Delete one by id
exports.delete = (req, res) => {
    Customer.findByIdAndRemove(req.params.customerId)
    .then(customer=>{
        if(!customer){
            return res.status(404).send({
                message:"Error"
            });
        }
        res.send({message:"Customer deleted successfully"});
    }).catch(err=>{
        return res.status(404).send({
            message:"Error"
        });
    });
};