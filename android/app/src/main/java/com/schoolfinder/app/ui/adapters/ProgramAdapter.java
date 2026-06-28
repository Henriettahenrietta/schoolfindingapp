package com.schoolfinder.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.schoolfinder.app.R;
import com.schoolfinder.app.data.remote.Program;
import com.schoolfinder.app.ui.Format;

import java.util.ArrayList;
import java.util.List;

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.VH> {

    private final List<Program> items = new ArrayList<>();
    private final String currency;

    public ProgramAdapter(List<Program> programs, String currency) {
        this.currency = currency;
        if (programs != null) items.addAll(programs);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_program, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Program p = items.get(position);

        if (p.faculty != null && !p.faculty.isEmpty()) {
            h.faculty.setVisibility(View.VISIBLE);
            h.faculty.setText(p.faculty);
        } else {
            h.faculty.setVisibility(View.GONE);
        }
        h.name.setText(p.name);

        List<String> parts = new ArrayList<>();
        if (p.level != null && !p.level.isEmpty()) parts.add(p.level);
        if (p.durationMonths != null) parts.add(p.durationMonths + " months");
        if (p.tuitionFee != null) parts.add(Format.money(p.tuitionFee, currency));
        h.meta.setText(android.text.TextUtils.join(" • ", parts));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView faculty;
        final TextView name;
        final TextView meta;

        VH(@NonNull View v) {
            super(v);
            faculty = v.findViewById(R.id.program_faculty);
            name = v.findViewById(R.id.program_name);
            meta = v.findViewById(R.id.program_meta);
        }
    }
}
