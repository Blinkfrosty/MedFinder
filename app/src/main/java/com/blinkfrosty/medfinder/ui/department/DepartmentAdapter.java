package com.blinkfrosty.medfinder.ui.department;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.blinkfrosty.medfinder.R;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Department;

import java.util.List;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder> {

    private final List<Department> departments;

    public DepartmentAdapter(List<Department> departments) {
        this.departments = departments;
    }

    @NonNull
    @Override
    public DepartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_department, parent, false);
        return new DepartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartmentViewHolder holder, int position) {
        Department department = departments.get(position);
        holder.departmentName.setText(department.getName());
        holder.departmentDescription.setText(department.getDescription());

        holder.itemView.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.item_click_animation));
            Bundle args = new Bundle();
            args.putString("departmentId", department.getId());
            args.putString("departmentName", department.getName());
            Navigation.findNavController(v).navigate(R.id.action_search_by_department_to_doctors_in_department, args);
        });
    }

    @Override
    public int getItemCount() {
        return departments.size();
    }

    public static class DepartmentViewHolder extends RecyclerView.ViewHolder {
        TextView departmentName;
        TextView departmentDescription;

        public DepartmentViewHolder(@NonNull View itemView) {
            super(itemView);
            departmentName = itemView.findViewById(R.id.department_name);
            departmentDescription = itemView.findViewById(R.id.department_description);
        }
    }
}