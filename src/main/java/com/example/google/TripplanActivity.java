package com.example.google;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TripplanActivity extends AppCompatActivity {

    private EditText titleEditText, dateEditText, destinationEditText, planEditText;
    private Button completeButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tripplan);

        // Firebase 초기화
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // 뷰 초기화
        titleEditText = findViewById(R.id.titleEditText);
        dateEditText = findViewById(R.id.dateEditText);
        destinationEditText = findViewById(R.id.destinationEditText);
        planEditText = findViewById(R.id.planEditText);
        completeButton = findViewById(R.id.completeButton);

        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTrip();
            }
        });
    }

    private void saveTrip() {
        // 사용자 아이디 가져오기
        String memberId = firebaseAuth.getCurrentUser().getUid();

        // 여행 정보 가져오기
        String title = titleEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String destination = destinationEditText.getText().toString().trim();
        String plan = planEditText.getText().toString().trim();

        // 필수 필드가 비어있는지 확인
        if (title.isEmpty() || date.isEmpty() || destination.isEmpty() || plan.isEmpty()) {
            // 필수 필드 중 하나라도 비어있으면 저장하지 않음
            return;
        }

        // 여행 정보를 Map 형태로 저장
        Map<String, Object> trip = new HashMap<>();
        trip.put("memberId", memberId);
        trip.put("title", title);
        trip.put("date", date);
        trip.put("destination", destination);
        trip.put("plan", plan);

        // Firestore에 여행 일정 정보 저장
        firestore.collection("trips").add(trip)
                .addOnSuccessListener(documentReference -> {
                    // 저장 성공
                    Intent intent = new Intent(TripplanActivity.this, TripActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // 저장 실패
                    // TODO: 저장 실패 시의 동작을 정의하세요.
                });
    }
}
