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

package com.sahildave.snackbar.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import com.sahildave.snackbar.SnackBar;
import com.sahildave.snackbar.SnackBar.MessageType;
import com.sahildave.snackbar.SnackBar.SnackBarType;


public class SnackBarActivity extends ActionBarActivity implements SnackBar.SnackBarListener {

    private SnackBar mSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snack_bar);
        mSnackBar = new SnackBar(this);
    }


    //Single Info - showSingleLineInfo
    //Single Action - showSingleLineAction
    //Single Option - showSingleLineOption
    //Multi Info - showMultiLineInfo


    //Single Line - message, submessage, type, type
    //Multi Line - message, submessage[], type, type

    public void addCall(View v){
        mSnackBar.showSingleLineSnack("Call Customer Support at:", "18001234567", SnackBar.MessageType.PHONE, SnackBar.SnackBarType.SINGLELINE_INFO);
    }

    public void addEmail(View v){
        mSnackBar.showSingleLineSnack("Email Customer Support at:", "support@hdfcbank.com", SnackBar.MessageType.EMAIL, SnackBar.SnackBarType.SINGLELINE_INFO);
    }

    public void addMap(View v){
        mSnackBar.showSingleLineSnack("Nearest Bank Branch at:", "goo.gl/sample", SnackBar.MessageType.MAP, SnackBar.SnackBarType.SINGLELINE_INFO);
    }

    public void addInfo(View v){
        mSnackBar.showSingleLineSnack("More info at:", "support.hdfcbank.com", SnackBar.MessageType.WEB, SnackBar.SnackBarType.SINGLELINE_INFO);
    }

    public void addAll(View v){
        mSnackBar.showSingleLineSnack("Call Customer Support at:", "18001234567", SnackBar.MessageType.PHONE, SnackBar.SnackBarType.SINGLELINE_INFO);
        mSnackBar.showSingleLineSnack("Email Customer Support at:", "support@hdfcbank.com", SnackBar.MessageType.EMAIL, SnackBar.SnackBarType.SINGLELINE_INFO);
        mSnackBar.showSingleLineSnack("Nearest Bank Branch at:", "goo.gl/sample", SnackBar.MessageType.MAP, SnackBar.SnackBarType.SINGLELINE_INFO);
        mSnackBar.showSingleLineSnack("More info at:", "support.hdfcbank.com", SnackBar.MessageType.WEB, SnackBar.SnackBarType.SINGLELINE_INFO);
    }

    public void addAction(View v){
        mSnackBar.showSingleLineSnack("Seems like you have forgotten your customer ID. Would you like some help?",
                "",
                SnackBar.MessageType.MESSAGE,
                SnackBar.SnackBarType.SINGLELINE_ACTION);
    }

    public void addMultiLine(View v) {

        String[] messageArray = {"Back of this", "In box below", "Next to Signature"};
        mSnackBar.showMultiLineSnack("Check Customer Id at:", messageArray, SnackBar.MessageType.CHEQUE, SnackBar.SnackBarType.MULTILINE_INFO);
    }

    @Override
    public void onBackPressed() {
        mSnackBar.onBackPressedHandler();
    }

    @Override
    public void positiveButtonClicked() {

        mSnackBar.showSingleLineSnack("Use above help to find your Customer ID", "", MessageType.NO_PHOTO, SnackBarType.SINGLELINE_FOOTER);

        String[] chequeBookMessageArray = {"Back of this", "In box below", "Next to Signature"};
        mSnackBar.showMultiLineSnack("Cheque Book:", chequeBookMessageArray, SnackBar.MessageType.CHEQUE, SnackBar.SnackBarType.MULTILINE_ACTION);

        String[] accStatementBookMessageArray = {"Back of this", "In box below", "Next to Signature"};
        mSnackBar.showMultiLineSnack("Account Statement:", accStatementBookMessageArray, MessageType.ACCOUNT_STATEMENT, SnackBar.SnackBarType.MULTILINE_ACTION);

//        String[] customerIdMessageArray = {"Cheque Book", "Account Statement"};
//        mSnackBar.showSingleLineOption("You can get your Customer ID in:", customerIdMessageArray, SnackBar.MessageType.NO_PHOTO, SnackBarType.MULTILINE_ACTION);
    }

    @Override
    public void negativeButtonClicked() {
        mSnackBar.removeAndClearAllSnacks();
    }

    @Override
    public void moreHelpButtonClicked() {
        addAll(null);
    }

    public void addFooter(View view) {
        positiveButtonClicked();
    }
}
