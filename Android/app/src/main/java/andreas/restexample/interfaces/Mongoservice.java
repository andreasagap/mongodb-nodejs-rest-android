package andreas.restexample.interfaces;

import java.util.List;

import andreas.restexample.models.Customer;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Mongoservice {
    @POST("/customer")
    Call<Customer> createUser(@Body Customer c);

    @GET("/customers")
    Call<List<Customer>> getAllCustomers();

    @DELETE("customers/{id}")
    Call<Void> deleteCustomer(@Path("id") String id);

    @PUT("customers/{id}")
    Call<Customer> updateCustomer(@Path("id")  String id, @Body Customer c);
}
