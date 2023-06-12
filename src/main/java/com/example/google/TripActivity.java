package com.example.google;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class TripActivity extends AppCompatActivity {

    private LinearLayout tripLayout;
    private FirebaseFirestore firestore;
    private Button mapButton;
    private Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        tripLayout = findViewById(R.id.tripLayout);
        firestore = FirebaseFirestore.getInstance();
        mapButton = findViewById(R.id.mapButton);
        createButton = findViewById(R.id.createButton);

        // 불러온 trip 정보를 표시
        displayTripInfo();

        // 지도 버튼 클릭 시 MainActivity로 이동
        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(TripActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // 일정 작성 버튼 클릭 시 TripplanActivity로 이동
        createButton.setOnClickListener(v -> {
            Intent intent = new Intent(TripActivity.this, TripplanActivity.class);
            startActivity(intent);
        });
    }

    private void displayTripInfo() {
        // 사용자 아이디 가져오기
        String memberId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firestore에서 해당 사용자의 trip 정보 가져오기
        firestore.collection("trips")
                .whereEqualTo("memberId", memberId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Firestore에서 가져온 trip 정보를 화면에 표시
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String date = document.getString("date");
                            String destination = document.getString("destination");
                            String plan = document.getString("plan");

                            // 칸에 정보를 추가하여 화면에 표시
                            addTripInfoToLayout(title, date, destination, plan);
                        }
                    } else {
                        Log.d("TripActivity", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void addTripInfoToLayout(String title, String date, String destination, String plan) {
        LinearLayout tripInfoLayout = new LinearLayout(this);
        tripInfoLayout.setOrientation(LinearLayout.VERTICAL);

        TextView titleTextView = new TextView(this);
        titleTextView.setText("여행제목: " + title);
        tripInfoLayout.addView(titleTextView);

        TextView dateTextView = new TextView(this);
        dateTextView.setText("날짜: " + date);
        tripInfoLayout.addView(dateTextView);

        TextView destinationTextView = new TextView(this);
        destinationTextView.setText("여행지: " + destination);
        tripInfoLayout.addView(destinationTextView);

        TextView planTextView = new TextView(this);
        planTextView.setText("여행일정: " + plan);
        tripInfoLayout.addView(planTextView);

        //구분선
        View separator = new View(this);
        separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        separator.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1));
        tripInfoLayout.addView(separator);

        // 칸을 LinearLayout에 추가
        tripLayout.addView(tripInfoLayout);
    }
}
