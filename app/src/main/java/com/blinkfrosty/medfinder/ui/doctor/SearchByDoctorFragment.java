package com.blinkfrosty.medfinder.ui.doctor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.blinkfrosty.medfinder.databinding.FragmentSearchByDoctorBinding;

public class SearchByDoctorFragment extends Fragment {

    private FragmentSearchByDoctorBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchByDoctorViewModel searchByDoctorViewModel =
                new ViewModelProvider(this).get(SearchByDoctorViewModel.class);

        binding = FragmentSearchByDoctorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSearchByDoctor;
        searchByDoctorViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}