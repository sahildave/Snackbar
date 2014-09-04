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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Stack;

public class SnackBar {

    private static final String SAVED_MSGS = "SAVED_MSGS";

    private static final String SAVED_CURR_MSG = "SAVED_CURR_MSG";

    private static final int ANIMATION_DURATION = 300;

    public static final short LONG_SNACK = 5000;

    public static final short MED_SNACK = 3500;

    public static final short SHORT_SNACK = 2000;
    private Activity activity;

    private View mContainer;
    private TextView mSnackMsgView;
    private TextView mSnackSubMsgView;
    private ImageView mSnackIcon;

    private String snackMsg;
    private String snackSubMsg;

    private Stack<Snack> mSnacks = new Stack<Snack>();
    private Snack mCurrentSnack;
    private boolean mShowing;
    private Handler mHandler;
    private AnimationSet mOutAnimationSet;
    private AnimationSet mInAnimationSet;
    private Context mContext;
    private View.OnClickListener snackIconListener;


    public SnackBar(Activity activity) {
        this.activity = activity;
        mContext = activity.getApplicationContext();
    }

    public SnackBar(Activity activity, SnackBarType snackBarType){
        this.activity = activity;
        mContext = activity.getApplicationContext();
        ViewGroup container = (ViewGroup) activity.findViewById(android.R.id.content);
        if(snackBarType==SnackBarType.SINGLELINE){
            View rootView = activity.getLayoutInflater().inflate(R.layout.usb_simple_text, container);
            initSingleLine(rootView);
        }
    }

    public SnackBar(Activity activity, String message) {
        this.activity = activity;
        mContext = activity.getApplicationContext();
        ViewGroup container = (ViewGroup) activity.findViewById(android.R.id.content);
        View v = activity.getLayoutInflater().inflate(R.layout.usb_simple_text, container);
        snackMsg = message;
        initSingleLine(v);
    }

    public SnackBar(Activity activity, String message, String subMessage) {
        this.activity = activity;
        mContext = activity.getApplicationContext();
        ViewGroup container = (ViewGroup) activity.findViewById(android.R.id.content);
        View v = activity.getLayoutInflater().inflate(R.layout.usb_simple_text, container);
        snackMsg = message;
        snackSubMsg = subMessage;
        initSingleLine(v);
    }

    private void initSingleLine(View v) {

        //Views
        mContainer = v.findViewById(R.id.snackContainer);
        mContainer.setVisibility(View.GONE);
        mSnackMsgView = (TextView) v.findViewById(R.id.snackMessage);
        mSnackSubMsgView = (TextView) v.findViewById(R.id.snackSubMessage);
        mSnackIcon = (ImageView) v.findViewById(R.id.snackIcon);

        mHandler = new Handler();

        //Animations
        setupAnimations();

    }

    //NEW
    public void show(String message, String subMessage, MessageType messageType, SnackBarType snackBarType){
        int snackIcon=R.drawable.question;
        snackMsg = message;
        snackSubMsg = subMessage;
        switch (messageType){
            case PHONE:
                snackIcon = R.drawable.phone;
                snackIconListener = phoneListener;
                break;
            case WEB:
                snackIcon = R.drawable.web;
                snackIconListener = webListener;
                break;
            case EMAIL:
                snackIcon = R.drawable.email;
                snackIconListener = emailListener;
                break;
            case MAP:
                snackIcon = R.drawable.map;
                snackIconListener = mapListener;
                break;
        }

        show(message, subMessage, snackIcon, messageType, snackBarType);
    }

    public void show(String message, String subMessage, int snackIcon, MessageType messageType, SnackBarType snackBarType){
        snackMsg = message;
        snackSubMsg = subMessage;
        mSnackMsgView.setText(snackMsg);
        mSnackSubMsgView.setText(snackSubMsg);
        mSnackIcon.setImageResource(snackIcon);
        mSnackIcon.setOnClickListener(snackIconListener);

        Snack snack = new Snack(message, subMessage, snackIcon);
        snack.setSnackBarType(snackBarType);
        snack.setMessageType(messageType);

        if (isShowing()) {
            mSnacks.push(snack);
        } else {
            show(snack);
        }
    }

    private void show(Snack snack) {
        show(snack, false);
    }

    private void show(Snack snack, boolean immediately) {
        mShowing = true;
        mContainer.setVisibility(View.VISIBLE);
        mSnackMsgView.setText(snack.mMessage);

        System.out.println("immediately " + immediately);

        if (immediately) {
            mInAnimationSet.setDuration(0);
        } else {
            mInAnimationSet.setDuration(ANIMATION_DURATION);
        }
        mContainer.startAnimation(mInAnimationSet);

        mContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                                mHandler.removeCallbacks(mHideRunnable);
                                mContainer.startAnimation(mOutAnimationSet);

                                if (!mSnacks.empty()) {
                                    mSnacks.clear();
                                }
                }
                return true;
            }
        });
    }

    private final View.OnClickListener phoneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + snackSubMsg));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    };

    private final View.OnClickListener emailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_SEND);

            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, snackSubMsg);
            intent.putExtra(Intent.EXTRA_SUBJECT, "");
            intent.putExtra(Intent.EXTRA_TEXT,"");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(Intent.createChooser(intent, "Choose an Email client :"));

        }
    };

    private final View.OnClickListener webListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private final View.OnClickListener mapListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public void setOnClickListener(View.OnClickListener listener) {
        snackIconListener = listener;
    }

    public void clear() {
        mSnacks.clear();
        mHideRunnable.run();
    }

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mContainer.startAnimation(mOutAnimationSet);
        }
    };

    public void onRestoreInstanceState(Bundle state) {
        Snack currentSnack = state.getParcelable(SAVED_CURR_MSG);
        if (currentSnack != null) {
            show(currentSnack, true);
            Parcelable[] messages = state.getParcelableArray(SAVED_MSGS);
            for (Parcelable p : messages) {
                mSnacks.push((Snack) p);
            }
        }
    }

    public void onBackPressedHandler(){
        if(isShowing()){
            clear();
        } else {
            activity.finish();
        }
    }


    public Bundle onSaveInstanceState() {
        Bundle b = new Bundle();

        b.putParcelable(SAVED_CURR_MSG, mCurrentSnack);

        final int count = mSnacks.size();
        final Snack[] snacks = new Snack[count];
        int i = 0;
        for (Snack snack : mSnacks) {
            snacks[i++] = snack;
        }

        b.putParcelableArray(SAVED_MSGS, snacks);

        return b;
    }

    private boolean isShowing(){
        return mShowing;
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

    private void setupAnimations() {
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

        mOutAnimationSet.setDuration(ANIMATION_DURATION);
        mOutAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!mSnacks.empty()) {
                    show(mSnacks.pop());
                } else {
                    mCurrentSnack = null;
                    mContainer.setVisibility(View.GONE);
                    mShowing = false;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private static class Snack implements Parcelable {

        final String mMessage;

        final String mActionMessage;

        final int mActionIcon;

        SnackBarType snackBarType;

        MessageType messageType;

        public Snack(String message, String actionMessage, int actionIcon) {
            mMessage = message;
            mActionMessage = actionMessage;
            mActionIcon = actionIcon;
        }

        public void setSnackBarType(SnackBarType snackBarType){
            this.snackBarType = snackBarType;
        }

        public void setMessageType(MessageType messageType){
            this.messageType = messageType;
        }

        // reads data from parcel
        public Snack(Parcel p) {
            mMessage = p.readString();
            mActionMessage = p.readString();
            mActionIcon = p.readInt();
        }

        // writes data to parcel
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(mMessage);
            out.writeString(mActionMessage);
            out.writeInt(mActionIcon);
        }

        public int describeContents() {
            return 0;
        }

        // creates snack array
        public static final Parcelable.Creator<Snack> CREATOR = new Parcelable.Creator<Snack>() {
            public Snack createFromParcel(Parcel in) {
                return new Snack(in);
            }

            public Snack[] newArray(int size) {
                return new Snack[size];
            }
        };
    }
}
