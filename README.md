# 웹강 요정 (LectureFairy) 
[![android](https://img.shields.io/badge/app-android-blue?logo=android)](https://developer.android.com/)
[![NLP](https://img.shields.io/badge/NLP-KOMORAN-red.svg?logo=github)](https://github.com/shin285/KOMORAN)
[![API](https://img.shields.io/badge/API-Google-blueviolet.svg?logo=google)](https://developers.google.com/calendar)
[![data](https://img.shields.io/badge/data-web-brightgreen.svg)](https://learn.inha.ac.kr/)


**2019 인하대학교 컴퓨터공학과 프로그래밍 공모전 대상 수상작 :1st_place_medal:**

## :open_book: Introduction
  인하대 학생들 사이에서 소위 ‘웹강’이라 불리는 이러닝 강의. 이 이러닝 강의를 정해진 기간 내에 듣는 것을 깜빡하고 출석을 놓쳐서 낭패를 보는 경험을 누구나 해봤을 것입니다.   앱 ‘웹강 요정’은 사용자가 설정한 시간을 기준으로 웹강을 듣지 않은 것이 확인이 된다면, 알림을 해줌으로써 출석을 놓치지 않도록 사용자에게 편의성을 제공합니다. 

  예를 들어 토요일 오후 9시로 설정해 놓았다면, 이 시간 이후 앱이 사용자의 이번 주의 웹강 출석 여부를 백그라운드에서 자동으로 수집하여 출석 완료가 되지 않았다면 알림을 띄워줍니다. 
  
  또한 웹강 알림 기능뿐만 아니라, 현재 학생들의 사용하고 있는 블랙보드 앱의 성적, 공지 등의 업데이트, 알림의 기능에 자잘한 오류가 많아 불편한 학생들을 위하여 ‘웹강요정’ 앱 내에서 온라인 출결, 공지사항, 성적 이 세 가지를 한꺼번에 모아서 확인할 수 있는 기능을 구현하였습니다. 또한 공지사항 확인 메뉴에서는, 한글 자연어 처리를 통한 문맥 분석 후 단어를 수집하여 이를 태그화 시켜서 각 공지사항 밑에 추가하였습니다.

## :computer: Techs

  ### JSoup
  - JSoup을 활용하여, 인하대학교 Blackboard 사이트의 정보를 크롤링했습니다. 이를 통해 사용자는 이번 주 웹강 출석 여부 및 총 출석율, 자신의 성적 조회 및 공지사항을 조회할 수 있습니다.
  - 또한 사용자가 에브리타임을 사용하는 경우, 에브리타임의 시간표를 크롤링하여 가져옵니다.

  ### Google Calendar API
  구글 캘린더 API를 활용하여, 앱과 구글 캘린더 간 통신을 진행했습니다. 앞서 에브리타임에서 가져온 시간표를 앱 내부에서 구글 캘린더의 일정 형태로 변환하여, 매 주마다 해당 주차에 해당하는 시간표를 구글 캘린더에 자동으로 갱신합니다. 이를 통해 사용자는 어떠한 조작도 할 필요 없이 해당 시간에 수업이 있다는 사실을 구글 캘린더를 통하여 확인할 수 있습니다.

  ### KOMORAN
  한글 자연어 처리 및 형태소 분석 API인 KOMORAN을 활용하여 다음과 같은 자연어 분석을 진행했습니다.
  - 공지사항의 내용을 읽어들여 중간 및 기말고사, 평균점수, 휴강, 보강 등 사용자가 관심을 가질 데이터를 추출 및 태그화하여 아래에 강조 표시
  - 공지사항에 해당 과목의 평균이 쓰여 있을 경우 해당 데이터를 자동으로 추출하여, 성적 일람 탭에서 해당 과목의 사용자의 점수와 과목 평균을 쉽게 비교. 즉, 일일이 공지에서 평균을 찾을 필요가 없음
  

## :handshake: Connection
```
github:
https://github.com/challenger71498
https://github.com/Ro4z

email:
challenger71498@gmail.com
insung1217@gmail.com
```
