package com.schoolfinder.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.schoolfinder.app.data.ResultCallback;
import com.schoolfinder.app.data.SchoolRepository;
import com.schoolfinder.app.data.remote.Review;
import com.schoolfinder.app.data.remote.SchoolDetail;
import com.schoolfinder.app.di.ServiceLocator;
import com.schoolfinder.app.ui.Format;
import com.schoolfinder.app.ui.adapters.ProgramAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_SCHOOL_ID = "school_id";

    private SchoolRepository repo;
    private long schoolId;
    private boolean favorite;
    private MenuItem favoriteItem;

    private ScrollView content;
    private ProgressBar progress;
    private TextView message;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        repo = ServiceLocator.repository();
        schoolId = getIntent().getLongExtra(EXTRA_SCHOOL_ID, -1);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("School");
        toolbar.setNavigationIconTint(Color.WHITE);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.inflateMenu(R.menu.menu_detail);
        favoriteItem = toolbar.getMenu().findItem(R.id.action_favorite);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_favorite) {
                toggleFavorite();
                return true;
            }
            return false;
        });

        content = findViewById(R.id.content);
        progress = findViewById(R.id.progress);
        message = findViewById(R.id.message);

        findViewById(R.id.btn_submit_review).setOnClickListener(v -> submitReview());

        load();
    }

    private void load() {
        showLoading();
        repo.school(schoolId, new ResultCallback<SchoolDetail>() {
            @Override
            public void onSuccess(SchoolDetail data) {
                if (isFinishing() || data == null) return;
                favorite = data.favorite;
                updateFavoriteIcon();
                bind(data);
                showContent();
                loadReviews();
            }

            @Override
            public void onError(String msg) {
                if (isFinishing()) return;
                showMessage(msg);
            }
        });
    }

    private void bind(SchoolDetail s) {
        toolbar.setTitle(s.name);

        Chip category = findViewById(R.id.category_chip);
        category.setText(Format.categoryLabel(s.category));

        ((RatingBar) findViewById(R.id.rating)).setRating((float) s.averageRating);
        ((TextView) findViewById(R.id.rating_text))
                .setText(String.format(Locale.US, "%.1f (%d)", s.averageRating, s.ratingCount));

        ((TextView) findViewById(R.id.tuition)).setText(Format.money(s.tuitionFee, s.currency));

        TextView address = findViewById(R.id.address);
        String addr = joinNonBlank(", ", s.address, s.city, s.region);
        if (addr.isEmpty()) {
            address.setVisibility(View.GONE);
        } else {
            address.setVisibility(View.VISIBLE);
            address.setText(addr);
        }

        TextView description = findViewById(R.id.description);
        if (s.description != null && !s.description.trim().isEmpty()) {
            description.setVisibility(View.VISIBLE);
            description.setText(s.description);
        } else {
            description.setVisibility(View.GONE);
        }

        boolean hasHistory = s.history != null && !s.history.trim().isEmpty();
        findViewById(R.id.history_title).setVisibility(hasHistory ? View.VISIBLE : View.GONE);
        TextView history = findViewById(R.id.history);
        history.setVisibility(hasHistory ? View.VISIBLE : View.GONE);
        if (hasHistory) history.setText(s.history);

        MaterialButton navigate = findViewById(R.id.btn_navigate);
        if (s.latitude != null && s.longitude != null) {
            navigate.setVisibility(View.VISIBLE);
            navigate.setOnClickListener(v -> openMap(s));
        } else {
            navigate.setVisibility(View.GONE);
        }

        boolean hasPrograms = s.programs != null && !s.programs.isEmpty();
        findViewById(R.id.programmes_title).setVisibility(hasPrograms ? View.VISIBLE : View.GONE);
        RecyclerView programmes = findViewById(R.id.programmes);
        if (hasPrograms) {
            programmes.setVisibility(View.VISIBLE);
            programmes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            programmes.setAdapter(new ProgramAdapter(s.programs, s.currency));
        } else {
            programmes.setVisibility(View.GONE);
        }
    }

    private void loadReviews() {
        repo.reviews(schoolId, new ResultCallback<List<Review>>() {
            @Override
            public void onSuccess(List<Review> data) {
                if (isFinishing()) return;
                renderReviews(data);
            }

            @Override
            public void onError(String msg) {
                // Keep silent — reviews are secondary.
            }
        });
    }

    private void renderReviews(@Nullable List<Review> reviews) {
        LinearLayout container = findViewById(R.id.reviews_container);
        TextView empty = findViewById(R.id.reviews_empty);
        container.removeAllViews();
        if (reviews == null || reviews.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            return;
        }
        empty.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Review r : reviews) {
            View card = inflater.inflate(R.layout.item_review, container, false);
            TextView title = card.findViewById(R.id.review_title);
            RatingBar rating = card.findViewById(R.id.review_rating);
            TextView comment = card.findViewById(R.id.review_comment);

            title.setText(r.userDisplayName != null ? r.userDisplayName : "Anonymous");
            rating.setRating(r.rating);
            if (r.comment != null && !r.comment.isEmpty()) {
                comment.setVisibility(View.VISIBLE);
                comment.setText(r.comment);
            } else {
                comment.setVisibility(View.GONE);
            }
            container.addView(card);
        }
    }

    private void toggleFavorite() {
        final boolean was = favorite;
        favorite = !was;
        updateFavoriteIcon();
        ResultCallback<Void> cb = new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                // ok
            }

            @Override
            public void onError(String msg) {
                if (isFinishing()) return;
                favorite = was;
                updateFavoriteIcon();
                Toast.makeText(DetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        };
        if (was) repo.removeFavorite(schoolId, cb);
        else repo.addFavorite(schoolId, cb);
    }

    private void updateFavoriteIcon() {
        if (favoriteItem == null) return;
        Drawable d = ContextCompat.getDrawable(this,
                favorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        if (d != null) {
            d = DrawableCompat.wrap(d.mutate());
            DrawableCompat.setTint(d, Color.WHITE);
            favoriteItem.setIcon(d);
        }
    }

    private void submitReview() {
        int rating = (int) ((RatingBar) findViewById(R.id.composer_rating)).getRating();
        TextInputEditText input = findViewById(R.id.composer_comment);
        String comment = input.getText() == null ? "" : input.getText().toString();
        repo.submitReview(schoolId, rating, comment, new ResultCallback<Review>() {
            @Override
            public void onSuccess(Review data) {
                if (isFinishing()) return;
                input.setText("");
                Toast.makeText(DetailActivity.this, "Review submitted", Toast.LENGTH_SHORT).show();
                load();
            }

            @Override
            public void onError(String msg) {
                if (isFinishing()) return;
                Toast.makeText(DetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMap(SchoolDetail s) {
        String label = Uri.encode(s.name);
        Uri uri = Uri.parse("geo:" + s.latitude + "," + s.longitude
                + "?q=" + s.latitude + "," + s.longitude + "(" + label + ")");
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No map app available", Toast.LENGTH_SHORT).show();
        }
    }

    private static String joinNonBlank(String sep, String... parts) {
        List<String> kept = new ArrayList<>();
        for (String p : parts) {
            if (p != null && !p.trim().isEmpty()) kept.add(p);
        }
        return android.text.TextUtils.join(sep, kept);
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
