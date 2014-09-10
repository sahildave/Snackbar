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
import android.view.*;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class SnackBar {

    private static final int IN_ANIMATION_DURATION = 150;
    private static final int OUT_ANIMATION_DURATION = 250;
    private static final String LOG_TAG = "SnackBar";
    private final ViewGroup snackbarListContainer;
    private final LinearLayout rootLayout;
    private final SnackBarListener snackBarListener;
    private final ViewGroup rootContainer;
    private GestureDetectorCompat mGestureDetector;
    private Activity activity;

    private AnimationSet mOutAnimationSet;
    private AnimationSet mInAnimationSet;

    private List<View> currentSnacks;

    public interface SnackBarListener{

        void positiveButtonClicked();

        void negativeButtonClicked();

        void radioButtonClicked(MessageType messageType);
    }


    public SnackBar(Activity activity) {
        this.activity = activity;
        rootContainer = (ViewGroup) activity.findViewById(android.R.id.content);
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
            addSingleLineInfo(message, subMessage, messageType);
        }

    }

    private void addSingleLineInfo(String message, String subMessage, MessageType messageType) {
        View v = activity.getLayoutInflater().inflate(R.layout.usb_singleline_info, null);
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

    public void showSingleLineAction(String message, String positiveText, String negativeText, MessageType messageType, SnackBarType snackBarType) {

        if(snackBarType == SnackBarType.SINGLELINE_ACTION){
            addSingleLineAction(message, positiveText, negativeText, messageType);
        }
    }

    private void addSingleLineAction(String message, String positiveText, String negativeText, MessageType messageType) {
        View v = activity.getLayoutInflater().inflate(R.layout.usb_singleline_action, null);
        TextView mSnackMsgView = (TextView) v.findViewById(R.id.snackMessage);
        ImageView mSnackIcon = (ImageView) v.findViewById(R.id.snackIcon);
        Button mSnackPositiveButton = (Button) v.findViewById(R.id.snackPositiveButton);
        Button mSnackNegativeButton = (Button) v.findViewById(R.id.snackNegativeButton);

        mSnackMsgView.setText(message);
        mSnackIcon.setImageResource(getSnackIcon(messageType));
        mSnackPositiveButton.setText(positiveText);
        mSnackNegativeButton.setText(negativeText);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(24, 12, 24, 12);
        v.setLayoutParams(params);
        v.setAnimation(getEntryAnimation());
//        v.setTag("Password"); //TODO: Add a tagging system
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
                        currentSnacks.clear();
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

    public void showSingleLineOption(String message, List<SnackOption> snackOptionList, SnackBarType snackBarType) {

        if(snackBarType == SnackBarType.SINGLELINE_OPTION){
            addSingleLineOption(message, snackOptionList);
        }
    }

    private void addSingleLineOption(String message, List<SnackOption> snackOptionList) {
        final View v = activity.getLayoutInflater().inflate(R.layout.usb_singleline_option, null);
        TextView mSnackMsgView = (TextView) v.findViewById(R.id.snackMessage);
        RadioGroup mSnackRadioGroup = (RadioGroup) v.findViewById(R.id.snackRadioGroup);

        for(SnackOption option: snackOptionList){
            Log.d(LOG_TAG, "Adding - "+option.getTitle()+", "+option.getMessageType());
            RadioButton rb = new RadioButton(activity);
            rb.setText(option.getTitle());
            rb.setTag(option.getMessageType());
            rb.setGravity(Gravity.CENTER);

            mSnackRadioGroup.addView(rb);
        }

        mSnackMsgView.setText(message);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(24,12,24,12);
        v.setLayoutParams(params);
        v.setAnimation(getEntryAnimation());
        addToView(v);


        mSnackRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
                rootLayout.startAnimation(getExitAnimation());
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Log.d(LOG_TAG, "Clicked - "+i);

                        int childId = radioGroup.getCheckedRadioButtonId();
                        RadioButton rb = (RadioButton) radioGroup.findViewById(childId);
                        MessageType returnMessagetype = (MessageType)rb.getTag();

                        rootLayout.clearAnimation();
                        //TODO: Can be used for something like removeAllButThis(View v)
                        for(View v: currentSnacks){
                            rootLayout.removeView(v);
                        }
                        currentSnacks.clear();
                        snackBarListener.radioButtonClicked(returnMessagetype);
                    }
                }, OUT_ANIMATION_DURATION);
            }
        });

    }

    public void showMultiLineInfo (String message, String[] subMessageArray, MessageType messageType, SnackBarType snackBarType){

        if(snackBarType==SnackBarType.MULTILINE_INFO){
            addMultiLineIfo(message, subMessageArray, messageType);
        }

    }

    private void addMultiLineIfo(String message, String[] subMessageArray, MessageType messageType) {

        View v = activity.getLayoutInflater().inflate(R.layout.usb_multiline_info, null);
        TextView mSnackMsgView = (TextView) v.findViewById(R.id.snackMessage);
        TextView mSnackSubMsgView = (TextView) v.findViewById(R.id.snackSubMessage);
        ImageView mSnackIcon = (ImageView) v.findViewById(R.id.snackIcon);

        mSnackMsgView.setText(message);
        mSnackIcon.setImageResource(getSnackIcon(messageType));

        CharSequence subMessage = getMultiLineInstructions(subMessageArray);
        mSnackSubMsgView.setText(subMessage.subSequence(0, subMessage.length()-1)); //to remove extra new line char

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

    //TODO: Change to ViewStub
    public View showLargeContainer (MessageType messageType, SnackBarType snackBarType, String url){
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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(24, 64, 24, 12);
        v.setLayoutParams(params);
        v.setAnimation(getEntryAnimation());
        addToView(v);

        return v;

    }

    public void setContainerLiveHelp(View snackView, String message, String[] subMessageArray){
        TextView mSnackSubMsgView = (TextView) snackView.findViewById(R.id.snackSubMessage);
        TextView mSnackMsgView = (TextView) snackView.findViewById(R.id.snackMessage);

        mSnackMsgView.setText(message);
        CharSequence subMessage = getMultiLineInstructions(subMessageArray);
        mSnackSubMsgView.setText(subMessage.subSequence(0, subMessage.length()-1)); //to remove extra new line char


    }

    private CharSequence getMultiLineInstructions(String[] subMessageArray) {
        CharSequence subMessage = "";
        for(String subMessageItem: subMessageArray){

            SpannableString spannableString = new SpannableString(subMessageItem+"\n");
            spannableString.setSpan(new BulletSpan(15), 0, subMessageItem.length(), 0);

            subMessage = TextUtils.concat(subMessage, spannableString);
        }
        return subMessage;
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
            case MESSAGE:
                snackIcon = R.drawable.message;
                break;
            case CHEQUE:
                snackIcon = R.drawable.cheque;
                break;
            case ACCOUNT_STATEMENT:
                snackIcon = R.drawable.account_statement;
                break;
            default:
                snackIcon = R.drawable.question;
                break;
        }
        return snackIcon;
    }

    public void onBackPressedHandler() {
        if(rootLayout.getChildCount()>0){
            removeAllSnacks();
            currentSnacks.clear();
        } else {
            activity.finish();
        }
    }

    public void removeAllSnacks() {
        rootLayout.startAnimation(getExitAnimation());
        new Handler().postDelayed(new Runnable() {
            public void run() {
                rootLayout.clearAnimation();
                rootLayout.removeAllViews();
                currentSnacks.clear();
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
        PASS_BOOK
    }

    public enum SnackBarType {
        SINGLELINE_INFO,
        SINGLELINE_ACTION,
        SINGLELINE_OPTION,
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
                removeAllSnacks();
            }
            return true;
        }
    }


    public static class SnackOption{
        MessageType messageType;
        String title;

        public SnackOption(String title, MessageType messageType){
            setMessageType(messageType);
            setTitle(title);
            Log.d(LOG_TAG, "Constructor Option - "+title+", of messagetype - "+messageType.toString());

        }

        public MessageType getMessageType() {
            return messageType;
        }

        public void setMessageType(MessageType messageType) {
            this.messageType = messageType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }

}
