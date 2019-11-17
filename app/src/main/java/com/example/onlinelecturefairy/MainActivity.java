package com.example.onlinelecturefairy;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.onlinelecturefairy.common.ScheduleJob;
import com.example.onlinelecturefairy.grade.CommonGrade;
import com.example.onlinelecturefairy.service.BackgroundService;
import com.example.onlinelecturefairy.service.GoogleSyncService;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MainActivity extends AppCompatActivity {
    public static Context context;

    private AppBarConfiguration mAppBarConfiguration;

    // for double back to exit function.
    private final long INTERNAL_TIME = 1000;
    private long previousTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Check if this is the first start.
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        startService(new Intent(this, GoogleSyncService.class));
        startService(new Intent(this, BackgroundService.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load common grades.
        CommonGrade.loadGrades(getApplicationContext());

        // Google Calendar API 사용하기 위해 필요한 인증 초기화( 자격 증명 credentials, 서비스 객체 )
        // OAuth 2.0를 사용하여 구글 계정 선택 및 인증하기 위한 준비
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(),
                Arrays.asList(SCOPES)
        ).setBackOff(new ExponentialBackOff()); // I/O 예외 상황을 대비해서 백오프 정책 사용

//        //Calendar
//        mID = 1;
//        if(getResultsFromApi() != null) {
//            Log.e(TAG, getResultsFromApi());
//        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show());
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_online, R.id.nav_notice, R.id.nav_grade)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Background initialization.
        ScheduleJob s = new ScheduleJob();
        s.refreshGoogle(this);
        s.refreshBackground(this);
//        //TODO 주기 제대로 바꾸기
//        long period = 30;  //minutes
//        long latency = 15;
//        long googlePeriod = 60;
//        long googleLatency = 30;

//        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        assert scheduler != null;
//        scheduler.schedule(
//                new JobInfo.Builder(getResources().getInteger(R.integer.REFRESH_BACKGROUND_TASK), new ComponentName(this, BackgroundJobService.class))
//                        .setPeriodic(period * 1000 * 60, latency * 1000 * 60)
//                        .setPersisted(true)
//                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                        .build());
//
//        //TODO Calendar 이용해서 조건에 맞을때만 해야함
//        scheduler.schedule(
//                new JobInfo.Builder(2, new ComponentName(this, GoogleSyncService.class))
//                        .setPeriodic(googlePeriod * 1000 * 60, googleLatency * 1000 * 60)
//                        .setPersisted(true)
//                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                        .build());
//
//        if (!shown) {
//            startService(new Intent(this, GoogleSyncService.class));
//            shown = true;
//        }

        // get intent for snackbar setting.
        Intent intent = getIntent();

        if (!pref.getBoolean("account-check", false)) {
            Log.e(TAG, "onReceive: RECEIVED account-check");
            Snackbar snackbar = Snackbar.make(findViewById(R.id.nav_view), "계정이 연동되지 않았습니다.", Snackbar.LENGTH_LONG);
            snackbar
                    .setAction("설정", v -> {
                        chooseAccount();
                        snackbar.dismiss();
                    })
                    .show();
        } else if (!pref.getBoolean("permission-check", false)) {
            Log.e(TAG, "onCreate: RECEIVED permission-check");
            Snackbar snackbar = Snackbar.make(findViewById(R.id.nav_view), "권한이 승인되지 않았습니다.", Snackbar.LENGTH_LONG);
            snackbar
                    .setAction("설정", v -> {
                        chooseAccount();
                        snackbar.dismiss();
                    })
                    .show();
        } else if (!pref.getBoolean("everytime-check", false)) {
            Log.e(TAG, "onCreate: RECEIVED everytime");
            Snackbar snackbar = Snackbar.make(findViewById(R.id.nav_view), "에브리타임 연동에 실패했습니다.", Snackbar.LENGTH_LONG);
            snackbar
                    .setAction("설정", v -> {
                        Intent i = new Intent(this, SettingsActivity.class);
                        startActivity(i);
                    })
                    .show();
        } else if (intent.getBooleanExtra("isInfoCorrect", false)) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.nav_view), "로그인 성공!", Snackbar.LENGTH_SHORT);
            snackbar
                    .setAction("Action", null)
                    .show();
        }

        //intent 초기화

        //LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("account-check"));
    }

//    BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.e(TAG, "onReceive: RECEIVED");
//            chooseAccount();
//        }
//    };

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - previousTime <= INTERNAL_TIME) {
            finishAffinity();
        } else {
            previousTime = currentTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        CommonGrade.saveGrades(getApplicationContext());    // 성적 저장
        super.onDestroy();
    }

    /**
     * Google Calendar API에 접근하기 위해 사용되는 구글 캘린더 API 서비스 객체
     */

    private com.google.api.services.calendar.Calendar mService = null;

    /**
     * Google Calendar API 호출 관련 메커니즘 및 AsyncTask을 재사용하기 위해 사용
     */
    public int mID = 0;


    public static GoogleAccountCredential mCredential;


    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;


    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};


    /*
     * 안드로이드 디바이스에 Google Play Services가 설치 안되어 있거나 오래된 버전인 경우 보여주는 대화상자
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode
    ) {

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();

        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
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

            Log.e(TAG, "chooseAccount: GET_NAME");
            // SharedPreferences에서 저장된 Google 계정 이름을 가져온다.
            String accountName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                Log.e(TAG, "chooseAccount: SET_INTENT " + accountName);
                // 선택된 구글 계정 이름으로 설정한다.
                GoogleSyncService.mCredential.setSelectedAccountName(accountName);
                Log.e(TAG, "chooseAccount: ACCOUNTNAME : " + GoogleSyncService.mCredential.getSelectedAccountName());
                //getResultsFromApi();
//                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
//                pref.edit()
//                        .putString(PREF_ACCOUNT_NAME, GoogleSyncService.mCredential.getSelectedAccountName())
//                        .apply();
                startService(new Intent(this, GoogleSyncService.class));
            } else {
                Log.e(TAG, "chooseAccount: DIALOG");
                // 사용자가 구글 계정을 선택할 수 있는 다이얼로그를 보여준다.
                startActivityForResult(
                        GoogleSyncService.mCredential.newChooseAccountIntent(),
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
    public void onActivityResult(
            int requestCode,  // onActivityResult가 호출되었을 때 요청 코드로 요청을 구분
            int resultCode,   // 요청에 대한 결과 코드
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {

            case REQUEST_GOOGLE_PLAY_SERVICES:

                if (resultCode != RESULT_OK) {

                    Log.e(TAG, " 앱을 실행시키려면 구글 플레이 서비스가 필요합니다."
                            + "구글 플레이 서비스를 설치 후 다시 실행하세요.");
                } else {
                    Intent intent = new Intent(this, GoogleSyncService.class);
                    startService(intent);
                    //getResultsFromApi();
                }
                break;


            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        settings.edit()
                                .putString(PREF_ACCOUNT_NAME, accountName)
                                .apply();
                        //mCredential.setSelectedAccountName(accountName);
                        GoogleSyncService.mCredential.setSelectedAccountName(accountName);
                        //getResultsFromApi();
                        startService(new Intent(this, GoogleSyncService.class));
                    }
                }
                break;


            case REQUEST_AUTHORIZATION:

                if (resultCode == RESULT_OK) {
                    startService(new Intent(this, GoogleSyncService.class));
                    //getResultsFromApi();
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
}
