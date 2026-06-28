package com.schoolfinder.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.schoolfinder.app.DetailActivity;
import com.schoolfinder.app.R;
import com.schoolfinder.app.data.ResultCallback;
import com.schoolfinder.app.data.SchoolRepository;
import com.schoolfinder.app.data.remote.SchoolSummary;
import com.schoolfinder.app.di.ServiceLocator;
import com.schoolfinder.app.ui.adapters.SchoolAdapter;

import java.util.List;

public class FavoritesFragment extends Fragment {

    private SchoolRepository repo;
    private SchoolAdapter adapter;
    private RecyclerView list;
    private ProgressBar progress;
    private TextView message;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        repo = ServiceLocator.repository();

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Favourites");

        list = view.findViewById(R.id.list);
        progress = view.findViewById(R.id.progress);
        message = view.findViewById(R.id.message);

        adapter = new SchoolAdapter(this::openSchool);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        showLoading();
        repo.favorites(new ResultCallback<List<SchoolSummary>>() {
            @Override
            public void onSuccess(List<SchoolSummary> data) {
                if (!isAdded()) return;
                if (data == null || data.isEmpty()) {
                    showMessage("No favourites yet. Tap the heart on a school to save it.");
                } else {
                    adapter.submit(data);
                    showList();
                }
            }

            @Override
            public void onError(String msg) {
                if (!isAdded()) return;
                showMessage(msg);
            }
        });
    }

    private void openSchool(long id) {
        Intent i = new Intent(requireContext(), DetailActivity.class);
        i.putExtra(DetailActivity.EXTRA_SCHOOL_ID, id);
        startActivity(i);
    }

    private void showLoading() {
        progress.setVisibility(View.VISIBLE);
        message.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
    }

    private void showMessage(String text) {
        progress.setVisibility(View.GONE);
        list.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
        message.setText(text);
    }

    private void showList() {
        progress.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
    }
}
