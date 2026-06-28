package com.schoolfinder.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.schoolfinder.app.DetailActivity;
import com.schoolfinder.app.CompareActivity;
import com.schoolfinder.app.R;
import com.schoolfinder.app.data.SchoolRepository;
import com.schoolfinder.app.data.ResultCallback;
import com.schoolfinder.app.data.remote.Meta;
import com.schoolfinder.app.data.remote.SchoolSummary;
import com.schoolfinder.app.di.ServiceLocator;
import com.schoolfinder.app.ui.adapters.SchoolAdapter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    private SchoolRepository repo;
    private final Set<Long> selected = new LinkedHashSet<>();
    @Nullable private String category = null;
    private boolean metaLoaded = false;

    private SchoolAdapter adapter;
    private RecyclerView list;
    private ProgressBar progress;
    private TextView message;
    private ChipGroup categoryGroup;
    private TextInputEditText searchInput;
    private MaterialButton compareBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        repo = ServiceLocator.repository();

        list = view.findViewById(R.id.list);
        progress = view.findViewById(R.id.progress);
        message = view.findViewById(R.id.message);
        categoryGroup = view.findViewById(R.id.category_group);
        searchInput = view.findViewById(R.id.input_search);
        compareBtn = view.findViewById(R.id.btn_compare);

        adapter = new SchoolAdapter(this::openSchool, true, selected, this::toggleCompare);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setAdapter(adapter);

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                refresh();
                return true;
            }
            return false;
        });

        compareBtn.setOnClickListener(v -> openCompare());

        loadMetaThenSearch();
    }

    private void loadMetaThenSearch() {
        showLoading();
        repo.meta(new ResultCallback<Meta>() {
            @Override
            public void onSuccess(Meta data) {
                if (!isAdded()) return;
                buildCategoryChips(data);
                refresh();
            }

            @Override
            public void onError(String msg) {
                if (!isAdded()) return;
                refresh();
            }
        });
    }

    private void buildCategoryChips(@Nullable Meta meta) {
        if (metaLoaded) return;
        metaLoaded = true;
        categoryGroup.removeAllViews();

        Chip all = new Chip(requireContext());
        all.setText("All");
        all.setCheckable(true);
        all.setChecked(true);
        all.setTag(null);
        categoryGroup.addView(all);

        if (meta != null && meta.categories != null) {
            for (String c : meta.categories) {
                Chip chip = new Chip(requireContext());
                chip.setText(Format.categoryLabel(c));
                chip.setCheckable(true);
                chip.setTag(c);
                categoryGroup.addView(chip);
            }
        }

        categoryGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                category = null;
            } else {
                View checked = group.findViewById(checkedIds.get(0));
                category = checked == null ? null : (String) checked.getTag();
            }
            refresh();
        });
    }

    private void refresh() {
        showLoading();
        String query = searchInput.getText() == null ? "" : searchInput.getText().toString();
        repo.search(query, category, null, null, null, "name", new ResultCallback<List<SchoolSummary>>() {
            @Override
            public void onSuccess(List<SchoolSummary> data) {
                if (!isAdded()) return;
                if (data == null || data.isEmpty()) {
                    showMessage("No schools match your search.");
                } else {
                    adapter.submit(data);
                    showList();
                }
            }

            @Override
            public void onError(String msg) {
                if (!isAdded()) return;
                showMessage(msg + "\n\nIs the backend reachable?");
            }
        });
    }

    private void toggleCompare(long id, boolean checked) {
        if (checked) {
            if (selected.size() >= 4 && !selected.contains(id)) {
                // Cap at 4 — revert the checkbox.
                adapter.notifyDataSetChanged();
            } else {
                selected.add(id);
            }
        } else {
            selected.remove(id);
        }
        updateCompareButton();
    }

    private void updateCompareButton() {
        if (selected.size() >= 2) {
            compareBtn.setVisibility(View.VISIBLE);
            compareBtn.setText("Compare " + selected.size());
        } else {
            compareBtn.setVisibility(View.GONE);
        }
    }

    private void openSchool(long id) {
        Intent i = new Intent(requireContext(), DetailActivity.class);
        i.putExtra(DetailActivity.EXTRA_SCHOOL_ID, id);
        startActivity(i);
    }

    private void openCompare() {
        long[] ids = new long[selected.size()];
        int i = 0;
        for (Long id : selected) ids[i++] = id;
        Intent intent = new Intent(requireContext(), CompareActivity.class);
        intent.putExtra(CompareActivity.EXTRA_IDS, ids);
        startActivity(intent);
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
