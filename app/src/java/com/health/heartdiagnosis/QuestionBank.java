package com.health.heartdiagnosis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionBank {

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // ── SECTION 1: DEMOGRAPHIC INFO ─────────────────────────────────

        // Q1 – Age
        questions.add(new Question(1, "Demographic Info", "👤",
                "What is your age?",
                Question.InputType.SLIDER)
                .setSubtitle("Age is one of the strongest predictors of heart disease risk.")
                .setSlider(18, 90, 35, "years")
                .setWeight(12));

        // Q2 – Gender
        questions.add(new Question(2, "Demographic Info", "👤",
                "What is your biological sex?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList("Male", "Female", "Prefer not to say"))
                .setRiskValues(1.0, 0.55, 0.5)
                .setWeight(8)
                .setInfoText("Men generally face a higher risk of heart disease earlier in life. " +
                        "Women's risk rises significantly after menopause."));

        // Q3 – Ethnicity
        questions.add(new Question(3, "Demographic Info", "👤",
                "What is your ethnic background?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList("South Asian", "African/Caribbean", "East Asian",
                        "White/European", "Other"))
                .setRiskValues(1.0, 0.85, 0.55, 0.60, 0.65)
                .setWeight(6)
                .setInfoText("South Asian populations have a higher genetic predisposition to " +
                        "heart disease, often developing it 5–10 years earlier than other groups."));

        // ── SECTION 2: PERSONAL HEALTH ──────────────────────────────────

        // Q4 – Blood Pressure
        questions.add(new Question(4, "Personal Health", "🩺",
                "What is your systolic blood pressure?",
                Question.InputType.SLIDER)
                .setSubtitle("The top number in a blood pressure reading (e.g. 120 in 120/80).")
                .setSlider(80, 200, 120, "mmHg")
                .setWeight(14)
                .setInfoText("Normal: <120 mmHg. Elevated: 120–129. High (Stage 1): 130–139. " +
                        "High (Stage 2): ≥140. Crisis: >180."));

        // Q5 – Cholesterol
        questions.add(new Question(5, "Personal Health", "🩺",
                "What is your total cholesterol level?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "Don't know",
                        "Normal (<200 mg/dL)",
                        "Borderline (200–239 mg/dL)",
                        "High (≥240 mg/dL)"))
                .setRiskValues(0.5, 0.1, 0.65, 1.0)
                .setWeight(13)
                .setInfoText("High cholesterol leads to plaque build-up in arteries, significantly " +
                        "increasing your risk of a heart attack or stroke."));

        // Q6 – Blood Sugar / Diabetes
        questions.add(new Question(6, "Personal Health", "🩺",
                "Do you have diabetes or elevated blood sugar?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "No",
                        "Pre-diabetic",
                        "Type 2 Diabetes",
                        "Type 1 Diabetes"))
                .setRiskValues(0.0, 0.45, 1.0, 0.9)
                .setWeight(12)
                .setInfoText("Diabetes doubles the risk of heart disease. High blood sugar damages " +
                        "blood vessels and nerves controlling the heart."));

        // Q7 – BMI / Weight
        questions.add(new Question(7, "Personal Health", "🩺",
                "What is your Body Mass Index (BMI)?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "Underweight (<18.5)",
                        "Normal (18.5–24.9)",
                        "Overweight (25–29.9)",
                        "Obese (≥30)"))
                .setRiskValues(0.3, 0.05, 0.5, 1.0)
                .setWeight(9)
                .setInfoText("Obesity is strongly linked to high blood pressure, high cholesterol " +
                        "and diabetes — all major risk factors for heart disease."));

        // Q8 – Resting Heart Rate
        questions.add(new Question(8, "Personal Health", "🩺",
                "What is your typical resting heart rate?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "Athlete (<50 bpm)",
                        "Normal (50–80 bpm)",
                        "Elevated (81–100 bpm)",
                        "High (>100 bpm)"))
                .setRiskValues(0.1, 0.05, 0.55, 1.0)
                .setWeight(7));

        // ── SECTION 3: LIFESTYLE ─────────────────────────────────────────

        // Q9 – Smoking
        questions.add(new Question(9, "Lifestyle", "🚭",
                "What is your smoking status?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "Never smoked",
                        "Quit > 5 years ago",
                        "Quit 1–5 years ago",
                        "Current smoker"))
                .setRiskValues(0.0, 0.2, 0.55, 1.0)
                .setWeight(14)
                .setInfoText("Smoking is one of the most powerful triggers for heart disease. " +
                        "It damages blood vessel walls, raises blood pressure and reduces oxygen."));

        // Q10 – Physical Activity
        questions.add(new Question(10, "Lifestyle", "🚭",
                "How active are you physically each week?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "Very active (>150 min)",
                        "Moderately active (75–150 min)",
                        "Lightly active (<75 min)",
                        "Sedentary"))
                .setRiskValues(0.0, 0.25, 0.65, 1.0)
                .setWeight(10)
                .setInfoText("Regular aerobic activity strengthens the heart and significantly " +
                        "lowers the risk of cardiovascular disease."));

        // Q11 – Diet
        questions.add(new Question(11, "Lifestyle", "🚭",
                "How would you describe your diet?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "Heart-healthy (low fat, high fiber)",
                        "Balanced",
                        "High in processed/fast food",
                        "High in red meat & saturated fat"))
                .setRiskValues(0.0, 0.3, 0.75, 1.0)
                .setWeight(8));

        // Q12 – Alcohol
        questions.add(new Question(12, "Lifestyle", "🚭",
                "How much alcohol do you consume weekly?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "None",
                        "Light (1–7 drinks/week)",
                        "Moderate (8–14 drinks/week)",
                        "Heavy (>14 drinks/week)"))
                .setRiskValues(0.05, 0.15, 0.6, 1.0)
                .setWeight(7));

        // Q13 – Stress
        questions.add(new Question(13, "Lifestyle", "🚭",
                "How would you rate your stress level?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "Low",
                        "Moderate",
                        "High",
                        "Very high / chronic"))
                .setRiskValues(0.0, 0.3, 0.7, 1.0)
                .setWeight(6)
                .setInfoText("Chronic stress elevates cortisol and adrenaline, which over time " +
                        "raises blood pressure and accelerates plaque build-up."));

        // Q14 – Sleep
        questions.add(new Question(14, "Lifestyle", "🚭",
                "How many hours of sleep do you get per night on average?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "7–9 hours (optimal)",
                        "6–7 hours",
                        "5–6 hours",
                        "Less than 5 hours"))
                .setRiskValues(0.0, 0.2, 0.6, 1.0)
                .setWeight(5));

        // ── SECTION 4: SYMPTOMS ──────────────────────────────────────────

        // Q15 – Chest pain
        questions.add(new Question(15, "Symptoms", "❤️",
                "Do you experience chest pain or tightness?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "Never",
                        "Occasionally",
                        "During exercise / exertion",
                        "Frequently or at rest"))
                .setRiskValues(0.0, 0.4, 0.8, 1.0)
                .setWeight(13)
                .setInfoText("Chest discomfort during activity (angina) is a hallmark warning " +
                        "sign of reduced blood flow to the heart muscle."));

        // Q16 – Shortness of breath
        questions.add(new Question(16, "Symptoms", "❤️",
                "Do you experience shortness of breath?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "No",
                        "Only with strenuous activity",
                        "With mild activity",
                        "At rest"))
                .setRiskValues(0.0, 0.25, 0.65, 1.0)
                .setWeight(10));

        // Q17 – Palpitations
        questions.add(new Question(17, "Symptoms", "❤️",
                "Do you experience heart palpitations or irregular heartbeat?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList("Never", "Rarely", "Sometimes", "Often"))
                .setRiskValues(0.0, 0.2, 0.6, 1.0)
                .setWeight(8));

        // Q18 – Fatigue
        questions.add(new Question(18, "Symptoms", "❤️",
                "Do you experience unexplained fatigue?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList("Never", "Rarely", "Often", "Most of the time"))
                .setRiskValues(0.0, 0.2, 0.55, 0.9)
                .setWeight(6));

        // Q19 – Swelling
        questions.add(new Question(19, "Symptoms", "❤️",
                "Do you have swelling in your legs, ankles or feet?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList("No", "Mild", "Moderate", "Severe"))
                .setRiskValues(0.0, 0.3, 0.7, 1.0)
                .setWeight(7)
                .setInfoText("Oedema (swelling) can indicate the heart is not pumping blood " +
                        "efficiently, a sign of heart failure."));

        // ── SECTION 5: FAMILY HISTORY ────────────────────────────────────

        // Q20 – First-degree relatives
        questions.add(new Question(20, "Family History", "🧬",
                "Has a first-degree relative (parent/sibling) had heart disease?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "No",
                        "Yes, after age 65",
                        "Yes, before age 65",
                        "Yes, before age 55"))
                .setRiskValues(0.0, 0.35, 0.8, 1.0)
                .setWeight(12)
                .setInfoText("Premature coronary artery disease in a first-degree relative " +
                        "significantly raises your genetic risk."));

        // Q21 – Previous cardiac events
        questions.add(new Question(21, "Family History", "🧬",
                "Have you personally had any previous cardiac events?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "No",
                        "Angina or chest pain episodes",
                        "Heart attack",
                        "Stroke or TIA"))
                .setRiskValues(0.0, 0.6, 1.0, 0.9)
                .setWeight(15)
                .setInfoText("A previous heart attack or stroke is the strongest predictor of a " +
                        "future cardiovascular event."));

        // Q22 – Known conditions
        questions.add(new Question(22, "Family History", "🧬",
                "Do you have any diagnosed cardiovascular conditions?",
                Question.InputType.OPTIONS)
                .setOptions(Arrays.asList(
                        "None",
                        "Atrial Fibrillation",
                        "Coronary Artery Disease",
                        "Heart Failure"))
                .setRiskValues(0.0, 0.6, 0.9, 1.0)
                .setWeight(14));

        return questions;
    }
}
