package com.schoolfinder.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.schoolfinder.app.R;
import com.schoolfinder.app.data.remote.SchoolSummary;
import com.schoolfinder.app.ui.Format;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.VH> {

    public interface OnSchoolClick {
        void open(long schoolId);
    }

    public interface OnCompareToggle {
        void toggle(long schoolId, boolean checked);
    }

    private final List<SchoolSummary> items = new ArrayList<>();
    private final OnSchoolClick onClick;
    private final boolean showCompare;
    @Nullable private final Set<Long> selected;
    @Nullable private final OnCompareToggle onToggle;

    public SchoolAdapter(OnSchoolClick onClick) {
        this(onClick, false, null, null);
    }

    public SchoolAdapter(OnSchoolClick onClick, boolean showCompare,
                         @Nullable Set<Long> selected, @Nullable OnCompareToggle onToggle) {
        this.onClick = onClick;
        this.showCompare = showCompare;
        this.selected = selected;
        this.onToggle = onToggle;
    }

    public void submit(@Nullable List<SchoolSummary> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_school, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        SchoolSummary s = items.get(position);

        h.name.setText(s.name);
        String label = Format.categoryLabel(s.category);
        h.subtitle.setText(s.city != null ? label + " • " + s.city : label);
        h.rating.setRating((float) s.averageRating);
        h.ratingText.setText(String.format(Locale.US, "%.1f (%d)", s.averageRating, s.ratingCount));
        h.tuition.setText(Format.money(s.tuitionFee, s.currency));

        Glide.with(h.cover)
                .load(s.coverImageUrl)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .centerCrop()
                .into(h.cover);

        if (showCompare && selected != null && onToggle != null) {
            h.check.setVisibility(View.VISIBLE);
            h.check.setOnCheckedChangeListener(null);
            h.check.setChecked(selected.contains(s.id));
            h.check.setOnCheckedChangeListener((btn, isChecked) -> onToggle.toggle(s.id, isChecked));
        } else {
            h.check.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v -> onClick.open(s.id));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView cover;
        final TextView name;
        final TextView subtitle;
        final RatingBar rating;
        final TextView ratingText;
        final TextView tuition;
        final CheckBox check;

        VH(@NonNull View v) {
            super(v);
            cover = v.findViewById(R.id.cover);
            name = v.findViewById(R.id.name);
            subtitle = v.findViewById(R.id.subtitle);
            rating = v.findViewById(R.id.rating);
            ratingText = v.findViewById(R.id.rating_text);
            tuition = v.findViewById(R.id.tuition);
            check = v.findViewById(R.id.compare_check);
        }
    }
}
