package com.threeatom.guidecore.excel.vo;

public class QuestionsDataExcel {
    private String videoId;
    private String timestamp;

    private String timeLimit;
    private String questionType;
    private String questionText;

    private String mutipleChoices;

    private String choicesAnswer;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getMutipleChoices() {
        return mutipleChoices;
    }

    public void setMutipleChoices(String mutipleChoices) {
        this.mutipleChoices = mutipleChoices;
    }

    public String getChoicesAnswer() {
        return choicesAnswer;
    }

    public void setChoicesAnswer(String choicesAnswer) {
        this.choicesAnswer = choicesAnswer;
    }
}
