package com.blinkfrosty.medfinder.ui.department;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blinkfrosty.medfinder.R;
import com.blinkfrosty.medfinder.dataaccess.DepartmentCallback;
import com.blinkfrosty.medfinder.dataaccess.DepartmentDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Department;

import java.util.List;

public class SearchByDepartmentFragment extends Fragment {

    private RecyclerView departmentRecyclerView;
    private DepartmentAdapter departmentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_by_department, container, false);

        departmentRecyclerView = view.findViewById(R.id.department_list);
        departmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DepartmentDataAccessHelper departmentDataAccessHelper = new DepartmentDataAccessHelper(getContext());
        departmentDataAccessHelper.getAllDepartments(new DepartmentCallback() {
            @Override
            public void onDepartmentsRetrieved(List<Department> departments) {
                departmentAdapter = new DepartmentAdapter(departments);
                departmentRecyclerView.setAdapter(departmentAdapter);
            }

            @Override
            public void onDepartmentRetrieved(Department department) {
                // Not used in this context
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "An error occurred while retrieving departments", Toast.LENGTH_SHORT).show();
                Log.e("SearchByDepartmentFragment", "An error occurred while retrieving departments", e);
            }
        });

        return view;
    }
}