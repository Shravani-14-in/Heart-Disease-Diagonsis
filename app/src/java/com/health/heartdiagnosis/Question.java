package com.health.heartdiagnosis;

import java.util.List;

public class Question {

    public enum InputType {
        OPTIONS,   // Multiple choice buttons
        SLIDER,    // SeekBar
        NUMBER     // TextInput
    }

    private int id;
    private String section;
    private String sectionEmoji;
    private String question;
    private String subtitle;
    private String infoText;
    private InputType inputType;
    private List<String> options;
    private int sliderMin;
    private int sliderMax;
    private String sliderUnit;
    private int sliderDefault;
    private String numberHint;
    private int numberMin;
    private int numberMax;

    // Risk weight: how much this question contributes to the score
    private double weight;
    // riskMap: for OPTIONS, maps option index -> risk contribution (0.0 to 1.0)
    private double[] riskValues;

    public Question(int id, String section, String sectionEmoji, String question,
                    InputType inputType) {
        this.id = id;
        this.section = section;
        this.sectionEmoji = sectionEmoji;
        this.question = question;
        this.inputType = inputType;
    }

    // Getters
    public int getId() { return id; }
    public String getSection() { return section; }
    public String getSectionEmoji() { return sectionEmoji; }
    public String getQuestion() { return question; }
    public String getSubtitle() { return subtitle; }
    public String getInfoText() { return infoText; }
    public InputType getInputType() { return inputType; }
    public List<String> getOptions() { return options; }
    public int getSliderMin() { return sliderMin; }
    public int getSliderMax() { return sliderMax; }
    public String getSliderUnit() { return sliderUnit; }
    public int getSliderDefault() { return sliderDefault; }
    public String getNumberHint() { return numberHint; }
    public int getNumberMin() { return numberMin; }
    public int getNumberMax() { return numberMax; }
    public double getWeight() { return weight; }
    public double[] getRiskValues() { return riskValues; }

    // Setters / Builder-style
    public Question setSubtitle(String subtitle) { this.subtitle = subtitle; return this; }
    public Question setInfoText(String info) { this.infoText = info; return this; }
    public Question setOptions(List<String> options) { this.options = options; return this; }
    public Question setSlider(int min, int max, int def, String unit) {
        this.sliderMin = min; this.sliderMax = max; this.sliderDefault = def;
        this.sliderUnit = unit; return this;
    }
    public Question setNumber(String hint, int min, int max) {
        this.numberHint = hint; this.numberMin = min; this.numberMax = max; return this;
    }
    public Question setWeight(double weight) { this.weight = weight; return this; }
    public Question setRiskValues(double... vals) { this.riskValues = vals; return this; }
}
