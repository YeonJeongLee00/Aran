package com.hackathon.aran2.LearnEmotion;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hackathon.aran2.R;

import java.util.Locale;
import java.util.Random;

public class OldEmotionActivity extends AppCompatActivity implements View.OnClickListener, OnInitListener {
    private ImageView questionImage;
    private Button emotion01, emotion02, emotion03, emotion04;
    private Button backButton;
    private String rightAnswer;
    private TextView text;
    private TextToSpeech tell;
    private String speech;
    int source;

    int quizData[][] = {
            {R.drawable.upset1, 1},
            {R.drawable.upset2, 1},
            {R.drawable.upset3, 1},
            {R.drawable.upset4, 1},
            {R.drawable.upset5, 1},
            {R.drawable.thank1, 2},
            {R.drawable.thank2, 2},
            {R.drawable.thank3, 2},
            {R.drawable.thank4, 2},
            {R.drawable.thank5, 2},
            {R.drawable.happy1, 3},
            {R.drawable.happy2, 3},
            {R.drawable.happy3, 3},
            {R.drawable.happy4, 3},
            {R.drawable.happy5, 3},
            {R.drawable.shy1, 4},
            {R.drawable.shy2, 4},
            {R.drawable.shy3, 4},
            {R.drawable.shy4, 4},
            {R.drawable.shy5, 4},
            {R.drawable.sad1, 5},
            {R.drawable.sad2, 5},
            {R.drawable.sad3, 5},
            {R.drawable.sad4, 5},
            {R.drawable.sad5, 5},
            {R.drawable.surprise1, 6},
            {R.drawable.surprise2, 6},
            {R.drawable.surprise3, 6},
            {R.drawable.surprise4, 6},
            {R.drawable.surprise5, 6},
            {R.drawable.envy1, 7},
            {R.drawable.envy2, 7},
            {R.drawable.envy3, 7},
            {R.drawable.envy4, 7},
            {R.drawable.envy5, 7},
            {R.drawable.hurt1, 8},
            {R.drawable.hurt2, 8},
            {R.drawable.hurt3, 8},
            {R.drawable.hurt4, 8},
            {R.drawable.hurt5, 8},
            {R.drawable.scary1, 9},
            {R.drawable.scary2, 9},
            {R.drawable.scary3, 9},
            {R.drawable.scary4, 9},
            {R.drawable.scary5, 9},
            {R.drawable.sorry1, 10},
            {R.drawable.sorry2, 10},
            {R.drawable.sorry3, 10},
            {R.drawable.sorry4, 10},
            {R.drawable.sorry5, 10},
            {R.drawable.love1, 11},
            {R.drawable.love2, 11},
            {R.drawable.love3, 11},
            {R.drawable.love4, 11},
            {R.drawable.love5, 11},
            {R.drawable.satisfy1, 12},
            {R.drawable.satisfy2, 12},
            {R.drawable.satisfy3, 12},
            {R.drawable.satisfy4, 12},
            {R.drawable.satisfy5, 12},
    };

    String answerData[][]={
            //화남
            {"화남","슬픔", "무서움","부러움"},
            {"사랑","화남", "미안","놀람"},
            {"고마움","행복", "화남","뿌듯함"},
            {"놀람","미안", "고마움","화남"},
            {"화남","고마움", "무서움","놀람"},
            //고마움
            {"슬픔","화남", "뿌듯함","고마움"},
            {"놀람","미안", "고마움","부러움"},
            {"화남","고마움", "무서움","놀람"},
            {"화남","고마움", "행복","미안"},
            {"고마움","놀람", "뿌듯함","행복"},
            //행복
            {"행복","놀람", "무서움","사랑"},
            {"화남","고마움", "행복","미안"},
            {"사랑","놀람", "뿌듯함","행복"},
            {"행복","부끄러움", "놀람","뿌듯함"},
            {"고마움","행복", "부끄러움","슬픔"},
            //부끄러움
            {"행복","슬픔", "화남","부끄러움"},
            {"화남","부끄러움", "놀람","뿌듯함"},
            {"고마움","사랑", "부끄러움","슬픔"},
            {"부끄러움","사랑", "뿌듯함","슬픔"},
            {"슬픔","고마움", "무서움","부끄러움"},
            //슬픔
            {"화남","슬픔", "놀람","뿌듯함"},
            {"화남","사랑", "뿌듯함","슬픔"},
            {"슬픔","고마움", "무서움","부끄러움"},
            {"화남","놀람", "슬픔","서운함"},
            {"놀람","슬픔", "사랑","행복"},
            //놀람
            {"고마움","슬픔", "놀람","부러움"},
            {"화남","놀람", "무서움","서운함"},
            {"놀람","슬픔", "사랑","미안"},
            {"놀람","사랑", "부러움","부끄러움"},
            {"고마움","부러움", "놀람","행복"},
            //부러움
            {"행복","화남", "놀람","부러움"},
            {"화남","사랑", "부러움","부끄러움"},
            {"고마움","부러움", "무서움","사랑"},
            {"미안","서운함", "부러움","고마움"},
            {"부러움","슬픔", "서운함","뿌듯함"},
            //서운함
            {"서운함","사랑", "무서움","놀람"},
            {"미안","서운함", "무서움","고마움"},
            {"화남","슬픔", "서운함","뿌듯함"},
            {"사랑","행복", "서운함","무서움"},
            {"화남","무서움", "뿌듯함","서운함"},
            //무서움
            {"화남","슬픔", "무서움","부끄러움"},
            {"사랑","행복", "서운함","무서움"},
            {"화남","무서움", "뿌듯함","부러움"},
            {"화남","부끄러움", "무서움","미안"},
            {"무서움","미안", "뿌듯함","부러움"},
            //미안
            {"미안","사랑", "고마움","부러움"},
            {"화남","부끄러움", "무서움","미안"},
            {"화남","미안", "뿌듯함","부러움"},
            {"사랑","뿌듯함", "미안","화남"},
            {"미안","부러움", "사랑","고마움"},
            //사랑
            {"부끄러움","사랑", "무서움","놀람"},
            {"사랑","뿌듯함", "미안","화남"},
            {"서운함","부러움", "사랑","고마움"},
            {"사랑","뿌듯함", "행복","부러움"},
            {"미안","부끄러움", "뿌듯함","사랑"},
            //뿌듯
            {"화남","슬픔", "무서움","뿌듯함"},
            {"사랑","뿌듯함", "행복","부러움"},
            {"미안","부끄러움", "뿌듯함","서운함"},
            {"뿌듯함","슬픔", "무서움","부끄러움"},
            {"사랑","행복", "서운함","뿌듯함"},
    };

    int emotion[][]={
//            //화남
//            {"화남","슬픔", "무서움","부러움"},
//            {"사랑","화남", "미안","놀람"},
//            {"고마움","행복", "화남","뿌듯함"},
//            {"놀람","미안", "고마움","화남"},
//            {"화남","고마움", "무서움","놀람"},
            {R.drawable.upset_drawable, R.drawable.sad_drawable, R.drawable.scary_drawable, R.drawable.shy_drawable},
            {R.drawable.love_drawable, R.drawable.upset_drawable, R.drawable.sorry_drawable, R.drawable.surprised_drawable},
            {R.drawable.thank_drawable, R.drawable.happy_drawable, R.drawable.upset_drawable, R.drawable.satisfy_drawable},
            {R.drawable.surprised_drawable, R.drawable.sorry_drawable, R.drawable.thank_drawable, R.drawable.upset_drawable},
            {R.drawable.upset_drawable, R.drawable.thank_drawable, R.drawable.scary_drawable, R.drawable.surprised_drawable},
//            //고마움
//            {"슬픔","화남", "뿌듯함","고마움"},
//            {"놀람","미안", "고마움","부러움"},
//            {"화남","고마움", "무서움","놀람"},
//            {"화남","고마움", "행복","미안"},
//            {"고마움","놀람", "뿌듯함","행복"},
            {R.drawable.sad_drawable, R.drawable.upset_drawable, R.drawable.satisfy_drawable, R.drawable.thank_drawable},
            {R.drawable.surprised_drawable, R.drawable.sorry_drawable, R.drawable.thank_drawable, R.drawable.envy_drawable},
            {R.drawable.upset_drawable, R.drawable.thank_drawable, R.drawable.scary_drawable, R.drawable.surprised_drawable},
            {R.drawable.upset_drawable, R.drawable.thank_drawable, R.drawable.happy_drawable, R.drawable.sorry_drawable},
            {R.drawable.thank_drawable, R.drawable.surprised_drawable, R.drawable.satisfy_drawable, R.drawable.happy_drawable},
//            //행복
//            {"행복","놀람", "무서움","사랑"},
//            {"화남","고마움", "행복","미안"},
//            {"사랑","놀람", "뿌듯함","행복"},
//            {"행복","부끄러움", "놀람","뿌듯함"},
//            {"고마움","행복", "부끄러움","슬픔"},
            {R.drawable.happy_drawable, R.drawable.surprised_drawable, R.drawable.scary_drawable, R.drawable.love_drawable},
            {R.drawable.upset_drawable, R.drawable.thank_drawable, R.drawable.happy_drawable, R.drawable.sorry_drawable},
            {R.drawable.love_drawable, R.drawable.surprised_drawable, R.drawable.satisfy_drawable, R.drawable.happy_drawable},
            {R.drawable.happy_drawable, R.drawable.shy_drawable, R.drawable.surprised_drawable, R.drawable.satisfy_drawable},
            {R.drawable.thank_drawable, R.drawable.happy_drawable, R.drawable.shy_drawable, R.drawable.sad_drawable},
//            //부끄러움
//            {"행복","슬픔", "화남","부끄러움"},
//            {"화남","부끄러움", "놀람","뿌듯함"},
//            {"고마움","사랑", "부끄러움","슬픔"},
//            {"부끄러움","사랑", "뿌듯함","슬픔"},
//            {"슬픔","고마움", "무서움","부끄러움"},
            {R.drawable.happy_drawable, R.drawable.sad_drawable, R.drawable.upset_drawable, R.drawable.shy_drawable},
            {R.drawable.upset_drawable, R.drawable.shy_drawable, R.drawable.surprised_drawable, R.drawable.satisfy_drawable},
            {R.drawable.thank_drawable, R.drawable.love_drawable, R.drawable.shy_drawable, R.drawable.sad_drawable},
            {R.drawable.shy_drawable, R.drawable.love_drawable, R.drawable.satisfy_drawable, R.drawable.sad_drawable},
            {R.drawable.sad_drawable, R.drawable.thank_drawable, R.drawable.scary_drawable, R.drawable.shy_drawable},
//            //슬픔
//            {"화남","슬픔", "놀람","뿌듯함"},
//            {"화남","사랑", "뿌듯함","슬픔"},
//            {"슬픔","고마움", "무서움","부끄러움"},
//            {"화남","놀람", "슬픔","서운함"},
//            {"놀람","슬픔", "사랑","행복"},
            {R.drawable.upset_drawable, R.drawable.sad_drawable, R.drawable.surprised_drawable, R.drawable.satisfy_drawable},
            {R.drawable.upset_drawable, R.drawable.love_drawable, R.drawable.satisfy_drawable, R.drawable.sad_drawable},
            {R.drawable.sad_drawable, R.drawable.thank_drawable, R.drawable.scary_drawable, R.drawable.shy_drawable},
            {R.drawable.upset_drawable, R.drawable.surprised_drawable, R.drawable.sad_drawable, R.drawable.hurt_drawable},
            {R.drawable.surprised_drawable, R.drawable.sad_drawable, R.drawable.love_drawable, R.drawable.happy_drawable},
//            //놀람
//            {"고마움","슬픔", "놀람","부러움"},
//            {"화남","놀람", "무서움","서운함"},
//            {"놀람","슬픔", "사랑","미안"},
//            {"놀람","사랑", "부러움","부끄러움"},
//            {"고마움","부러움", "놀람","행복"},
            {R.drawable.thank_drawable, R.drawable.sad_drawable, R.drawable.surprised_drawable, R.drawable.envy_drawable},
            {R.drawable.upset_drawable, R.drawable.surprised_drawable, R.drawable.scary_drawable, R.drawable.hurt_drawable},
            {R.drawable.surprised_drawable, R.drawable.sad_drawable, R.drawable.love_drawable, R.drawable.sorry_drawable},
            {R.drawable.surprised_drawable, R.drawable.love_drawable, R.drawable.envy_drawable, R.drawable.shy_drawable},
            {R.drawable.thank_drawable, R.drawable.envy_drawable, R.drawable.surprised_drawable, R.drawable.happy_drawable},
//            //부러움
//            {"행복","화남", "놀람","부러움"},
//            {"화남","사랑", "부러움","부끄러움"},
//            {"고마움","부러움", "무서움","사랑"},
//            {"미안","서운함", "부러움","고마움"},
//            {"부러움","슬픔", "서운함","뿌듯함"},
            {R.drawable.happy_drawable, R.drawable.upset_drawable, R.drawable.surprised_drawable, R.drawable.envy_drawable},
            {R.drawable.upset_drawable, R.drawable.love_drawable, R.drawable.envy_drawable, R.drawable.shy_drawable},
            {R.drawable.thank_drawable, R.drawable.envy_drawable, R.drawable.scary_drawable, R.drawable.love_drawable},
            {R.drawable.sorry_drawable, R.drawable.hurt_drawable, R.drawable.envy_drawable, R.drawable.thank_drawable},
            {R.drawable.envy_drawable, R.drawable.sad_drawable, R.drawable.hurt_drawable, R.drawable.satisfy_drawable},
//            //서운함
//            {"서운함","사랑", "무서움","놀람"},
//            {"미안","서운함", "무서움","고마움"},
//            {"화남","슬픔", "서운함","뿌듯함"},
//            {"사랑","행복", "서운함","무서움"},
//            {"화남","무서움", "뿌듯함","서운함"},
            {R.drawable.hurt_drawable, R.drawable.love_drawable, R.drawable.scary_drawable, R.drawable.surprised_drawable},
            {R.drawable.sorry_drawable, R.drawable.hurt_drawable, R.drawable.scary_drawable, R.drawable.thank_drawable},
            {R.drawable.upset_drawable, R.drawable.sad_drawable, R.drawable.hurt_drawable, R.drawable.satisfy_drawable},
            {R.drawable.love_drawable, R.drawable.happy_drawable, R.drawable.hurt_drawable, R.drawable.scary_drawable},
            {R.drawable.upset_drawable, R.drawable.scary_drawable, R.drawable.satisfy_drawable, R.drawable.hurt_drawable},
//            //무서움
//            {"화남","슬픔", "무서움","부끄러움"},
//            {"사랑","행복", "서운함","무서움"},
//            {"화남","무서움", "뿌듯함","부러움"},
//            {"화남","부끄러움", "무서움","미안"},
//            {"무서움","미안", "뿌듯함","부러움"},
            {R.drawable.upset_drawable, R.drawable.sad_drawable, R.drawable.scary_drawable, R.drawable.shy_drawable},
            {R.drawable.love_drawable, R.drawable.happy_drawable, R.drawable.hurt_drawable, R.drawable.scary_drawable},
            {R.drawable.upset_drawable, R.drawable.scary_drawable, R.drawable.satisfy_drawable, R.drawable.envy_drawable},
            {R.drawable.upset_drawable, R.drawable.shy_drawable, R.drawable.scary_drawable, R.drawable.sorry_drawable},
            {R.drawable.scary_drawable, R.drawable.sorry_drawable, R.drawable.satisfy_drawable, R.drawable.envy_drawable},
//            //미안
//            {"미안","사랑", "고마움","부러움"},
//            {"화남","부끄러움", "무서움","미안"},
//            {"화남","미안", "뿌듯함","부러움"},
//            {"사랑","뿌듯함", "미안","화남"},
//            {"미안","부러움", "사랑","고마움"},
            {R.drawable.sorry_drawable, R.drawable.love_drawable, R.drawable.thank_drawable, R.drawable.shy_drawable},
            {R.drawable.upset_drawable, R.drawable.shy_drawable, R.drawable.scary_drawable, R.drawable.sorry_drawable},
            {R.drawable.upset_drawable, R.drawable.sorry_drawable, R.drawable.satisfy_drawable, R.drawable.envy_drawable},
            {R.drawable.love_drawable, R.drawable.satisfy_drawable, R.drawable.sorry_drawable, R.drawable.upset_drawable},
            {R.drawable.sorry_drawable, R.drawable.envy_drawable, R.drawable.love_drawable, R.drawable.thank_drawable},
//            //사랑
//            {"부끄러움","사랑", "무서움","놀람"},
//            {"사랑","뿌듯함", "미안","화남"},
//            {"서운함","부러움", "사랑","고마움"},
//            {"사랑","뿌듯함", "행복","부러움"},
//            {"미안","부끄러움", "뿌듯함","사랑"},
            {R.drawable.shy_drawable, R.drawable.love_drawable, R.drawable.scary_drawable, R.drawable.surprised_drawable},
            {R.drawable.love_drawable, R.drawable.satisfy_drawable, R.drawable.sorry_drawable, R.drawable.upset_drawable},
            {R.drawable.hurt_drawable, R.drawable.envy_drawable, R.drawable.love_drawable, R.drawable.thank_drawable},
            {R.drawable.love_drawable, R.drawable.satisfy_drawable, R.drawable.happy_drawable, R.drawable.envy_drawable},
            {R.drawable.sorry_drawable, R.drawable.shy_drawable, R.drawable.satisfy_drawable, R.drawable.love_drawable},
//            //뿌듯
//            {"화남","슬픔", "무서움","뿌듯함"},
//            {"사랑","뿌듯함", "행복","부러움"},
//            {"미안","부끄러움", "뿌듯함","서운함"},
//            {"뿌듯함","슬픔", "무서움","부끄러움"},
//            {"사랑","행복", "서운함","뿌듯함"},
            {R.drawable.upset_drawable, R.drawable.sad_drawable, R.drawable.scary_drawable, R.drawable.satisfy_drawable},
            {R.drawable.love_drawable, R.drawable.satisfy_drawable, R.drawable.happy_drawable, R.drawable.envy_drawable},
            {R.drawable.sorry_drawable, R.drawable.shy_drawable, R.drawable.satisfy_drawable, R.drawable.hurt_drawable},
            {R.drawable.satisfy_drawable, R.drawable.sad_drawable, R.drawable.scary_drawable, R.drawable.shy_drawable},
            {R.drawable.love_drawable, R.drawable.happy_drawable, R.drawable.hurt_drawable, R.drawable.satisfy_drawable},
    };

    String Answertext[][]={
            {"화남"},
            {"화남"},
            {"화남"},
            {"화남"},
            {"화남"},
            {"고마움"},
            {"고마움"},
            {"고마움"},
            {"고마움"},
            {"고마움"},
            {"행복"},
            {"행복"},
            {"행복"},
            {"행복"},
            {"행복"},
            {"부끄러움"},
            {"부끄러움"},
            {"부끄러움"},
            {"부끄러움"},
            {"부끄러움"},
            {"슬픔"},
            {"슬픔"},
            {"슬픔"},
            {"슬픔"},
            {"슬픔"},
            {"놀람"},
            {"놀람"},
            {"놀람"},
            {"놀람"},
            {"놀람"},
            {"부러움"},
            {"부러움"},
            {"부러움"},
            {"부러움"},
            {"부러움"},
            {"서운함"},
            {"서운함"},
            {"서운함"},
            {"서운함"},
            {"서운함"},
            {"무서움"},
            {"무서움"},
            {"무서움"},
            {"무서움"},
            {"무서움"},
            {"미안"},
            {"미안"},
            {"미안"},
            {"미안"},
            {"미안"},
            {"사랑"},
            {"사랑"},
            {"사랑"},
            {"사랑"},
            {"사랑"},
            {"뿌듯함"},
            {"뿌듯함"},
            {"뿌듯함"},
            {"뿌듯함"},
            {"뿌듯함"},};

    String Sentence[][] = {
            //0 = 여자, 1 = 남자
            {"친구가 장난을 심하게 쳐요.", "친구가 장난을 심하게 쳐요."},
            {"친구가 물건을 뺏아갔어요.", "친구가 물건을 뺏아갔어요."},
            {"동생이 제 색연필을 마음대로 쓰고 어지럽혀 놨어요.", "동생이 제 색연필을 마음대로 쓰고 어지럽혀 놨어요."},
            {"새로 산 신발에 언니가 콜라를 쏟았어요.", "새로 산 신발에 누나가 콜라를 쏟았어요."},
            {"소중한 물건을 엄마가 버렸어요.", "소중한 물건을 엄마가 버렸어요."},
            {"친구가 맛있는 과자를 나누어 주었어요.", "친구가 맛있는 과자를 나누어 주었어요."},
            {"친구가 생일인 나에게 선물을 주었어요.", "친구가 생일인 나에게 선물을 주었어요."},
            {"달리다가 넘어졌는데 친구가 도와줬어요.", "달리다가 넘어졌는데 친구가 도와줬어요."},
            {"친구가 나에게 장난감을 빌려줬어요.", "친구가 나에게 장난감을 빌려줬어요."},
            {"비를 맞고있는데 친구가 우산을 씌워줬어요.", "비를 맞고있는데 친구가 우산을 씌워줬어요."},
            {"엄마와 아빠랑 함께 맛있는 식사를 했어요.", "엄마와 아빠랑 함께 맛있는 식사를 했어요."},
            {"가족과 함께 소풍을 갔어요.", "가족과 함께 소풍을 갔어요."},
            {"읽고있는 책이 정말 재미있어요.", "읽고있는 책이 정말 재미있어요."},
            {"갖고 싶던 장난감을 아빠가 사줬어요.", "갖고 싶던 장난감을 아빠가 사줬어요."},
            {"오늘은 나의 생일이에요.", "오늘은 나의 생일이에요."},
            {"새로운 친구를 만났어요.", "새로운 친구를 만났어요."},
            {"좋아하는 친구가 절 빤히 쳐다봐요.", "좋아하는 친구가 절 빤히 쳐다봐요."},
            {"실수로 바지에 오줌을 쌌어요.", "실수로 바지에 오줌을 쌌어요."},
            {"친구에게 알려준 것이 틀렸어요.", "친구에게 알려준 것이 틀렸어요."},
            {"방에서 몰래 노래를 부르고 있는데 언니가 봤어요.", "방에서 몰래 노래를 부르고 있는데 누나가 봤어요."},
            {"키우던 강아지가 다쳤어요.", "키우던 강아지가 다쳤어요."},
            {"친한 친구가 이사를 갔어요.", "친한 친구가 이사를 갔어요."},
            {"가장 좋아하는 색깔의 크레파스를 잃어버렸어요.", "가장 좋아하는 색깔의 크레파스를 잃어버렸어요."},
            {"친한 친구와 싸웠어요.", "친한 친구와 싸웠어요."},
            {"비가 와서 놀이터에서 친구랑 못 놀아요.", "비가 와서 놀이터에서 친구랑 못 놀아요."},
            {"친구가 갑자기 소리를 질러요.", "친구가 갑자기 소리를 질러요."},
            {"밖에서 갑자기 큰소리가 났어요.", "밖에서 갑자기 큰소리가 났어요."},
            {"친구가 갑자기 뒤에서 놀래켰어요.", "친구가 갑자기 뒤에서 놀래켰어요."},
            {"몰래 가족들이 저의 생일파티를 준비했어요.", "몰래 가족들이 저의 생일파티를 준비했어요."},
            {"길을 가고있는데 큰 개가 짖어요.", "길을 가고있는데 큰 개가 짖어요."},
            {"갖고 싶은 장난감을 친구가 가지고 있어요.", "갖고 싶은 장난감을 친구가 가지고 있어요."},
            {"친구가 발표를 잘해서 선생님께 칭찬을 받았어요.", "친구가 발표를 잘해서 선생님께 칭찬을 받았어요."},
            {"친구가 새로 산 옷을 자랑해요.", "친구가 새로 산 옷을 자랑해요."},
            {"친구가 다른 사람에게 인기가 많아요.", "친구가 다른 사람에게 인기가 많아요."},
            {"동생이 제 신발보다 더 멋진 신발을 샀어요.", "동생이 제 신발보다 더 멋진 신발을 샀어요."},
            {"친구가 나랑 안 놀아줘요.", "친구가 나랑 안 놀아줘요."},
            {"친구의 거짓말 때문에 대신 혼났어요.", "친구의 거짓말 때문에 대신 혼났어요."},
            {"엄마가 저와 안 놀아주고 동생만 놀아줘요.", "엄마가 저와 안 놀아주고 동생만 놀아줘요."},
            {"아빠가 집에 올 때 사오기로 한 아이스크림을 안 사왔어요.", "아빠가 집에 올 때 사오기로 한 아이스크림을 안 사왔어요."},
            {"놀러가기로 했는데 엄마와 아빠가 바빠서 못 놀았어요.", "놀러가기로 했는데 엄마와 아빠가 바빠서 못 놀았어요."},
            {"집에 있는데 밖에서 비바람이 치고 천둥번개가 쳐요.", "집에 있는데 밖에서 비바람이 치고 천둥번개가 쳐요."},
            {"수영장에 갔는데 발이 땅에 닫지 않아요.", "수영장에 갔는데 발이 땅에 닫지 않아요."},
            {"꿈에 유령이 나타났어요.", "꿈에 유령이 나타났어요."},
            {"놀이공원에 있는 귀신의 집에 갔어요.", "놀이공원에 있는 귀신의 집에 갔어요."},
            {"불 꺼진 방에 집에 혼자 있어요.", "불 꺼진 방에 집에 혼자 있어요."},
            {"언니가 아끼는 옷에 우유를 쏟았어요.", "누나가 아끼는 옷에 우유를 쏟았어요."},
            {"실수로 친구 물건을 떨어뜨렸어요.", "실수로 친구 물건을 떨어뜨렸어요."},
            {"친구에게 심한 장난을 쳤어요.", "친구에게 심한 장난을 쳤어요."},
            {"엄마 아빠한테 짜증을 냈어요.", "엄마 아빠한테 짜증을 냈어요."},
            {"친구에게 거짓말을 했어요.", "친구에게 거짓말을 했어요."},
            {"엄마와 아빠가 자기 전에 뽀뽀를 해줘요.", "엄마와 아빠가 자기 전에 뽀뽀를 해줘요."},
            {"내 옆에 앉은 짝꿍이 계속 생각나요.", "내 옆에 앉은 짝꿍이 계속 생각나요."},
            {"유치원 가기 전에 엄마와 아빠가 안아줘요.", "유치원 가기 전에 엄마와 아빠가 안아줘요."},
            {"할머니가 저를 웃으면서 반겨 주셔요.", "할머니가 저를 웃으면서 반겨 주셔요."},
            {"동생이 새로 태어났어요.", "동생이 새로 태어났어요."},
            {"상장을 받았어요.", "상장을 받았어요."},
            {"달리기에서 1등을 했어요.", "달리기에서 1등을 했어요."},
            {"발표를 했는데 칭찬을 받았어요.", "발표를 했는데 칭찬을 받았어요."},
            {"키가 전보다 더 컸어요.", "키가 전보다 더 컸어요."},
            {"엄마가 시킨 심부름을 혼자서 했어요.", "엄마가 시킨 심부름을 혼자서 했어요."},};
    String select;
    int age;
    String gender;

    private Typeface mTypeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_emotion);
        mTypeface = Typeface.createFromAsset(getAssets(), "BinggraeMelona.ttf");
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        setGlobalFont(root);
        backButton = (Button)findViewById(R.id.back);
        backButton.setOnClickListener(this);
        tell = new TextToSpeech(this,this);
        questionImage = findViewById(R.id.emotion_image);
        text = findViewById(R.id.sentence);
        emotion01 = findViewById(R.id.emotion01);
        emotion02 = findViewById(R.id.emotion02);
        emotion03 = findViewById(R.id.emotion03);
        emotion04 = findViewById(R.id.emotion04);

        Random random = new Random();

        Intent intent =  getIntent();
        select = intent.getExtras().getString("emotion");
        age= intent.getIntExtra("age", 0);
        gender = intent.getStringExtra("gender");

        if(select.equals("고마움")){
            int r = random.nextInt((9-5)+1)+5;
            source = quizData[r][0];
            questionImage.setImageResource(quizData[r][0]);
            if(gender.equals("여자")) {
                text.setText(Sentence[r][0]);
                speech = Sentence[r][0];
            }else {
                //남자아이
                text.setText(Sentence[r][1]);
                speech = Sentence[r][1];
            }
            emotion01.setCompoundDrawablesWithIntrinsicBounds( emotion[r][0], 0, 0, 0);
            emotion01.setText(answerData[r][0]);
            emotion02.setCompoundDrawablesWithIntrinsicBounds( emotion[r][1], 0, 0, 0);
            emotion02.setText(answerData[r][1]);
            emotion03.setCompoundDrawablesWithIntrinsicBounds( emotion[r][2], 0, 0, 0);
            emotion03.setText(answerData[r][2]);
            emotion04.setCompoundDrawablesWithIntrinsicBounds( emotion[r][3], 0, 0, 0);
            emotion04.setText(answerData[r][3]);
            rightAnswer = Answertext[r][0];
        }else if(select.equals("화남")){
            int r = random.nextInt((4-0)+1)+0;
            source = quizData[r][0];
            questionImage.setImageResource(quizData[r][0]);
            if(gender.equals("여자")) {
                text.setText(Sentence[r][0]);
                speech = Sentence[r][0];
            }else {
                //남자아이
                text.setText(Sentence[r][1]);
                speech = Sentence[r][1];
            }
            emotion01.setCompoundDrawablesWithIntrinsicBounds( emotion[r][0], 0, 0, 0);
            emotion01.setText(answerData[r][0]);
            emotion02.setCompoundDrawablesWithIntrinsicBounds( emotion[r][1], 0, 0, 0);
            emotion02.setText(answerData[r][1]);
            emotion03.setCompoundDrawablesWithIntrinsicBounds( emotion[r][2], 0, 0, 0);
            emotion03.setText(answerData[r][2]);
            emotion04.setCompoundDrawablesWithIntrinsicBounds( emotion[r][3], 0, 0, 0);
            emotion04.setText(answerData[r][3]);
            rightAnswer = Answertext[r][0];
        }else if(select.equals("행복")){
            int r = random.nextInt((14-10)+1)+10;
            source = quizData[r][0];
            questionImage.setImageResource(quizData[r][0]);
            if(gender.equals("여자")) {
                text.setText(Sentence[r][0]);
                speech = Sentence[r][0];
            }else {
                //남자아이
                text.setText(Sentence[r][1]);
                speech = Sentence[r][1];
            }
            emotion01.setCompoundDrawablesWithIntrinsicBounds( emotion[r][0], 0, 0, 0);
            emotion01.setText(answerData[r][0]);
            emotion02.setCompoundDrawablesWithIntrinsicBounds( emotion[r][1], 0, 0, 0);
            emotion02.setText(answerData[r][1]);
            emotion03.setCompoundDrawablesWithIntrinsicBounds( emotion[r][2], 0, 0, 0);
            emotion03.setText(answerData[r][2]);
            emotion04.setCompoundDrawablesWithIntrinsicBounds( emotion[r][3], 0, 0, 0);
            emotion04.setText(answerData[r][3]);
            rightAnswer = Answertext[r][0];
        }else if(select.equals("부끄러움")){
            int r = random.nextInt((19-15)+1)+15;
            source = quizData[r][0];
            questionImage.setImageResource(quizData[r][0]);
            if(gender.equals("여자")) {
                text.setText(Sentence[r][0]);
                speech = Sentence[r][0];
            }else {
                //남자아이
                text.setText(Sentence[r][1]);
                speech = Sentence[r][1];
            }
            emotion01.setCompoundDrawablesWithIntrinsicBounds( emotion[r][0], 0, 0, 0);
            emotion01.setText(answerData[r][0]);
            emotion02.setCompoundDrawablesWithIntrinsicBounds( emotion[r][1], 0, 0, 0);
            emotion02.setText(answerData[r][1]);
            emotion03.setCompoundDrawablesWithIntrinsicBounds( emotion[r][2], 0, 0, 0);
            emotion03.setText(answerData[r][2]);
            emotion04.setCompoundDrawablesWithIntrinsicBounds( emotion[r][3], 0, 0, 0);
            emotion04.setText(answerData[r][3]);
            rightAnswer = Answertext[r][0];
        }else if(select.equals("슬픔")){
            int r = random.nextInt((24-20)+1)+20;
            source = quizData[r][0];
            questionImage.setImageResource(quizData[r][0]);
            if(gender.equals("여자")) {
                text.setText(Sentence[r][0]);
                speech = Sentence[r][0];
            }else {
                //남자아이
                text.setText(Sentence[r][1]);
                speech = Sentence[r][1];
            }
            emotion01.setCompoundDrawablesWithIntrinsicBounds( emotion[r][0], 0, 0, 0);
            emotion01.setText(answerData[r][0]);
            emotion02.setCompoundDrawablesWithIntrinsicBounds( emotion[r][1], 0, 0, 0);
            emotion02.setText(answerData[r][1]);
            emotion03.setCompoundDrawablesWithIntrinsicBounds( emotion[r][2], 0, 0, 0);
            emotion03.setText(answerData[r][2]);
            emotion04.setCompoundDrawablesWithIntrinsicBounds( emotion[r][3], 0, 0, 0);
            emotion04.setText(answerData[r][3]);
            rightAnswer = Answertext[r][0];
        }else if(select.equals("놀람")){
            int r = random.nextInt((29-25)+1)+25;
            source = quizData[r][0];
            questionImage.setImageResource(quizData[r][0]);
            if(gender.equals("여자")) {
                text.setText(Sentence[r][0]);
                speech = Sentence[r][0];
            }else {
                //남자아이
                text.setText(Sentence[r][1]);
                speech = Sentence[r][1];
            }
            emotion01.setCompoundDrawablesWithIntrinsicBounds( emotion[r][0], 0, 0, 0);
            emotion01.setText(answerData[r][0]);
            emotion02.setCompoundDrawablesWithIntrinsicBounds( emotion[r][1], 0, 0, 0);
            emotion02.setText(answerData[r][1]);
            emotion03.setCompoundDrawablesWithIntrinsicBounds( emotion[r][2], 0, 0, 0);
            emotion03.setText(answerData[r][2]);
            emotion04.setCompoundDrawablesWithIntrinsicBounds( emotion[r][3], 0, 0, 0);
            emotion04.setText(answerData[r][3]);
            rightAnswer = Answertext[r][0];
        }else if(select.equals("부러움")){
            int r = random.nextInt((34-30)+1)+30;
            source = quizData[r][0];
            questionImage.setImageResource(quizData[r][0]);
            if(gender.equals("여자")) {
                text.setText(Sentence[r][0]);
                speech = Sentence[r][0];
            }else {
                //남자아이
                text.setText(Sentence[r][1]);
                speech = Sentence[r][1];
            }
            emotion01.setCompoundDrawablesWithIntrinsicBounds( emotion[r][0], 0, 0, 0);
            emotion01.setText(answerData[r][0]);
            emotion02.setCompoundDrawablesWithIntrinsicBounds( emotion[r][1], 0, 0, 0);
            emotion02.setText(answerData[r][1]);
            emotion03.setCompoundDrawablesWithIntrinsicBounds( emotion[r][2], 0, 0, 0);
            emotion03.setText(answerData[r][2]);
            emotion04.setCompoundDrawablesWithIntrinsicBounds( emotion[r][3], 0, 0, 0);
            emotion04.setText(answerData[r][3]);
            rightAnswer = Answertext[r][0];
        }else if(select.equals("서운함")){
            int r = random.nextInt((39-35)+1)+35;
            source = quizData[r][0];
            questionImage.setImageResource(quizData[r][0]);
            if(gender.equals("여자")) {
                text.setText(Sentence[r][0]);
                speech = Sentence[r][0];
            }else {
                //남자아이
                text.setText(Sentence[r][1]);
                speech = Sentence[r][1];
            }
            emotion01.setCompoundDrawablesWithIntrinsicBounds( emotion[r][0], 0, 0, 0);
            emotion01.setText(answerData[r][0]);
            emotion02.setCompoundDrawablesWithIntrinsicBounds( emotion[r][1], 0, 0, 0);
            emotion02.setText(answerData[r][1]);
            emotion03.setCompoundDrawablesWithIntrinsicBounds( emotion[r][2], 0, 0, 0);
            emotion03.setText(answerData[r][2]);
            emotion04.setCompoundDrawablesWithIntrinsicBounds( emotion[r][3], 0, 0, 0);
            emotion04.setText(answerData[r][3]);
            rightAnswer = Answertext[r][0];
        }else if(select.equals("무서움")){
            int r = random.nextInt((44-40)+1)+40;
            source = quizData[r][0];
            questionImage.setImageResource(quizData[r][0]);
            if(gender.equals("여자")) {
                text.setText(Sentence[r][0]);
                speech = Sentence[r][0];
            }else {
                //남자아이
                text.setText(Sentence[r][1]);
                speech = Sentence[r][1];
            }
            emotion01.setCompoundDrawablesWithIntrinsicBounds( emotion[r][0], 0, 0, 0);
            emotion01.setText(answerData[r][0]);
            emotion02.setCompoundDrawablesWithIntrinsicBounds( emotion[r][1], 0, 0, 0);
            emotion02.setText(answerData[r][1]);
            emotion03.setCompoundDrawablesWithIntrinsicBounds( emotion[r][2], 0, 0, 0);
            emotion03.setText(answerData[r][2]);
            emotion04.setCompoundDrawablesWithIntrinsicBounds( emotion[r][3], 0, 0, 0);
            emotion04.setText(answerData[r][3]);
            rightAnswer = Answertext[r][0];
        }else if(select.equals("미안")){
            int r = random.nextInt((49-45)+1)+45;
            source = quizData[r][0];
            questionImage.setImageResource(quizData[r][0]);
            if(gender.equals("여자")) {
                text.setText(Sentence[r][0]);
                speech = Sentence[r][0];
            }else {
                //남자아이
                text.setText(Sentence[r][1]);
                speech = Sentence[r][1];
            }
            emotion01.setCompoundDrawablesWithIntrinsicBounds( emotion[r][0], 0, 0, 0);
            emotion01.setText(answerData[r][0]);
            emotion02.setCompoundDrawablesWithIntrinsicBounds( emotion[r][1], 0, 0, 0);
            emotion02.setText(answerData[r][1]);
            emotion03.setCompoundDrawablesWithIntrinsicBounds( emotion[r][2], 0, 0, 0);
            emotion03.setText(answerData[r][2]);
            emotion04.setCompoundDrawablesWithIntrinsicBounds( emotion[r][3], 0, 0, 0);
            emotion04.setText(answerData[r][3]);
            rightAnswer = Answertext[r][0];
        }else if(select.equals("사랑")){
            int r = random.nextInt((54-50)+1)+50;

            source = quizData[r][0];
            questionImage.setImageResource(quizData[r][0]);
            if(gender.equals("여자")) {
                text.setText(Sentence[r][0]);
                speech = Sentence[r][0];
            }else {
                //남자아이
                text.setText(Sentence[r][1]);
                speech = Sentence[r][1];
            }
            emotion01.setCompoundDrawablesWithIntrinsicBounds( emotion[r][0], 0, 0, 0);
            emotion01.setText(answerData[r][0]);
            emotion02.setCompoundDrawablesWithIntrinsicBounds( emotion[r][1], 0, 0, 0);
            emotion02.setText(answerData[r][1]);
            emotion03.setCompoundDrawablesWithIntrinsicBounds( emotion[r][2], 0, 0, 0);
            emotion03.setText(answerData[r][2]);
            emotion04.setCompoundDrawablesWithIntrinsicBounds( emotion[r][3], 0, 0, 0);
            emotion04.setText(answerData[r][3]);
            rightAnswer = Answertext[r][0];
        }else if(select.equals("뿌듯함")){
            int r = random.nextInt((59-55)+1)+55;
            source = quizData[r][0];
            questionImage.setImageResource(quizData[r][0]);
            if(gender.equals("여자")) {
                text.setText(Sentence[r][0]);
                speech = Sentence[r][0];
            }else {
                //남자아이
                text.setText(Sentence[r][1]);
                speech = Sentence[r][1];
            }
            emotion01.setCompoundDrawablesWithIntrinsicBounds( emotion[r][0], 0, 0, 0);
            emotion01.setText(answerData[r][0]);
            emotion02.setCompoundDrawablesWithIntrinsicBounds( emotion[r][1], 0, 0, 0);
            emotion02.setText(answerData[r][1]);
            emotion03.setCompoundDrawablesWithIntrinsicBounds( emotion[r][2], 0, 0, 0);
            emotion03.setText(answerData[r][2]);
            emotion04.setCompoundDrawablesWithIntrinsicBounds( emotion[r][3], 0, 0, 0);
            emotion04.setText(answerData[r][3]);
            rightAnswer = Answertext[r][0];
        }else if(select.equals("random")){//랜덤
            int r = random.nextInt((59-0)+1)+0;
            source = quizData[r][0];
            questionImage.setImageResource(quizData[r][0]);
            if(gender.equals("여자")) {
                text.setText(Sentence[r][0]);
                speech = Sentence[r][0];
            }else {
                //남자아이
                text.setText(Sentence[r][1]);
                speech = Sentence[r][1];
            }
            emotion01.setCompoundDrawablesWithIntrinsicBounds( emotion[r][0], 0, 0, 0);
            emotion01.setText(answerData[r][0]);
            emotion02.setCompoundDrawablesWithIntrinsicBounds( emotion[r][1], 0, 0, 0);
            emotion02.setText(answerData[r][1]);
            emotion03.setCompoundDrawablesWithIntrinsicBounds( emotion[r][2], 0, 0, 0);
            emotion03.setText(answerData[r][2]);
            emotion04.setCompoundDrawablesWithIntrinsicBounds( emotion[r][3], 0, 0, 0);
            emotion04.setText(answerData[r][3]);
            rightAnswer = Answertext[r][0];
            if(0<=r&&r<=4){
//                intent1.putExtra("ran", "화남");
                select = "화남";
            }
            else if(5<=r&&r<=9){
//                intent1.putExtra("ran", "고마움");
                select = "고마움";
            }
            else if(10<=r&&r<=14){
//                intent1.putExtra("ran", "행복");
                select = "행복";
            }
            else if(15<=r&&r<=19){
//                intent1.putExtra("ran", "부끄러움");
                select = "부끄러움";
            }
            else if(20<=r&&r<=24){
                select = "슬픔";
//                intent1.putExtra("ran", "슬픔");
            }
            else if(25<=r&&r<=29){
                select = "놀람";
//                intent1.putExtra("ran", "놀람");
            }
            else if(30<=r&&r<=34){
                select = "부러움";
//                intent1.putExtra("ran", "부러움");
            }
            else if(35<=r&&r<=39){
                select = "서운함";
//                intent1.putExtra("ran", "서운함");
            }
            else if(40<=r&&r<=44){
                select = "무서움";
//                intent1.putExtra("ran", "무서움");
            }
            else if(45<=r&&r<=49){
                select = "미안";
//                intent1.putExtra("ran", "미안");
            }
            else if(50<=r&&r<=54){
                select = "사랑";
//                intent1.putExtra("ran", "사랑");
            }
            else {
                select = "뿌듯함";
//                intent1.putExtra("ran", "뿌듯함");
            }
        }
    }

    @Override
    public void onClick(View view) {
        //get pushed button.

        switch(view.getId()){
            case R.id.back:
                onBackPressed();
                return;
        }
        Button answerBtn = (Button) findViewById(view.getId());

        if(answerBtn.getText().equals(rightAnswer)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("맞았습니다!");
            builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getApplicationContext(), DrawImageActivity.class);
                    intent.putExtra("age", age);
                    intent.putExtra("emotion",select);
                    intent.putExtra("gender", gender);
                    intent.putExtra("image",source);
                    intent.putExtra("선택", -3);
                    startActivityForResult(intent,5000);
                }
            });
            builder.show();

        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("다시 생각해보세요.");
            builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        }
    }

    @Override
    public void onInit(int i) {
        tell.setLanguage(Locale.KOREAN);
        tell.setPitch(0.6f);
        tell.setSpeechRate(0.95f);
        tell.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case  5000:
                    Intent outintent = new Intent();
                    setResult(RESULT_OK, outintent);
                    finish();
                    break;
            }
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        tell.stop();
    }
    @Override
    public void onResume(){
        super.onResume();;
        tell.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        tell.shutdown();
    }
    void setGlobalFont(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView)
                ((TextView)child).setTypeface(mTypeface);
            else if (child instanceof ViewGroup)
                setGlobalFont((ViewGroup)child);
        }
    }
}