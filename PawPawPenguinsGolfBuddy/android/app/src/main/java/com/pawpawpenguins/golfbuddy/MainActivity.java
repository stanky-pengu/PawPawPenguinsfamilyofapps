package com.pawpawpenguins.golfbuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Build;
import android.view.WindowInsets;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int GREEN=Color.rgb(18,61,53), GREEN2=Color.rgb(29,102,82);
    private static final int CREAM=Color.rgb(250,248,239), MINT=Color.rgb(223,242,232);
    private static final int INK=Color.rgb(23,52,46), MUTED=Color.rgb(115,132,125), GOLD=Color.rgb(245,185,76);
    private SharedPreferences prefs;
    private Round round;
    private EditText course;
    private Spinner holes;
    private LinearLayout scoreRows;
    private TextView holeTitle, strokeValue, parValue, strokesTotal, coursePar, relativeScore;
    private boolean editing=false;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(CREAM);
        getWindow().setNavigationBarColor(CREAM);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        if(Build.VERSION.SDK_INT>=30)getWindow().setDecorFitsSystemWindows(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        prefs=getSharedPreferences("golf_buddy",MODE_PRIVATE);
        round=Round.read(prefs.getString("current",null));
        if(round==null)round=new Round(18);
        makeUi();
        render();
    }

    private void makeUi(){
        ScrollView scroller=new ScrollView(this);
        scroller.setFillViewport(true);
        scroller.setBackgroundColor(CREAM);
        LinearLayout page=new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(16),dp(16),dp(16),dp(26));
        scroller.addView(page);
        scroller.setOnApplyWindowInsetsListener((view,insets)->{
            int top=0,bottom=0;
            if(Build.VERSION.SDK_INT>=30){Insets bars=insets.getInsets(WindowInsets.Type.systemBars());top=bars.top;bottom=bars.bottom;}
            else {top=insets.getSystemWindowInsetTop();bottom=insets.getSystemWindowInsetBottom();}
            page.setPadding(dp(16),dp(16)+top,dp(16),dp(26)+bottom);
            return insets;
        });
        setContentView(scroller);

        LinearLayout hero=new LinearLayout(this);
        hero.setGravity(Gravity.CENTER_VERTICAL);
        hero.setPadding(dp(16),dp(12),dp(16),dp(14));
        hero.setBackground(box(GREEN,24));
        ImageView mascot=new ImageView(this);
        mascot.setImageResource(R.drawable.golf_buddies_scene);
        mascot.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mascot.setContentDescription("PawPawPenguins penguin and squirrel playing golf together; both holding golf clubs");
        hero.addView(mascot,new LinearLayout.LayoutParams(dp(132),dp(92)));
        LinearLayout heroText=new LinearLayout(this);
        heroText.setOrientation(LinearLayout.VERTICAL);
        heroText.setPadding(dp(12),0,0,0);
        LinearLayout brandLine=new LinearLayout(this); brandLine.setGravity(Gravity.CENTER_VERTICAL);
        brandLine.addView(text("PawPawPenguins",11,Color.rgb(205,233,218),true));
        TextView friendSeal=friendMadeStamp(); LinearLayout.LayoutParams sealLp=new LinearLayout.LayoutParams(dp(36),dp(28)); sealLp.leftMargin=dp(6); brandLine.addView(friendSeal,sealLp);
        heroText.addView(brandLine);
        heroText.addView(text("Golf Buddy",25,Color.WHITE,true));
        heroText.addView(text("Your friendly golf scorecard",13,Color.rgb(225,239,231),false));
        hero.addView(heroText,new LinearLayout.LayoutParams(0,-2,1));
        page.addView(hero,full());

        LinearLayout setup=card();
        setup.setOrientation(LinearLayout.HORIZONTAL);
        setup.setGravity(Gravity.CENTER_VERTICAL);
        course=new EditText(this);
        course.setSingleLine(true);
        course.setTextSize(15);
        course.setTextColor(INK);
        course.setHintTextColor(MUTED);
        course.setHint("Course name");
        course.setPadding(dp(12),0,dp(8),0);
        course.setBackground(box(Color.rgb(245,248,243),13));
        setup.addView(course,new LinearLayout.LayoutParams(0,dp(48),1));
        holes=new Spinner(this);
        ArrayAdapter<String> options=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,new String[]{"18 holes","9 holes"});
        options.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holes.setAdapter(options);
        LinearLayout.LayoutParams spinnerLp=new LinearLayout.LayoutParams(dp(112),dp(48));
        spinnerLp.leftMargin=dp(8);
        setup.addView(holes,spinnerLp);
        addSection(page,setup);

        LinearLayout summary=card();
        summary.setOrientation(LinearLayout.VERTICAL);
        summary.addView(text("Round totals",18,INK,true));
        summary.addView(text(new SimpleDateFormat("MMM d, yyyy",Locale.getDefault()).format(new Date()),12,MUTED,false));
        LinearLayout metrics=new LinearLayout(this);
        metrics.setPadding(0,dp(10),0,0);
        strokesTotal=metric(metrics,"0","STROKES");
        coursePar=metric(metrics,"72","COURSE PAR");
        relativeScore=metric(metrics,"—","TO PAR");
        summary.addView(metrics);
        addSection(page,summary);

        LinearLayout hole=card();
        hole.setOrientation(LinearLayout.VERTICAL);
        LinearLayout holeHead=new LinearLayout(this);
        holeHead.setGravity(Gravity.CENTER_VERTICAL);
        holeHead.addView(text("Hole score",18,INK,true),new LinearLayout.LayoutParams(0,-2,1));
        holeTitle=text("HOLE 1 OF 18",11,GREEN2,true);
        holeTitle.setPadding(dp(10),dp(5),dp(10),dp(5));
        holeTitle.setBackground(box(MINT,80));
        holeHead.addView(holeTitle);
        hole.addView(holeHead);

        LinearLayout values=new LinearLayout(this);
        values.setGravity(Gravity.CENTER);
        values.setPadding(0,dp(12),0,dp(10));
        Button back=navButton("‹");
        back.setContentDescription("Previous hole");
        back.setOnClickListener(v->{round.current=(round.current+round.size()-1)%round.size();render();});
        values.addView(back);
        LinearLayout numbers=new LinearLayout(this);
        numbers.setGravity(Gravity.CENTER);
        numbers.setPadding(dp(18),0,dp(18),0);
        LinearLayout a=valueColumn("4","STROKES"), b=valueColumn("4","PAR");
        strokeValue=(TextView)a.getChildAt(0);
        parValue=(TextView)b.getChildAt(0);
        numbers.addView(a); numbers.addView(b);
        values.addView(numbers,new LinearLayout.LayoutParams(0,-2,1));
        Button forward=navButton("›");
        forward.setContentDescription("Next hole");
        forward.setOnClickListener(v->{round.current=(round.current+1)%round.size();render();});
        values.addView(forward);
        hole.addView(values);

        LinearLayout strokeButtons=new LinearLayout(this);
        Button minusStroke=action("− Stroke"), plusStroke=action("+ Stroke");
        strokeButtons.addView(minusStroke,new LinearLayout.LayoutParams(0,dp(46),1));
        strokeButtons.addView(plusStroke,new LinearLayout.LayoutParams(0,dp(46),1));
        minusStroke.setOnClickListener(v->adjustStroke(-1));
        plusStroke.setOnClickListener(v->adjustStroke(1));
        hole.addView(strokeButtons);

        LinearLayout parButtons=new LinearLayout(this);
        Button minusPar=action("− Par"), plusPar=action("+ Par");
        LinearLayout.LayoutParams rowLp=new LinearLayout.LayoutParams(-1,dp(46));
        rowLp.topMargin=dp(7);
        parButtons.addView(minusPar,new LinearLayout.LayoutParams(0,dp(46),1));
        parButtons.addView(plusPar,new LinearLayout.LayoutParams(0,dp(46),1));
        minusPar.setOnClickListener(v->adjustPar(-1));
        plusPar.setOnClickListener(v->adjustPar(1));
        hole.addView(parButtons,rowLp);

        Button saveHole=primary("Save score & next hole");
        LinearLayout.LayoutParams saveLp=new LinearLayout.LayoutParams(-1,dp(48));
        saveLp.topMargin=dp(11);
        hole.addView(saveHole,saveLp);
        saveHole.setOnClickListener(v->{int i=round.current;if(round.scores[i]==0)round.scores[i]=round.pars[i];if(i<round.size()-1)round.current++;render();toast("Score saved");});
        addSection(page,hole);

        LinearLayout scorecard=card();
        scorecard.setOrientation(LinearLayout.VERTICAL);
        LinearLayout scoreHeading=new LinearLayout(this);
        scoreHeading.setGravity(Gravity.CENTER_VERTICAL);
        scoreHeading.addView(text("Scorecard",18,INK,true),new LinearLayout.LayoutParams(0,-2,1));
        Button save=action("Save round");
        scoreHeading.addView(save);
        save.setOnClickListener(v->saveRound());
        scorecard.addView(scoreHeading);
        HorizontalScrollView horizontal=new HorizontalScrollView(this);
        horizontal.setHorizontalScrollBarEnabled(true);
        scoreRows=new LinearLayout(this);
        scoreRows.setOrientation(LinearLayout.VERTICAL);
        horizontal.addView(scoreRows);
        scorecard.addView(horizontal);
        addSection(page,scorecard);

        LinearLayout actions=card();
        actions.setOrientation(LinearLayout.VERTICAL);
        LinearLayout actionRow=new LinearLayout(this);
        Button saved=action("Saved rounds"), share=action("Share scorecard");
        actionRow.addView(saved,new LinearLayout.LayoutParams(0,dp(46),1));
        actionRow.addView(share,new LinearLayout.LayoutParams(0,dp(46),1));
        actions.addView(actionRow);
        Button fresh=primary("Start a new round");
        LinearLayout.LayoutParams freshLp=new LinearLayout.LayoutParams(-1,dp(48));
        freshLp.topMargin=dp(8);
        actions.addView(fresh,freshLp);
        saved.setOnClickListener(v->openSavedRounds());
        share.setOnClickListener(v->share());
        fresh.setOnClickListener(v->newRound());
        addSection(page,actions);

        TextView foot=text("Scores stay on this device. No account or internet connection needed.",11,MUTED,false);
        foot.setGravity(Gravity.CENTER);
        foot.setPadding(dp(8),0,dp(8),0);
        page.addView(foot,full());

        course.setText(round.course);
        holes.setSelection(round.size()==9?1:0);
        course.addTextChangedListener(new android.text.TextWatcher(){
            public void beforeTextChanged(CharSequence s,int start,int count,int after){}
            public void onTextChanged(CharSequence s,int start,int before,int count){if(!editing){round.course=s.toString();saveCurrent();}}
            public void afterTextChanged(android.text.Editable e){}
        });
        holes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent,View view,int position,long id){
                int count=position==1?9:18;
                if(round.size()!=count){round=new Round(count);editing=true;course.setText("");editing=false;render();}
            }
            public void onNothingSelected(AdapterView<?> parent){}
        });
    }

    private void render(){
        int i=round.current;
        if(course!=null){editing=true;if(!course.getText().toString().equals(round.course))course.setText(round.course);editing=false;}
        if(holes!=null){int pos=round.size()==9?1:0;if(holes.getSelectedItemPosition()!=pos)holes.setSelection(pos);}
        holeTitle.setText("HOLE "+(i+1)+" OF "+round.size());
        strokeValue.setText(String.valueOf(round.scores[i]==0?round.pars[i]:round.scores[i]));
        parValue.setText(String.valueOf(round.pars[i]));
        int allPar=0,total=0,playedPar=0,played=0;
        for(int k=0;k<round.size();k++){allPar+=round.pars[k];if(round.scores[k]>0){played++;total+=round.scores[k];playedPar+=round.pars[k];}}
        strokesTotal.setText(String.valueOf(total));
        coursePar.setText(String.valueOf(allPar));
        int diff=total-playedPar;
        relativeScore.setText(played==0?"—":diff==0?"E":(diff>0?"+":"")+diff);
        scoreRows.removeAllViews();
        addScoreRow("HOLE",0);addScoreRow("PAR",1);addScoreRow("SCORE",2);
        saveCurrent();
    }

    private void addScoreRow(String title,int type){
        LinearLayout row=new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        TextView label=text(title,10,MUTED,true);
        label.setGravity(Gravity.CENTER);
        row.addView(label,new LinearLayout.LayoutParams(dp(52),dp(40)));
        for(int i=0;i<round.size();i++){
            final int hole=i;
            String value=type==0?String.valueOf(i+1):type==1?String.valueOf(round.pars[i]):round.scores[i]==0?"·":String.valueOf(round.scores[i]);
            TextView cell=text(value,12,INK,true);
            cell.setGravity(Gravity.CENTER);
            int bg=i==round.current?MINT:(type==2&&round.scores[i]>0?Color.rgb(232,245,235):Color.rgb(245,248,243));
            cell.setBackground(box(bg,40));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(38),dp(34));
            lp.setMargins(dp(2),dp(2),dp(2),dp(2));
            row.addView(cell,lp);
            cell.setContentDescription("Hole "+(i+1)+(type==1?", par "+round.pars[i]:type==2?", score "+value:""));
            cell.setOnClickListener(v->{round.current=hole;render();});
        }
        scoreRows.addView(row);
    }

    private void adjustStroke(int delta){int i=round.current;int base=round.scores[i]==0?round.pars[i]:round.scores[i];round.scores[i]=Math.max(1,base+delta);render();}
    private void adjustPar(int delta){int i=round.current;round.pars[i]=Math.max(3,Math.min(6,round.pars[i]+delta));render();}
    private void saveCurrent(){if(prefs!=null&&round!=null)prefs.edit().putString("current",round.json().toString()).apply();}
    private void saveRound(){
        JSONArray saved;try{saved=new JSONArray(prefs.getString("saved","[]"));}catch(Exception e){saved=new JSONArray();}
        saved.put(round.json());
        while(saved.length()>30)saved.remove(0);
        prefs.edit().putString("saved",saved.toString()).apply();
        toast("Round saved on this device");
    }
    private void openSavedRounds(){
        JSONArray loaded;try{loaded=new JSONArray(prefs.getString("saved","[]"));}catch(Exception e){loaded=new JSONArray();}
        final JSONArray saved=loaded;
        if(saved.length()==0){new AlertDialog.Builder(this).setTitle("Saved rounds").setMessage("No saved rounds yet.").setPositiveButton("Close",null).show();return;}
        String[] labels=new String[saved.length()];
        for(int i=0;i<saved.length();i++){Round r=Round.read(saved.optJSONObject(i));labels[i]=(r==null||r.course.isEmpty()?"Golf round":r.course)+" · "+scoreSum(r)+" strokes";}
        new AlertDialog.Builder(this).setTitle("Open a saved round").setItems(labels,(dialog,which)->{round=Round.read(saved.optJSONObject(which));if(round!=null)render();}).setNegativeButton("Close",null).show();
    }
    private int scoreSum(Round r){int total=0;for(int x:r.scores)total+=x;return total;}
    private void share(){
        int total=scoreSum(round),played=0,relative=0,par=0;
        StringBuilder perHole=new StringBuilder();
        for(int i=0;i<round.size();i++){par+=round.pars[i];if(round.scores[i]>0){played++;relative+=round.scores[i]-round.pars[i];perHole.append("Hole ").append(i+1).append(": ").append(round.scores[i]).append(" · ");}}
        String result=played==0?"":(" ("+(relative==0?"even":(relative>0?"+":"")+relative)+" to par)");
        String message=(round.course.isEmpty()?"Golf round":round.course)+" — "+total+" strokes"+result+"\nCourse par: "+par+"\n"+perHole;
        Intent send=new Intent(Intent.ACTION_SEND);send.setType("text/plain");send.putExtra(Intent.EXTRA_SUBJECT,"My Golf Buddy scorecard");send.putExtra(Intent.EXTRA_TEXT,message);
        startActivity(Intent.createChooser(send,"Share scorecard"));
    }
    private void newRound(){
        new AlertDialog.Builder(this).setTitle("Start a new round?")
            .setMessage("Save this round first if you want to keep it.")
            .setNegativeButton("Keep playing",null)
            .setPositiveButton("New round",(d,w)->{int count=round.size();round=new Round(count);editing=true;course.setText("");editing=false;render();}).show();
    }
    private void toast(String text){Toast.makeText(this,text,Toast.LENGTH_SHORT).show();}

    private TextView friendMadeStamp(){
        TextView seal=text("FRIEND\\nMADE",7,GOLD,true);seal.setGravity(Gravity.CENTER);seal.setLineSpacing(0,0.88f);
        seal.setContentDescription("Gold FRIEND MADE PawPawPenguins seal");seal.setTag("friend_made_stamp");
        GradientDrawable badge=new GradientDrawable();badge.setShape(GradientDrawable.OVAL);badge.setColor(GREEN);badge.setStroke(dp(1),GOLD);seal.setBackground(badge);return seal;
    }
    private LinearLayout card(){LinearLayout x=new LinearLayout(this);x.setOrientation(LinearLayout.VERTICAL);x.setPadding(dp(15),dp(14),dp(15),dp(14));x.setBackground(box(Color.WHITE,20));return x;}
    private void addSection(LinearLayout parent,View child){LinearLayout.LayoutParams lp=full();lp.topMargin=dp(12);parent.addView(child,lp);}
    private LinearLayout.LayoutParams full(){return new LinearLayout.LayoutParams(-1,-2);}
    private TextView metric(LinearLayout parent,String value,String caption){
        LinearLayout group=new LinearLayout(this);group.setOrientation(LinearLayout.VERTICAL);group.setPadding(dp(9),dp(7),dp(6),dp(7));group.setBackground(box(Color.rgb(245,248,243),13));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.setMargins(dp(3),0,dp(3),0);parent.addView(group,lp);
        TextView number=text(value,20,INK,true);group.addView(number);group.addView(text(caption,9,MUTED,true));return number;
    }
    private LinearLayout valueColumn(String number,String caption){LinearLayout x=new LinearLayout(this);x.setOrientation(LinearLayout.VERTICAL);x.setGravity(Gravity.CENTER);x.addView(text(number,27,INK,true));x.addView(text(caption,10,MUTED,true));return x;}
    private TextView text(String value,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(value);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    private Button action(String value){Button b=new Button(this);b.setText(value);b.setTextSize(12);b.setTextColor(GREEN);b.setAllCaps(false);b.setBackground(box(Color.rgb(244,246,240),13));return b;}
    private Button primary(String value){Button b=new Button(this);b.setText(value);b.setTextSize(14);b.setTextColor(Color.WHITE);b.setAllCaps(false);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setBackground(box(GREEN,13));return b;}
    private Button navButton(String value){Button b=new Button(this);b.setText(value);b.setTextSize(22);b.setTextColor(GREEN);b.setMinWidth(dp(48));b.setMinHeight(dp(48));b.setPadding(0,0,0,0);b.setBackground(box(Color.rgb(242,246,240),15));return b;}
    private GradientDrawable box(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}

    private static final class Round {
        String course="";int current=0;int[] pars,scores;
        Round(int count){pars=new int[count];scores=new int[count];Arrays.fill(pars,4);}
        int size(){return pars.length;}
        JSONObject json(){JSONObject o=new JSONObject();try{o.put("course",course);o.put("current",current);JSONArray p=new JSONArray(),s=new JSONArray();for(int x:pars)p.put(x);for(int x:scores)s.put(x);o.put("pars",p);o.put("scores",s);}catch(Exception ignored){}return o;}
        static Round read(String value){if(value==null)return null;try{return read(new JSONObject(value));}catch(Exception e){return null;}}
        static Round read(JSONObject o){if(o==null)return null;try{JSONArray p=o.getJSONArray("pars"),s=o.getJSONArray("scores");int n=p.length();if((n!=9&&n!=18)||s.length()!=n)return null;Round r=new Round(n);r.course=o.optString("course","");r.current=Math.max(0,Math.min(n-1,o.optInt("current",0)));for(int i=0;i<n;i++){r.pars[i]=Math.max(3,Math.min(6,p.optInt(i,4)));r.scores[i]=Math.max(0,s.optInt(i,0));}return r;}catch(Exception e){return null;}}
    }
}

