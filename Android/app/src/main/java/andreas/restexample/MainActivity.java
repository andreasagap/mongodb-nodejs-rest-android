package andreas.restexample;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import andreas.restexample.Utils.ItemClickListener;
import andreas.restexample.adapter.CustomersAdapter;
import andreas.restexample.interfaces.Mongoservice;
import andreas.restexample.models.Customer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements ItemClickListener {
    private CustomersAdapter adapter;
    private Mongoservice mongoservice;
    private AlertDialog dialog;
    private List<Customer> list;
    private SwipeRefreshLayout refreshLayout;
    private ItemTouchHelper.SimpleCallback simpleItemTouchCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mongoservice = retrofit.create(Mongoservice.class);
        retrieveAll();
        refreshLayout = findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        retrieveAll();
                    }
                }
        );
    }

    private void retrieveAll() {
        Call<List<Customer>> call = mongoservice.getAllCustomers();
        call.enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Customer>> call, @NonNull Response<List<Customer>> response) {
                if (list != null) {
                    list.clear();
                    list.addAll(Objects.requireNonNull(response.body()));
                } else
                    list = Objects.requireNonNull(response.body());
                generateDataList();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<List<Customer>> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    /*Method to generate List of data using RecyclerView with custom adapter*/
    private void generateDataList() {
        RecyclerView recyclerView = findViewById(R.id.recycler);
        simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                Call<Void> call = mongoservice.deleteCustomer(list.get(position).getId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        Toast.makeText(MainActivity.this, "Customer deleted successfully", Toast.LENGTH_SHORT).show();
                        list.remove(position);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }

                });

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter = new CustomersAdapter(list);
        adapter.setClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_add:
                insert();
                break;
            default:
                break;
        }
        return true;
    }

    private void insert() {
        LayoutInflater inflater = getLayoutInflater();
        final EditText name, age, city;
        View malertLayout = inflater.inflate(R.layout.add_customer, null);
        name = malertLayout.findViewById(R.id.name);
        age = malertLayout.findViewById(R.id.age);
        city = malertLayout.findViewById(R.id.city);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.cancel();
            }
        });
        alert.setView(malertLayout);
        alert.setPositiveButton("Add", null);
        dialog = alert.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (name.getText().toString().length() < 1 || age.getText().toString().length() < 1 || city.getText().toString().length() < 1) {
                            Toast.makeText(MainActivity.this, "Fill the fields!", Toast.LENGTH_SHORT).show();
                        } else {
                            Customer c = new Customer(null, name.getText().toString(), Integer.valueOf(age.getText().toString()), city.getText().toString());
                            Call<Customer> call = mongoservice.createUser(c);
                            call.enqueue(new Callback<Customer>() {
                                @Override
                                public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                                    list.add(response.body());
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                                    Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View view, final int position) {
        LayoutInflater inflater = getLayoutInflater();
        final EditText name, age, city;
        View malertLayout = inflater.inflate(R.layout.add_customer, null);
        name = malertLayout.findViewById(R.id.name);
        age = malertLayout.findViewById(R.id.age);
        city = malertLayout.findViewById(R.id.city);
        name.setText(list.get(position).getName());
        age.setText(String.valueOf(list.get(position).getAge()));
        city.setText(list.get(position).getCity());

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.create().cancel();
            }
        });
        alert.setView(malertLayout);
        alert.setPositiveButton("Update", null);
        dialog = alert.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (name.getText().toString().length() < 1 || age.getText().toString().length() < 1 || city.getText().toString().length() < 1) {
                            Toast.makeText(MainActivity.this, "Fill the fields!", Toast.LENGTH_SHORT).show();
                        } else {
                            Customer c = new Customer(null, name.getText().toString(), Integer.valueOf(age.getText().toString()), city.getText().toString());
                            Call<Customer> call = mongoservice.updateCustomer(list.get(position).getId(), c);
                            call.enqueue(new Callback<Customer>() {
                                @Override
                                public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                                    Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                                    list.get(position).setName(Objects.requireNonNull(response.body()).getName());
                                    list.get(position).setAge(Objects.requireNonNull(response.body()).getAge());
                                    list.get(position).setCity(Objects.requireNonNull(response.body()).getCity());
                                    list.get(position).setId(Objects.requireNonNull(response.body()).getId());
                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                                    Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        });
        dialog.show();
    }
}
