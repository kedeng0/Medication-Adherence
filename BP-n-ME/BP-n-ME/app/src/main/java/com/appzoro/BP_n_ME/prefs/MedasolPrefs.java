package com.appzoro.BP_n_ME.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Appzoro_ 5 on 8/1/2017.
 */

public class MedasolPrefs {
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefs_edit;
    private static MedasolPrefs instance;
    private int daysToSend = 7;
    private int[] lifestyleSurveyAnswers = {0, 0, 0, 0, 0, 0, 0, 0};

    public MedasolPrefs(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs_edit = prefs.edit();
    }

    public final static MedasolPrefs getInstance(Context context) {
        if (instance == null) {
            instance = new MedasolPrefs(context);
        }
        return instance;
    }

    private ArrayList<String> uidList = new ArrayList<>();
    private ArrayList<String> timestamp = new ArrayList<>();
    private ArrayList<String> emailList = new ArrayList<>();
    private ArrayList<String> sevenDayList = new ArrayList<>();
    private ArrayList<ArrayList<String>> medicationIntake = new ArrayList<ArrayList<String>>();

    //******************************************************************************************

    public void setType(int type) {
        prefs_edit.putInt("type", type);
        prefs_edit.commit();
    }

    public int getType() {
        return prefs.getInt("type", 0);
    }

    public void setUID(String uid) {
        prefs_edit.putString("uid", uid);
        prefs_edit.commit();
    }

    public String getUID() {
        return prefs.getString("uid", "");
    }

    public void setUIDList(ArrayList<String> uidlist) {
        this.uidList = uidlist;
    }

    public ArrayList<String> getUIDList() {
        return this.uidList;
    }

    public void setTimestampList(ArrayList<String> timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<String> getTimestampList() {
        return this.timestamp;
    }

    public String getUsername() {
        return prefs.getString("username", "");
    }

    public void setUsername(String username) {
        prefs_edit.putString("username", username);
        prefs_edit.commit();
    }

    public void addPatientEmail(String emailgiven) {
        this.emailList.add(emailgiven);
    }

    public void clearPatientEmails() {
        emailList.clear();
    }

    public ArrayList<String> getPatientEmails() {
        return emailList;
    }

    public String getEmail() {
        return prefs.getString("email", "");
    }

    public void setEmail(String email) {
        prefs_edit.putString("email", email);
        prefs_edit.commit();
    }

    public void setNameList(ArrayList<String> patientname) {
        prefs_edit.putString("patientname", String.valueOf(patientname));
        prefs_edit.commit();
    }

    public String getNameList() {
        return prefs.getString("patientname", "");
    }

    public void setFristName(String fristName) {
        prefs_edit.putString("fristName", fristName);
        prefs_edit.commit();
    }

    public String getFristName() {
        return prefs.getString("fristName", "");
    }

    public void setLastName(String lastName) {
        prefs_edit.putString("lastName", lastName);
        prefs_edit.commit();
    }

    public String getLastName() {
        return prefs.getString("lastName", "");
    }

    public void setDischargeDate(String dischargedate) {
        prefs_edit.putString("dischargedate", dischargedate);
        prefs_edit.commit();
    }

    public String getDischargeDate() {
        return prefs.getString("dischargedate", "");
    }

    public void setContactNumber(String contactNumber) {
        prefs_edit.putString("contactNumber", contactNumber);
        prefs_edit.commit();
    }

    public String getContactNumber() {
        return prefs.getString("contactNumber", "");
    }

    public String getPassword() {
        return prefs.getString("password", "");
    }

    public void setPassword(String password) {
        prefs_edit.putString("password", password);
        prefs_edit.commit();
    }

    public String getClinicName() {
        return prefs.getString("clinicName", "");
    }

    public void setClinicName(String clinicName) {
        prefs_edit.putString("clinicName", clinicName);
        prefs_edit.commit();
    }

    public String getPharmaName() {
        return prefs.getString("pharmaName", "");
    }

    public void setPharmaName(String pharmaName) {
        prefs_edit.putString("pharmaName", pharmaName);
        prefs_edit.commit();
    }

    public String getPharmaNumber() {
        return prefs.getString("pharmaNumber", "");
    }

    public void setPharmaNumber(String pharmaNumber) {
        prefs_edit.putString("pharmaNumber", pharmaNumber);
        prefs_edit.commit();
    }

    public void setPhysicianName(String physicianName) {
        prefs_edit.putString("physicianName", physicianName);
        prefs_edit.commit();
    }

    public String getPhysicianName() {
        return prefs.getString("physicianName", "");
    }

    public void setPhysicianNumber(String physicianNumber) {
        prefs_edit.putString("physicianNumber", physicianNumber);
        prefs_edit.commit();
    }

    public String getPhysicianNumber() {
        return prefs.getString("physicianNumber", "");
    }

    public void setRegistrationDate(String RegistrationDate) {
        prefs_edit.putString("RegistrationDate", RegistrationDate);
        prefs_edit.commit();
    }

    public String getRegistrationDate() {
        return prefs.getString("RegistrationDate", "");
    }

    public void setEnrollmentDate(String EnrollmentDate) {
        prefs_edit.putString("EnrollmentDate", EnrollmentDate);
        prefs_edit.commit();
    }

    public String getEnrollmentDate() {
        return prefs.getString("EnrollmentDate", "");
    }

    public void clear() {
        prefs_edit.clear();
        prefs_edit.commit();
    }

    public void setOkUidList(ArrayList<String> GreenUid) {
        prefs_edit.putString("GreenUid", String.valueOf(GreenUid));
        prefs_edit.commit();
    }

    public String getOkUidList() {
        return prefs.getString("GreenUid", "");
    }


    //******************************Patient Condition***************************

    public void setPatientServeyCondition(String serveyCondition) {
        prefs_edit.putString("serveyCondition", serveyCondition);
        prefs_edit.commit();
    }

    public String getPatientServeyCondition() {
        return prefs.getString("serveyCondition", "");
    }


    public void setPatientBPCondition(String bpCondition) {
        prefs_edit.putString("bpCondition", bpCondition);
        prefs_edit.commit();
    }

    public String getPatientBPCondition() {
        return prefs.getString("bpCondition", "");
    }


    public void setPatientMedicalCondition(String medicalCondition) {
        prefs_edit.putString("medicalCondition", medicalCondition);
        prefs_edit.commit();
    }

    public String getPatientMedicalCondition() {
        return prefs.getString("medicalCondition", "");
    }


    //*******************************  Doctor info (Type = 1)  *********************************

    public void setDrName(String drname) {
        prefs_edit.putString("drname", drname);
        prefs_edit.commit();
    }

    public String getDrName() {
        return prefs.getString("drname", "");
    }


    //******************************************************************************************
    // for use in color changes of calendar dates

    public void setBPFirstTimeOpen(Boolean bpFirstTimeOpen) {
        prefs_edit.putBoolean("bpFirstTimeOpen", bpFirstTimeOpen);
        prefs_edit.commit();
    }

    public Boolean isBPFirstTimeOpen() {
        return prefs.getBoolean("bpFirstTimeOpen", false);
    }

    public void setBPTimeOpen(Boolean bpTimeOpen) {
        prefs_edit.putBoolean("bpTimeOpen", bpTimeOpen);
        prefs_edit.commit();
    }

    public Boolean isBPTimeOpen() {
        return prefs.getBoolean("bpTimeOpen", false);
    }

    public void setMedsFirstTimeOpen(Boolean medsFirstTimeOpen) {
        prefs_edit.putBoolean("medsFirstTimeOpen", medsFirstTimeOpen);
        prefs_edit.commit();
    }

    public Boolean isMedsFirstTimeOpen() {
        return prefs.getBoolean("medsFirstTimeOpen", false);
    }

    public void setMedsFirstOpen(Boolean medsFirstOpen) {
        prefs_edit.putBoolean("medsFirstOpen", medsFirstOpen);
        prefs_edit.commit();
    }

    public Boolean isMedsFirstOpen() {
        return prefs.getBoolean("medsFirstOpen", false);
    }

    //*******************************  Blood Pressure Goal  ************************************

    public String getBloodPressureGoal() {
        return prefs.getString("bloodPressureGoal", "");
    }

    public void setBloodPressureGoal(String bloodPressureGoal) {
        prefs_edit.putString("bloodPressureGoal", bloodPressureGoal);
        prefs_edit.commit();
    }

    public void setHeartRate(ArrayList<String> HeartRate) {
        prefs_edit.putString("HeartRate", String.valueOf(HeartRate));
        prefs_edit.commit();
    }

    public String getHeartRate() {
        return prefs.getString("HeartRate", "");
    }

    //****************************  BP, BW, HR Last Log Value  *********************************

    public String getBpValue() {
        return prefs.getString("bpValueLast", "");
    }

    public void setBpValue(String bpValueLast) {
        prefs_edit.putString("bpValueLast", bpValueLast);
        prefs_edit.commit();
    }

    public String getHeartRateValue() {
        return prefs.getString("HearRateLast", "");
    }

    public void setHeartRateValue(String HearRateLast) {
        prefs_edit.putString("HearRateLast", HearRateLast);
        prefs_edit.commit();
    }

    public String getBwValue() {
        return prefs.getString("bwValueLast", "");
    }

    public void setBwValue(String bwValueLast) {
        prefs_edit.putString("bwValueLast", bwValueLast);
        prefs_edit.commit();
    }

    //*****************************  Demo graphics Survey Answers  ******************************

    public void setDemographicsSurveyAnswers(int[] demoanswers) {
        prefs_edit.putString("demoanswers", Arrays.toString(demoanswers));
        prefs_edit.commit();
    }

    public String getDemographicsSurveyAnswers() {
        return prefs.getString("demoanswers", "");
    }

    //*********************************** ailments ***********************************************

    public void setMedicalConditions(String[] medicalconditions) {
        prefs_edit.putString("medicalconditions", Arrays.toString(medicalconditions));
        prefs_edit.commit();
    }

    public String getMedicalConditions() {
        return prefs.getString("medicalconditions", "");
    }

    //**************************  MEDICATION  ***************************************

    public void setMeds(ArrayList<String> medicine) {
        prefs_edit.putString("medicine", String.valueOf(medicine));
        prefs_edit.commit();
    }

    public String getMeds() {
        return prefs.getString("medicine", "");
    }

    public void resetMedicationList() {
        prefs_edit.putString("medicine", "");
        prefs_edit.commit();
    }

    public void setMedsLog(ArrayList<String> medicineLog) {
        prefs_edit.putString("medicineLog", String.valueOf(medicineLog));
        prefs_edit.commit();
    }

    public String getMedsLog() {
        return prefs.getString("medicineLog", "");
    }

    //***********************************  FREQUENCY  ******************************************

    public void setFrequency(ArrayList<String> freq) {
        prefs_edit.putString("freq", String.valueOf(freq));
        prefs_edit.commit();
    }

    public String getFreqList() {
        return prefs.getString("freq", "");
    }

    //*************************  DIAGNOSIS  ****************************************

    public void setDiagList(ArrayList<ArrayList<String>> diag) {
        prefs_edit.putString("diag", String.valueOf(diag));
        prefs_edit.commit();
    }

    public String getDiagList() {
        return prefs.getString("diag", "");
    }


    //************************************  SURVEYS  **************************************


    public void setSymptomSurveyDate(String sdate) {
        prefs_edit.putString("sdate", sdate);
        prefs_edit.commit();
    }

    public String getSymptomSurveyDate() {
        return prefs.getString("sdate", "");
    }


    //**************************  ARMS-7 Survey  *************************************

    public String getArmsSurveyAnswers() {
        return prefs.getString("armsAns", "");
    }

    public void setArmsSurveyAnswers(int[] armsAns) {
        prefs_edit.putString("armsAns", Arrays.toString(armsAns));
        prefs_edit.commit();
    }

                    //************  Date  ***********

    public void setArmsDate(String armsDate) {
        prefs_edit.putString("armsDate", armsDate);
        prefs_edit.commit();
    }

    public String getArmsDate() {
        return prefs.getString("armsDate", "");
    }



    //**************************  Lifestyle Survey  *************************************

    public String getLifestyleSurveyAnswersRW() {
        return prefs.getString("answersRW", "");
    }

    public void setLifestyleSurveyAnswersRW(int[] answersRW) {
        prefs_edit.putString("answersRW", Arrays.toString(answersRW));
        prefs_edit.commit();
    }

    public void setLifestyleSurveyAnswers(int[] answers) {
        this.lifestyleSurveyAnswers = answers;
    }

    public void setLifestyleSurveyAnswersString(ArrayList<String> answersRW) {
        String lastEntry = answersRW.get(answersRW.size() - 1);
        int ind = lastEntry.indexOf(",");
        int count = 0;
        while (ind != -1) {
            if (ind == 1) {
                this.lifestyleSurveyAnswers[count] = Integer.parseInt(String.valueOf(lastEntry.charAt(1)));
            } else {
                this.lifestyleSurveyAnswers[count] = Integer.parseInt(lastEntry.substring(1, ind));
                Log.e("non-one", lastEntry.substring(1, ind));
            }
            lastEntry = lastEntry.substring(ind + 1);
            Log.e("latest", lastEntry);
            ind = lastEntry.indexOf(",");
            count++;
        }
        if (lastEntry.length() > 2) {
            this.lifestyleSurveyAnswers[count] = Integer.parseInt(lastEntry.substring(1, lastEntry.length() - 1));
        } else {
            this.lifestyleSurveyAnswers[count] = Integer.parseInt(String.valueOf(lastEntry.charAt(1)));
        }
        //Log.e("Intake Life",answersRW.toString());
        //Log.e("OutTake", Arrays.toString(lifestyleSurveyAnswers));
    }

    //************  Date  ***********

    public void setLifestyleDate(String date) {
        prefs_edit.putString("date", date);
        prefs_edit.commit();
    }

    public String getLifestyleDate() {
        return prefs.getString("date", "");
    }

    //*************************  Literacy Survey  **************************************

    private int[] literacySurveyAnswers;// = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    public String getLiteracySurveyAnswers() {
        return prefs.getString("answers", "");
    }

    public void setLiteracySurveyAnswers(int[] answers) {
        prefs_edit.putString("answers", Arrays.toString(answers));
        prefs_edit.commit();
    }

    public void setLiteracySurveyAnswersString(ArrayList<String> answersRW) {
        String lastEntry = answersRW.get(answersRW.size() - 1);
        int ind = lastEntry.indexOf(",");
        int count = 0;
        while (ind != -1) {
            if (ind == 1) {
                this.literacySurveyAnswers[count] = Integer.parseInt(String.valueOf(lastEntry.charAt(1)));

            } else {
                this.literacySurveyAnswers[count] = Integer.parseInt(lastEntry.substring(1, ind));
                Log.e("non-one", lastEntry.substring(1, ind));
            }
            lastEntry = lastEntry.substring(ind + 1);
            Log.e("latest", lastEntry);
            ind = lastEntry.indexOf(",");
            count++;
        }
        if (lastEntry.length() > 2) {
            this.literacySurveyAnswers[count] = Integer.parseInt(lastEntry.substring(1, lastEntry.length() - 1));
        } else {
            this.literacySurveyAnswers[count] = Integer.parseInt(String.valueOf(lastEntry.charAt(1)));
        }
        //Log.e("Lit Answers", Arrays.toString(literacySurveyAnswers));
    }

    //************  Date  ***********

    public void setLiteracyDate(String litdate) {
        prefs_edit.putString("litdate", litdate);
        prefs_edit.commit();
    }

    public String getLiteracyDate() {
        return prefs.getString("litdate", "");
    }

    //**********************************  Days to Send  ***************************************

    public void changeDaystoSend(Integer daysChosen) {
        this.daysToSend = daysChosen;
    }

    public Integer getDaystoSend() {
        return daysToSend;
    }

    //*********************************  SEVEN DAYS  ********************************************

    public void setSevenDay(ArrayList<String> sevenDays, ArrayList<ArrayList<String>> medicationIntake) {
        this.sevenDayList = sevenDays;
        this.medicationIntake = medicationIntake;
    }

    public Pair<ArrayList<String>, ArrayList<ArrayList<String>>> getWeek() {
        String data = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD");
        Boolean week;
        if (sevenDayList.size() > 0) {
            week = false;
        } else {
            week = true;
        }

        Date comp = null;
        Date start = null;
        //int arrayIndex = 0;
        while (!week) {
            try {
                comp = formatter.parse(sevenDayList.get(0));
                start = formatter.parse(data);
            } catch (ParseException e) {
                Log.e("Failure", "YEp");
                e.printStackTrace();
            }
            int dif = (int) TimeUnit.DAYS.convert(start.getTime() - comp.getTime(), TimeUnit.MILLISECONDS);
            if (dif < 7) {
                week = true;
            } else {
                medicationIntake.remove(0);
                sevenDayList.remove(0);
            }
            if (sevenDayList.size() == 0) {
                week = true;
            }
        }
        Log.e("SevenDayList", sevenDayList.toString());
        return new Pair<>(sevenDayList, medicationIntake);
    }

    //********************************************************************************************
}
