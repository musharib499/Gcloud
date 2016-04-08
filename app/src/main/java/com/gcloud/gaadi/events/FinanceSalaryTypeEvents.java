package com.gcloud.gaadi.events;

/**
 * Created by dipanshugarg on 2/12/15.
 */
public class FinanceSalaryTypeEvents {
    String mSalaryType;

    public FinanceSalaryTypeEvents(String mSalaryType) {
        this.mSalaryType = mSalaryType;
    }
    public void setSalary(String salary) {
        this.mSalaryType = salary;
    }
    public String getmSalaryType() {
        return mSalaryType;
    }

}
