package com.example.pokemonapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Move implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Long fId;
    private String fName;
//    @ManyToMany
//    private List<Type> type;
    private String fCategory;
    private Long fPower;
    private Integer fAccuracy;
    private Integer fMinTimesPerTour;
    private Integer fMaxTimesPerTour;
    private Boolean fUserFaints;
    private Integer fRoundsToLoad;
    private Integer fPp;
    private Boolean fTrapping;
    private Integer fFlinchingProbability;

    public Move() {
    }

    public Move(Long fId, String fName, String fCategory, Long fPower, Integer fAccuracy) {
        this.fId = fId;
        this.fName = fName;
        this.fCategory = fCategory;
        this.fPower = fPower;
        this.fAccuracy = fAccuracy;
    }

    public Move(Long fId, String fName, String fCategory, Long fPower, Integer fAccuracy,
                Integer fMinTimesPerTour, Integer fMaxTimesPerTour, Boolean fUserFaints, Integer fRoundsToLoad, Integer fPp,
                Boolean fTrapping, Integer fFlinchingProbability) {
        this.fId = fId;
        this.fName = fName;
        this.fCategory = fCategory;
        this.fPower = fPower;
        this.fAccuracy = fAccuracy;
        this.fMinTimesPerTour = fMinTimesPerTour;
        this.fMaxTimesPerTour = fMaxTimesPerTour;
        this.fUserFaints = fUserFaints;
        this.fRoundsToLoad = fRoundsToLoad;
        this.fPp = fPp;
        this.fTrapping = fTrapping;
        this.fFlinchingProbability = fFlinchingProbability;
    }

    public Move(String fName, String fCategory, Long fPower, Integer fAccuracy) {
        this.fName = fName;
        this.fCategory = fCategory;
        this.fPower = fPower;
        this.fAccuracy = fAccuracy;
    }

    public Move(String fName, String fCategory, Long fPower, Integer fAccuracy,
                Integer fMinTimesPerTour, Integer fMaxTimesPerTour, Boolean fUserFaints, Integer fRoundsToLoad, Integer fPp,
                Boolean fTrapping, Integer fFlinchingProbability) {
        this.fName = fName;
        this.fCategory = fCategory;
        this.fPower = fPower;
        this.fAccuracy = fAccuracy;
        this.fMinTimesPerTour = fMinTimesPerTour;
        this.fMaxTimesPerTour = fMaxTimesPerTour;
        this.fUserFaints = fUserFaints;
        this.fRoundsToLoad = fRoundsToLoad;
        this.fPp = fPp;
        this.fTrapping = fTrapping;
        this.fFlinchingProbability = fFlinchingProbability;
    }

    public Long getFId() {
        return fId;
    }

    public void setFId(Long fId) {
        this.fId = fId;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getFCategory() {
        return fCategory;
    }

    public void setFCategory(String fCategory) {
        this.fCategory = fCategory;
    }

    public Long getFPower() {
        return fPower;
    }

    public void setFPower(Long fPower) {
        this.fPower = fPower;
    }

    public Integer getFAccuracy() {
        return fAccuracy;
    }

    public void setFAccuracy(Integer fAccuracy) {
        this.fAccuracy = fAccuracy;
    }

    public Integer getFMinTimesPerTour() {
        return fMinTimesPerTour;
    }

    public void setFMinTimesPerTour(Integer fMinTimesPerTour) {
        this.fMinTimesPerTour = fMinTimesPerTour;
    }

    public Integer getFMaxTimesPerTour() {
        return fMaxTimesPerTour;
    }

    public void setFMaxTimesPerTour(Integer fMaxTimesPerTour) {
        this.fMaxTimesPerTour = fMaxTimesPerTour;
    }

    public Boolean getFUserFaints() {
        return fUserFaints;
    }

    public void setFUserFaints(Boolean fUserFaints) {
        this.fUserFaints = fUserFaints;
    }

    public Integer getFRoundsToLoad() {
        return fRoundsToLoad;
    }

    public void setFRoundsToLoad(Integer fRoundsToLoad) {
        this.fRoundsToLoad = fRoundsToLoad;
    }

    public Integer getFPp() {
        return fPp;
    }

    public void setFPp(Integer fPp) {
        this.fPp = fPp;
    }

    public Boolean getFTrapping() {
        return fTrapping;
    }

    public void setFTrapping(Boolean fTrapping) {
        this.fTrapping = fTrapping;
    }

    public Integer getFFlinchingProbability() {
        return fFlinchingProbability;
    }

    public void setFFlinchingProbability(Integer fFlinchingProbability) {
        this.fFlinchingProbability = fFlinchingProbability;
    }

}
