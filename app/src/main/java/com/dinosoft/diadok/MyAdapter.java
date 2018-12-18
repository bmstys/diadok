package com.dinosoft.diadok;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
{
    Context context;
    ArrayList<DataGejala> dataGejalas;
    ArrayList<DataGejala> gejalanyas = new ArrayList<>();

    public MyAdapter(Context c,ArrayList<DataGejala> d)
    {
        context = c;
        dataGejalas = d;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.gejala_row,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i)
    {
        myViewHolder.checkBox.setText(dataGejalas.get(i).getNama());

        myViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (buttonView.isChecked())
                {
                    gejalanyas.add(dataGejalas.get(i));
                }
                else
                {
                    gejalanyas.remove(dataGejalas.get(i));
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return dataGejalas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            checkBox  = (CheckBox)itemView.findViewById(R.id.checkbox_gejala);
        }
    }
}
