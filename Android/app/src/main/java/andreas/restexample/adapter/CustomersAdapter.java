package andreas.restexample.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import andreas.restexample.R;
import andreas.restexample.Utils.ItemClickListener;
import andreas.restexample.models.Customer;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.CustomViewHolder> {

    private final List<Customer> dataList;
    private ItemClickListener clickListener;
    public CustomersAdapter(List<Customer> dataList){
        this.dataList = dataList;
    }
    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
    class CustomViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        private final TextView txtName;
        private final TextView txtAge;
        private final TextView txtCity;
        CustomViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.name);
            txtAge = itemView.findViewById(R.id.age);
            txtCity = itemView.findViewById(R.id.city);
            itemView.setOnClickListener(this); // bind the listener
        }
        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }

    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, final int position) {
        holder.txtName.setText(dataList.get(position).getName());
        holder.txtAge.setText(String.valueOf(dataList.get(position).getAge()));
        holder.txtCity.setText(dataList.get(position).getCity());

    }



    @Override
    public int getItemCount() {
        return dataList.size();
    }
}