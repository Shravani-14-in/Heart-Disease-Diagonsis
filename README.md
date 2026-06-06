# CardioScan — Heart Disease Risk Analyzer

> An Android application that predicts heart disease risk using a multi-factor assessment engine and delivers personalized health recommendations.

---

## Overview

CardioScan is a mobile health tool that evaluates a user's cardiovascular risk based on clinical and lifestyle inputs. A weighted scoring engine processes the responses and generates a risk score between 0–100, visualized as an animated circular gauge. Based on the score, the app provides a personalized action plan with diet, lifestyle, and medical recommendations.

Built entirely in native Android (Java + XML) with no external APIs — fully offline.

---

## Features

- **Multi-Step Questionnaire** — Collects clinical, lifestyle, and family history inputs across structured steps
- **Weighted Scoring Engine** — Calculates a 0–100 risk score using medically informed factor weights
- **Circular Risk Gauge** — Custom canvas-drawn disc visualization with color-coded risk zones
- **Personalized Recommendations** — Tailored action plan based on risk level (Low / Moderate / High)
- **Fully Offline** — No internet connection required, no data leaves the device

---

## Risk Factors Assessed

| Category | Parameters |
|---|---|
| Clinical | Blood pressure, cholesterol levels |
| Personal | Age, gender, BMI |
| Lifestyle | Smoking, physical activity, diet |
| History | Family history of heart disease, prior cardiac events |
| Geographic | Regional/environmental risk factors |

---

## Risk Score Interpretation

| Score | Risk Level | Gauge Color |
|---|---|---|
| 0 – 30 | Low | Green |
| 31 – 60 | Moderate | Amber |
| 61 – 100 | High | Red |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| UI | XML Layouts (Android Views) |
| Custom Graphics | Android Canvas API |
| IDE | Android Studio |
| Min SDK | Android 7.0 (API 24) |

---
