package com.example.onlinelecturefairy.ui.grade;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.onlinelecturefairy.GoogleCalendarSyncTest;
import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.common.ColorPicker;
import com.example.onlinelecturefairy.grade.Grade;
import com.example.onlinelecturefairy.notice.Notice;
import com.example.onlinelecturefairy.ui.notice.NoticeRecyclerAdapter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class GradeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipe;
    GradeFragmentViewModel model;
    private ArrayList<Grade> grades;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_grade, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = ViewModelProviders.of(this).get(GradeFragmentViewModel.class);


        model.getGrades().observe(getActivity(), grades -> {
            //UI updates.
            if (getView() != null) {
                RecyclerView recyclerView = getView().findViewById(R.id.gradeRecyclerView);
                GradeRecyclerAdapter adapter = (GradeRecyclerAdapter) recyclerView.getAdapter();
                if (adapter != null) {
                    adapter.setGrades(grades);
                    adapter.notifyDataSetChanged();
                } else {
                    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                    adapter = new GradeRecyclerAdapter(grades);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(manager);
                }
            }
        });

        //당겨서 새로고침
        swipe = getView().findViewById(R.id.gradeSwipeRefresh);
        swipe.setOnRefreshListener(this);
        //Color changes by each color.
        swipe.setProgressBackgroundColorSchemeColor(Color.BLACK);
        swipe.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        //Crawling
        CrawlingBlackBoardGrade crw = new CrawlingBlackBoardGrade();
        crw.execute();
    }

    @Override
    public void onRefresh() {
        //Crawling Method.
        CrawlingBlackBoardGrade crw = new CrawlingBlackBoardGrade();
        crw.execute();
    }

    private class CrawlingBlackBoardGrade extends AsyncTask<Void, Void, Void> {

        ArrayList<Grade> gradesWaiting = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Set refresh ui to true.
            if (!swipe.isRefreshing()) {
                swipe.setRefreshing(true);
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //Should stop refreshing.
            swipe.setRefreshing(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36";
                //TODO: 개발 완료 후 defValue 수정
                SharedPreferences appData = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String blackboard_user_id = appData.getString("ID", "12181637");
                String blackboard_user_password = appData.getString("PW", "!dlstjd1105");
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
                Map<String, String> loginCookie = res.cookies();
                if (loginCookie.isEmpty()) {
                    return null;
                }
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

                if (grades == null) {
                    grades = new ArrayList<>();
                }
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
                        String score_temp = "";
                        elem2 = tempDoc.select("span.grade").first();
                        if (elem2 != null) score = elem2.text();
                        if(score.contains("000")) {
                            score = score.replaceAll("00000", "00");
                        }


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

                        gradesWaiting.add(new Grade(lecture, score, scoreSub, description));
                        ColorPicker.addLectureId(lecture);

                        //Should use postValue because it is on background thread.
                        model.postGrades(gradesWaiting);
                    }

                }
            } catch (IOException o) {
                o.printStackTrace();
            }
            return null;
        }

    }

}