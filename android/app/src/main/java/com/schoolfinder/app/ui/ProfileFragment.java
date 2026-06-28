package com.schoolfinder.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.schoolfinder.app.LoginActivity;
import com.schoolfinder.app.R;
import com.schoolfinder.app.data.ResultCallback;
import com.schoolfinder.app.data.Session;
import com.schoolfinder.app.data.remote.Review;
import com.schoolfinder.app.di.ServiceLocator;

import java.util.List;

public class ProfileFragment extends Fragment {

    private LinearLayout reviewsContainer;
    private TextView reviewsEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Session session = ServiceLocator.sessionStore().load();
        if (session == null) {
            goLogin();
            return;
        }

        TextView name = view.findViewById(R.id.profile_name);
        TextView role = view.findViewById(R.id.profile_role);
        TextView idText = view.findViewById(R.id.profile_id);
        MaterialCardView adminCard = view.findViewById(R.id.admin_card);
        reviewsContainer = view.findViewById(R.id.reviews_container);
        reviewsEmpty = view.findViewById(R.id.reviews_empty);
        MaterialButton logout = view.findViewById(R.id.btn_logout);

        name.setText(session.displayName);
        role.setText(session.isAdmin() ? "Administrator" : "Student");
        idText.setText("ID: " + session.uid);
        adminCard.setVisibility(session.isAdmin() ? View.VISIBLE : View.GONE);

        logout.setOnClickListener(v -> {
            ServiceLocator.sessionStore().clear();
            goLogin();
        });

        loadReviews();
    }

    private void loadReviews() {
        ServiceLocator.repository().myReviews(new ResultCallback<List<Review>>() {
            @Override
            public void onSuccess(List<Review> data) {
                if (!isAdded()) return;
                renderReviews(data);
            }

            @Override
            public void onError(String msg) {
                // Leave the "no reviews" placeholder in place.
            }
        });
    }

    private void renderReviews(@Nullable List<Review> reviews) {
        reviewsContainer.removeAllViews();
        if (reviews == null || reviews.isEmpty()) {
            reviewsEmpty.setVisibility(View.VISIBLE);
            return;
        }
        reviewsEmpty.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (Review r : reviews) {
            View card = inflater.inflate(R.layout.item_review, reviewsContainer, false);
            TextView title = card.findViewById(R.id.review_title);
            RatingBar rating = card.findViewById(R.id.review_rating);
            TextView comment = card.findViewById(R.id.review_comment);

            title.setText(r.schoolName != null ? r.schoolName : "School #" + r.schoolId);
            rating.setRating(r.rating);
            if (r.comment != null && !r.comment.isEmpty()) {
                comment.setVisibility(View.VISIBLE);
                comment.setText(r.comment);
            } else {
                comment.setVisibility(View.GONE);
            }
            reviewsContainer.addView(card);
        }
    }

    private void goLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
