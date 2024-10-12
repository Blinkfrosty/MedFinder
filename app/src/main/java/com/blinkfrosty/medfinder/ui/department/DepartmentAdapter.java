package com.blinkfrosty.medfinder.ui.department;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.department_list_item, parent, false);
        return new DepartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartmentViewHolder holder, int position) {
        Department department = departments.get(position);
        holder.departmentName.setText(department.getName());
        holder.departmentDescription.setText(department.getDescription());
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