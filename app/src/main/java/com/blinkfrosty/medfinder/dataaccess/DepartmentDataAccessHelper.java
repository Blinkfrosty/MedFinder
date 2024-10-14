package com.blinkfrosty.medfinder.dataaccess;

import android.content.Context;

import androidx.annotation.NonNull;

import com.blinkfrosty.medfinder.dataaccess.datastructure.Department;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentDataAccessHelper extends DatabaseHelperBase {

    private static DepartmentDataAccessHelper instance;
    private final DatabaseReference departmentsReference;
    private static final String DEPARTMENTS = "departments";
    private final Map<String, ValueEventListener> departmentListenersMap;
    private final List<ValueEventListener> departmentListeners;

    private DepartmentDataAccessHelper(Context context) {
        super(context);
        departmentsReference = getDatabaseReference().child(DEPARTMENTS);
        departmentListenersMap = new HashMap<>();
        departmentListeners = new ArrayList<>();
    }

    public static synchronized DepartmentDataAccessHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DepartmentDataAccessHelper(context.getApplicationContext());
        }
        return instance;
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
        ValueEventListener listener = new ValueEventListener() {
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
        };

        departmentsReference.child(departmentId).addValueEventListener(listener);
        departmentListenersMap.put(departmentId, listener);
    }

    public void getAllDepartments(DepartmentCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
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
        };

        departmentsReference.addValueEventListener(listener);
        departmentListeners.add(listener);
    }

    public void removeDepartmentDataChangeListeners() {
        for (Map.Entry<String, ValueEventListener> entry : departmentListenersMap.entrySet()) {
            departmentsReference.child(entry.getKey()).removeEventListener(entry.getValue());
        }
        departmentListenersMap.clear();

        for (ValueEventListener listener : departmentListeners) {
            departmentsReference.removeEventListener(listener);
        }
        departmentListeners.clear();
    }
}