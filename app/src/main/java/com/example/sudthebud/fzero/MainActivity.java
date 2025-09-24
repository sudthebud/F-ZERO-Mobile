package com.example.sudthebud.fzero;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //Masterpiece by Sudharshan Srinivasan

    //Global variables defined

    GameSurface gameSurface;

    SharedPreferences pref;

    MediaPlayer musicplayer,flyingplayer,racestartplayer,countdownplayer,resultsplayer,winbeepplayer,hitsoundplayer,boostsoundplayer,titlescreenplayer,splashsoundplayer,buttonsoundplayer;

    Boolean boost,finalize,bigrun;

    int timefromboost;
    int time;
    int boostprev;
    int boostamount;
    int postouchx;
    int postouchy;

    //What happenes when app is created

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameSurface = new GameSurface(this);
        timefromboost = 0;
        time = 0;
        boostamount = 10;
        boostprev = 10;
        postouchx = 0;
        postouchy = 0;
        bigrun = true;
        gameSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (boostamount > 0 && (boostprev > boostamount || boostprev == 10)) {
                    boost = !boost;
                    timefromboost = time;
                }
                postouchx = (int)event.getX();
                postouchy = (int)event.getY();
                finalize = !finalize;
                return false;
            }
        });
        /*gameSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        setContentView(gameSurface);
        boost = false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    //Callbacks to other methods in GameSurface

    @Override
    protected void onPause() {
        super.onPause();
        gameSurface.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameSurface.resume();
    }

    //What happens when app is exited

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameSurface.destroy();
    }

    //GameSurface created

    public class GameSurface extends SurfaceView implements Runnable, SensorEventListener {

        //All variables made

        Thread gameThread;
        SurfaceHolder holder;

        Vibrator vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        Canvas canvas;

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        volatile boolean running = false;
        volatile boolean starting = false;
        volatile boolean jingle = false;
        volatile boolean end = false;
        volatile boolean hit = false;
        volatile boolean pasthit = false;
        volatile boolean boostsounddone = false;
        volatile boolean restart = false;
        volatile boolean hitrestart = false;
        volatile boolean splash = true;
        volatile boolean title = false;
        volatile boolean hitstart = false;
        volatile boolean master = false;
        volatile boolean hitfinalize = false;

        String whatever = "none";
        String name = "MUTE CITY";

        Bitmap car, enemy, carleft, carright, carboosted, track, city, screen, shadow, carhitleft, carhitback, carhitright, enemy1, enemy2, enemy3, titlepaper, logo;

        int carX = 0;
        int x = 200;
        int carXprev = 0;
        int move = 0;
        int trackdisplace = 0;
        int canvasdisplase = 0;
        int enemyX = 0;
        int enemyY = 0;
        int score = 0;
        int boostchange = 1;
        int playerdisplace = 0;
        int currenttime = 0;
        int hiscore = 0;
        int racenumb = 0;
        int timeprev = 0;

        String sensorOutput = "";
        Paint paintProperty;

        int screenWidth;
        int screenHeight;

        Timer timegame;

        Typeface typeface = ResourcesCompat.getFont(MainActivity.this, R.font.fontfile);

        //All variables given values

        public GameSurface(Context context) {
            super(context);

            holder = getHolder();

            pref = getSharedPreferences("FZERO",0);
            hiscore = pref.getInt("hisc",0);

            Bitmap car2 = BitmapFactory.decodeResource(getResources(), R.drawable.sprite);
            Bitmap carleft2 = BitmapFactory.decodeResource(getResources(), R.drawable.left);
            Bitmap carright2 = BitmapFactory.decodeResource(getResources(), R.drawable.right);
            enemy1 = BitmapFactory.decodeResource(getResources(),R.drawable.enemy2);
            enemy2 = BitmapFactory.decodeResource(getResources(),R.drawable.enemy);
            enemy3 = BitmapFactory.decodeResource(getResources(),R.drawable.enemy3);
            Bitmap track2 = BitmapFactory.decodeResource(getResources(),R.drawable.hqdefault);
            Bitmap city2 = BitmapFactory.decodeResource(getResources(),R.drawable.city);
            Bitmap screen2 = BitmapFactory.decodeResource(getResources(),R.drawable.screen);
            Bitmap shadow2 = BitmapFactory.decodeResource(getResources(),R.drawable.shadow);
            Bitmap carhitleft2 = BitmapFactory.decodeResource(getResources(),R.drawable.hitleft);
            Bitmap carhitback2 = BitmapFactory.decodeResource(getResources(),R.drawable.hitback);
            Bitmap carhitright2 = BitmapFactory.decodeResource(getResources(),R.drawable.hitright);
            Bitmap carboosted1 = BitmapFactory.decodeResource(getResources(),R.drawable.spriteboosted);
            Bitmap titlepaper2 = BitmapFactory.decodeResource(getResources(),R.drawable.titlepaper);
            Bitmap logo2 = BitmapFactory.decodeResource(getResources(),R.drawable.logo);

            car = Bitmap.createScaledBitmap(car2,200,122,false);
            carleft = Bitmap.createScaledBitmap(carleft2,200,122,false);
            carright = Bitmap.createScaledBitmap(carright2,200,122,false);
            enemy = Bitmap.createScaledBitmap(enemy2,200,122,false);
            screen = Bitmap.createScaledBitmap(screen2,400,144,false);
            shadow = Bitmap.createScaledBitmap(shadow2,230,122,false);
            carhitleft = Bitmap.createScaledBitmap(carhitleft2,200,122,false);
            carhitback = Bitmap.createScaledBitmap(carhitback2,200,122,false);
            carhitright = Bitmap.createScaledBitmap(carhitright2,200,122,false);
            carboosted = Bitmap.createScaledBitmap(carboosted1,200,122,false);

            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth = sizeOfScreen.x;
            screenHeight = sizeOfScreen.y;

            track = Bitmap.createScaledBitmap(track2,screenWidth-140,track2.getHeight(),false);
            city = Bitmap.createScaledBitmap(city2,screenWidth,screenHeight,false);
            titlepaper = Bitmap.createScaledBitmap(titlepaper2,screenWidth,screenHeight,false);
            logo = Bitmap.createScaledBitmap(logo2,screenWidth-300,screenHeight/9,false);

            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            sensorManager.registerListener(this, accelerometerSensor, sensorManager.SENSOR_DELAY_FASTEST);

            paintProperty = new Paint();
            paintProperty.setTextSize(100);

            musicplayer = MediaPlayer.create(MainActivity.this,R.raw.mutecity);
            flyingplayer = MediaPlayer.create(MainActivity.this,R.raw.flying);
            racestartplayer = MediaPlayer.create(MainActivity.this,R.raw.racestart);
            countdownplayer = MediaPlayer.create(MainActivity.this,R.raw.countdown);
            resultsplayer = MediaPlayer.create(MainActivity.this,R.raw.results);
            winbeepplayer = MediaPlayer.create(MainActivity.this,R.raw.winbeep);
            hitsoundplayer = MediaPlayer.create(MainActivity.this,R.raw.hitsound);
            boostsoundplayer = MediaPlayer.create(MainActivity.this,R.raw.boostsound);
            titlescreenplayer = MediaPlayer.create(MainActivity.this,R.raw.titlescreen);
            splashsoundplayer = MediaPlayer.create(MainActivity.this,R.raw.splashsound);
            buttonsoundplayer = MediaPlayer.create(MainActivity.this,R.raw.buttonsound);
            flyingplayer.setLooping(true);
            resultsplayer.setLooping(true);
            boostsoundplayer.setLooping(true);

            timegame = new Timer();
            timegame.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    time++;
                }
            },0,1);

            enemyY = -enemy.getHeight()-30;
            enemyX = (int)(Math.random()*screenWidth/2-track.getWidth()/2+200)+track.getWidth()-500;
        }

        //Methods that occur when app starts

        @Override
        public void run() {

            if (bigrun) {
                try {

                    //Splash screen

                    while (splash) {
                        if (holder.getSurface().isValid() == false)
                            continue;
                        canvas = holder.lockCanvas();

                        SharedPreferences.Editor prefeditor = pref.edit();
                        prefeditor.clear();
                        prefeditor.putInt("hisc", hiscore).commit();

                        finalize = false;

                        if (postouchx != 0 || postouchy != 0) {
                            postouchx = 0;
                            postouchy = 0;
                        }

                        if (boost)
                            boost = false;

                        paintProperty.setTypeface(typeface);

                        if (time < 500) {
                            paintProperty.setColor(Color.BLACK);
                            splashsoundplayer.start();
                            canvas.drawRect(0f, 0f, 1f, 1f, paintProperty);
                        }
                        if (time < 2500) {
                            paintProperty.setColor(Color.WHITE);
                            Rect rect = new Rect();
                            paintProperty.getTextBounds("Sud™", 0, ("Sud™").length(), rect);
                            canvas.drawText("Sud™", screenWidth / 2 - rect.width() / 2, screenHeight / 2 - rect.height() / 2, paintProperty);
                        }
                        if (time >= 2500) {
                            if ((int) ((time - 2500) / 1000.0 * 255) < 255) {
                                paintProperty.setColor(Color.argb((int) ((time - 2500) / 1000.0 * 255), 0, 0, 0));
                                canvas.drawRect(0f, 0f, (float) screenWidth, (float) screenHeight, paintProperty);
                            } else {
                                paintProperty.setColor(Color.BLACK);
                                canvas.drawRect(0f, 0f, (float) screenWidth, (float) screenHeight, paintProperty);
                                splash = false;
                                title = true;
                                time = 0;
                            }
                        }

                        holder.unlockCanvasAndPost(canvas);
                    }

                    //Title screen

                    while (title) {
                        if (holder.getSurface().isValid() == false)
                            continue;
                        canvas = holder.lockCanvas();

                        finalize = false;

                        SharedPreferences.Editor prefeditor = pref.edit();
                        prefeditor.clear();
                        prefeditor.putInt("hisc", hiscore).commit();

                        titlescreenplayer.start();

                        if (canvasdisplase < titlepaper.getHeight())
                            canvas.drawBitmap(titlepaper, 0, canvasdisplase, null);
                        Matrix matrix = new Matrix();
                        matrix.postScale(1, -1);
                        canvas.drawBitmap(Bitmap.createBitmap(titlepaper, 0, 0, titlepaper.getWidth(), titlepaper.getHeight(), matrix, false), 0, -titlepaper.getHeight() + canvasdisplase, null);
                        if (canvasdisplase > titlepaper.getHeight())
                            canvas.drawBitmap(titlepaper, 0, -titlepaper.getHeight() * 2 + canvasdisplase, null);
                        if (canvasdisplase == titlepaper.getHeight() * 2)
                            canvasdisplase = 0;
                        canvasdisplase++;
                        for (int x = 0; x < screenHeight / track.getHeight() + 2; x++) {
                            canvas.drawBitmap(track, 70, -track.getHeight() + x * track.getHeight() + trackdisplace, null);
                            if (trackdisplace >= track.getHeight())
                                trackdisplace = 0;
                        }
                        trackdisplace += 15;
                        Paint templogo = new Paint();
                        templogo.setTypeface(typeface);
                        templogo.setColorFilter(new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN));
                        templogo.setTextSize(100f);
                        canvas.drawBitmap(logo, 160, 160, templogo);
                        canvas.drawBitmap(shadow, (screenWidth / 2) - car.getWidth() / 2 - 15, screenHeight - (screenHeight / 5) + car.getHeight() + 15, null);
                        canvas.drawBitmap(car, (screenWidth / 2) - car.getWidth() / 2, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                        canvas.drawBitmap(logo, 150, 150, null);
                        templogo.setColorFilter(null);
                        Rect rect = new Rect();
                        templogo.setColor(Color.BLACK);
                        templogo.getTextBounds("MOBILE", 0, ("MOBILE").length(), rect);
                        canvas.drawText("MOBILE", screenWidth / 2 - rect.width() / 2 + 10, 285 + logo.getHeight(), templogo);
                        templogo.setColor(Color.WHITE);
                        canvas.drawText("MOBILE", screenWidth / 2 - rect.width() / 2, 275 + logo.getHeight(), templogo);

                        if (time < 2000) {
                            if (255 - (int) ((time) / 2000.0 * 255) < 255) {
                                paintProperty.setColor(Color.argb(255 - (int) ((time) / 2000.0 * 255), 0, 0, 0));
                                canvas.drawRect(0f, 0f, (float) screenWidth, (float) screenHeight, paintProperty);
                            }
                        }

                        if (time >= 3000) {
                            paintProperty.setTextSize(30f);
                            rect = new Rect();
                            paintProperty.getTextBounds("© 1990 REDACTED, © 2019 Sud", 0, ("© 1990 REDACTED, © 2019 Sud").length(), rect);
                            paintProperty.setColor(Color.BLACK);
                            canvas.drawText("© 1990 REDACTED, © 2019 Sud", screenWidth / 2 - rect.width() / 2 + 5, screenHeight - screenHeight / 20 + 5, paintProperty);
                            paintProperty.setColor(Color.WHITE);
                            canvas.drawText("© 1990 REDACTED, © 2019 Sud", screenWidth / 2 - rect.width() / 2, screenHeight - screenHeight / 20, paintProperty);
                            paintProperty.setTextSize(60f);
                            rect = new Rect();
                            paintProperty.getTextBounds("START", 0, ("START").length(), rect);
                            paintProperty.setColor(Color.BLACK);
                            canvas.drawText("START", screenWidth / 2 - rect.width() / 2 + 10, screenHeight - screenHeight / 6 + 10, paintProperty);
                            paintProperty.setColor(Color.WHITE);
                            canvas.drawText("START", screenWidth / 2 - rect.width() / 2, screenHeight - screenHeight / 6, paintProperty);
                            if ((postouchx >= screenWidth / 2 - rect.width() / 2 - 30 && postouchx <= screenWidth / 2 + rect.width() / 2 + 40 && postouchy >= screenHeight - screenHeight / 6 - 30 && postouchy <= screenHeight - screenHeight / 6 + rect.height() + 40) || hitstart) {
                                if (!hitstart) {
                                    timefromboost = time;
                                    buttonsoundplayer.start();
                                }
                                hitstart = true;
                                if ((int) ((time - timefromboost) / 1000.0 * 255) < 255) {
                                    paintProperty.setColor(Color.argb((int) ((time - timefromboost) / 1000.0 * 255), 0, 0, 0));
                                    canvas.drawRect(0f, 0f, (float) screenWidth, (float) screenHeight, paintProperty);
                                } else {
                                    paintProperty.setColor(Color.BLACK);
                                    titlescreenplayer.stop();
                                    canvas.drawRect(0f, 0f, (float) screenWidth, (float) screenHeight, paintProperty);
                                    title = false;
                                    restart = true;
                                    time = 0;
                                    hitstart = false;
                                }
                            }
                        }

                        if (postouchx != 0 || postouchy != 0) {
                            postouchx = 0;
                            postouchy = 0;
                        }

                        holder.unlockCanvasAndPost(canvas);
                    }

                    //Jingle plays before race

                    while (jingle) {
                        if (holder.getSurface().isValid() == false)
                            continue;
                        canvas = holder.lockCanvas();

                        SharedPreferences.Editor prefeditor = pref.edit();
                        prefeditor.clear();
                        prefeditor.putInt("hisc", hiscore).commit();

                        if (postouchx != 0 || postouchy != 0) {
                            postouchx = 0;
                            postouchy = 0;
                        }

                        if (boost)
                            boost = false;

                        racestartplayer.start();

                        if (canvasdisplase < city.getHeight())
                            canvas.drawBitmap(city, 0, canvasdisplase, null);
                        Matrix matrix = new Matrix();
                        matrix.postScale(1, -1);
                        canvas.drawBitmap(Bitmap.createBitmap(city, 0, 0, city.getWidth(), city.getHeight(), matrix, false), 0, -city.getHeight() + canvasdisplase, null);
                        if (canvasdisplase > city.getHeight())
                            canvas.drawBitmap(city, 0, -city.getHeight() * 2 + canvasdisplase, null);
                        if (canvasdisplase == city.getHeight() * 2)
                            canvasdisplase = 0;
                        canvasdisplase++;
                        for (int x = 0; x < screenHeight / track.getHeight() + 2; x++) {
                            canvas.drawBitmap(track, 70, -track.getHeight() + x * track.getHeight() + trackdisplace, null);
                            if (trackdisplace >= track.getHeight())
                                trackdisplace = 0;
                        }
                        trackdisplace += 60;

                        canvas.drawBitmap(shadow, (screenWidth / 2) - car.getWidth() / 2 + carX - 15, screenHeight - (screenHeight / 5) + car.getHeight() + 15, null);
                        if (carX < carXprev)
                            canvas.drawBitmap(carleft, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                        else if (carX > carXprev)
                            canvas.drawBitmap(carright, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                        else
                            canvas.drawBitmap(car, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                        carXprev = carX;

                        Rect rect = new Rect();
                        paintProperty.setTypeface(typeface);
                        paintProperty.setTextSize(120f);
                        paintProperty.getTextBounds("" + name, 0, ("" + name).length(), rect);
                        paintProperty.setColor(Color.rgb(23, 77, 0));
                        canvas.drawText("" + name, screenWidth / 2 - rect.width() / 2 + 10, screenHeight / 2 - rect.height() / 2 + 10, paintProperty);
                        paintProperty.setColor(Color.GREEN);
                        canvas.drawText("" + name, screenWidth / 2 - rect.width() / 2, screenHeight / 2 - rect.height() / 2, paintProperty);

                        if ((int) (time / 3000.0 * 255) < 255)
                            paintProperty.setColor(Color.argb((int) (255 - time / 3000.0 * 255), 0, 0, 0));
                        else
                            paintProperty.setColor(Color.argb(0, 0, 0, 0));
                        canvas.drawRect(0f, 0f, (float) screenWidth, (float) screenHeight, paintProperty);

                        if (racestartplayer.getCurrentPosition() >= 2100) {
                            flyingplayer.start();
                            time = 0;
                            starting = true;
                            jingle = false;
                            switch ((int) (Math.random() * 3) + 1) {
                                case 1:
                                    enemy = Bitmap.createScaledBitmap(enemy1, 200, 122, false);
                                    break;
                                case 2:
                                    enemy = Bitmap.createScaledBitmap(enemy2, 200, 122, false);
                                    break;
                                case 3:
                                    enemy = Bitmap.createScaledBitmap(enemy3, 200, 122, false);
                                    break;
                            }
                        }

                        holder.unlockCanvasAndPost(canvas);
                    }

                    //Starting countdown

                    while (starting) {
                        if (holder.getSurface().isValid() == false)
                            continue;
                        canvas = holder.lockCanvas();

                        finalize = false;

                        if (boost)
                            boost = false;

                        SharedPreferences.Editor prefeditor = pref.edit();
                        prefeditor.clear();
                        prefeditor.putInt("hisc", hiscore).commit();

                        if (postouchx != 0 || postouchy != 0) {
                            postouchx = 0;
                            postouchy = 0;
                        }

                        countdownplayer.start();

                        if (canvasdisplase < city.getHeight())
                            canvas.drawBitmap(city, 0, canvasdisplase, null);
                        Matrix matrix = new Matrix();
                        matrix.postScale(1, -1);
                        canvas.drawBitmap(Bitmap.createBitmap(city, 0, 0, city.getWidth(), city.getHeight(), matrix, false), 0, -city.getHeight() + canvasdisplase, null);
                        if (canvasdisplase > city.getHeight())
                            canvas.drawBitmap(city, 0, -city.getHeight() * 2 + canvasdisplase, null);
                        if (canvasdisplase == city.getHeight() * 2)
                            canvasdisplase = 0;
                        canvasdisplase++;
                        for (int x = 0; x < screenHeight / track.getHeight() + 2; x++) {
                            canvas.drawBitmap(track, 70, -track.getHeight() + x * track.getHeight() + trackdisplace, null);
                            if (trackdisplace >= track.getHeight())
                                trackdisplace = 0;
                        }
                        trackdisplace += 60;

                        canvas.drawBitmap(shadow, (screenWidth / 2) - car.getWidth() / 2 + carX - 15, screenHeight - (screenHeight / 5) + car.getHeight() + 15, null);
                        if (carX < carXprev)
                            canvas.drawBitmap(carleft, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                        else if (carX > carXprev)
                            canvas.drawBitmap(carright, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                        else
                            canvas.drawBitmap(car, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                        carXprev = carX;

                        Rect rect = new Rect();
                        paintProperty.setTypeface(typeface);
                        paintProperty.setColor(Color.BLACK);
                        paintProperty.setTextSize(300f);

                        if (time >= 3000) {
                            time = 0;
                            running = true;
                            starting = false;
                            paintProperty.getTextBounds("GO!!", 0, ("GO!!").length(), rect);
                            canvas.drawText("GO!!", screenWidth / 2 - rect.width() / 2 + 10, screenHeight / 2 - rect.height() / 2 + 10, paintProperty);
                            paintProperty.setColor(Color.WHITE);
                            canvas.drawText("GO!!", screenWidth / 2 - rect.width() / 2, screenHeight / 2 - rect.height() / 2, paintProperty);
                        } else if (time > 2000) {
                            paintProperty.getTextBounds("1", 0, ("1").length(), rect);
                            canvas.drawText("1", screenWidth / 2 - rect.width() / 2 + 10, screenHeight / 2 - rect.height() / 2 + 10, paintProperty);
                            paintProperty.setColor(Color.WHITE);
                            canvas.drawText("1", screenWidth / 2 - rect.width() / 2, screenHeight / 2 - rect.height() / 2, paintProperty);
                        } else if (time > 1000) {
                            paintProperty.getTextBounds("2", 0, ("2").length(), rect);
                            canvas.drawText("2", screenWidth / 2 - rect.width() / 2 + 10, screenHeight / 2 - rect.height() / 2 + 10, paintProperty);
                            paintProperty.setColor(Color.WHITE);
                            canvas.drawText("2", screenWidth / 2 - rect.width() / 2, screenHeight / 2 - rect.height() / 2, paintProperty);
                        } else if (time > 0) {
                            paintProperty.getTextBounds("3", 0, ("3").length(), rect);
                            canvas.drawText("3", screenWidth / 2 - rect.width() / 2 + 10, screenHeight / 2 - rect.height() / 2 + 10, paintProperty);
                            paintProperty.setColor(Color.WHITE);
                            canvas.drawText("3", screenWidth / 2 - rect.width() / 2, screenHeight / 2 - rect.height() / 2, paintProperty);
                        }

                        holder.unlockCanvasAndPost(canvas);
                    }

                    //When hit during running

                    while (hit) {
                        if (holder.getSurface().isValid() == false)
                            continue;
                        canvas = holder.lockCanvas();

                        finalize = false;

                        SharedPreferences.Editor prefeditor = pref.edit();
                        prefeditor.clear();
                        prefeditor.putInt("hisc", hiscore).commit();

                        if (postouchx != 0 || postouchy != 0) {
                            postouchx = 0;
                            postouchy = 0;
                        }

                        if (boost && boostamount > 0 && (boostprev > boostamount || boostprev == 10)) {
                            boostchange = 2;
                            boostsoundplayer.start();
                            boostsounddone = true;
                            boostamount = 10 - (time - timefromboost) / 1500;
                            if (boostamount <= 0) {
                                timefromboost = time;
                                boost = false;
                            }
                        } else {
                            boost = false;
                            if ((time - timefromboost) / 500 < 10)
                                boostamount = (time - timefromboost) / 500;
                            else
                                boostamount = 10;
                            boostchange = 1;
                        }
                        boostprev = boostamount;

                        if (canvasdisplase < city.getHeight())
                            canvas.drawBitmap(city, 0, canvasdisplase, null);
                        Matrix matrix = new Matrix();
                        matrix.postScale(1, -1);
                        canvas.drawBitmap(Bitmap.createBitmap(city, 0, 0, city.getWidth(), city.getHeight(), matrix, false), 0, -city.getHeight() + canvasdisplase, null);
                        if (canvasdisplase > city.getHeight())
                            canvas.drawBitmap(city, 0, -city.getHeight() * 2 + canvasdisplase, null);
                        if (canvasdisplase == city.getHeight() * 2)
                            canvasdisplase = 0;
                        canvasdisplase += 1 * boostchange;
                        for (int x = 0; x < screenHeight / track.getHeight() + 2; x++) {
                            canvas.drawBitmap(track, 70, -track.getHeight() + x * track.getHeight() + trackdisplace, null);
                            if (trackdisplace >= track.getHeight())
                                trackdisplace = 0;
                        }
                        trackdisplace += 30 * boostchange;

                        canvas.drawBitmap(shadow, (screenWidth / 2) - car.getWidth() / 2 + carX - 15, screenHeight - (screenHeight / 5) + car.getHeight() + 15, null);
                        if ((time - currenttime) % 200 <= 50)
                            canvas.drawBitmap(carhitright, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                        else if ((time - currenttime) % 200 <= 100)
                            canvas.drawBitmap(carhitback, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                        else if ((time - currenttime) % 200 <= 150)
                            canvas.drawBitmap(carhitleft, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                        else if ((time - currenttime) % 200 > 150) {
                            if (!boost)
                                canvas.drawBitmap(car, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                            else
                                canvas.drawBitmap(carboosted, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                        }

                        if (time - currenttime >= 1200) {
                            hit = false;
                            pasthit = true;
                            running = true;
                            hitsoundplayer = MediaPlayer.create(MainActivity.this, R.raw.hitsound);
                        }

                        canvas.drawBitmap(enemy, enemyX, enemyY, null);
                        enemyY += 50 * boostchange;

                        canvas.drawBitmap(screen, screenWidth - screen.getWidth(), 0, null);
                        paintProperty.setTypeface(typeface);
                        paintProperty.setColor(Color.GREEN);
                        paintProperty.setTextSize(30f);
                        Rect rect = new Rect();
                        if (time >= 60000) {
                            paintProperty.getTextBounds("1:00", 0, ("1:00").length(), rect);
                            canvas.drawText("1:00", screenWidth - rect.width() - 40, 60, paintProperty);
                            end = true;
                            hit = false;
                            pasthit = false;
                            running = false;
                            time = 0;
                            musicplayer.stop();
                            winbeepplayer.start();
                        } else {
                            if (time < 10000) {
                                paintProperty.getTextBounds("0:0" + (time / 1000), 0, ("0:0" + (time / 1000)).length(), rect);
                                canvas.drawText("0:0" + (time / 1000), screenWidth - rect.width() - 40, 60, paintProperty);
                            } else {
                                paintProperty.getTextBounds("0:" + (time / 1000), 0, ("0:" + (time / 1000)).length(), rect);
                                canvas.drawText("0:" + (time / 1000), screenWidth - rect.width() - 40, 60, paintProperty);
                            }
                        }
                        rect = new Rect();
                        paintProperty.getTextBounds("" + score + " PTS", 0, ("" + score + " PTS").length(), rect);
                        canvas.drawText("" + score + " PTS", screenWidth - rect.width() - 40, 100, paintProperty);
                        Paint silhouetteset = new Paint();
                        silhouetteset.setColorFilter(new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN));
                        canvas.drawBitmap(Bitmap.createScaledBitmap(carboosted, (int) (carboosted.getWidth() / 2.5), (int) (carboosted.getHeight() / 2.5), false), screenWidth - 350, 20, silhouetteset);
                        if (!boost && boostamount == 10 && time % 250 <= 125) {
                            Paint blinkset = new Paint();
                            blinkset.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
                            canvas.drawBitmap(Bitmap.createScaledBitmap(carboosted, (int) (carboosted.getWidth() / 2.5), (int) (carboosted.getHeight() / 2.5), false), screenWidth - 350, 20, blinkset);
                        } else {
                            Paint alphaset = new Paint();
                            alphaset.setAlpha((int) (255 * boostamount / 10.0));
                            canvas.drawBitmap(Bitmap.createScaledBitmap(carboosted, (int) (carboosted.getWidth() / 2.5), (int) (carboosted.getHeight() / 2.5), false), screenWidth - 350, 20, alphaset);
                        }
                        if (!boost && boostsounddone) {
                            boostsoundplayer = MediaPlayer.create(MainActivity.this, R.raw.boostsound);
                            boostsounddone = false;
                        }
                        Paint winkset = new Paint();
                        winkset.setTypeface(typeface);
                        rect = new Rect();
                        winkset.setTextSize(30f);
                        winkset.getTextBounds("BOOST", 0, ("BOOST").length(), rect);
                        winkset.setColor(Color.rgb(255, 119, 0));
                        canvas.drawText("BOOST", screenWidth - 360, 60 + (Bitmap.createScaledBitmap(carboosted, (int) (carboosted.getWidth() / 2.5), (int) (carboosted.getHeight() / 2.5), false)).getHeight(), winkset);
                        if (!boost && boostsounddone) {
                            boostsoundplayer = MediaPlayer.create(MainActivity.this, R.raw.boostsound);
                            boostsounddone = false;
                        }

                        holder.unlockCanvasAndPost(canvas);
                    }

                    //While running and not hit

                    while (running) {
                        if (holder.getSurface().isValid() == false)
                            continue;
                        canvas = holder.lockCanvas();

                        finalize = false;

                        if (boost && boostamount > 0 && (boostprev > boostamount || boostprev == 10)) {
                            boostchange = 2;
                            boostsoundplayer.start();
                            boostsounddone = true;
                            boostamount = 10 - (time - timefromboost) / 1500;
                            if (boostamount <= 0) {
                                timefromboost = time;
                                boost = false;
                            }
                        } else {
                            boost = false;
                            if ((time - timefromboost) / 500 < 10)
                                boostamount = (time - timefromboost) / 500;
                            else
                                boostamount = 10;
                            boostchange = 1;
                        }
                        boostprev = boostamount;

                        SharedPreferences.Editor prefeditor = pref.edit();
                        prefeditor.clear();
                        prefeditor.putInt("hisc", hiscore).commit();

                        if (postouchx != 0 || postouchy != 0) {
                            postouchx = 0;
                            postouchy = 0;
                        }

                        if (countdownplayer.getDuration() <= countdownplayer.getCurrentPosition())
                            musicplayer.start();

                        if (canvasdisplase < city.getHeight())
                            canvas.drawBitmap(city, 0, canvasdisplase, null);
                        Matrix matrix = new Matrix();
                        matrix.postScale(1, -1);
                        canvas.drawBitmap(Bitmap.createBitmap(city, 0, 0, city.getWidth(), city.getHeight(), matrix, false), 0, -city.getHeight() + canvasdisplase, null);
                        if (canvasdisplase > city.getHeight())
                            canvas.drawBitmap(city, 0, -city.getHeight() * 2 + canvasdisplase, null);
                        if (canvasdisplase == city.getHeight() * 2)
                            canvasdisplase = 0;
                        canvasdisplase += 1 * boostchange;
                        for (int x = 0; x < screenHeight / track.getHeight() + 2; x++) {
                            canvas.drawBitmap(track, 70, -track.getHeight() + x * track.getHeight() + trackdisplace, null);
                            if (trackdisplace >= track.getHeight())
                                trackdisplace = 0;
                        }
                        trackdisplace += 60 * boostchange;

                        canvas.drawBitmap(shadow, (screenWidth / 2) - car.getWidth() / 2 + carX - 15, screenHeight - (screenHeight / 5) + car.getHeight() + 15, null);
                        if (carX < carXprev)
                            canvas.drawBitmap(carleft, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                        else if (carX > carXprev)
                            canvas.drawBitmap(carright, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                        else {
                            if (!boost)
                                canvas.drawBitmap(car, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                            else
                                canvas.drawBitmap(carboosted, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight(), null);
                        }
                        carXprev = carX;

                        canvas.drawBitmap(shadow, enemyX - 15, enemyY + 35, null);
                        canvas.drawBitmap(enemy, enemyX, enemyY, null);

                        if (screenHeight - (screenHeight / 5) + car.getHeight() < enemyY && pasthit == false) {
                            if (boostchange == 2)
                                score += 100 * 4;
                            else
                                score += 100;
                            pasthit = true;
                        }

                        if (time < 1500) {
                            Rect rect2 = new Rect();
                            paintProperty.setTypeface(typeface);
                            paintProperty.setColor(Color.BLACK);
                            paintProperty.setTextSize(300f);
                            paintProperty.getTextBounds("GO!!", 0, ("GO!!").length(), rect2);
                            canvas.drawText("GO!!", screenWidth / 2 - rect2.width() / 2 + 10, screenHeight / 2 - rect2.height() / 2 + 10, paintProperty);
                            paintProperty.setColor(Color.WHITE);
                            canvas.drawText("GO!!", screenWidth / 2 - rect2.width() / 2, screenHeight / 2 - rect2.height() / 2, paintProperty);
                        }
                        if (time > 3000) {
                            countdownplayer.stop();
                            if (!pasthit && ((enemyY <= screenHeight - (screenHeight / 5) + car.getHeight() && enemyY + enemy.getHeight() >= screenHeight - (screenHeight / 5) + car.getHeight() && enemyX <= (screenWidth / 2) - car.getWidth() / 2 + carX && enemyX + enemy.getWidth() >= (screenWidth / 2) - car.getWidth() / 2 + carX) || (enemyY <= screenHeight - (screenHeight / 5) + car.getHeight() && enemyY + enemy.getHeight() >= screenHeight - (screenHeight / 5) + car.getHeight() && enemyX <= (screenWidth / 2) - car.getWidth() / 2 + carX + car.getWidth() && enemyX + enemy.getWidth() >= (screenWidth / 2) - car.getWidth() / 2 + carX + car.getWidth()))) {
                                hit = true;
                                pasthit = true;
                                currenttime = time;
                                running = false;
                                hitsoundplayer.start();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vib.vibrate(VibrationEffect.createOneShot(1200, 75));
                                } else {
                                    vib.vibrate(1200);
                                }
                            }
                            enemyY += 50 * boostchange;
                            if (enemyY >= screenHeight + enemy.getHeight()) {
                                enemyY = -enemy.getHeight();
                                enemyX = (int) (Math.random() * screenWidth / 2 - track.getWidth() / 2 + 200) + track.getWidth() - 500;
                                pasthit = false;
                                switch ((int) (Math.random() * 3) + 1) {
                                    case 1:
                                        enemy = Bitmap.createScaledBitmap(enemy1, 200, 122, false);
                                        break;
                                    case 2:
                                        enemy = Bitmap.createScaledBitmap(enemy2, 200, 122, false);
                                        break;
                                    case 3:
                                        enemy = Bitmap.createScaledBitmap(enemy3, 200, 122, false);
                                        break;
                                }
                            }
                        }

                        canvas.drawBitmap(screen, screenWidth - screen.getWidth(), 0, null);
                        paintProperty.setTypeface(typeface);
                        paintProperty.setColor(Color.GREEN);
                        paintProperty.setTextSize(30f);
                        Rect rect = new Rect();
                        if (time >= 60000) {
                            paintProperty.setColor(Color.BLACK);
                            paintProperty.getTextBounds("1:00", 0, ("1:00").length(), rect);
                            canvas.drawText("1:00", screenWidth - rect.width() - 30, 70, paintProperty);
                            paintProperty.setColor(Color.GREEN);
                            canvas.drawText("1:00", screenWidth - rect.width() - 40, 60, paintProperty);
                            end = true;
                            running = false;
                            hit = false;
                            time = 0;
                            musicplayer.stop();
                            winbeepplayer.start();
                        } else {
                            if (time < 10000) {
                                paintProperty.getTextBounds("0:0" + (time / 1000), 0, ("0:0" + (time / 1000)).length(), rect);
                                paintProperty.setColor(Color.BLACK);
                                canvas.drawText("0:0" + (time / 1000), screenWidth - rect.width() - 30, 70, paintProperty);
                                paintProperty.setColor(Color.GREEN);
                                canvas.drawText("0:0" + (time / 1000), screenWidth - rect.width() - 40, 60, paintProperty);
                            } else {
                                paintProperty.getTextBounds("0:" + (time / 1000), 0, ("0:" + (time / 1000)).length(), rect);
                                paintProperty.setColor(Color.BLACK);
                                canvas.drawText("0:" + (time / 1000), screenWidth - rect.width() - 30, 70, paintProperty);
                                paintProperty.setColor(Color.GREEN);
                                canvas.drawText("0:" + (time / 1000), screenWidth - rect.width() - 40, 60, paintProperty);
                            }
                        }
                        rect = new Rect();
                        paintProperty.setColor(Color.BLACK);
                        paintProperty.getTextBounds("" + score + " PTS", 0, ("" + score + " PTS").length(), rect);
                        canvas.drawText("" + score + " PTS", screenWidth - rect.width() - 30, 110, paintProperty);
                        paintProperty.setColor(Color.GREEN);
                        canvas.drawText("" + score + " PTS", screenWidth - rect.width() - 40, 100, paintProperty);
                        Paint silhouetteset = new Paint();
                        silhouetteset.setColorFilter(new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN));
                        canvas.drawBitmap(Bitmap.createScaledBitmap(carboosted, (int) (carboosted.getWidth() / 2.5), (int) (carboosted.getHeight() / 2.5), false), screenWidth - 350, 20, silhouetteset);
                        if (!boost && boostamount == 10 && time % 250 <= 125) {
                            Paint blinkset = new Paint();
                            blinkset.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
                            canvas.drawBitmap(Bitmap.createScaledBitmap(carboosted, (int) (carboosted.getWidth() / 2.5), (int) (carboosted.getHeight() / 2.5), false), screenWidth - 350, 20, blinkset);
                        } else {
                            Paint alphaset = new Paint();
                            alphaset.setAlpha((int) (255 * boostamount / 10.0));
                            canvas.drawBitmap(Bitmap.createScaledBitmap(carboosted, (int) (carboosted.getWidth() / 2.5), (int) (carboosted.getHeight() / 2.5), false), screenWidth - 350, 20, alphaset);
                        }
                        if (!boost && boostsounddone) {
                            boostsoundplayer = MediaPlayer.create(MainActivity.this, R.raw.boostsound);
                            boostsounddone = false;
                        }
                        Paint winkset = new Paint();
                        winkset.setTypeface(typeface);
                        rect = new Rect();
                        winkset.setTextSize(30f);
                        winkset.getTextBounds("BOOST", 0, ("BOOST").length(), rect);
                        winkset.setColor(Color.BLACK);
                        canvas.drawText("BOOST", screenWidth - 350, 70 + (Bitmap.createScaledBitmap(carboosted, (int) (carboosted.getWidth() / 2.5), (int) (carboosted.getHeight() / 2.5), false)).getHeight(), winkset);
                        winkset.setColor(Color.rgb(255, 119, 0));
                        canvas.drawText("BOOST", screenWidth - 360, 60 + (Bitmap.createScaledBitmap(carboosted, (int) (carboosted.getWidth() / 2.5), (int) (carboosted.getHeight() / 2.5), false)).getHeight(), winkset);

                        holder.unlockCanvasAndPost(canvas);
                    }

                    //When race is over

                    while (end) {
                        if (holder.getSurface().isValid() == false)
                            continue;
                        canvas = holder.lockCanvas();

                        if (boost)
                            boost = false;

                        finalize = false;

                        vib.cancel();

                        SharedPreferences.Editor prefeditor = pref.edit();
                        prefeditor.clear();
                        prefeditor.putInt("hisc", hiscore).commit();

                        hitsoundplayer.stop();
                        musicplayer.stop();

                        if (score > hiscore)
                            hiscore = score;

                        if (canvasdisplase < city.getHeight())
                            canvas.drawBitmap(city, 0, canvasdisplase, null);
                        Matrix matrix = new Matrix();
                        matrix.postScale(1, -1);
                        canvas.drawBitmap(Bitmap.createBitmap(city, 0, 0, city.getWidth(), city.getHeight(), matrix, false), 0, -city.getHeight() + canvasdisplase, null);
                        if (canvasdisplase > city.getHeight())
                            canvas.drawBitmap(city, 0, -city.getHeight() * 2 + canvasdisplase, null);
                        if (canvasdisplase == city.getHeight() * 2)
                            canvasdisplase = 0;
                        for (int x = 0; x < screenHeight / track.getHeight() + 2; x++) {
                            canvas.drawBitmap(track, 70, -track.getHeight() + x * track.getHeight() + trackdisplace, null);
                            if (trackdisplace >= track.getHeight())
                                trackdisplace = 0;
                        }

                        canvas.drawBitmap(shadow, (screenWidth / 2) - car.getWidth() / 2 + carX - 15, screenHeight - (screenHeight / 5) + car.getHeight() + 15 - playerdisplace, null);
                        canvas.drawBitmap(car, (screenWidth / 2) - car.getWidth() / 2 + carX, screenHeight - (screenHeight / 5) + car.getHeight() - playerdisplace, null);
                        playerdisplace += 50;

                        if (screenHeight - (screenHeight / 5) + car.getHeight() - playerdisplace - 15 < 0 && winbeepplayer.getCurrentPosition() == winbeepplayer.getDuration()) {
                            flyingplayer.stop();
                            resultsplayer.start();
                            if (resultsplayer.getCurrentPosition() <= 50 && whatever.equals("none")) {
                                time = 0;
                                whatever = "some";
                            }
                            if (time >= 1500) {
                                paintProperty.setColor(Color.argb(128, 0, 0, 0));
                                canvas.drawRect((float) (100), (float) (400), (float) (screenWidth - 100), (float) (screenHeight - 400), paintProperty);
                            }
                            if (time >= 2250) {
                                paintProperty.setTextSize(200f);
                                Rect rect = new Rect();
                                paintProperty.setColor(Color.BLACK);
                                paintProperty.getTextBounds("FINISH", 0, ("FINISH").length(), rect);
                                canvas.drawText("FINISH", screenWidth / 2 - rect.width() / 2 + 10, 640, paintProperty);
                                paintProperty.setColor(Color.MAGENTA);
                                canvas.drawText("FINISH", screenWidth / 2 - rect.width() / 2, 630, paintProperty);
                            }
                            if (time >= 3000) {
                                paintProperty.setTextSize(80f);
                                Rect rect = new Rect();
                                paintProperty.getTextBounds("SCORE", 0, ("SCORE").length(), rect);
                                paintProperty.setColor(Color.BLACK);
                                canvas.drawText("SCORE", screenWidth / 2 - rect.width() / 2 + 10, 840, paintProperty);
                                paintProperty.setColor(Color.WHITE);
                                canvas.drawText("SCORE", screenWidth / 2 - rect.width() / 2, 830, paintProperty);
                            }
                            if (time >= 3750) {
                                Rect rect = new Rect();
                                paintProperty.getTextBounds("" + score + " PTS", 0, ("" + score + " PTS").length(), rect);
                                paintProperty.setColor(Color.BLACK);
                                canvas.drawText("" + score + " PTS", screenWidth / 2 - rect.width() / 2 + 10, 940, paintProperty);
                                if (score == hiscore)
                                    paintProperty.setColor(Color.YELLOW);
                                else
                                    paintProperty.setColor(Color.WHITE);
                                canvas.drawText("" + score + " PTS", screenWidth / 2 - rect.width() / 2, 930, paintProperty);
                            }
                            if (time >= 4500) {
                                Rect rect = new Rect();
                                paintProperty.setColor(Color.BLACK);
                                paintProperty.getTextBounds("HI SCORE", 0, ("HI SCORE").length(), rect);
                                canvas.drawText("HI SCORE", screenWidth / 2 - rect.width() / 2 + 10, 1070, paintProperty);
                                paintProperty.setColor(Color.WHITE);
                                canvas.drawText("HI SCORE", screenWidth / 2 - rect.width() / 2, 1060, paintProperty);
                            }
                            if (time >= 5250) {
                                Rect rect = new Rect();
                                paintProperty.getTextBounds("" + hiscore + " PTS", 0, ("" + hiscore + " PTS").length(), rect);
                                paintProperty.setColor(Color.BLACK);
                                canvas.drawText("" + hiscore + " PTS", screenWidth / 2 - rect.width() / 2 + 10, 1170, paintProperty);
                                if (score == hiscore)
                                    paintProperty.setColor(Color.YELLOW);
                                else
                                    paintProperty.setColor(Color.WHITE);
                                canvas.drawText("" + hiscore + " PTS", screenWidth / 2 - rect.width() / 2, 1160, paintProperty);
                            }
                            if (time >= 6000) {
                                Rect rect = new Rect();
                                if (racenumb % 10 == 9) {
                                    paintProperty.getTextBounds("NEXT", 0, ("NEXT").length(), rect);
                                    paintProperty.setColor(Color.BLACK);
                                    canvas.drawText("NEXT", screenWidth / 2 - rect.width() / 2 + 10, 1450, paintProperty);
                                    paintProperty.setColor(Color.WHITE);
                                    canvas.drawText("NEXT", screenWidth / 2 - rect.width() / 2, 1440, paintProperty);
                                } else {
                                    paintProperty.getTextBounds("NEXT RACE", 0, ("NEXT RACE").length(), rect);
                                    paintProperty.setColor(Color.BLACK);
                                    canvas.drawText("NEXT RACE", screenWidth / 2 - rect.width() / 2 + 10, 1450, paintProperty);
                                    paintProperty.setColor(Color.WHITE);
                                    canvas.drawText("NEXT RACE", screenWidth / 2 - rect.width() / 2, 1440, paintProperty);
                                }
                                if ((postouchx >= 100 && postouchx <= screenWidth - 100 && postouchy >= 1260 && postouchy <= screenHeight - 400) || hitrestart) {
                                    if (!hitrestart) {
                                        timefromboost = time;
                                        buttonsoundplayer.start();
                                    }
                                    hitrestart = true;
                                    if ((int) ((time - timefromboost) / 500.0 * 255) < 255) {
                                        paintProperty.setColor(Color.argb((int) ((time - timefromboost) / 500.0 * 255), 0, 0, 0));
                                        canvas.drawRect(0f, 0f, (float) screenWidth, (float) screenHeight, paintProperty);
                                    } else {
                                        paintProperty.setColor(Color.BLACK);
                                        canvas.drawRect(0f, 0f, (float) screenWidth, (float) screenHeight, paintProperty);
                                        if (time - timefromboost >= 1500) {
                                            end = false;
                                            resultsplayer.stop();
                                            if (racenumb % 10 == 9) {
                                                master = true;
                                                Bitmap space2 = BitmapFactory.decodeResource(getResources(), R.drawable.space);
                                                city = Bitmap.createScaledBitmap(space2, screenWidth, screenHeight, false);
                                                musicplayer = MediaPlayer.create(MainActivity.this, R.raw.finaltheme);
                                            } else
                                                restart = true;
                                            racenumb++;
                                            time = 0;
                                            hitrestart = false;
                                        }
                                    }
                                }
                            }
                        } else {
                            Rect rect = new Rect();
                            paintProperty.setTypeface(typeface);
                            paintProperty.setTextSize(280f);
                            paintProperty.setColor(Color.BLACK);
                            paintProperty.getTextBounds("1:00", 0, ("1:00").length(), rect);
                            canvas.drawText("1:00", screenWidth / 2 - rect.width() / 2 + 10, screenHeight / 2 - rect.height() / 4 + 10, paintProperty);
                            if (time % 200 <= 100)
                                paintProperty.setColor(Color.WHITE);
                            else
                                paintProperty.setColor(Color.rgb(252, 146, 255));
                            canvas.drawText("1:00", screenWidth / 2 - rect.width() / 2, screenHeight / 2 - rect.height() / 4, paintProperty);
                        }

                        if (postouchx != 0 || postouchy != 0) {
                            postouchx = 0;
                            postouchy = 0;
                        }

                        holder.unlockCanvasAndPost(canvas);
                    }

                    //Restarting all variables right before next race

                    while (restart) {
                        if (holder.getSurface().isValid() == false)
                            continue;
                        canvas = holder.lockCanvas();

                        SharedPreferences.Editor prefeditor = pref.edit();
                        prefeditor.clear();
                        prefeditor.putInt("hisc", hiscore).commit();

                        finalize = false;

                        timefromboost = 0;
                        time = 0;
                        boostamount = 10;
                        boostprev = 10;
                        postouchx = 0;
                        postouchy = 0;

                        whatever = "none";

                        carX = 0;
                        x = 200;
                        carXprev = 0;
                        move = 0;
                        trackdisplace = 0;
                        canvasdisplase = 0;
                        enemyX = 0;
                        enemyY = 0;
                        score = 0;
                        boostchange = 1;
                        playerdisplace = 0;
                        currenttime = 0;

                        String sensorOutput = "";

                        Bitmap city2 = BitmapFactory.decodeResource(getResources(), R.drawable.city);
                        Bitmap ocean2 = BitmapFactory.decodeResource(getResources(), R.drawable.ocean);
                        Bitmap sand2 = BitmapFactory.decodeResource(getResources(), R.drawable.desert);
                        Bitmap death2 = BitmapFactory.decodeResource(getResources(), R.drawable.death);
                        Bitmap wasteland2 = BitmapFactory.decodeResource(getResources(), R.drawable.wasteland);
                        Bitmap port2 = BitmapFactory.decodeResource(getResources(), R.drawable.port);
                        Bitmap canyon2 = BitmapFactory.decodeResource(getResources(), R.drawable.canyon);
                        Bitmap snowy2 = BitmapFactory.decodeResource(getResources(), R.drawable.snowy);
                        Bitmap snowy22 = BitmapFactory.decodeResource(getResources(), R.drawable.snowy2);
                        Bitmap fire2 = BitmapFactory.decodeResource(getResources(), R.drawable.fire);

                        switch (racenumb % 10) {
                            case 0:
                                city = Bitmap.createScaledBitmap(city2, screenWidth, screenHeight, false);
                                musicplayer = MediaPlayer.create(MainActivity.this, R.raw.mutecity);
                                name = "MUTE CITY";
                                break;
                            case 1:
                                city = Bitmap.createScaledBitmap(ocean2, screenWidth, screenHeight, false);
                                musicplayer = MediaPlayer.create(MainActivity.this, R.raw.bigblue);
                                name = "BIG BLUE";
                                break;
                            case 2:
                                city = Bitmap.createScaledBitmap(sand2, screenWidth, screenHeight, false);
                                musicplayer = MediaPlayer.create(MainActivity.this, R.raw.sandocean);
                                name = "SAND OCEAN";
                                break;
                            case 3:
                                city = Bitmap.createScaledBitmap(death2, screenWidth, screenHeight, false);
                                musicplayer = MediaPlayer.create(MainActivity.this, R.raw.deathwind);
                                name = "DEATH WIND";
                                break;
                            case 4:
                                city = Bitmap.createScaledBitmap(wasteland2, screenWidth, screenHeight, false);
                                musicplayer = MediaPlayer.create(MainActivity.this, R.raw.silence);
                                name = "SILENCE";
                                break;
                            case 5:
                                city = Bitmap.createScaledBitmap(port2, screenWidth, screenHeight, false);
                                musicplayer = MediaPlayer.create(MainActivity.this, R.raw.porttown);
                                name = "PORT TOWN";
                                break;
                            case 6:
                                city = Bitmap.createScaledBitmap(canyon2, screenWidth, screenHeight, false);
                                musicplayer = MediaPlayer.create(MainActivity.this, R.raw.redcanyon);
                                name = "RED CANYON";
                                break;
                            case 7:
                                city = Bitmap.createScaledBitmap(snowy2, screenWidth, screenHeight, false);
                                musicplayer = MediaPlayer.create(MainActivity.this, R.raw.whitelandno1);
                                name = "WHITE LAND I";
                                break;
                            case 8:
                                city = Bitmap.createScaledBitmap(snowy22, screenWidth, screenHeight, false);
                                musicplayer = MediaPlayer.create(MainActivity.this, R.raw.whitelandno2);
                                name = "WHITE LAND II";
                                break;
                            case 9:
                                city = Bitmap.createScaledBitmap(fire2, screenWidth, screenHeight, false);
                                musicplayer = MediaPlayer.create(MainActivity.this, R.raw.firefield);
                                name = "FIRE FIELD";
                                break;
                        }

                        paintProperty = new Paint();
                        paintProperty.setTextSize(100);

                        flyingplayer = MediaPlayer.create(MainActivity.this, R.raw.flying);
                        racestartplayer = MediaPlayer.create(MainActivity.this, R.raw.racestart);
                        countdownplayer = MediaPlayer.create(MainActivity.this, R.raw.countdown);
                        resultsplayer = MediaPlayer.create(MainActivity.this, R.raw.results);
                        winbeepplayer = MediaPlayer.create(MainActivity.this, R.raw.winbeep);
                        hitsoundplayer = MediaPlayer.create(MainActivity.this, R.raw.hitsound);
                        boostsoundplayer = MediaPlayer.create(MainActivity.this, R.raw.boostsound);
                        buttonsoundplayer = MediaPlayer.create(MainActivity.this, R.raw.buttonsound);
                        flyingplayer.setLooping(true);
                        resultsplayer.setLooping(true);
                        boostsoundplayer.setLooping(true);

                        enemyY = -enemy.getHeight() - 30;
                        enemyX = (int) (Math.random() * screenWidth / 2 - track.getWidth() / 2 + 200) + track.getWidth() - 500;

                        restart = false;
                        jingle = true;

                        holder.unlockCanvasAndPost(canvas);
                    }

                    //Pseudo ending screen

                    while (master) {
                        if (holder.getSurface().isValid() == false)
                            continue;
                        canvas = holder.lockCanvas();

                        SharedPreferences.Editor prefeditor = pref.edit();
                        prefeditor.clear();
                        prefeditor.putInt("hisc", hiscore).commit();

                        musicplayer.start();

                        if (canvasdisplase < city.getHeight())
                            canvas.drawBitmap(city, 0, canvasdisplase, null);
                        Matrix matrix = new Matrix();
                        matrix.postScale(1, -1);
                        canvas.drawBitmap(Bitmap.createBitmap(city, 0, 0, city.getWidth(), city.getHeight(), matrix, false), 0, -city.getHeight() + canvasdisplase, null);
                        if (canvasdisplase > city.getHeight())
                            canvas.drawBitmap(city, 0, -city.getHeight() * 2 + canvasdisplase, null);
                        if (canvasdisplase == city.getHeight() * 2)
                            canvasdisplase = 0;
                        canvasdisplase++;
                        for (int x = 0; x < screenHeight / track.getHeight() + 2; x++) {
                            canvas.drawBitmap(track, 70, -track.getHeight() + x * track.getHeight() + trackdisplace, null);
                            if (trackdisplace >= track.getHeight())
                                trackdisplace = 0;
                        }
                        trackdisplace += 15;
                        canvas.drawBitmap(shadow, (screenWidth / 2) - car.getWidth() / 2 - 15, screenHeight - (screenHeight / 5) + car.getHeight() + 15, null);
                        canvas.drawBitmap(car, (screenWidth / 2) - car.getWidth() / 2, screenHeight - (screenHeight / 5) + car.getHeight(), null);

                        paintProperty.setColor(Color.WHITE);
                        paintProperty.setTypeface(typeface);
                        paintProperty.setTextSize(60f);
                        Paint templogo = new Paint();
                        templogo.setTypeface(typeface);
                        templogo.setColorFilter(new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN));
                        templogo.setTextSize(100f);
                        canvas.drawBitmap(logo, 160, screenHeight / 2 - logo.getHeight() / 2 + 10, templogo);
                        canvas.drawBitmap(logo, 150, screenHeight / 2 - logo.getHeight() / 2, null);
                        templogo.setColorFilter(null);
                        Rect rect = new Rect();
                        templogo.setColor(Color.BLACK);
                        templogo.getTextBounds("MOBILE", 0, ("MOBILE").length(), rect);
                        canvas.drawText("MOBILE", screenWidth / 2 - rect.width() / 2 + 10, 355 + screenHeight / 2 - logo.getHeight() / 2, templogo);
                        templogo.setColor(Color.WHITE);
                        canvas.drawText("MOBILE", screenWidth / 2 - rect.width() / 2, 345 + screenHeight / 2 - logo.getHeight() / 2, templogo);
                        paintProperty.setColor(Color.BLACK);
                        paintProperty.getTextBounds("YOU ARE THE MASTER OF", 0, ("YOU ARE THE MASTER OF").length(), rect);
                        canvas.drawText("YOU ARE THE MASTER OF", screenWidth / 2 - rect.width() / 2 + 10, -120 + rect.height() + screenHeight / 2 - logo.getHeight() / 2, paintProperty);
                        paintProperty.setColor(Color.WHITE);
                        canvas.drawText("YOU ARE THE MASTER OF", screenWidth / 2 - rect.width() / 2, -130 + rect.height() + screenHeight / 2 - logo.getHeight() / 2, paintProperty);
                        paintProperty.setColor(Color.BLACK);
                        paintProperty.getTextBounds("CONGRATULATIONS", 0, ("CONGRATULATIONS").length(), rect);
                        canvas.drawText("CONGRATULATIONS", screenWidth / 2 - rect.width() / 2 + 10, -250 + rect.height() + screenHeight / 2 - logo.getHeight() / 2, paintProperty);
                        paintProperty.setColor(Color.WHITE);
                        canvas.drawText("CONGRATULATIONS", screenWidth / 2 - rect.width() / 2, -260 + rect.height() + screenHeight / 2 - logo.getHeight() / 2, paintProperty);
                        paintProperty.setColor(Color.BLACK);
                        paintProperty.setTextSize(30f);
                        paintProperty.getTextBounds("~ CAPTAIN FALCON & THE F-ZERO CREW", 0, ("~ CAPTAIN FALCON & THE F-ZERO CREW").length(), rect);
                        canvas.drawText("~ CAPTAIN FALCON & THE F-ZERO CREW", screenWidth / 2 - rect.width() / 2 + 10, 435 + screenHeight / 2 - logo.getHeight() / 2, paintProperty);
                        paintProperty.setColor(Color.WHITE);
                        canvas.drawText("~ CAPTAIN FALCON & THE F-ZERO CREW", screenWidth / 2 - rect.width() / 2, 425 + screenHeight / 2 - logo.getHeight() / 2, paintProperty);
                        paintProperty.setTextSize(40f);
                        paintProperty.setColor(Color.BLACK);
                        paintProperty.getTextBounds("TAP TO CONTINUE PLAYING", 0, ("TAP TO CONTINUE PLAYING").length(), rect);
                        canvas.drawText("TAP TO CONTINUE PLAYING", screenWidth / 2 - rect.width() / 2 + 10, screenHeight - screenHeight / 6 + 10, paintProperty);
                        paintProperty.setColor(Color.WHITE);
                        canvas.drawText("TAP TO CONTINUE PLAYING", screenWidth / 2 - rect.width() / 2, screenHeight - screenHeight / 6, paintProperty);

                        if (time < 2000) {
                            if (255 - (int) ((time) / 2000.0 * 255) < 255) {
                                paintProperty.setColor(Color.argb(255 - (int) ((time) / 2000.0 * 255), 0, 0, 0));
                                canvas.drawRect(0f, 0f, (float) screenWidth, (float) screenHeight, paintProperty);
                            }
                        } else {
                            if (finalize || hitfinalize) {
                                if (!hitfinalize) {
                                    timefromboost = time;
                                    buttonsoundplayer.start();
                                }
                                hitfinalize = true;
                                if ((int) ((time - timefromboost) / 500.0 * 255) < 255) {
                                    paintProperty.setColor(Color.argb((int) ((time - timefromboost) / 500.0 * 255), 0, 0, 0));
                                    canvas.drawRect(0f, 0f, (float) screenWidth, (float) screenHeight, paintProperty);
                                } else {
                                    paintProperty.setColor(Color.BLACK);
                                    canvas.drawRect(0f, 0f, (float) screenWidth, (float) screenHeight, paintProperty);
                                    if (time >= 1500) {
                                        musicplayer.stop();
                                        restart = true;
                                        time = 0;
                                        hitfinalize = false;
                                        master = false;
                                    }
                                }
                            }
                        }

                        finalize = false;

                        holder.unlockCanvasAndPost(canvas);
                    }
                }
                catch (Exception e) {

                }
                gameThread = new Thread(this);
                gameThread.start();
            }
        }

        //What happens when app returns from multitasking

        public void destroy() {
            vib.cancel();
        }

        public void resume() {
            if (gameThread != null && !bigrun) {
                synchronized (gameThread) {
                    if (timeprev != 0)
                        time = timeprev;
                    bigrun = true;
                    timeprev = 0;
                    timegame = new Timer();
                    timegame.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            time++;
                        }
                    },0,1);
                    gameThread = new Thread(this);
                    gameThread.start();
                }
            }
            else if (gameThread == null) {
                gameThread = new Thread(this);
                gameThread.start();
            }
        }

        //What happens when app goes into multitasking

        public void pause() {
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        //Actions when accelerometer detects change in value

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (-(int) ((event.values[0]) / 9.81 * screenWidth / 2) <= ((screenWidth - 400 - car.getWidth()) / 2) && -(int) ((event.values[0])/ 9.81 * screenWidth / 2) >= -((screenWidth - 400 - car.getWidth()) / 2) && !end && !title && !splash)
                carX = -(int) ((event.values[0]) / 9.81 * screenWidth / 2);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}