package com.blinkfrosty.medfinder.dataaccess;

import android.content.Context;

import androidx.annotation.NonNull;

import com.blinkfrosty.medfinder.dataaccess.datastructure.Department;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DepartmentDataAccessHelper extends DatabaseHelperBase {

    private final DatabaseReference departmentsReference;
    private static final String DEPARTMENTS = "departments";

    public DepartmentDataAccessHelper(Context context) {
        super(context);
        departmentsReference = getDatabaseReference().child(DEPARTMENTS);
    }

    public void getDepartmentOnce(String departmentId, DepartmentCallback callback) {
        departmentsReference.child(departmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Department department = dataSnapshot.getValue(Department.class);
                if (department != null) {
                    callback.onDepartmentRetrieved(department);
                } else {
                    callback.onError(new Exception("Department not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void addDepartmentDataChangeListener(String departmentId, DepartmentCallback callback) {
        departmentsReference.child(departmentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Department department = dataSnapshot.getValue(Department.class);
                if (department != null) {
                    callback.onDepartmentRetrieved(department);
                } else {
                    callback.onError(new Exception("Department not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void getAllDepartments(DepartmentCallback callback) {
        departmentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Department> departments = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Department department = snapshot.getValue(Department.class);
                    if (department != null) {
                        departments.add(department);
                    }
                }
                callback.onDepartmentsRetrieved(departments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }
}
