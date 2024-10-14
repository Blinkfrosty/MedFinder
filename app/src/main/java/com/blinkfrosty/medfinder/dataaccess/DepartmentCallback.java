package com.blinkfrosty.medfinder.dataaccess;

import com.blinkfrosty.medfinder.dataaccess.datastructure.Department;

import java.util.List;

public interface DepartmentCallback {
    void onDepartmentRetrieved(Department department);
    void onDepartmentsRetrieved(List<Department> departments);
    void onError(Exception e);
}