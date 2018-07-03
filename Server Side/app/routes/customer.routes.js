module.exports = (app) => {
    const customers = require('../controllers/customer.controller');

    // Create a new customer
    app.post('/customer', customers.create);

    // Retrieve all customers
    app.get('/customers', customers.findAll);

    // Retrieve a single customer with customerId
    app.get('/customers/:customerId', customers.findOne);

    // Update a customer with custoumerId
    app.put('/customers/:customerId', customers.update);

    // Delete a Customer with customerId
    app.delete('/customers/:customerId', customers.delete);
}