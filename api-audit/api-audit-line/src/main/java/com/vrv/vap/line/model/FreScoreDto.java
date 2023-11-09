package com.vrv.vap.line.model;

import java.util.ArrayList;
import java.util.List;

public class FreScoreDto {
    private float score;
    private List<String> frequents;

    public FreScoreDto() {
        this.frequents = new ArrayList<>();
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public List<String> getFrequents() {
        return frequents;
    }

    public void setFrequents(List<String> frequents) {
        this.frequents = frequents;
    }
}
