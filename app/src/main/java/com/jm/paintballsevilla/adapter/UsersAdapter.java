package com.jm.paintballsevilla.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jm.paintballsevilla.R;
import com.jm.paintballsevilla.model.Users;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private ArrayList<Users> listaUsers;
    private OnItemClickListener listener;

    public UsersAdapter(ArrayList<Users> listaUsers) {
        this.listaUsers = listaUsers;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user, null, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        final Users user = listaUsers.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(user);
                }
            }
        });
        holder.email.setText(listaUsers.get(position).getEmail());
        holder.name.setText(listaUsers.get(position).getName());
        holder.last_name.setText(listaUsers.get(position).getLast_name());
        holder.phone.setText(String.valueOf(listaUsers.get(position).getPhone()));
        Boolean master = listaUsers.get(position).isMaster();
        if (master) {
            holder.master.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.master.setText("Master");
        } else {
            holder.master.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.grey));
            holder.master.setText(".");
        }
    }

    @Override
    public int getItemCount() {
        return listaUsers.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView email, name, last_name, phone, master;
        public UserViewHolder (View itemView)
        {
            super(itemView);
            email = (TextView) itemView.findViewById(R.id.view_user_email);
            name = (TextView) itemView.findViewById(R.id.view_user_name);
            last_name = (TextView) itemView.findViewById(R.id.view_user_last_name);
            phone = (TextView) itemView.findViewById(R.id.view_user_plazas_m);
            master = (TextView) itemView.findViewById(R.id.view_user_master);
        }

    }

    // Métodos para acceder al usuario cuando se haga click sobre él
    public interface OnItemClickListener {
        void onItemClick(Users user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
