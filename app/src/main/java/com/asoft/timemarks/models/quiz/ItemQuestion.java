package com.asoft.timemarks.models.quiz;

import java.io.Serializable;

public class ItemQuestion implements Serializable {
    private String user_id;
    private String my_answer;
    private String question_id;
    private String question_number;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String totalOptions;
    private String question_ans;
    private String standard_id;
    private String subject_id;
    private String chapter_id;
    private String question;
    private String points;
    private String type;
    private String inserted_at;
    private String course;
    private String stage;
    private String subject_name;
    private String FA;
    private String Chapter;
    private String quiz_title;
    private String quiz_heading;
    private String nature_of_test;
    private String time_in_mins;
    private String qus_exp;
    private String right_status;

    public ItemQuestion(String question_id, String question, String option1, String option2, String option3, String option4){
        this.question_id = question_id;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
    }
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMy_answer() {
        return my_answer;
    }

    public void setMy_answer(String my_answer) {
        this.my_answer = my_answer;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getQuestion_number() {
        return question_number;
    }

    public void setQuestion_number(String question_number) {
        this.question_number = question_number;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getTotalOptions() {
        return totalOptions;
    }

    public void setTotalOptions(String totalOptions) {
        this.totalOptions = totalOptions;
    }

    public String getQuestion_ans() {
        return question_ans;
    }

    public void setQuestion_ans(String question_ans) {
        this.question_ans = question_ans;
    }

    public String getStandard_id() {
        return standard_id;
    }

    public void setStandard_id(String standard_id) {
        this.standard_id = standard_id;
    }

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInserted_at() {
        return inserted_at;
    }

    public void setInserted_at(String inserted_at) {
        this.inserted_at = inserted_at;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getFA() {
        return FA;
    }

    public void setFA(String FA) {
        this.FA = FA;
    }

    public String getChapter() {
        return Chapter;
    }

    public void setChapter(String Chapter) {
        this.Chapter = Chapter;
    }

    public String getQuiz_title() {
        return quiz_title;
    }

    public void setQuiz_title(String quiz_title) {
        this.quiz_title = quiz_title;
    }

    public String getQuiz_heading() {
        return quiz_heading;
    }

    public void setQuiz_heading(String quiz_heading) {
        this.quiz_heading = quiz_heading;
    }

    public String getNature_of_test() {
        return nature_of_test;
    }

    public void setNature_of_test(String nature_of_test) {
        this.nature_of_test = nature_of_test;
    }

    public String getTime_in_mins() {
        return time_in_mins;
    }

    public void setTime_in_mins(String time_in_mins) {
        this.time_in_mins = time_in_mins;
    }

    public String getQus_exp() {
        return qus_exp;
    }

    public void setQus_exp(String qus_exp) {
        this.qus_exp = qus_exp;
    }

    public String getRight_status() {
        return right_status;
    }

    public void setRight_status(String right_status) {
        this.right_status = right_status;
    }
}
