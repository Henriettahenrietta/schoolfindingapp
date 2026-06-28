package com.schoolfinder.app;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.schoolfinder.app.data.ResultCallback;
import com.schoolfinder.app.data.remote.CompareResponse;
import com.schoolfinder.app.data.remote.SchoolDetail;
import com.schoolfinder.app.di.ServiceLocator;
import com.schoolfinder.app.ui.Format;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CompareActivity extends AppCompatActivity {

    public static final String EXTRA_IDS = "ids";

    private ScrollView content;
    private ProgressBar progress;
    private TextView message;
    private LinearLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIconTint(Color.WHITE);
        toolbar.setNavigationOnClickListener(v -> finish());

        content = findViewById(R.id.content);
        progress = findViewById(R.id.progress);
        message = findViewById(R.id.message);
        table = findViewById(R.id.table);

        long[] idsArray = getIntent().getLongArrayExtra(EXTRA_IDS);
        List<Long> ids = new ArrayList<>();
        if (idsArray != null) {
            for (long id : idsArray) ids.add(id);
        }
        if (ids.isEmpty()) {
            showMessage("Nothing to compare.");
            return;
        }
        load(ids);
    }

    private void load(List<Long> ids) {
        showLoading();
        ServiceLocator.repository().compare(ids, new ResultCallback<CompareResponse>() {
            @Override
            public void onSuccess(CompareResponse data) {
                if (isFinishing() || data == null || data.schools == null) {
                    showMessage("Could not load comparison.");
                    return;
                }
                buildTable(data);
                showContent();
            }

            @Override
            public void onError(String msg) {
                if (isFinishing()) return;
                showMessage(msg);
            }
        });
    }

    private void buildTable(CompareResponse data) {
        table.removeAllViews();
        List<SchoolDetail> schools = data.schools;

        int cheapest = indexOfId(schools, data.cheapestSchoolId);
        int highest = indexOfId(schools, data.highestRatedSchoolId);

        List<String> names = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        List<String> cities = new ArrayList<>();
        List<String> tuitions = new ArrayList<>();
        List<String> ratings = new ArrayList<>();
        List<String> programs = new ArrayList<>();
        for (SchoolDetail s : schools) {
            names.add(s.name);
            categories.add(Format.categoryLabel(s.category));
            cities.add(s.city != null ? s.city : "—");
            tuitions.add(Format.money(s.tuitionFee, s.currency));
            ratings.add(String.format(Locale.US, "%.1f (%d)", s.averageRating, s.ratingCount));
            programs.add(String.valueOf(s.programs != null ? s.programs.size() : 0));
        }

        table.addView(row("", names, true, -1));
        table.addView(divider());
        table.addView(row("Category", categories, false, -1));
        table.addView(row("City", cities, false, -1));
        table.addView(row("Tuition", tuitions, false, cheapest));
        table.addView(row("Rating", ratings, false, highest));
        table.addView(row("Programs", programs, false, -1));
    }

    private LinearLayout row(String label, List<String> values, boolean header, int highlightIndex) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(6), 0, dp(6));

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setWidth(dp(96));
        labelView.setTypeface(null, Typeface.BOLD);
        labelView.setTextColor(ContextCompat.getColor(this, R.color.muted));
        row.addView(labelView);

        for (int i = 0; i < values.size(); i++) {
            boolean best = highlightIndex >= 0 && i == highlightIndex;
            TextView cell = new TextView(this);
            cell.setText(values.get(i));
            cell.setWidth(dp(140));
            cell.setPadding(dp(4), 0, dp(4), 0);
            cell.setTypeface(null, (header || best) ? Typeface.BOLD : Typeface.NORMAL);
            cell.setTextColor(ContextCompat.getColor(this, best ? R.color.maroon : R.color.on_surface));
            row.addView(cell);
        }
        return row;
    }

    private View divider() {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1)));
        v.setBackgroundColor(ContextCompat.getColor(this, R.color.divider));
        return v;
    }

    private static int indexOfId(List<SchoolDetail> schools, @Nullable Long id) {
        if (id == null) return -1;
        for (int i = 0; i < schools.size(); i++) {
            if (schools.get(i).id == id) return i;
        }
        return -1;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void showLoading() {
        progress.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
    }

    private void showContent() {
        progress.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
        message.setVisibility(View.GONE);
    }

    private void showMessage(String text) {
        progress.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
        message.setText(text);
    }
}
