package com.example.onlinelecturefairy;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.onlinelecturefairy.ui.tutorial.CircleAnimIndicator;
import com.example.onlinelecturefairy.ui.tutorial.TutorialBatteryManagement;
import com.example.onlinelecturefairy.ui.tutorial.TutorialGoogleAccount;
import com.example.onlinelecturefairy.ui.tutorial.TutorialHelloFragment;
import com.example.onlinelecturefairy.ui.tutorial.TutorialPagerAdapter;
import com.example.onlinelecturefairy.ui.tutorial.TutorialStart;
import com.example.onlinelecturefairy.ui.tutorial.TutorialSyncEverytime;

import java.util.ArrayList;

public class TutorialActivity extends AppCompatActivity {
    private CircleAnimIndicator circleAnimIndicator;
    private ConstraintLayout layout;
    private ArrayList<Fragment> fragments;
    private ViewPager vp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutorial);
        layout = findViewById(R.id.tutorialLayout);

        circleAnimIndicator = findViewById(R.id.circleAnimIndicator);

        initData();
        initIndicator();

        vp = findViewById(R.id.tutorialViewPager);
        TutorialPagerAdapter tpa = new TutorialPagerAdapter(getSupportFragmentManager());
        tpa.setFragments(fragments);
        vp.setAdapter(tpa);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                circleAnimIndicator.selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        fragments = new ArrayList<>();
        fragments.add(new TutorialHelloFragment());
        fragments.add(new TutorialGoogleAccount());
        fragments.add(new TutorialSyncEverytime());
        fragments.add(new TutorialBatteryManagement());
        fragments.add(new TutorialStart());
    }

    /**
     * Indicator 초기화
     */
    private void initIndicator(){
        //원사이의 간격
        circleAnimIndicator.setItemMargin(4);
        //애니메이션 속도
        circleAnimIndicator.setAnimDuration(300);
        //indecator 생성
        circleAnimIndicator.createDotPanel(fragments.size(), R.drawable.rounded , R.drawable.web_fairy_icon);
    }
}
