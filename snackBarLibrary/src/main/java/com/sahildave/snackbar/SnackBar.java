/*
 * Copyright (c) 2014 MrEngineer13
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sahildave.snackbar;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SnackBar {

    private static final int IN_ANIMATION_DURATION = 150;
    private static final int OUT_ANIMATION_DURATION = 250;
    private static final String LOG_TAG = "SnackBar";
    private final ViewGroup snackbarListContainer;
    private final LinearLayout rootLayout;
    private final SnackBarListener snackBarListener;
    private GestureDetectorCompat mGestureDetector;
    private Activity activity;

    private AnimationSet mOutAnimationSet;
    private AnimationSet mInAnimationSet;

    private List<View> currentSnacks;

    public interface SnackBarListener{

        void positiveButtonClicked();

        void negativeButtonClicked();
    }


    public SnackBar(Activity activity) {
        this.activity = activity;
        ViewGroup rootContainer = (ViewGroup) activity.findViewById(android.R.id.content);
        snackbarListContainer = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.snackbar_container, rootContainer);
        rootLayout = (LinearLayout)snackbarListContainer.findViewById(R.id.snackListContainer);
        snackBarListener = (SnackBarListener) activity;
        currentSnacks = new ArrayList<View>();
        setupFlingToDismiss();
    }

    private void setupFlingToDismiss() {
        mGestureDetector = new GestureDetectorCompat(activity, new GestureListener());
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    public void showSingleLineInfo(String message, String subMessage, MessageType messageType, SnackBarType snackBarType){

        if(snackBarType==SnackBarType.SINGLELINE_INFO){
            addSingleLineSnack(message, subMessage, messageType);
        }

    }

    private void addSingleLineSnack(String message, String subMessage, MessageType messageType) {
        View v = activity.getLayoutInflater().inflate(R.layout.usb_simple_text_info, null);
        TextView mSnackMsgView = (TextView) v.findViewById(R.id.snackMessage);
        TextView mSnackSubMsgView = (TextView) v.findViewById(R.id.snackSubMessage);
        ImageView mSnackIcon = (ImageView) v.findViewById(R.id.snackIcon);

        mSnackMsgView.setText(message);
        mSnackSubMsgView.setText(subMessage);
        mSnackIcon.setImageResource(getSnackIcon(messageType));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(24,12,24,12);
        v.setLayoutParams(params);
        v.setTag(messageType);
        v.setAnimation(getEntryAnimation());
        addToView(v);
    }

    public void showSingleLineAction(String message, String positiveText, String negativeText, SnackBarType snackBarType) {

        if(snackBarType == SnackBarType.SINGLELINE_ACTION){
            addSingleLineAction(message, positiveText, negativeText);
        }
    }

    private void addSingleLineAction(String message, String positiveText, String negativeText) {
        View v = activity.getLayoutInflater().inflate(R.layout.usb_simple_text_action, null);
        TextView mSnackMsgView = (TextView) v.findViewById(R.id.snackMessage);
        Button mSnackPositiveButton = (Button) v.findViewById(R.id.snackPositiveButton);
        Button mSnackNegativeButton = (Button) v.findViewById(R.id.snackNegativeButton);

        mSnackMsgView.setText(message);
        mSnackPositiveButton.setText(positiveText);
        mSnackNegativeButton.setText(negativeText);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(24, 12, 24, 12);
        v.setLayoutParams(params);
        v.setAnimation(getEntryAnimation());
        addToView(v);

        mSnackPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootLayout.startAnimation(getExitAnimation());
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        rootLayout.clearAnimation();
                        for(View v: currentSnacks){
                            rootLayout.removeView(v);
                        }
                        Log.e(LOG_TAG, "Size of current "+currentSnacks.size());
                        currentSnacks.clear();
                        Log.e(LOG_TAG, "Size of current "+currentSnacks.size());
                        snackBarListener.positiveButtonClicked();
                    }
                }, OUT_ANIMATION_DURATION);
            }
        });

        mSnackNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBarListener.negativeButtonClicked();
            }
        });

    }

    private void addToView(View v) {
        rootLayout.addView(v, 0);
        currentSnacks.add(v);
    }

    private int getSnackIcon(MessageType messageType) {
        int snackIcon;
        switch (messageType){
            case PHONE:
                snackIcon = R.drawable.phone;
                break;
            case WEB:
                snackIcon = R.drawable.web;
                break;
            case EMAIL:
                snackIcon = R.drawable.email;
                break;
            case MAP:
                snackIcon = R.drawable.map;
                break;
            default:
                snackIcon = R.drawable.question;
                break;
        }
        return snackIcon;
    }

    public void onBackPressedHandler() {
        if(rootLayout.getChildCount()>0){
            removeSnacks();
            currentSnacks.clear();
        } else {
            activity.finish();
        }
    }

    public void removeSnacks() {
        rootLayout.startAnimation(getExitAnimation());
        new Handler().postDelayed(new Runnable() {
            public void run() {
                rootLayout.clearAnimation();
                rootLayout.removeAllViews();
            }
        }, OUT_ANIMATION_DURATION);
    }

    public enum MessageType {
        PHONE,
        EMAIL,
        WEB,
        MAP
    }

    public enum SnackBarType {
        SINGLELINE_INFO,
        SINGLELINE_ACTION,
        MULTILINE,
        CONTAINER
    }

    private AnimationSet getEntryAnimation() {
        //In
        mInAnimationSet = new AnimationSet(false);

        TranslateAnimation mSlideInAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 1.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
        mSlideInAnimation.setFillAfter(true);

        AlphaAnimation mFadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        mFadeInAnimation.setFillAfter(true);

        mInAnimationSet.addAnimation(mSlideInAnimation);
        mInAnimationSet.addAnimation(mFadeInAnimation);

        mInAnimationSet.setDuration(IN_ANIMATION_DURATION);

        return mInAnimationSet;

    }

    private AnimationSet getExitAnimation(){
        //Out
        mOutAnimationSet = new AnimationSet(false);

        TranslateAnimation mSlideOutAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 1.0f);

        mSlideOutAnimation.setFillAfter(true);

        AlphaAnimation mFadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        mFadeOutAnimation.setFillAfter(true);

        mOutAnimationSet.addAnimation(mSlideOutAnimation);
        mOutAnimationSet.addAnimation(mFadeOutAnimation);

        mOutAnimationSet.setDuration(OUT_ANIMATION_DURATION);

        return  mOutAnimationSet;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            if (event2.getY() > event1.getY()) {
                removeSnacks();
            }
            return true;
        }
    }
}
