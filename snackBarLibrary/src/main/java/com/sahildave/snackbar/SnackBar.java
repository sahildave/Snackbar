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
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SnackBar {

    private static final int IN_ANIMATION_DURATION = 150;
    private static final int OUT_ANIMATION_DURATION = 150;
    private static final String LOG_TAG = "SnackBar";
    private LinearLayout rootLayout;
    private final SnackBarListener snackBarListener;
    private final Stack<List<View>> allSnacks;
    private GestureDetectorCompat mGestureDetector;
    private Activity activity;

    private AnimationSet mOutAnimationSet;
    private AnimationSet mInAnimationSet;

    private List<View> currentSnackList;
    private ViewGroup snackbarContainer;
    private ViewGroup activityContainer;

    public interface SnackBarListener{

        void positiveButtonClicked();

        void negativeButtonClicked();

        void moreHelpButtonClicked();
    }


    public SnackBar(Activity activity) {
        this.activity = activity;
        snackBarListener = (SnackBarListener) activity;

        currentSnackList = new ArrayList<View>();
        allSnacks = new Stack<List<View>>();

    }

    private void inflateSnackBarContainer() {
        activityContainer = (ViewGroup) activity.findViewById(android.R.id.content);
        snackbarContainer = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.snackbar_container, activityContainer);
        rootLayout = (LinearLayout) snackbarContainer.findViewById(R.id.snackBarContainer);

        Log.e(LOG_TAG, "Inflating - ");

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

    /*
        SINGLELINES
    */

    public void showSingleLineSnack(String message, String subMessage, MessageType messageType, SnackBarType snackBarType){

        if(rootLayout == null){
            inflateSnackBarContainer();
        }

        if(snackBarType==SnackBarType.SINGLELINE_INFO){
            addSingleLineInfo(message, subMessage, messageType);
        } else if(snackBarType == SnackBarType.SINGLELINE_ACTION){
            addSingleLineAction(message, messageType);
        } else if(snackBarType == SnackBarType.SINGLELINE_FOOTER){
            addSingleLineFooter(message);
        }
    }

    private void addSingleLineInfo(String message, String subMessage, MessageType messageType) {
        View v = activity.getLayoutInflater().inflate(R.layout.usb_singleline_info, null);
        TextView mSnackMsgView = (TextView) v.findViewById(R.id.snackMessage);
        TextView mSnackSubMsgView = (TextView) v.findViewById(R.id.snackSubMessage);
        ImageView mSnackIcon = (ImageView) v.findViewById(R.id.snackIcon);

        mSnackMsgView.setText(message);
        mSnackSubMsgView.setText(subMessage);
        setSnackIcon(messageType, mSnackIcon);

        v.setTag(messageType);
        addToView(v);
    }

    private void addSingleLineAction(String message, MessageType messageType) {
        View v = activity.getLayoutInflater().inflate(R.layout.usb_singleline_action, null);
        TextView mSnackMsgView = (TextView) v.findViewById(R.id.snackMessage);
        ImageView mSnackIcon = (ImageView) v.findViewById(R.id.snackIcon);
        Button mSnackPositiveButton = (Button) v.findViewById(R.id.snackPositiveButton);
        Button mSnackNegativeButton = (Button) v.findViewById(R.id.snackNegativeButton);

        mSnackMsgView.setText(message);
        setSnackIcon(messageType, mSnackIcon);

        v.setTag(messageType);
        addToView(v);

        mSnackPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootLayout.startAnimation(getExitAnimation());
                updateAllSnackArray();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        rootLayout.clearAnimation();
                        for(View v: currentSnackList){
                            rootLayout.removeView(v);
                        }
                        currentSnackList = new ArrayList<View>();
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

    private void addSingleLineFooter(String message) {
        View v = activity.getLayoutInflater().inflate(R.layout.usb_singleline_footer, null);
        TextView mSnackMsgView = (TextView) v.findViewById(R.id.snackMessage);
        Button mSnackBackButton = (Button) v.findViewById(R.id.snackBackButton);
        Button mSnackMoreHelpButton = (Button) v.findViewById(R.id.snackMoreHelp);

        mSnackMsgView.setText(message);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(12,6,12,0);
        v.setLayoutParams(params);

        v.setAnimation(getEntryAnimation());

        rootLayout.addView(v, 0);
        currentSnackList.add(v);

        mSnackMoreHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootLayout.startAnimation(getExitAnimation());
                updateAllSnackArray();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        rootLayout.clearAnimation();
                        for(View v: currentSnackList){
                            rootLayout.removeView(v);
                        }
                        currentSnackList = new ArrayList<View>();
                        snackBarListener.moreHelpButtonClicked();
                    }
                }, OUT_ANIMATION_DURATION);
            }
        });

        mSnackBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPreviousSnacks();
            }
        });

    }


    /*
        MULTILINES
    */

    public void showMultiLineSnack(String message, String[] subMessageArray, MessageType messageType, SnackBarType snackBarType){

        if(rootLayout == null){
            inflateSnackBarContainer();
        }

        if(snackBarType==SnackBarType.MULTILINE_INFO){
            addMultiLineInfo(message, subMessageArray, messageType);
        } else if(snackBarType == SnackBarType.MULTILINE_ACTION){
            addMultiLineOption(message, subMessageArray, messageType);
        }
    }

    private void addMultiLineOption(String message, String[] subMessageArray, MessageType messageType) {
        final View v = activity.getLayoutInflater().inflate(R.layout.usb_multiline_action, null);
        TextView mSnackMsgView = (TextView) v.findViewById(R.id.snackMessage);
        TextView mSnackSubMsgView = (TextView) v.findViewById(R.id.snackSubMessage);
        Button mSnackIcon = (Button) v.findViewById(R.id.snackIcon);

        CharSequence subMessage = getBulletSpanMessage(subMessageArray);

        mSnackMsgView.setText(message);
        mSnackSubMsgView.setText(subMessage);

        if(messageType == MessageType.NO_PHOTO){
            View divider = v.findViewById(R.id.snackDivider);
            divider.setVisibility(View.GONE);
            mSnackIcon.setVisibility(View.GONE);
        }

        mSnackIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        v.setTag(messageType);
        addToView(v);

    }

    private void addMultiLineInfo(String message, String[] subMessageArray, MessageType messageType) {

        View v = activity.getLayoutInflater().inflate(R.layout.usb_multiline_info, null);
        TextView mSnackMsgView = (TextView) v.findViewById(R.id.snackMessage);
        TextView mSnackSubMsgView = (TextView) v.findViewById(R.id.snackSubMessage);
        ImageView mSnackIcon = (ImageView) v.findViewById(R.id.snackIcon);

        mSnackMsgView.setText(message);

        setSnackIcon(messageType, mSnackIcon);

        CharSequence subMessage = getBulletSpanMessage(subMessageArray);
        mSnackSubMsgView.setText(subMessage.subSequence(0, subMessage.length()-1)); //to remove extra new line char

        v.setTag(messageType);
        addToView(v);
    }

    /*
        CONTAINERS
     */

    //TODO: Change to ViewStub
        public View showContainerSnack(MessageType messageType, SnackBarType snackBarType, String url){

        if(rootLayout == null){
            inflateSnackBarContainer();
        }

        if(snackBarType ==SnackBarType.LARGE_CONTAINER){
            return addLargeContainer(messageType, url);
        }
        return null;
    }

    private View addLargeContainer(MessageType messageType, String inputUrl){
        View v = activity.getLayoutInflater().inflate(R.layout.usb_large_container, null);
        WebView webView = (WebView) v.findViewById(R.id.snackWebview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(inputUrl);

        v.setTag(messageType);
        addToView(v);

        return v;

    }

    public void setContainerLiveHelp(View snackView, String message, String[] subMessageArray){
        TextView mSnackSubMsgView = (TextView) snackView.findViewById(R.id.snackSubMessage);
        TextView mSnackMsgView = (TextView) snackView.findViewById(R.id.snackMessage);

        mSnackMsgView.setText(message);
        CharSequence subMessage = getBulletSpanMessage(subMessageArray);
        mSnackSubMsgView.setText(subMessage.subSequence(0, subMessage.length() - 1)); //to remove extra new line char
    }

    /*
        UTILS
     */

    private void showPreviousSnacks() {

        if(rootLayout == null){
            inflateSnackBarContainer();
        }

        rootLayout.startAnimation(getExitAnimation());
        new Handler().postDelayed(new Runnable() {
            public void run() {
                rootLayout.clearAnimation();
                for(View v: currentSnackList){
                    rootLayout.removeView(v);
                }
                if(allSnacks.size()>0){
                    List<View> previousSnacks = allSnacks.pop();

                    currentSnackList = new ArrayList<View>();
                    for(View v: previousSnacks){
                        v.setAnimation(getEntryAnimation());
                        addToView(v);
                    }
                }

            }
        }, OUT_ANIMATION_DURATION);
    }

    private void addToView(View v) {


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(12,6,12,6);
        v.setLayoutParams(params);

        v.setAnimation(getEntryAnimation());

        rootLayout.addView(v, 0);
        currentSnackList.add(v);
    }

    private void updateAllSnackArray() {
        allSnacks.push(currentSnackList);
    }

    private CharSequence getBulletSpanMessage(String[] subMessageArray) {
        CharSequence subMessage = "";
        for(String subMessageItem: subMessageArray){

            SpannableString spannableString = new SpannableString(subMessageItem+"\n");
            spannableString.setSpan(new BulletSpan(15), 0, subMessageItem.length(), 0);

            subMessage = TextUtils.concat(subMessage, spannableString);
        }
        return subMessage;
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
            case MESSAGE:
                snackIcon = R.drawable.message;
                break;
            case CHEQUE:
                snackIcon = R.drawable.cheque;
                break;
            case ACCOUNT_STATEMENT:
                snackIcon = R.drawable.account_statement;
                break;
            case NO_PHOTO:
                snackIcon = 0;

                break;
            default:
                snackIcon = R.drawable.question;
                break;
        }
        return snackIcon;
    }

    private void setSnackIcon(MessageType messageType, ImageView mSnackIcon) {
        int icon = getSnackIcon(messageType);
        if(icon!=0){
            mSnackIcon.setImageResource(getSnackIcon(messageType));
        } else {
            mSnackIcon.setVisibility(View.GONE);
        }
    }

    public void onBackPressedHandler() {
        if(rootLayout!=null && rootLayout.getChildCount()>0){
            removeAndClearAllSnacks();
        } else {
            activity.finish();
        }
    }

    public void removeAndClearAllSnacks() {
        rootLayout.startAnimation(getExitAnimation());
        new Handler().postDelayed(new Runnable() {
            public void run() {
                rootLayout.clearAnimation();
                rootLayout.removeAllViews();
                currentSnackList.clear();
                allSnacks.clear();

                View snackBarParent = activityContainer.findViewById(R.id.snackBarContainerParent);
                if(snackBarParent!=null && activityContainer!=null){
                    activityContainer.removeView(snackBarParent);
                }
                rootLayout=null;
            }
        }, OUT_ANIMATION_DURATION);
    }

    public enum MessageType {
        PHONE,
        EMAIL,
        WEB,
        MAP,
        MESSAGE,
        CHEQUE,
        ACCOUNT_STATEMENT,
        NO_PHOTO,
        PASS_BOOK
    }

    public enum SnackBarType {
        SINGLELINE_INFO,
        SINGLELINE_ACTION,
        SINGLELINE_FOOTER,
        MULTILINE_ACTION,
        MULTILINE_INFO,
        LARGE_CONTAINER
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
                removeAndClearAllSnacks();
            }
            return true;
        }
    }
}
