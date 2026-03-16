package com.health.heartdiagnosis;

import java.util.ArrayList;
import java.util.List;

public class RiskEngine {

    public static class RiskResult {
        public double totalScore;          // 0–100
        public String riskLevel;           // LOW / MODERATE / HIGH / VERY HIGH
        public int riskColor;              // resource color
        public String riskDescription;
        public List<CategoryScore> breakdown;
        public List<String> findings;
        public List<Tip> tips;
        public String doctorAdvice;
    }

    public static class CategoryScore {
        public String emoji;
        public String name;
        public double score;               // 0–100
        CategoryScore(String emoji, String name, double score) {
            this.emoji = emoji; this.name = name; this.score = score;
        }
    }

    public static class Tip {
        public String emoji;
        public String title;
        public String body;
        Tip(String emoji, String title, String body) {
            this.emoji = emoji; this.title = title; this.body = body;
        }
    }

    /**
     * answers: array of numeric answers per question
     *   - OPTIONS  → index of selected option (0-based)
     *   - SLIDER   → raw slider value (int)
     *   - NUMBER   → raw numeric input (int)
     */
    public static RiskResult calculate(List<Question> questions, int[] answers) {

        List<Question> qs = questions;

        /* ── Per-question risk contributions ──────────────────────── */
        double totalWeight = 0;
        double weightedRisk = 0;

        // Category buckets (indices match sections)
        double[] catWeightedRisk  = new double[5];
        double[] catTotalWeight   = new double[5];

        for (int i = 0; i < qs.size(); i++) {
            Question q = qs.get(i);
            int sectionIdx = getSectionIndex(q.getSection());
            double risk = getRiskForAnswer(q, answers[i]);

            double contribution = risk * q.getWeight();
            weightedRisk    += contribution;
            totalWeight     += q.getWeight();

            catWeightedRisk[sectionIdx]  += contribution;
            catTotalWeight[sectionIdx]   += q.getWeight();
        }

        double rawScore = totalWeight > 0 ? (weightedRisk / totalWeight) * 100.0 : 0;
        // Clamp to 0-100
        rawScore = Math.max(0, Math.min(100, rawScore));

        // Apply a mild S-curve to spread mid-range scores
        double finalScore = sCurve(rawScore);

        /* ── Build result ─────────────────────────────────────────── */
        RiskResult result = new RiskResult();
        result.totalScore = Math.round(finalScore * 10.0) / 10.0;

        // Risk levels
        if (finalScore < 20) {
            result.riskLevel = "LOW RISK";
            result.riskColor = R.color.risk_low;
            result.riskDescription = "Your risk indicators are within a healthy range";
        } else if (finalScore < 40) {
            result.riskLevel = "MODERATE RISK";
            result.riskColor = R.color.risk_moderate;
            result.riskDescription = "Some risk factors are present — lifestyle changes can help";
        } else if (finalScore < 65) {
            result.riskLevel = "HIGH RISK";
            result.riskColor = R.color.risk_high;
            result.riskDescription = "Multiple significant risk factors detected";
        } else {
            result.riskLevel = "VERY HIGH RISK";
            result.riskColor = R.color.risk_very_high;
            result.riskDescription = "Strongly elevated risk — medical evaluation is essential";
        }

        /* ── Category breakdown ───────────────────────────────────── */
        String[] catEmojis = {"👤","🩺","🚭","❤️","🧬"};
        String[] catNames  = {"Demographic","Personal Health","Lifestyle","Symptoms","Family History"};
        result.breakdown = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            double catScore = catTotalWeight[i] > 0
                    ? (catWeightedRisk[i] / catTotalWeight[i]) * 100.0 : 0;
            result.breakdown.add(new CategoryScore(catEmojis[i], catNames[i],
                    Math.min(100, Math.round(catScore))));
        }

        /* ── Key Findings ─────────────────────────────────────────── */
        result.findings = buildFindings(qs, answers);

        /* ── Tips ─────────────────────────────────────────────────── */
        result.tips = buildTips(qs, answers, finalScore);

        /* ── Doctor advice ────────────────────────────────────────── */
        if (finalScore < 20) {
            result.doctorAdvice = "Continue routine check-ups. Annual health screening recommended.";
        } else if (finalScore < 40) {
            result.doctorAdvice = "Schedule a cardiovascular check-up within the next 6 months.";
        } else if (finalScore < 65) {
            result.doctorAdvice = "Consult a cardiologist within the next 1–2 months. Discuss a full lipid panel and ECG.";
        } else {
            result.doctorAdvice = "Seek urgent medical evaluation. Do not delay — speak to a doctor as soon as possible.";
        }

        return result;
    }

    /* ── Helpers ──────────────────────────────────────────────────────── */

    private static double getRiskForAnswer(Question q, int answer) {
        switch (q.getInputType()) {
            case OPTIONS:
                double[] rv = q.getRiskValues();
                if (rv == null || answer < 0 || answer >= rv.length) return 0;
                return rv[answer];

            case SLIDER:
                return normaliseSlider(q, answer);

            case NUMBER:
                return normaliseNumber(q, answer);

            default:
                return 0;
        }
    }

    /** Normalise a slider value to 0–1 risk based on question semantics */
    private static double normaliseSlider(Question q, int value) {
        int id = q.getId();
        if (id == 1) { // Age: risk rises with age
            if (value < 30) return 0.05;
            if (value < 40) return 0.15;
            if (value < 50) return 0.3;
            if (value < 60) return 0.55;
            if (value < 70) return 0.75;
            return 1.0;
        }
        if (id == 4) { // Systolic BP
            if (value < 120) return 0.05;
            if (value < 130) return 0.2;
            if (value < 140) return 0.5;
            if (value < 160) return 0.75;
            return 1.0;
        }
        // Generic: linear
        int min = q.getSliderMin(), max = q.getSliderMax();
        return (double)(value - min) / (max - min);
    }

    private static double normaliseNumber(Question q, int value) {
        int min = q.getNumberMin(), max = q.getNumberMax();
        if (max == min) return 0;
        double norm = (double)(value - min) / (max - min);
        return Math.max(0, Math.min(1, norm));
    }

    /** Mild S-curve to give a more natural spread */
    private static double sCurve(double x) {
        // x in [0,100] → apply logistic-like transform
        double t = x / 100.0;
        double s = t * t * (3 - 2 * t); // Smoothstep
        return s * 100.0;
    }

    private static int getSectionIndex(String section) {
        switch (section) {
            case "Demographic Info":  return 0;
            case "Personal Health":   return 1;
            case "Lifestyle":         return 2;
            case "Symptoms":          return 3;
            case "Family History":    return 4;
            default:                  return 0;
        }
    }

    private static List<String> buildFindings(List<Question> qs, int[] answers) {
        List<String> findings = new ArrayList<>();

        // Q1 Age
        if (answers[0] >= 55) findings.add("⚠️ Age above 55 — a significant non-modifiable risk factor.");
        // Q2 Gender
        if (answers[1] == 0) findings.add("ℹ️ Male sex is associated with earlier onset of heart disease.");
        // Q4 BP
        int bp = answers[3];
        if (bp >= 140) findings.add("🔴 Systolic BP ≥140 mmHg — Stage 2 hypertension detected.");
        else if (bp >= 130) findings.add("🟡 Elevated blood pressure (Stage 1 hypertension).");
        // Q5 Cholesterol
        if (answers[4] == 3) findings.add("🔴 High total cholesterol (≥240 mg/dL) significantly raises risk.");
        else if (answers[4] == 2) findings.add("🟡 Borderline high cholesterol — monitor closely.");
        // Q6 Diabetes
        if (answers[5] == 2 || answers[5] == 3) findings.add("🔴 Diabetes doubles your cardiovascular risk.");
        else if (answers[5] == 1) findings.add("🟡 Pre-diabetes detected — intervention can prevent progression.");
        // Q7 BMI
        if (answers[6] == 3) findings.add("🔴 Obesity is a major modifiable risk factor.");
        // Q9 Smoking
        if (answers[8] == 3) findings.add("🔴 Current smoking is one of the top risk factors for heart disease.");
        else if (answers[8] == 2) findings.add("🟡 Recent ex-smoker — risk is declining but still elevated.");
        // Q10 Activity
        if (answers[9] == 3) findings.add("🔴 Sedentary lifestyle significantly increases cardiovascular risk.");
        // Q15 Chest pain
        if (answers[14] == 2 || answers[14] == 3) findings.add("🔴 Chest pain during exertion — possible angina, seek evaluation.");
        // Q20 Family history
        if (answers[19] == 3) findings.add("🔴 Strong family history of premature heart disease.");
        // Q21 Cardiac events
        if (answers[20] == 2) findings.add("🔴 Previous heart attack — you are in a very high-risk category.");

        if (findings.isEmpty()) {
            findings.add("✅ No major individual risk flags detected in your responses.");
            findings.add("✅ Continue maintaining your current healthy habits.");
        }
        return findings;
    }

    private static List<Tip> buildTips(List<Question> qs, int[] answers, double score) {
        List<Tip> tips = new ArrayList<>();

        // Always add general tips
        tips.add(new Tip("🥗", "Adopt a Heart-Healthy Diet",
                "Focus on vegetables, whole grains, lean proteins and healthy fats (olive oil, nuts, fish). " +
                        "Limit sodium, saturated fat and added sugars."));

        // Smoking
        if (answers[8] == 3) {
            tips.add(new Tip("🚭", "Quit Smoking Now",
                    "Stopping smoking is the single most impactful change you can make. " +
                            "Within 1 year of quitting, heart attack risk drops by 50%."));
        }

        // BP
        if (answers[3] >= 130) {
            tips.add(new Tip("💊", "Manage Blood Pressure",
                    "Reduce sodium intake to <1500mg/day, exercise regularly, limit alcohol, " +
                            "and consult your doctor about medication if lifestyle changes are insufficient."));
        }

        // Cholesterol
        if (answers[4] >= 2) {
            tips.add(new Tip("🫀", "Lower Your Cholesterol",
                    "Eat more soluble fibre (oats, beans, lentils), reduce saturated fat, " +
                            "exercise regularly and discuss statins with your doctor."));
        }

        // Diabetes
        if (answers[5] >= 1) {
            tips.add(new Tip("🩸", "Control Blood Sugar",
                    "Follow a low-glycaemic diet, exercise for at least 150 minutes per week, " +
                            "and monitor your blood sugar regularly with your healthcare provider."));
        }

        // Sedentary
        if (answers[9] >= 2) {
            tips.add(new Tip("🏃", "Increase Physical Activity",
                    "Aim for at least 150 minutes of moderate-intensity exercise per week. " +
                            "Start with brisk walking for 30 minutes, 5 days a week."));
        }

        // Stress
        if (answers[12] >= 2) {
            tips.add(new Tip("🧘", "Manage Stress Actively",
                    "Practice mindfulness, yoga, or deep breathing daily. " +
                            "Chronic stress raises cortisol and blood pressure — addressing it protects your heart."));
        }

        // Sleep
        if (answers[13] >= 2) {
            tips.add(new Tip("😴", "Prioritise Sleep",
                    "Aim for 7–9 hours per night. Poor sleep is linked to hypertension, " +
                            "obesity and inflammation — all heart disease risk factors."));
        }

        // BMI
        if (answers[6] == 3) {
            tips.add(new Tip("⚖️", "Achieve a Healthy Weight",
                    "Even losing 5–10% of body weight can significantly reduce blood pressure, " +
                            "cholesterol and blood sugar levels."));
        }

        // Alcohol
        if (answers[11] >= 2) {
            tips.add(new Tip("🍷", "Reduce Alcohol Intake",
                    "Limit to no more than 14 units per week with alcohol-free days. " +
                            "Excess alcohol raises blood pressure and contributes to arrhythmias."));
        }

        // High score general tip
        if (score >= 40) {
            tips.add(new Tip("📋", "Get a Comprehensive Heart Screening",
                    "Ask your doctor for a full cardiovascular panel: ECG, fasting lipids, " +
                            "blood glucose, and blood pressure monitoring."));
        }

        return tips;
    }
}
