package com.blinkfrosty.medfinder.ui.department;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.blinkfrosty.medfinder.databinding.FragmentSearchByDepartmentBinding;

public class SearchByDepartmentFragment extends Fragment {

    private FragmentSearchByDepartmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchByDepartmentViewModel searchByDepartmentViewModel =
                new ViewModelProvider(this).get(SearchByDepartmentViewModel.class);

        binding = FragmentSearchByDepartmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSearchByDepartment;
        searchByDepartmentViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}