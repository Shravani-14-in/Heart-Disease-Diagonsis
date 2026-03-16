package com.health.heartdiagnosis;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class QuestionnaireActivity extends AppCompatActivity {

    private List<Question> questions;
    private int[] answers;
    private int currentIndex = 0;

    // Views
    private TextView tvSection, tvProgress, tvPercent;
    private TextView tvQuestion, tvSubtitle;
    private LinearLayout llOptions, llSlider, llInfo;
    private TextInputLayout tilInput;
    private TextInputEditText etInput;
    private SeekBar seekBar;
    private TextView tvSliderValue, tvSliderUnit, tvSliderMin, tvSliderMax;
    private TextView tvInfo;
    private ProgressBar progressBar;
    private MaterialButton btnNext, btnPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        questions = QuestionBank.getQuestions();
        answers   = new int[questions.size()];
        // Pre-fill slider defaults
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            if (q.getInputType() == Question.InputType.SLIDER) {
                answers[i] = q.getSliderDefault();
            }
        }

        bindViews();
        renderQuestion(currentIndex, false);

        btnNext.setOnClickListener(v -> handleNext());
        btnPrev.setOnClickListener(v -> handlePrev());
        findViewById(R.id.btnBack).setOnClickListener(v -> handlePrev());
    }

    private void bindViews() {
        tvSection      = findViewById(R.id.tvSection);
        tvProgress     = findViewById(R.id.tvProgress);
        tvPercent      = findViewById(R.id.tvPercent);
        tvQuestion     = findViewById(R.id.tvQuestion);
        tvSubtitle     = findViewById(R.id.tvSubtitle);
        llOptions      = findViewById(R.id.llOptions);
        llSlider       = findViewById(R.id.llSlider);
        llInfo         = findViewById(R.id.llInfo);
        tilInput       = findViewById(R.id.tilInput);
        etInput        = findViewById(R.id.etInput);
        seekBar        = findViewById(R.id.seekBar);
        tvSliderValue  = findViewById(R.id.tvSliderValue);
        tvSliderUnit   = findViewById(R.id.tvSliderUnit);
        tvSliderMin    = findViewById(R.id.tvSliderMin);
        tvSliderMax    = findViewById(R.id.tvSliderMax);
        tvInfo         = findViewById(R.id.tvInfo);
        progressBar    = findViewById(R.id.progressBar);
        btnNext        = findViewById(R.id.btnNext);
        btnPrev        = findViewById(R.id.btnPrev);
    }

    private void renderQuestion(int index, boolean fromNext) {
        Question q = questions.get(index);

        // Animate transition
        animateFadeIn();

        // Progress
        int pct = (int) ((index / (float) questions.size()) * 100);
        progressBar.setProgress(pct);
        tvPercent.setText(pct + "%");
        tvSection.setText(q.getSectionEmoji() + "  " + q.getSection().toUpperCase());
        tvProgress.setText("Question " + (index + 1) + " of " + questions.size());

        // Question text
        tvQuestion.setText(q.getQuestion());

        // Subtitle
        if (q.getSubtitle() != null && !q.getSubtitle().isEmpty()) {
            tvSubtitle.setText(q.getSubtitle());
            tvSubtitle.setVisibility(View.VISIBLE);
        } else {
            tvSubtitle.setVisibility(View.GONE);
        }

        // Info
        if (q.getInfoText() != null && !q.getInfoText().isEmpty()) {
            tvInfo.setText(q.getInfoText());
            llInfo.setVisibility(View.VISIBLE);
        } else {
            llInfo.setVisibility(View.GONE);
        }

        // Hide all input types first
        llOptions.setVisibility(View.GONE);
        llSlider.setVisibility(View.GONE);
        tilInput.setVisibility(View.GONE);

        switch (q.getInputType()) {
            case OPTIONS:
                renderOptions(q, index);
                break;
            case SLIDER:
                renderSlider(q, index);
                break;
            case NUMBER:
                renderNumber(q);
                break;
        }

        // Buttons
        btnPrev.setVisibility(index > 0 ? View.VISIBLE : View.INVISIBLE);
        boolean isLast = (index == questions.size() - 1);
        btnNext.setText(isLast ? "Get Results" : "Next");
    }

    private void renderOptions(Question q, int index) {
        llOptions.setVisibility(View.VISIBLE);
        llOptions.removeAllViews();

        List<String> opts = q.getOptions();
        int selected = answers[index]; // saved answer

        for (int i = 0; i < opts.size(); i++) {
            final int optIdx = i;

            // Create button programmatically
            TextView btn = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = dpToPx(10);
            btn.setLayoutParams(lp);
            btn.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));
            btn.setText(opts.get(i));
            btn.setTextSize(15f);
            btn.setClickable(true);
            btn.setFocusable(true);

            updateOptionStyle(btn, optIdx == selected);

            btn.setOnClickListener(v -> {
                answers[index] = optIdx;
                // Refresh all buttons
                for (int j = 0; j < llOptions.getChildCount(); j++) {
                    View child = llOptions.getChildAt(j);
                    if (child instanceof TextView) {
                        updateOptionStyle((TextView) child, j == optIdx);
                    }
                }
                // Auto-advance after short delay
                btn.postDelayed(this::handleNext, 250);
            });

            llOptions.addView(btn);
        }
    }

    private void updateOptionStyle(TextView btn, boolean selected) {
        if (selected) {
            btn.setBackgroundResource(R.drawable.bg_option_selected);
            btn.setTextColor(getResources().getColor(R.color.white));
        } else {
            btn.setBackgroundResource(R.drawable.bg_option_normal);
            btn.setTextColor(getResources().getColor(R.color.text_primary));
        }
    }

    private void renderSlider(Question q, int index) {
        llSlider.setVisibility(View.VISIBLE);

        int min = q.getSliderMin();
        int max = q.getSliderMax();
        int def = answers[index];

        tvSliderUnit.setText(q.getSliderUnit());
        tvSliderMin.setText(String.valueOf(min));
        tvSliderMax.setText(String.valueOf(max));
        tvSliderValue.setText(String.valueOf(def));

        seekBar.setMax(max - min);
        seekBar.setProgress(def - min);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int progress, boolean fromUser) {
                int val = min + progress;
                answers[index] = val;
                tvSliderValue.setText(String.valueOf(val));
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });
    }

    private void renderNumber(Question q) {
        tilInput.setVisibility(View.VISIBLE);
        tilInput.setHint(q.getNumberHint());
        etInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etInput.setText("");
        etInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int i, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                try {
                    int val = Integer.parseInt(s.toString());
                    answers[currentIndex] = val;
                } catch (Exception ignored) {}
            }
        });
    }

    private void handleNext() {
        Question q = questions.get(currentIndex);

        // Validate
        if (q.getInputType() == Question.InputType.NUMBER) {
            String txt = etInput.getText() != null ? etInput.getText().toString() : "";
            if (txt.isEmpty()) {
                Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            renderQuestion(currentIndex, true);
        } else {
            // Done — go to results
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("answers", answers);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        }
    }

    private void handlePrev() {
        if (currentIndex > 0) {
            currentIndex--;
            renderQuestion(currentIndex, false);
        } else {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    private void animateFadeIn() {
        View content = findViewById(R.id.tvQuestion);
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(250);
        content.startAnimation(anim);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
