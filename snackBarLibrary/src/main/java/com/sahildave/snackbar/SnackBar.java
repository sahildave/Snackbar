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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SnackBar {

    private static final int IN_ANIMATION_DURATION = 150;
    private static final int OUT_ANIMATION_DURATION = 250;
    private static final String LOG_TAG = "SnackBar";
    private final ViewGroup snackbarListContainer;
    private Activity activity;

    private AnimationSet mOutAnimationSet;
    private AnimationSet mInAnimationSet;


    public SnackBar(Activity activity) {
        this.activity = activity;
        ViewGroup rootContainer = (ViewGroup) activity.findViewById(android.R.id.content);
        snackbarListContainer = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.snackbar_container, rootContainer);
    }

    public void show(String message, String subMessage, MessageType messageType, SnackBarType snackBarType){

        if(snackBarType==SnackBarType.SINGLELINE){
            //Views
            View v = activity.getLayoutInflater().inflate(R.layout.usb_simple_text, null);
            TextView mSnackMsgView = (TextView) v.findViewById(R.id.snackMessage);
            TextView mSnackSubMsgView = (TextView) v.findViewById(R.id.snackSubMessage);
            ImageView mSnackIcon = (ImageView) v.findViewById(R.id.snackIcon);

            mSnackMsgView.setText(message);
            mSnackSubMsgView.setText(subMessage);
            mSnackIcon.setImageResource(getSnackIcon(messageType));

            LinearLayout rootLayout = (LinearLayout)snackbarListContainer.findViewById(R.id.snackListContainer);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(24,12,24,12);
            v.setLayoutParams(params);
            v.setTag(messageType);
            v.setAnimation(getEntryAnimation());
            rootLayout.addView(v, 0);
        }

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


    public enum MessageType {
        PHONE,
        EMAIL,
        WEB,
        MAP
    }

    public enum SnackBarType {
        SINGLELINE,
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

        AlphaAnimation mFadeInAnimation = new AlphaAnimation(0.0f, 1.0f);

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

        AlphaAnimation mFadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);

        mOutAnimationSet.addAnimation(mSlideOutAnimation);
        mOutAnimationSet.addAnimation(mFadeOutAnimation);

        mOutAnimationSet.setDuration(OUT_ANIMATION_DURATION);

        return  mOutAnimationSet;
    }
}
