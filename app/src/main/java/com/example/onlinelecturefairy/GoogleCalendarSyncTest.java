package com.example.onlinelecturefairy;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.onlinelecturefairy.grade.Grade;
import com.example.onlinelecturefairy.notice.Notice;
import com.example.onlinelecturefairy.onlinelecture.OnlineLecture;
import com.example.onlinelecturefairy.ui.onlinelecture.OnlineLectureAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class GoogleCalendarSyncTest extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {


    /**
     * Google Calendar API에 접근하기 위해 사용되는 구글 캘린더 API 서비스 객체
     */

    private com.google.api.services.calendar.Calendar mService = null;

    /**
     * Google Calendar API 호출 관련 메커니즘 및 AsyncTask을 재사용하기 위해 사용
     */
    private int mID = 0;


    GoogleAccountCredential mCredential;
    private TextView mStatusText;
    private TextView mResultText;
    private Button mGetEventButton;
    private Button mAddEventButton;
    private Button mAddCalendarButton;
    private Button mGetEveryTime;
    private Button mGetBlackBoard_login;
    private Button mGetBlackBoard_test;
    private Button mGetBlackBoard_web;
    private Button mGetBlackBoard_grade;

    ProgressDialog mProgress;


    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;


    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    /*
    everytime crawling variable
     */
    private String userIdentifier;

    //배열의 순서와 과목의 순서가 일치한다.
    private String[] arrSubId;
    private String[] arrSubInfo; // 수업의 날짜, 시간, 장소
    private String result;
    private int numOfSubject;

    /*
    blackboard crawling variable
     */
    private Map<String, String> loginCookie;
    private String blackboard_user_id;
    private String blackboard_user_password;
    private boolean user_already_login = false;
    private String[] blackboard_subject;
    private String[] blackboard_noticeTitle;
    private String[] blackboard_noticeLink;
    private String lectureInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.google_calendar_sync_test);

        mAddCalendarButton = (Button) findViewById(R.id.button_main_add_calendar);
        mAddEventButton = (Button) findViewById(R.id.button_main_add_event);
        mGetEventButton = (Button) findViewById(R.id.button_main_get_event);

        mStatusText = (TextView) findViewById(R.id.textview_main_status);
        mResultText = (TextView) findViewById(R.id.textview_main_result);


        /**
         * 버튼 클릭으로 동작 테스트
         */
        mAddCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddCalendarButton.setEnabled(false);
                mStatusText.setText("");
                mID = 1;           //캘린더 생성
                getResultsFromApi();
                mAddCalendarButton.setEnabled(true);
            }
        });


        mAddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddEventButton.setEnabled(false);
                mStatusText.setText("");
                mID = 2;        //이벤트 생성
                getResultsFromApi();
                mAddEventButton.setEnabled(true);
            }
        });


        mGetEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetEventButton.setEnabled(false);
                mStatusText.setText("");
                mID = 3;        //이벤트 가져오기
                getResultsFromApi();
                mGetEventButton.setEnabled(true);
            }
        });


        // Google Calendar API의 호출 결과를 표시하는 TextView를 준비
        mResultText.setVerticalScrollBarEnabled(true);
        mResultText.setMovementMethod(new ScrollingMovementMethod());

        mStatusText.setVerticalScrollBarEnabled(true);
        mStatusText.setMovementMethod(new ScrollingMovementMethod());
        mStatusText.setText("버튼을 눌러 테스트를 진행하세요.");


        // Google Calendar API 호출중에 표시되는 ProgressDialog
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Google Calendar API 호출 중입니다.");


        // Google Calendar API 사용하기 위해 필요한 인증 초기화( 자격 증명 credentials, 서비스 객체 )
        // OAuth 2.0를 사용하여 구글 계정 선택 및 인증하기 위한 준비
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(),
                Arrays.asList(SCOPES)
        ).setBackOff(new ExponentialBackOff()); // I/O 예외 상황을 대비해서 백오프 정책 사용


        //everyTime crawling test
        final Context context = this;
        mGetEveryTime = (Button) findViewById(R.id.everyTime);
        mGetEveryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences appData = PreferenceManager.getDefaultSharedPreferences(context);
                String everytimeAddress = appData.getString("everytimeAddress", "");
                if (everytimeAddress.equals("")) {
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.prompts, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    alertDialogBuilder.setView(promptsView);
                    final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                    //set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    (dialog, id) -> {
                                        //get user input and set everytime URL.
                                        String[] temp = userInput.getText().toString().split("@");
                                        userIdentifier = temp[1];
                                        CrawlingEveryTime crw = new CrawlingEveryTime();
                                        crw.execute();
                                        mGetEveryTime.setEnabled(false);
                                        mStatusText.setText("");
                                        mID = 5;        //이벤트 생성
                                        getResultsFromApi();
                                        mGetEveryTime.setEnabled(true);
                                    })
                            .setNegativeButton("Cancel",
                                    (dialog, which) -> dialog.cancel());
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.show();
                } else {
                    String[] temp = everytimeAddress.split("@");
                    userIdentifier = temp[1];
                    CrawlingEveryTime crw = new CrawlingEveryTime();
                    crw.execute();
                    mGetEveryTime.setEnabled(false);
                    mStatusText.setText("");
                    mID = 5;        //이벤트 생성
                    getResultsFromApi();
                    mGetEveryTime.setEnabled(true);
                }
            }
        });

        mGetBlackBoard_login = (Button) findViewById(R.id.blackBoard_login);
        mGetBlackBoard_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt2, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setView(promptsView);
                final EditText userInput1 = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput1);
                final EditText userInput2 = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput2);
                //set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //get user id and password
                                        blackboard_user_id = userInput1.getText().toString();
                                        blackboard_user_password = userInput2.getText().toString();

                                        //비어있으면 자동로그인 해놓음 귀찮아서
                                        if (blackboard_user_id.matches(""))
                                            blackboard_user_id = "12181637";
                                        if (blackboard_user_password.matches(""))
                                            blackboard_user_password = "!dlstjd1105";
                                        CrawlingBlackBoard crw = new CrawlingBlackBoard();
                                        crw.execute();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            }

        });

        /**
         * 블랙보드 웹강 처리 버튼 : CrawlingBlackBoardWeb 실행
         */
        mGetBlackBoard_web = (Button) findViewById(R.id.blackBoard_web);
        mGetBlackBoard_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrawlingBlackBoardWeb crw = new CrawlingBlackBoardWeb();
                crw.execute();


//                mGetBlackBoard_web.setEnabled(false);
//                mStatusText.setText("");
//                mID = 6;
//                getResultsFromApi();
//                mGetBlackBoard_web.setEnabled(true);
            }

        });

        /**
         * 구글계정에서 캘린더 가져오기
         */
        mGetBlackBoard_test = (Button) findViewById(R.id.blackBoard_test);
        mGetBlackBoard_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddCalendarButton.setEnabled(false);
                mStatusText.setText("");
                mID = 4;           //사용자의 구글 계정에서 일정 가져오기
                getResultsFromApi();
                mAddCalendarButton.setEnabled(true);
            }
        });
        //todo 디버깅 버튼 여기있음
        /**
         * 블랙보드 웹강 처리 버튼 : CrawlingBlackBoardWeb 실행
         */
        mGetBlackBoard_grade = (Button) findViewById(R.id.blackBoard_grade);
        mGetBlackBoard_grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetBlackBoard_grade.setEnabled(false);
                deleteCalendar();
                mGetBlackBoard_grade.setEnabled(true);
            }

        });
    }

    private void deleteCalendar() {

        if (!isGooglePlayServicesAvailable()) { // Google Play Services를 사용할 수 없는 경우

            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) { // 유효한 Google 계정이 선택되어 있지 않은 경우

            chooseAccount();
        } else if (!isDeviceOnline()) {    // 인터넷을 사용할 수 없는 경우

            mStatusText.setText("No network connection available.");
        } else {

            // Google Calendar API 호출
            new deleteCal(this, mCredential).execute();
        }
        return;
    }


    /**
     * crawling everyTime url
     * crawling한 data를 일정화시킨다.
     */
    ProgressDialog progressDialog;

    private class CrawlingEveryTime extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GoogleCalendarSyncTest.this);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            result = "";
            for (int i = 0; i < numOfSubject; i++) {
                result += arrSubInfo[i];
                result += "\n";
            }
            result += numOfSubject;
            mResultText.setText(result);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                Document doc = Jsoup.connect("https://everytime.kr/find/timetable/table/friend")
                        .data("identifier", userIdentifier)
                        .data("friendInfo", "true")
                        .parser(Parser.xmlParser())
                        .post();


                //initialize information (과목수가 최대 20개라고 가정함.)
                arrSubId = new String[20];
                arrSubInfo = new String[20];

                //select subject id
                int idx = 0;
                numOfSubject = 0;
                String target = "subject";
                Elements selector = doc.select(target);
                for (Element e : selector) {
                    arrSubId[idx] = e.attr("id");
                    idx++;
                    numOfSubject++;
                }
                Event everytime = new Event();

                //select subject data name, professor, day, startTime, endTime, place
                int subIdx = 0;
                for (int i = 0; i < numOfSubject; i++) {


                    //get subject name
                    target = "subject#" + arrSubId[i];
                    target += " name";
                    selector = doc.select(target);
                    String subject_name = selector.attr("value");


                    //get professor name
                    target = "subject#" + arrSubId[i];
                    target += " professor";
                    selector = doc.select(target);
                    String professor_name = selector.attr("value");


                    //get subject data = (day, startTime, endTime, place)
                    target = "subject#" + arrSubId[i] + " data";
                    selector = doc.select(target);
                    for (Element e : selector) {
                        arrSubInfo[subIdx] = "";
                        arrSubInfo[subIdx] += subject_name + "@";
                        arrSubInfo[subIdx] += professor_name + "@";
                        //get day
                        int intDay = Integer.parseInt(e.attr("day"));
                        if (intDay == 0) { // 0 == Monday
                            arrSubInfo[subIdx] += "2@";
                        } else if (intDay == 1) {
                            arrSubInfo[subIdx] += "3@";
                        } else if (intDay == 2) {
                            arrSubInfo[subIdx] += "4@";
                        } else if (intDay == 3) {
                            arrSubInfo[subIdx] += "5@";
                        } else if (intDay == 4) {
                            arrSubInfo[subIdx] += "6@";
                        } else if (intDay == 5) {
                            arrSubInfo[subIdx] += "7@";
                        } else if (intDay == 6) {
                            arrSubInfo[subIdx] += "1@";
                        }


                        //get startTime
                        String Hour;
                        String Minute;
                        int calHour = Integer.parseInt(e.attr("starttime"));
                        Hour = Integer.toString(calHour / 12);
                        if (calHour % 12 == 0) {
                            Minute = "00";
                        } else {
                            Minute = Integer.toString((calHour % 12) * 5);
                        }
                        arrSubInfo[subIdx] += Hour + ":" + Minute;
                        arrSubInfo[subIdx] += "@";

                        //get endTime
                        calHour = Integer.parseInt(e.attr("endtime"));
                        Hour = Integer.toString(calHour / 12);
                        if (calHour % 12 == 0) {
                            Minute = "00";
                        } else {
                            Minute = Integer.toString((calHour % 12) * 5);
                        }
                        arrSubInfo[subIdx] += Hour + ":" + Minute;

                        //get place
                        arrSubInfo[subIdx] += "@" + e.attr("place");
                        arrSubInfo[subIdx] += "\n";
                        subIdx++;
                    }


                }
                numOfSubject = subIdx;
            } catch (IOException o) {
                o.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 다음 사전 조건을 모두 만족해야 Google Calendar API를 사용할 수 있다.
     * <p>
     * 사전 조건
     * - Google Play Services 설치
     * - 유효한 구글 계정 선택
     * - 안드로이드 디바이스에서 인터넷 사용 가능
     * <p>
     * 하나라도 만족하지 않으면 해당 사항을 사용자에게 알림.
     */
    private String getResultsFromApi() {

        if (!isGooglePlayServicesAvailable()) { // Google Play Services를 사용할 수 없는 경우

            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) { // 유효한 Google 계정이 선택되어 있지 않은 경우

            chooseAccount();
        } else if (!isDeviceOnline()) {    // 인터넷을 사용할 수 없는 경우

            mStatusText.setText("No network connection available.");
        } else {

            // Google Calendar API 호출
            new MakeRequestTask(this, mCredential).execute();
        }
        return null;
    }

    /**
     * crawling blackboard url
     */
    private class CrawlingBlackBoard extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GoogleCalendarSyncTest.this);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mResultText.setText(result);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36";


                Connection.Response loginPageResponse = Jsoup.connect("https://learn.inha.ac.kr/webapps/login/")
                        .timeout(3000)
                        .header("Origin", "https://learn.inha.ac.kr")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .data("user_id", blackboard_user_id, "password", blackboard_user_password)
                        .method(Connection.Method.POST)
                        .execute();
                Map<String, String> loginTryCookie = loginPageResponse.cookies();

                Map<String, String> userData = new HashMap<>();
                userData.put("user_id", blackboard_user_id);
                userData.put("password", blackboard_user_password);

                Connection.Response res = Jsoup.connect("https://learn.inha.ac.kr/webapps/login/")
                        .userAgent(userAgent)
                        .timeout(3000)
                        .header("Origin", "https://learn.inha.ac.kr")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .cookies(loginTryCookie)
                        .data(userData)
                        .method(Connection.Method.POST)
                        .execute();
                loginCookie = res.cookies();
                if (loginCookie.isEmpty()) {
                    result = "로그인 실패";
                    return null;
                }
                Document blackBoard = Jsoup.connect("https://learn.inha.ac.kr/webapps/portal/execute/tabs/tabAction")
                        .userAgent(userAgent)
                        .header("Origin", "https://learn.inha.ac.kr")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
                        .data("action", "refreshAjaxModule")
                        .data("modId", "_1_1")
                        .data("tabId", "_1_1")
                        .data("tab_tab_group_id", "_1_1")
                        .parser(Parser.xmlParser())
                        .post();
                String temp1 = "";
                Element contest = blackBoard.select("contents").first();
                Document doc = Jsoup.parse(contest.text().split("<!-- Display course/org announcements -->")[1]);
                result = "";
                int idx = 0;
                int numOfSub;
                blackboard_subject = new String[10];
                blackboard_noticeTitle = new String[10];
                blackboard_noticeLink = new String[10];
                //selecting subject name

                Elements elem = doc.select("h3");
                for (Element e : elem) {
                    blackboard_subject[idx] = e.text().split("\\)")[1];
                    idx++;
                }
                numOfSub = idx;

                //selecting notice of each subject
                String[] temp2 = doc.html().split("</div>");
                for (int i = 0; i < temp2.length; i++) {
                    doc = Jsoup.parse(temp2[i]);
                    elem = doc.select("li");
                    blackboard_noticeTitle[i] = "";
                    for (Element e : elem) {
                        blackboard_noticeTitle[i] += e.text();
                        blackboard_noticeTitle[i] += "\n";
                    }

                    blackboard_noticeLink[i] = "";
                    elem = doc.select("a");
                    for (Element e : elem) {
                        blackboard_noticeLink[i] = e.attr("href");
                    }
                }

                String[] arrTitle;
                result = "";
                ArrayList<Notice> arrNotice = new ArrayList<>();
                String lecture;
                String title;
                String calendar;
                String description;
                for (int i = 0; i < numOfSub; i++) {
                    arrTitle = blackboard_noticeTitle[i].split("\n");
                    Document course = Jsoup.connect("https://learn.inha.ac.kr/" + blackboard_noticeLink[i])
                            .userAgent(userAgent)
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                            .cookies(loginCookie)
                            .get();
                    Document docNotice = Jsoup.parse(course.select("ul#announcementList").html());

                    for (String s : arrTitle) {
                        elem = docNotice.select("li");
                        for (Element e : elem) {
                            if (e.text().contains(s)) {
                                title = e.text().split("게시 날짜:")[0];
                                calendar = (e.text().split("게시 날짜:")[1]).split("KST")[0];
                                lecture = e.text().split("게시한 곳:")[1];
                                description = (e.text().split("KST")[2]).split("작성자:")[0];
                                arrNotice.add(new Notice(lecture, title, calendar, description));
                            }
                        }


                    }
                }
                result = "";
                for (Notice n : arrNotice) {
                    result += "과목명 : " + n.getLecture() + "\n";
                    result += "제목 : " + n.getTitle() + "\n";
                    result += "게시 날짜: " + n.getCalendar() + "\n";
                    result += "내용: " + n.getDescription() + "\n";
                    result += "-------------------\n";
                }
            } catch (IOException o) {
                o.printStackTrace();
            }
            return null;
        }

    }

    private ArrayList<Grade> arrGrade;
    //TODO 함수 정의

    /**
     * get grade from blackboard after login
     */
    private class CrawlingBlackBoardGrade extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GoogleCalendarSyncTest.this);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mResultText.setText(result);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36";
                Document blackboard = Jsoup.connect("https://learn.inha.ac.kr/webapps/portal/execute/tabs/tabAction")
                        .userAgent(userAgent)
                        .header("Origin", "https://learn.inha.ac.kr")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
                        .data("action", "refreshAjaxModule")
                        .data("modId", "_3_1")
                        .data("tabId", "_1_1")
                        .data("tab_tab_group_id", "_1_1")
                        .parser(Parser.xmlParser())
                        .post();
                Element contest = blackboard.select("contents").first();
                Document doc = Jsoup.parse(contest.text());


                /**
                 * TODO: Calendar를 이용해서 현재 학기에 해당하는 과목의 주소만 들고오는 논리 구현
                 *
                 */

                //현재가 무슨학기인지 알아내자.
                String current_semester = "";
                java.util.Calendar cal = java.util.Calendar.getInstance();
                current_semester += cal.get(java.util.Calendar.YEAR);
                if (cal.get(java.util.Calendar.MONTH) >= 8) {
                    current_semester += "02";
                } else {
                    current_semester += "01";
                }

                //현재 수강중인 과목들의 course_id를 파싱
                Elements elem = doc.select("a");
                result = "";
                int numOfCourse = 0;
                //수강하는 과목의 수가 최대 100개라고 가정함.
                String[] course_id = new String[100];
                for (Element e : elem) {
                    String temp = e.text();
                    if (temp.contains(":") && temp.contains(current_semester)) {
                        temp = e.attr("href");
                        temp = temp.split("_")[1];
                        course_id[numOfCourse] = temp;
                        numOfCourse++;
                    }
                }
                result = "";

                //각 성적마다 '나의 성적'으로 들어가는 a 태그의 href 값 저장
                //'나의 성적' 의 href 값에 tool_id=_158_1 이 들어가있는 규칙성 확인


                ArrayList<String> arrGradeLink = new ArrayList<>();
                // "온라인 출결 확인"이 존재하는 과목 parsing
                for (int i = 0; i < numOfCourse; i++) {
                    Document course = Jsoup.connect("https://learn.inha.ac.kr/webapps/blackboard/execute/modulepage/view?course_id=_" + course_id[i] + "_1")
                            .userAgent(userAgent)
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                            .cookies(loginCookie)
                            .get();
                    elem = course.select("a");
                    for (Element e : elem) {
                        if (e.attr("href").contains("tool_id=_158_1")) {
                            arrGradeLink.add(e.attr("href"));
                        }
                    }
                }
                result = "";
                arrGrade = new ArrayList<>();
                for (String gradeLink : arrGradeLink) {
                    Document gradePage = Jsoup.connect("https://learn.inha.ac.kr" + gradeLink)
                            .userAgent(userAgent)
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                            .cookies(loginCookie)
                            .get();
                    elem = gradePage.select("div.sortable_item_row.graded_item_row.row.expanded");
                    for (Element e : elem) {
                        String lecture = ((gradePage.select("a.comboLink").text()).split("\\)")[1]).split("-")[0];
                        Document tempDoc = Jsoup.parse(e.html());
                        Element elem2;

                        String score = "";
                        elem2 = tempDoc.select("span.grade").first();
                        if (elem2 != null) score = elem2.text();

                        String scoreSub = "";
                        elem2 = tempDoc.select("span.pointsPossible.clearfloats").first();
                        if (elem2 != null) scoreSub = elem2.text();

                        String description = "";
                        String des_temp = "none";
                        elem2 = tempDoc.select("div.cell.gradable").first();
                        if (elem2 != null) description = elem2.text();
                        elem2 = tempDoc.select("div.itemCat").first();
                        if (elem2 != null) des_temp = elem2.text();
                        if (des_temp != "none")
                            description = description.replaceFirst(des_temp, "");

                        des_temp = "none";
                        elem2 = tempDoc.select("div.activityType").first();
                        if (elem2 != null) des_temp = elem2.text();
                        if (des_temp != "none")
                            description = description.replaceFirst(des_temp, "");

                        if (scoreSub == "") scoreSub = "none";

                        arrGrade.add(new Grade(lecture, score, scoreSub, description));
                    }

                }

                for (Grade g : arrGrade) {
                    result += g.getLecture() + "@" + g.getDescription() + "@" + g.getScore() + "@" + g.getScoreSub() + "\n";
                }
            } catch (IOException o) {
                o.printStackTrace();
            }
            return null;
        }

    }

    /**
     * 웹강 요정의 화룡점정!!
     */
    private List<OnlineLecture> lectures;

    private class CrawlingBlackBoardWeb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(GoogleCalendarSyncTest.this);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mResultText.setText(result);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36";
                Document blackboard = Jsoup.connect("https://learn.inha.ac.kr/webapps/portal/execute/tabs/tabAction")
                        .userAgent(userAgent)
                        .header("Origin", "https://learn.inha.ac.kr")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
                        .data("action", "refreshAjaxModule")
                        .data("modId", "_3_1")
                        .data("tabId", "_1_1")
                        .data("tab_tab_group_id", "_1_1")
                        .parser(Parser.xmlParser())
                        .post();

//                //CDATA parsing
//                String temp1 = "";
//                Elements contest = blackboard.select("contents");
//                for (Element e : contest) {
//                    temp1 += e.html();
//                }
//                temp1 = temp1.replace("<![CDATA[", "").replace("]]>", "");
//                temp2 = temp1.split("<!-- Display course/org announcements -->");
//
//
//                Document doc = Jsoup.parse(temp1);
                Element contest = blackboard.select("contents").first();
                Document doc = Jsoup.parse(contest.text());
                //현재 수강중인 과목들의 course_id를 파싱
                Elements elem = doc.select("a");
                result = "";
                int numOfCourse = 0;
                //수강하는 과목의 수가 최대 100개라고 가정함.
                String[] course_id = new String[100];
                for (Element e : elem) {
                    String temp = e.text();
                    if (temp.contains(":")) {
                        temp = e.attr("href");
                        temp = temp.split("_")[1];
                        course_id[numOfCourse] = temp;
                        numOfCourse++;
                    }
                }

                int numOfWeb_temp = 0;
                int numOfWeb = 0;
                String[] course_id_web_temp = new String[100];
                String[] course_id_web = new String[100];
                result = "";
                // "온라인 출결 확인"이 존재하는 과목 parsing
                for (int i = 0; i < numOfCourse; i++) {
                    Document course = Jsoup.connect("https://learn.inha.ac.kr/webapps/blackboard/execute/modulepage/view?course_id=_" + course_id[i] + "_1")
                            .userAgent(userAgent)
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                            .cookies(loginCookie)
                            .get();
                    elem = course.select("span");
                    for (Element e : elem) {
                        if (e.text().contains("온라인 출결 확인")) {
                            course_id_web_temp[numOfWeb_temp] = course_id[i];
                            numOfWeb_temp++;
                        }
                    }
                }

                // "온라인 출결 확인" 메뉴만 존재하고 출결이 없는 과목 거르기
                for (int i = 0; i < numOfWeb_temp; i++) {
                    Document course = Jsoup.connect("https://learn.inha.ac.kr/webapps/bbgs-OnlineAttendance-BBLEARN/app/atdView?course_id=_" + course_id_web_temp[i] + "_1")
                            .userAgent(userAgent)
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                            .cookies(loginCookie)
                            .get();
                    elem = course.select("div");
                    boolean isCourseWeb = true;
                    for (Element e : elem) {
                        if (!(e.attr("class").contains("noItems"))) {
                            isCourseWeb = true;
                        } else {
                            isCourseWeb = false;
                        }
                    }
                    if (isCourseWeb) {
                        course_id_web[numOfWeb] = course_id_web_temp[i];
                        numOfWeb++;
                    }
                }


                int resultNum = 0;
                String webLectureName = "";
                lectureInfo = "";
                lectures = new ArrayList<>();
                ArrayList<OnlineLecture> child;
                for (int i = 0; i < numOfWeb; i++) {
                    java.util.Calendar today = java.util.Calendar.getInstance();
                    java.util.Calendar webStartDate = java.util.Calendar.getInstance();
                    java.util.Calendar webEndDate = java.util.Calendar.getInstance();
                    Document course = Jsoup.connect("https://learn.inha.ac.kr/webapps/bbgs-OnlineAttendance-BBLEARN/app/atdView?sortDir=ASCENDING&showAll=true&editPaging=false&course_id=_" + course_id_web[i] + "_1")
                            .userAgent(userAgent)
                            .cookies(loginCookie)
                            .get();
                    elem = course.select("tr");

                    webLectureName = "";

                    int startMonth, startDay, startHour, startMinute;
                    String strStartDate;
                    int endMonth, endDay, endHour, endMinute;
                    String strEndDate;

                    String lecture;
                    String week;
                    String date = "";
                    String pass;

                    String lecture_header = "";
                    String week_header = "";
                    String date_header = "";
                    String pass_header = "";

                    child = new ArrayList<>();
                    for (Element e : elem) {
                        date = "";
                        if (e.text().contains("XIN")) {
                            strStartDate = (e.text().split(" / ")[1]).split(" ~ ")[0];
                            strEndDate = (e.text().split(" / ")[1]).split(" ~ ")[1];

                            date += (strEndDate.split(" ")[0]).split("-")[0] + "년 "
                                    + (strEndDate.split(" ")[0]).split("-")[1] + "월 "
                                    + (strEndDate.split(" ")[0]).split("-")[2] + " 일";

                            endMonth = Integer.parseInt((strEndDate.split(" ")[0]).split("-")[1]);
                            endDay = Integer.parseInt((strEndDate.split(" ")[0]).split("-")[2]);
                            endHour = Integer.parseInt((strEndDate.split(" ")[1]).split(":")[0]);
                            endMinute = Integer.parseInt((strEndDate.split(" ")[1]).split(":")[1]);
                            webEndDate.set(java.util.Calendar.MONTH, endMonth - 1);
                            webEndDate.set(java.util.Calendar.DAY_OF_MONTH, endDay);
                            webEndDate.set(java.util.Calendar.HOUR, endHour);
                            webEndDate.set(java.util.Calendar.MINUTE, endMinute);

                            startMonth = Integer.parseInt((strStartDate.split(" ")[0]).split("-")[1]);
                            startDay = Integer.parseInt((strStartDate.split(" ")[0]).split("-")[2]);
                            startHour = Integer.parseInt((strStartDate.split(" ")[1]).split(":")[0]);
                            startMinute = Integer.parseInt((strStartDate.split(" ")[1]).split(":")[1]);
                            webStartDate.set(java.util.Calendar.MONTH, startMonth - 1);
                            webStartDate.set(java.util.Calendar.DAY_OF_MONTH, startDay);
                            webStartDate.set(java.util.Calendar.HOUR, startHour);
                            webStartDate.set(java.util.Calendar.MINUTE, startMinute);

                            if (today.compareTo(webEndDate) == -1 && (today.compareTo(webStartDate) == 1)) {
//                                webEndDate.add(java.util.Calendar.DATE, +7);
//                                webEndDate.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
//                                webEndDate.add(java.util.Calendar.DATE, -7);
//                                SimpleDateFormat simpledateformat;
//                                simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREA);

                                webLectureName = ((e.text().split(" > ")[0]).split("XIN")[0]).split(">")[1];
                                String match2 = "\\s{2,}";
                                webLectureName = webLectureName.replaceAll(match2, "");


                                lecture_header = ((course.select("a.comboLink").text()).split("\\)")[1]).split("-")[0];
                                week_header = webLectureName;
                                date_header = date;
                                pass_header = Character.toString(e.text().charAt(e.text().length() - 1));

                                lecture = ((course.select("a.comboLink").text()).split("\\)")[1]).split("-")[0];
                                Log.e(TAG, "lecture :" + lecture + "@" + date);
                                week = (e.text().split("XIN -")[1]).split("/")[0];
                                pass = Character.toString(e.text().charAt(e.text().length() - 1));
                                child.add(new OnlineLecture(lecture, week, date, pass, OnlineLectureAdapter.CHILD));
                                //lectureInfo += ((course.select("a.comboLink").text()).split("\\)")[1]).split("-")[0] + "@" + webLectureName + "@" + simpledateformat.format(webEndDate.getTime()) + "\n";
                            } else if ((today.compareTo(webStartDate) == 1)) {

                                webLectureName = ((e.text().split(" > ")[0]).split("XIN")[0]).split(">")[1];
                                String match2 = "\\s{2,}";
                                webLectureName = webLectureName.replaceAll(match2, "");

                                lecture = ((course.select("a.comboLink").text()).split("\\)")[1]).split("-")[0];
                                Log.e(TAG, "lecture :" + lecture);
                                week = (e.text().split("XIN - ")[1]).split("/")[0];
                                pass = Character.toString(e.text().charAt(e.text().length() - 1));
                                child.add(new OnlineLecture(lecture, week, date, pass, OnlineLectureAdapter.CHILD));
                            } else {
                                break;
                            }
                        }
                    }
                    lectures.add(new OnlineLecture(lecture_header, week_header, date_header, pass_header, OnlineLectureAdapter.HEADER, child));
                }
                result = "";
                for (OnlineLecture lec : lectures) {
                    result += "Header: " + lec.getLecture() + " " + lec.getWeek() + " " + lec.getDate() + " " + lec.getPass() + "\n";
                    for (OnlineLecture lec2 : lec.invisibleChildren) {
                        result += "Child: " + lec2.getLecture() + " " + lec2.getWeek() + " " + lec2.getDate() + " " + lec2.getPass() + "\n";
                    }
                    result += "---------------------------------------\n";
                }
            } catch (IOException o) {
                o.printStackTrace();
            }
            return null;
        }

    }


    /**
     * 안드로이드 디바이스에 최신 버전의 Google Play Services가 설치되어 있는지 확인
     */
    private boolean isGooglePlayServicesAvailable() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }


    /*
     * Google Play Services 업데이트로 해결가능하다면 사용자가 최신 버전으로 업데이트하도록 유도하기위해
     * 대화상자를 보여줌.
     */
    private void acquireGooglePlayServices() {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {

            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /*
     * 안드로이드 디바이스에 Google Play Services가 설치 안되어 있거나 오래된 버전인 경우 보여주는 대화상자
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode
    ) {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        Dialog dialog = apiAvailability.getErrorDialog(
                GoogleCalendarSyncTest.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        );
        dialog.show();
    }


    /*
     * Google Calendar API의 자격 증명( credentials ) 에 사용할 구글 계정을 설정한다.
     *
     * 전에 사용자가 구글 계정을 선택한 적이 없다면 다이얼로그에서 사용자를 선택하도록 한다.
     * GET_ACCOUNTS 퍼미션이 필요하다.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {

        // GET_ACCOUNTS 권한을 가지고 있다면
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {


            // SharedPreferences에서 저장된 Google 계정 이름을 가져온다.
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {

                // 선택된 구글 계정 이름으로 설정한다.
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {


                // 사용자가 구글 계정을 선택할 수 있는 다이얼로그를 보여준다.
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }


            // GET_ACCOUNTS 권한을 가지고 있지 않다면
        } else {


            // 사용자에게 GET_ACCOUNTS 권한을 요구하는 다이얼로그를 보여준다.(주소록 권한 요청함)
            EasyPermissions.requestPermissions(
                    (Activity) this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }



    /*
     * 구글 플레이 서비스 업데이트 다이얼로그, 구글 계정 선택 다이얼로그, 인증 다이얼로그에서 되돌아올때 호출된다.
     */

    @Override
    protected void onActivityResult(
            int requestCode,  // onActivityResult가 호출되었을 때 요청 코드로 요청을 구분
            int resultCode,   // 요청에 대한 결과 코드
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {

            case REQUEST_GOOGLE_PLAY_SERVICES:

                if (resultCode != RESULT_OK) {

                    mStatusText.setText(" 앱을 실행시키려면 구글 플레이 서비스가 필요합니다."
                            + "구글 플레이 서비스를 설치 후 다시 실행하세요.");
                } else {

                    getResultsFromApi();
                }
                break;


            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;


            case REQUEST_AUTHORIZATION:

                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }


    /*
     * Android 6.0 (API 23) 이상에서 런타임 권한 요청시 결과를 리턴받음
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,  //requestPermissions(android.app.Activity, String, int, String[])에서 전달된 요청 코드
            @NonNull String[] permissions, // 요청한 퍼미션
            @NonNull int[] grantResults    // 퍼미션 처리 결과. PERMISSION_GRANTED 또는 PERMISSION_DENIED
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    /*
     * EasyPermissions 라이브러리를 사용하여 요청한 권한을 사용자가 승인한 경우 호출된다.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> requestPermissionList) {

        // 아무일도 하지 않음
    }


    /*
     * EasyPermissions 라이브러리를 사용하여 요청한 권한을 사용자가 거부한 경우 호출된다.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> requestPermissionList) {

        // 아무일도 하지 않음
    }


    /*
     * 안드로이드 디바이스가 인터넷 연결되어 있는지 확인한다. 연결되어 있다면 True 리턴, 아니면 False 리턴
     */
    private boolean isDeviceOnline() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }


    /*
     * 캘린더 이름에 대응하는 캘린더 ID를 리턴
     */
    private String getCalendarID(String calendarTitle) {

        String id = null;

        // Iterate through entries in calendar list
        String pageToken = null;
        do {
            CalendarList calendarList = null;
            try {
                calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<CalendarListEntry> items = calendarList.getItems();


            for (CalendarListEntry calendarListEntry : items) {

                if (calendarListEntry.getSummary().toString().equals(calendarTitle)) {

                    id = calendarListEntry.getId().toString();
                }
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        return id;
    }

    private class deleteCal extends AsyncTask<Void, Void, String> {

        private Exception mLastError = null;
        private GoogleCalendarSyncTest mActivity;
        List<String> eventStrings = new ArrayList<String>();
        List<Event> ourEventArray = new ArrayList<Event>();

        public deleteCal(GoogleCalendarSyncTest activity, GoogleAccountCredential credential) {

            mActivity = activity;

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            mService = new com.google.api.services.calendar.Calendar
                    .Builder(transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        @Override
        protected void onPostExecute(String result) {

            mProgress.hide();
            mResultText.setText(result);
        }

        @Override
        protected void onPreExecute() {
            // mStatusText.setText("");
            mProgress.show();
            mStatusText.setText("데이터 가져오는 중...");
            mResultText.setText("");
        }


        /*
         * 백그라운드에서 Google Calendar API 호출 처리
         */

        @Override
        protected String doInBackground(Void... params) {
            try {
                String calendarID = getCalendarID("CalendarTitle");
                if(calendarID == null){
                    result = "지울 캘린더가 없습니다.";
                }
                mService.calendarList().delete(calendarID).execute();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
            result="지웠음";
            return null;
        }
    }

    /*
     * 비동기적으로 Google Calendar API 호출
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, String> {

        private Exception mLastError = null;
        private GoogleCalendarSyncTest mActivity;
        List<String> eventStrings = new ArrayList<String>();
        List<Event> ourEventArray = new ArrayList<Event>();

        public MakeRequestTask(GoogleCalendarSyncTest activity, GoogleAccountCredential credential) {

            mActivity = activity;

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            mService = new com.google.api.services.calendar.Calendar
                    .Builder(transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }


        @Override
        protected void onPreExecute() {
            // mStatusText.setText("");
            mProgress.show();
            mStatusText.setText("데이터 가져오는 중...");
            mResultText.setText("");
        }


        /*
         * 백그라운드에서 Google Calendar API 호출 처리
         */

        @Override
        protected String doInBackground(Void... params) {
            try {

                if (mID == 1) {

                    return createCalendar();

                } else if (mID == 2) {

                    return addEvent();
                } else if (mID == 3) {

                    return getEvent();
                } else if (mID == 4) {

                    return getEventById(mCredential.getSelectedAccountName());
                } else if (mID == 5) {
                    return addEveryTimeEvent();
                } else if (mID == 6) {
                    return addBlackBoardEvent();
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }

            return null;
        }


        /*
         * CalendarTitle 이름의 캘린더에서 10개의 이벤트를 가져와 리턴
         */
        private String getEvent() throws IOException {


            DateTime now = new DateTime(System.currentTimeMillis());

            String calendarID = getCalendarID("CalendarTitle");
            if (calendarID == null) {

                return "캘린더를 먼저 생성하세요.";
            }


            Events events = mService.events().list(calendarID)//"primary")
                    .setMaxResults(10)
                    //.setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();


            for (Event event : items) {

                DateTime start = event.getStart().getDateTime();
                if (start == null) {

                    // 모든 이벤트가 시작 시간을 갖고 있지는 않다. 그런 경우 시작 날짜만 사용
                    start = event.getStart().getDate();
                }


                eventStrings.add(String.format("%s \n (%s)", event.getSummary(), start));
            }


            return eventStrings.size() + "개의 데이터를 가져왔습니다.";
        }

        /**
         * api event를 project 내의 event로 변환하여 관리
         *
         * @param calendarId
         * @return
         * @throws IOException
         */
        private String getEventById(String calendarId) throws IOException {


            DateTime now = new DateTime(System.currentTimeMillis());

            String calendarID = getCalendarID(calendarId);
            if (calendarID == null) {

                return "캘린더를 먼저 생성하세요.";
            }


            Events events = mService.events().list(calendarID)//"primary")
                    .setMaxResults(10)
                    //.setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();


            for (Event event : items) {

                DateTime start = event.getStart().getDateTime();
                DateTime end = event.getEnd().getDateTime();
                if (start == null) {
                    // 모든 이벤트가 시작 시간을 갖고 있지는 않다. 그런 경우 시작 날짜만 사용
                    start = event.getStart().getDate();
                }
                if (end == null) {
                    // 모든 이벤트가 시작 시간을 갖고 있지는 않다. 그런 경우 시작 날짜만 사용
                    end = event.getEnd().getDate();
                }
                Event tempEvent = event;
                ourEventArray.add(tempEvent);

            }

            for (Event event : ourEventArray) {
                //시작 시간을 가지고 있는 경우
                if (event.getEnd().getDateTime() == null) {
                    eventStrings.add(String.format("%s \n (%s) ~ (%s)", event.getSummary(), event.getStart().getDate(), event.getEnd().getDate()));
                } else {
                    eventStrings.add(String.format("%s \n (%s) ~ (%s)", event.getSummary(), event.getStart().getDateTime(), event.getEnd().getDateTime()));
                }
            }
            return eventStrings.size() + "개의 데이터를 가져왔습니다.";
        }

        /*
         * 선택되어 있는 Google 계정에 새 캘린더를 추가한다.
         */
        private String createCalendar() throws IOException {

            String ids = getCalendarID("CalendarTitle");

            if (ids != null) {

                return "이미 캘린더가 생성되어 있습니다. ";
            }

            // 새로운 캘린더 생성
            com.google.api.services.calendar.model.Calendar calendar = new Calendar();

            // 캘린더의 제목 설정
            calendar.setSummary("CalendarTitle");


            // 캘린더의 시간대 설정
            calendar.setTimeZone("Asia/Seoul");

            // 구글 캘린더에 새로 만든 캘린더를 추가
            Calendar createdCalendar = mService.calendars().insert(calendar).execute();

            // 추가한 캘린더의 ID를 가져옴.
            String calendarId = createdCalendar.getId();


            // 구글 캘린더의 캘린더 목록에서 새로 만든 캘린더를 검색
            CalendarListEntry calendarListEntry = mService.calendarList().get(calendarId).execute();

            // 캘린더의 배경색을 파란색으로 표시  RGB
            calendarListEntry.setBackgroundColor("#0000ff");

            // 변경한 내용을 구글 캘린더에 반영
            CalendarListEntry updatedCalendarListEntry =
                    mService.calendarList()
                            .update(calendarListEntry.getId(), calendarListEntry)
                            .setColorRgbFormat(true)
                            .execute();

            // 새로 추가한 캘린더의 ID를 리턴
            return "캘린더가 생성되었습니다.";
        }


        @Override
        protected void onPostExecute(String output) {

            mProgress.hide();
            mStatusText.setText(output);

            if (mID == 3) mResultText.setText(TextUtils.join("\n\n", eventStrings));
            if (mID == 4) mResultText.setText(TextUtils.join("\n\n", eventStrings));
            if (mID == 5) mResultText.setText(TextUtils.join("\n\n", eventStrings));
        }


        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleCalendarSyncTest.REQUEST_AUTHORIZATION);
                } else {
                    mStatusText.setText("MakeRequestTask The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                mStatusText.setText("요청 취소됨.");
            }

        }


        private String addEvent() {


            String calendarID = getCalendarID("CalendarTitle");

            if (calendarID == null) {

                return "캘린더를 먼저 생성하세요.";

            }

            Event event = new Event()
                    .setSummary("구글 캘린더 테스트")
                    .setLocation("서울시")
                    .setDescription("캘린더에 이벤트 추가하는 것을 테스트합니다.");


            java.util.Calendar calander;

            calander = java.util.Calendar.getInstance();
            SimpleDateFormat simpledateformat;
            //simpledateformat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ", Locale.KOREA);
            // Z에 대응하여 +0900이 입력되어 문제 생겨 수작업으로 입력
            simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREA);
            String datetime = simpledateformat.format(calander.getTime());

            DateTime startDateTime = new DateTime(datetime);
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Asia/Seoul");
            event.setStart(start);

            Log.d("@@@", datetime);


            DateTime endDateTime = new DateTime(datetime);
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Asia/Seoul");
            event.setEnd(end);

            //String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
            //fragment_event.setRecurrence(Arrays.asList(recurrence));


            try {
                event = mService.events().insert(calendarID, event).execute();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "Exception : " + e.toString());
            }
            System.out.printf("EventViewModel created: %s\n", event.getHtmlLink());
            Log.e("EventViewModel", "created : " + event.getHtmlLink());
            String eventStrings = "created : " + event.getHtmlLink();
            return eventStrings;
        }

        /**
         * In google calendar,
         * subjectName == summary
         * professorName == description
         * startTime == start
         * endTime == end
         * location == location
         */
        private String addEveryTimeEvent() {
            String subjectName;
            String professorName;
            String startTime;
            String endTime;
            String location;
            String[] temp;
            String calendarID = getCalendarID("CalendarTitle");
            java.util.Calendar calendar;


            SimpleDateFormat simpledateformat;
            //simpledateformat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ", Locale.KOREA);
            // Z에 대응하여 +0900이 입력되어 문제 생겨 수작업으로 입력
            simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREA);

            if (calendarID == null) {

                return "캘린더를 먼저 생성하세요.";

            }
            calendar = java.util.Calendar.getInstance();
            int tYear = calendar.get(calendar.YEAR);
            int tMonth = calendar.get(calendar.MONTH);
            int tDate = calendar.get(calendar.DATE);
            for (int i = 0; i < numOfSubject; i++) {
                calendar = java.util.Calendar.getInstance();
                temp = arrSubInfo[i].split("@");
                subjectName = temp[0];
                professorName = temp[1];
                int week = Integer.parseInt(temp[2]);
                startTime = temp[3];
                endTime = temp[4];
                location = temp[5];
                Event event = new Event()
                        .setSummary(subjectName)
                        .setLocation(location)
                        .setDescription(professorName);


                if (week == 1) {
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
                } else if (week == 2) {
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
                } else if (week == 3) {
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.TUESDAY);
                } else if (week == 4) {
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.WEDNESDAY);
                } else if (week == 5) {
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.THURSDAY);
                } else if (week == 6) {
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.FRIDAY);
                } else if (week == 7) {
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SATURDAY);
                }
                //set start time
                temp = startTime.split(":");
                int startHour = Integer.parseInt(temp[0]);
                int startMinute = Integer.parseInt(temp[1]);
                calendar.set(calendar.HOUR_OF_DAY, startHour);
                calendar.set(calendar.MINUTE, +startMinute);
                String datetime = simpledateformat.format(calendar.getTime());
                DateTime startDateTime = new DateTime(datetime);
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDateTime)
                        .setTimeZone("Asia/Seoul");
                event.setStart(start);

                //set start time  default한 시간을 위해 다시 초기화
                calendar = java.util.Calendar.getInstance();
                if (week == 1) {
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
                } else if (week == 2) {
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
                } else if (week == 3) {
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.TUESDAY);
                } else if (week == 4) {
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.WEDNESDAY);
                } else if (week == 5) {
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.THURSDAY);
                } else if (week == 6) {
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.FRIDAY);
                } else if (week == 7) {
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SATURDAY);
                }
                temp = endTime.split(":");
                int endHour = Integer.parseInt(temp[0]);
                int endMinute = Integer.parseInt(temp[1]);
                calendar.set(calendar.HOUR_OF_DAY, endHour);
                calendar.set(calendar.MINUTE, +endMinute);
                datetime = simpledateformat.format(calendar.getTime());
                DateTime endDateTime = new DateTime(datetime);
                EventDateTime end = new EventDateTime()
                        .setDateTime(endDateTime)
                        .setTimeZone("Asia/Seoul");
                event.setEnd(end);
                Random random = new Random();
                int cR = random.nextInt(11) + 1;
                event.setColorId(Integer.toString(cR));
                //String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
                //fragment_event.setRecurrence(Arrays.asList(recurrence));


                try {
                    event = mService.events().insert(calendarID, event).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception", "Exception : " + e.toString());
                }
            }
            String eventStrings = "시간표가 구글 캘린더 이번 주의 일정에 추가되었습니다.";


            return eventStrings;
        }

        /**
         * crawling 해온 사용자의 웹강 data를
         * api를 이용하여 event로 추가한다.
         *
         * @return
         */
        private String addBlackBoardEvent() {
            String subjectName;
            String week;
            String endTime;
            String calendarID = getCalendarID(mCredential.getSelectedAccountName());
            java.util.Calendar calendar;


            SimpleDateFormat simpledateformat;
            //simpledateformat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ", Locale.KOREA);
            // Z에 대응하여 +0900이 입력되어 문제 생겨 수작업으로 입력
            simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREA);

            if (calendarID == null) {
                return "사용자의 구글 계정이 존재하지 않습니다.";

            }
            String[] arrLectureInfo = lectureInfo.split("\n");
            for (String _lectureInfo : arrLectureInfo) {
                calendar = java.util.Calendar.getInstance();

                subjectName = _lectureInfo.split("@")[0];
                week = _lectureInfo.split("@")[1];
                endTime = _lectureInfo.split("@")[2];


                Event event = new Event()
                        .setSummary(subjectName)
                        .setDescription(week);

                DateTime endDateTime = new DateTime(endTime);

                EventDateTime end = new EventDateTime()
                        .setDateTime(endDateTime)
                        .setTimeZone("Asia/Seoul");
                event.setStart(end);
                event.setEnd(end);

                //set reminder
                EventReminder[] reminderOverrides = new EventReminder[]{
                        new EventReminder().setMethod("popup").setMinutes(1080)
                };
                Event.Reminders reminders = new Event.Reminders()
                        .setUseDefault(false)
                        .setOverrides(Arrays.asList(reminderOverrides));

                event.setReminders(reminders);

                Random random = new Random();
                int cR = random.nextInt(11) + 1;
                event.setColorId(Integer.toString(cR));
                try {
                    event = mService.events().insert(calendarID, event).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception", "Exception : " + e.toString());
                }
            }
            String eventStrings = "시간표가 구글 캘린더 이번 주의 일정에 추가되었습니다.";


            return eventStrings;
        }
    }
}

