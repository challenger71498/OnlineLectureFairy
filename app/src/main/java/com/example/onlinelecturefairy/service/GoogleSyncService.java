package com.example.onlinelecturefairy.service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.example.onlinelecturefairy.LoginActivity;
import com.example.onlinelecturefairy.MainActivity;
import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.common.ColorPicker;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class GoogleSyncService extends Service implements EasyPermissions.PermissionCallbacks {
    boolean isDirect = false;

    @Override
    public void onCreate() {
        // Google Calendar API 사용하기 위해 필요한 인증 초기화( 자격 증명 credentials, 서비스 객체 )
        // OAuth 2.0를 사용하여 구글 계정 선택 및 인증하기 위한 준비
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(),
                Arrays.asList(SCOPES)
        ).setBackOff(new ExponentialBackOff()); // I/O 예외 상황을 대비해서 백오프 정책 사용

        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "GOOGLE_SYNC_SERVICE: ON_BIND");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "GOOGLE_SYNC_SERVICE: ON_START_COMMAND");

        // load is-direct.
        if(intent != null) {
            isDirect = intent.getBooleanExtra("is-direct", false);
        }

        // check setting first.
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean sync = pref.getBoolean("everytimeSync", false);

        isGoogleValid = true;
        AtomicBoolean done = new AtomicBoolean(false);

        // Google Calendar API 사용하기 위해 필요한 인증 초기화( 자격 증명 credentials, 서비스 객체 )
        // OAuth 2.0를 사용하여 구글 계정 선택 및 인증하기 위한 준비
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(),
                Arrays.asList(SCOPES)
        ).setBackOff(new ExponentialBackOff()); // I/O 예외 상황을 대비해서 백오프 정책 사용

        // SharedPreferences에서 저장된 Google 계정 이름을 가져온다.
        String accountName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(PREF_ACCOUNT_NAME, null);

        if (accountName != null) {
            Log.e(TAG, "getResultsFromApi: CHOOSE_SAVED_ACCOUNT");
            mCredential.setSelectedAccountName(accountName);
            SharedPreferences appData = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String everytimeAddress = appData.getString("everytimeAddress", "");
            String[] temp = everytimeAddress.split("@");
            if (temp.length > 1) {
                userIdentifier = temp[1];
            } else {
                userIdentifier = "";
            }

            pref.edit()
                    .putBoolean("account-check", false)
                    .apply();
        } else {
            isGoogleValid = false;
            getResultsFromApi();
        }

        if (sync) {
            if (accountName != null) {
                Log.e(TAG, "onPreExecute: EVERYTIME_CRAWLER_EXECUTED");
                CrawlingEveryTime crw = new CrawlingEveryTime();
                crw.execute();
            } else {
                isGoogleValid = false;
                getResultsFromApi();
            }

            Handler handler = new Handler();
            Thread thread = new Thread(() -> {
                done.set(false);
                while (!crawlingEveryTimeDone) {
                    Log.e(TAG, "GOOGLE_SYNC_SERVICE: WAITING_CRAWLER_DONE");
                    // wait until crawling is done.
                    if(!isEverytimeValid || !isGoogleValid) {
                        //If everytime is invalid, or google is invalid, return asap.
                        break;
                    }
                }
                if (isGoogleValid && isEverytimeValid) {
                    // 구글 validity check에 성공 시 작업 실행.
                    Log.e(TAG, "GOOGLE_SYNC_SERVICE: VALIDATION_COMPLETE");

                    pref.edit()
                            .putBoolean("account-check", false)
                            .apply();

                    //TODO: 일요일의 랜덤한 시간이 지났으면 하도록 설정해놓음.
                    GregorianCalendar calendar1 = new GregorianCalendar();
                    GregorianCalendar calendar2 = new GregorianCalendar();

                    calendar2.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
                    calendar2.set(java.util.Calendar.DAY_OF_MONTH, calendar2.get(java.util.Calendar.DAY_OF_MONTH) + 7);
                    calendar2.set(java.util.Calendar.HOUR_OF_DAY, (int) (Math.random() * 24));
                    calendar2.set(java.util.Calendar.MINUTE, (int) (Math.random() * 60));
                    calendar2.set(java.util.Calendar.SECOND, (int) (Math.random() * 60));

                    // (isDirect가 참이거나, 오늘이 일요일이고 동기화가 되어 있지 않았을) 때 동기화 시간이 지났다면,
                    Log.e(TAG, "GOOGLE_SYNC_SERVICE: CAL1 :" + calendar1.getTime().toString() +  " CAL2: " + calendar2.getTime().toString());
                    if ((isDirect || calendar1.compareTo(calendar2) > 0) && pref.getBoolean(getString(R.string.GOOGLE_CALENDAR_SYNCHRONIZED), true)) {

                        // 여기에 구글 캘린더 동기화 작업을 작성.
                        Log.e(TAG, "onPreExecute: REMOVE_CALENDAR");
                        deleteCalendar();

                        mID = 5;
                        Log.e(TAG, "done " + GoogleSyncService.this.getResultsFromApi());

                        // 동기화가 되었다는 신호를 SharedPreference에 전달
                        pref.edit()
                                .putBoolean(getString(R.string.GOOGLE_CALENDAR_SYNCHRONIZED), true)
                                .apply();

                        Log.e(TAG, "BACKGROUND_SERVICE: NOTI_MATCHED");
                    } else if (calendar1.compareTo(calendar2) > 0) {  // 이미 동기화를 진행했고 오늘이 동기화 시간을 지났을 경우
                        // 동기화 플래그를 초기화
                        pref.edit()
                                .putBoolean(getString(R.string.GOOGLE_CALENDAR_SYNCHRONIZED), false)
                                .apply();
                    } else {
                        Log.e(TAG, "BACKGROUND_SERVICE: NOTI_CONDITION_NOT_MATCHED");
                    }

                    done.set(true);
                } else {
                    Log.e(TAG, "GOOGLE_SYNC_SERVICE: VALIDATION_FAILED; GOOGLE: " + isGoogleValid + " EVERYTIME: " + isEverytimeValid);
                }
                handler.post(() -> {
                    {
                        if (isDirect && done.get()) {
                            //notifyTimetableSyncFinished();
                            Log.e(TAG, "GOOGLE_SYNC_SERVICE: THREAD HANDLER POST");
                        }
                    }
                });
                Log.e(TAG, "GOOGLE_SYNC_SERVICE: THREAD DONE!");
            });
            thread.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    boolean isGoogleValid = true;
    boolean isEverytimeValid = true;

    //notifications

    protected void notifyTimetableSyncFinished() {
        //종료 알림
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "0417")
                .setSmallIcon(R.drawable.web_fairy_short)
                .setContentTitle("시간표 동기화됨")
                .setContentText("에브리타임 시간표가 구글 캘린더에 동기화되었습니다.")
                .setContentIntent(pendingIntent)
                .setChannelId("0417")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup("WEB_FAIRY")
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(123, builder.build());
    }

    protected void notifyPermissionError() {
        // 권한이 제대로 주어지지 않았을 때
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("permission-check", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "0417")
                .setSmallIcon(R.drawable.web_fairy_short)
                .setContentTitle("계정 오류")
                .setContentText("계정 설정에 오류가 발생하였습니다. 탭하여 설정을 확인하세요.")
                .setChannelId("0417")
                .setContentIntent(pendingIntent)
                .setGroup("WEB_FAIRY")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(530, builder.build());

        // set account check pref to true.
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        pref.edit()
                .putBoolean("permission-check", true)
                .apply();
    }

    protected void notifyAccountError() {
        // 계정이 제대로 설정되어 있지 않을 때
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "0417")
                .setSmallIcon(R.drawable.web_fairy_short)
                .setContentTitle("계정 오류")
                .setContentText("계정 설정에 오류가 발생하였습니다. 탭하여 설정을 확인하세요.")
                .setContentIntent(pendingIntent)
                .setChannelId("0417")
                .setGroup("WEB_FAIRY")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(714, builder.build());

        // set account check pref to true.
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        pref.edit()
//                .putBoolean("account-check", true)
//                .apply();
    }

    protected void notifyEverytimeAccountError() {
        //계정 설정 알림
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("everytime", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "0417")
                .setSmallIcon(R.drawable.web_fairy_short)
                .setContentTitle("계정 오류")
                .setContentText("에브리타임 계정 설정에 오류가 발생하였습니다. 탭하여 설정을 확인하세요.")
                .setContentIntent(pendingIntent)
                .setChannelId("0417")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup("WEB_FAIRY")
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(234, builder.build());

        // set account check pref to true.
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        pref.edit()
                .putBoolean("everytime-check", true)
                .apply();
    }


    // GoogleSync

    /**
     * Google Calendar API에 접근하기 위해 사용되는 구글 캘린더 API 서비스 객체
     */

    private com.google.api.services.calendar.Calendar mService = null;

    /**
     * Google Calendar API 호출 관련 메커니즘 및 AsyncTask을 재사용하기 위해 사용
     */
    private int mID = 0;


    public static GoogleAccountCredential mCredential;


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

    private void deleteCalendar() {

        if (!isGooglePlayServicesAvailable()) { // Google Play Services를 사용할 수 없는 경우

            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) { // 유효한 Google 계정이 선택되어 있지 않은 경우

            chooseAccount();
        } else if (!isDeviceOnline()) {    // 인터넷을 사용할 수 없는 경우

            Log.e(TAG, "No network connection available.");
        } else {

            // Google Calendar API 호출
            new GoogleSyncService.deleteCal(this, mCredential).execute();
        }
        return;
    }

    /**
     * crawling everyTime url
     * crawling한 data를 일정화시킨다.
     */
    ProgressDialog progressDialog;

    public boolean crawlingEveryTimeDone = false;

    private class CrawlingEveryTime extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isEverytimeValid = true;
            crawlingEveryTimeDone = false;
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
            Log.e(TAG, "EVERYTIME CRAWLER GOT : " + result);

            // subject 개수가 0이고 everytime 주소가 공백이라면 noti 띄움.
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (numOfSubject == 0 || pref.getString("everytimeAddress", "").equals("")) {

                Log.e(TAG, "GOOGLE_SYNC_SERVICE/CRAWLING_EVERY_TIME: INVALID_EVERYTIME_ADDRESS");
                notifyEverytimeAccountError();

                isEverytimeValid = false;
                crawlingEveryTimeDone = true;
            } else {
                pref.edit()
                        .putBoolean("everytime-check", false)
                        .apply();
            }

            crawlingEveryTimeDone = true;
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
        try {
            if (!isGooglePlayServicesAvailable()) { // Google Play Services를 사용할 수 없는 경우
                Log.e(TAG, "getResultsFromApi: CANNOT_USE_GOOGLE_PLAY_SERVICES");
                acquireGooglePlayServices();
            } else if (mCredential.getSelectedAccountName() == null) { // 유효한 Google 계정이 선택되어 있지 않은 경우

                Log.e(TAG, "getResultsFromApi: CANNOT_GET_VAILD_GOOGLE_ACCOUNT");
                chooseAccount();

                //startActivity(new Intent(this, MainActivity.class));
//            Intent intent = new Intent("service-callback");
//            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            } else if (!isDeviceOnline()) {    // 인터넷을 사용할 수 없는 경우

                Log.e(TAG, "No network connection available.");
            } else {
                Log.e(TAG, "getResultsFromApi MakeRequestTask");
                // Google Calendar API 호출
                new MakeRequestTask(this, mCredential).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e(TAG, "getResultsFromApi: You shouldn't reach here.");
        return null;
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

//        Dialog dialog = apiAvailability.getErrorDialog(
//                GoogleSyncService.this,
//                connectionStatusCode,
//                REQUEST_GOOGLE_PLAY_SERVICES
//        );
//        dialog.show();
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
            Log.e(TAG, "chooseAccount: Bring name at sharedpref");
            // SharedPreferences에서 저장된 Google 계정 이름을 가져온다.
            String accountName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                // 선택된 구글 계정 이름으로 설정한다.
                Log.e(TAG, "set");
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                pref.edit()
                        .putBoolean("account-check", false)
                        .apply();
            } else {
                Log.e(TAG, "new");

                notifyAccountError();

//                startActivity(new Intent(this, MainActivity.class));
//                ((MainActivity) MainActivity.context).startActivityForResult(
//                        mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            pref.edit()
                    .putBoolean("permission-check", false)
                    .apply();

            // GET_ACCOUNTS 권한을 가지고 있지 않다면
        } else {

            Log.e(TAG, "getAccount");

            notifyPermissionError();

//            // 사용자에게 GET_ACCOUNTS 권한을 요구하는 다이얼로그를 보여준다.(주소록 권한 요청함)
//            EasyPermissions.requestPermissions(
//                    (MainActivity) getApplicationContext(),
//                    "This app needs to access your Google account (via Contacts).",
//                    REQUEST_PERMISSION_GET_ACCOUNTS,
//                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 아무일도 하지 않음
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
                //권한 요구
                notifyPermissionError();
//                ((MainActivity) getApplicationContext()).startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
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
        private GoogleSyncService mActivity;
        List<String> eventStrings = new ArrayList<String>();
        List<Event> ourEventArray = new ArrayList<Event>();

        public deleteCal(GoogleSyncService activity, GoogleAccountCredential credential) {

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
            Log.e(TAG, "post delete");
        }

        @Override
        protected void onPreExecute() {
            // mStatusText.setText("");
            Log.e(TAG, "데이터 가져오는 중...");
        }


        /*
         * 백그라운드에서 Google Calendar API 호출 처리
         */

        @Override
        protected String doInBackground(Void... params) {
            try {
                String calendarID = getCalendarID("웹강요정");
                if (calendarID == null) {
                    result = "지울 캘린더가 없습니다.";
                }
                mService.calendarList().delete(calendarID).execute();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
            Log.e(TAG, "지웠습니다.");
            return null;
        }
    }

    /*
     * 비동기적으로 Google Calendar API 호출
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, String> {

        private Exception mLastError = null;
        private GoogleSyncService mActivity;
        List<String> eventStrings = new ArrayList<String>();
        List<Event> ourEventArray = new ArrayList<Event>();

        public MakeRequestTask(GoogleSyncService activity, GoogleAccountCredential credential) {

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
            Log.e(TAG, "데이터 가져오는 중...");
        }


        /*
         * 백그라운드에서 Google Calendar API 호출 처리
         */

        @RequiresApi(api = Build.VERSION_CODES.N)
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
                    createCalendar();
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

            String calendarID = getCalendarID("웹강요정");
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
                ourEventArray.add(event);

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

            String ids = getCalendarID("웹강요정");

            if (ids != null) {

                return "이미 캘린더가 생성되어 있습니다. ";
            }

            // 새로운 캘린더 생성
            com.google.api.services.calendar.model.Calendar calendar = new Calendar();

            // 캘린더의 제목 설정
            calendar.setSummary("웹강요정");


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
            Log.e(TAG, output);

//            if (mID == 3) mResultText.setText(TextUtils.join("\n\n", eventStrings));
//            if (mID == 4) mResultText.setText(TextUtils.join("\n\n", eventStrings));
//            if (mID == 5) mResultText.setText(TextUtils.join("\n\n", eventStrings));
        }


        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    ((MainActivity) getApplicationContext()).startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleSyncService.REQUEST_AUTHORIZATION);
                } else {
                    Log.e(TAG, "MakeRequestTask The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                Log.e(TAG, "요청 취소됨.");
            }

        }


        private String addEvent() {


            String calendarID = getCalendarID("웹강요정");

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
            return "created : " + event.getHtmlLink();
        }

        /**
         * In google calendar,
         * subjectName == summary
         * professorName == description
         * startTime == start
         * endTime == end
         * location == location
         */
        @RequiresApi(api = Build.VERSION_CODES.N)
        private String addEveryTimeEvent() {
            String subjectName;
            String professorName;
            String startTime;
            String endTime;
            String location;
            String[] temp;
            String calendarID = getCalendarID("웹강요정");
            java.util.Calendar calendar;


            SimpleDateFormat simpledateformat;
            //simpledateformat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ", Locale.KOREA);
            // Z에 대응하여 +0900이 입력되어 문제 생겨 수작업으로 입력
            simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREA);

            if (calendarID == null) {

                return "캘린더를 먼저 생성하세요.";

            }
            calendar = java.util.Calendar.getInstance();
            int tYear = calendar.get(java.util.Calendar.YEAR);
            int tMonth = calendar.get(java.util.Calendar.MONTH);
            int tDate = calendar.get(java.util.Calendar.DATE);
            Event[] events = new Event[numOfSubject];
            for (int i = 0; i < numOfSubject; i++) {
                calendar = java.util.Calendar.getInstance();
                temp = arrSubInfo[i].split("@");
                subjectName = temp[0];
                professorName = temp[1];
                int week = Integer.parseInt(temp[2]);
                startTime = temp[3];
                endTime = temp[4];
                location = temp[5];
                events[i] = new Event()
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
                calendar.set(java.util.Calendar.HOUR_OF_DAY, startHour);
                calendar.set(java.util.Calendar.MINUTE, +startMinute);
                String datetime = simpledateformat.format(calendar.getTime());
                DateTime startDateTime = new DateTime(datetime);
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDateTime)
                        .setTimeZone("Asia/Seoul");
                events[i].setStart(start);

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
                events[i].setEnd(end);
                ColorPicker.addLectureId(subjectName);

                //String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
                //fragment_event.setRecurrence(Arrays.asList(recurrence));

            }
            //TODO 과목별 고유한 색상 넣기
            for (Event e : events) {
                e.setColorId(ColorPicker.lectureMap.getOrDefault(e.getSummary(), "1"));
                try {
                    e = mService.events().insert(calendarID, e).execute();
                } catch (Exception o) {
                    o.printStackTrace();
                }
            }
            String eventStrings = "시간표가 구글 캘린더 이번 주의 일정에 추가되었습니다.";

            notifyTimetableSyncFinished();
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
