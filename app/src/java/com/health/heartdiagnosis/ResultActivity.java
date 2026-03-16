package com.health.heartdiagnosis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private CircularProgressView circularProgress;
    private TextView tvScore, tvRiskLevel, tvRiskDesc, tvDoctorAdvice;
    private LinearLayout llBreakdown, llFindings, llTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get answers
        int[] answers = getIntent().getIntArrayExtra("answers");
        if (answers == null) {
            finish();
            return;
        }

        bindViews();

        // Calculate risk
        List<Question> questions = QuestionBank.getQuestions();
        RiskEngine.RiskResult result = RiskEngine.calculate(questions, answers);

        // Animate in after short delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            displayResult(result);
        }, 300);

        // Buttons
        findViewById(R.id.btnRetake).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        findViewById(R.id.btnShare).setOnClickListener(v -> shareResults(result));
    }

    private void bindViews() {
        circularProgress = findViewById(R.id.circularProgress);
        tvScore          = findViewById(R.id.tvScore);
        tvRiskLevel      = findViewById(R.id.tvRiskLevel);
        tvRiskDesc       = findViewById(R.id.tvRiskDesc);
        tvDoctorAdvice   = findViewById(R.id.tvDoctorAdvice);
        llBreakdown      = findViewById(R.id.llBreakdown);
        llFindings       = findViewById(R.id.llFindings);
        llTips           = findViewById(R.id.llTips);
    }

    private void displayResult(RiskEngine.RiskResult result) {
        int score = (int) Math.round(result.totalScore);

        // Animated score counter
        animateScore(0, score, 1200);

        // Circular gauge
        circularProgress.setProgress(score, result.riskColor);

        // Risk level
        tvRiskLevel.setText(result.riskLevel);
        tvRiskLevel.setTextColor(getResources().getColor(result.riskColor));
        tvRiskDesc.setText(result.riskDescription);
        tvDoctorAdvice.setText(result.doctorAdvice);

        // Breakdown
        llBreakdown.removeAllViews();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            for (RiskEngine.CategoryScore cat : result.breakdown) {
                addBreakdownItem(cat);
            }
        }, 600);

        // Findings
        llFindings.removeAllViews();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            for (String finding : result.findings) {
                addFindingItem(finding);
            }
        }, 900);

        // Tips
        llTips.removeAllViews();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            for (RiskEngine.Tip tip : result.tips) {
                addTipItem(tip);
            }
        }, 1100);
    }

    private void addBreakdownItem(RiskEngine.CategoryScore cat) {
        View v = LayoutInflater.from(this).inflate(R.layout.item_breakdown, llBreakdown, false);
        ((TextView) v.findViewById(R.id.tvCatEmoji)).setText(cat.emoji);
        ((TextView) v.findViewById(R.id.tvCatName)).setText(cat.name);
        ((TextView) v.findViewById(R.id.tvCatScore)).setText((int)cat.score + "%");
        ProgressBar pb = v.findViewById(R.id.pbCat);
        pb.setProgress((int) cat.score);

        // Color based on score
        int colorRes;
        if (cat.score < 25) colorRes = R.color.risk_low;
        else if (cat.score < 50) colorRes = R.color.risk_moderate;
        else if (cat.score < 75) colorRes = R.color.risk_high;
        else colorRes = R.color.risk_very_high;

        ((TextView) v.findViewById(R.id.tvCatScore))
                .setTextColor(getResources().getColor(colorRes));

        fadeInView(v);
        llBreakdown.addView(v);
    }

    private void addFindingItem(String text) {
        View v = LayoutInflater.from(this).inflate(R.layout.item_finding, llFindings, false);
        // Split emoji from text
        if (text.length() > 2) {
            ((TextView) v.findViewById(R.id.tvFindingIcon)).setText(text.substring(0, 2));
            ((TextView) v.findViewById(R.id.tvFindingText)).setText(text.substring(2).trim());
        } else {
            ((TextView) v.findViewById(R.id.tvFindingText)).setText(text);
        }
        fadeInView(v);
        llFindings.addView(v);
    }

    private void addTipItem(RiskEngine.Tip tip) {
        View v = LayoutInflater.from(this).inflate(R.layout.item_tip, llTips, false);
        ((TextView) v.findViewById(R.id.tvTipEmoji)).setText(tip.emoji);
        ((TextView) v.findViewById(R.id.tvTipTitle)).setText(tip.title);
        ((TextView) v.findViewById(R.id.tvTipBody)).setText(tip.body);
        fadeInView(v);
        llTips.addView(v);
    }

    private void animateScore(int from, int to, int duration) {
        android.animation.ValueAnimator anim = android.animation.ValueAnimator.ofInt(from, to);
        anim.setDuration(duration);
        anim.setInterpolator(new android.view.animation.DecelerateInterpolator(1.5f));
        anim.addUpdateListener(animation -> {
            tvScore.setText(String.valueOf(animation.getAnimatedValue()));
        });
        anim.start();
    }

    private void fadeInView(View v) {
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(400);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

    private void shareResults(RiskEngine.RiskResult result) {
        String shareText = "HeartScan Assessment Results\n\n" +
                "Risk Score: " + (int) result.totalScore + "%\n" +
                "Risk Level: " + result.riskLevel + "\n\n" +
                result.riskDescription + "\n\n" +
                "Doctor Advice: " + result.doctorAdvice + "\n\n" +
                "⚠ This is for informational purposes only. Not a medical diagnosis.";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My HeartScan Results");
        startActivity(Intent.createChooser(shareIntent, "Share Results"));
    }
}
