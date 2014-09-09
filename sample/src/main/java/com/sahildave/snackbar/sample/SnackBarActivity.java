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
import android.widget.Toast;
import com.sahildave.snackbar.SnackBar;


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

    public void addCall(View v){
        mSnackBar.showSingleLineInfo("Call Customer Support at:", "18001234567", SnackBar.MessageType.PHONE, SnackBar.SnackBarType.SINGLELINE_INFO);
    }

    public void addEmail(View v){
        mSnackBar.showSingleLineInfo("Email Customer Support at:", "support@hdfcbank.com", SnackBar.MessageType.EMAIL, SnackBar.SnackBarType.SINGLELINE_INFO);
    }

    public void addMap(View v){
        mSnackBar.showSingleLineInfo("Nearest Bank Branch at:", "goo.gl/sample", SnackBar.MessageType.MAP, SnackBar.SnackBarType.SINGLELINE_INFO);
    }

    public void addInfo(View v){
        mSnackBar.showSingleLineInfo("More info at:", "support.hdfcbank.com", SnackBar.MessageType.WEB, SnackBar.SnackBarType.SINGLELINE_INFO);
    }

    public void addAction(View v){
        mSnackBar.showSingleLineAction("Seems like you have forgotten your customer ID. Would you like some help?",
                "Yes",
                "No",
                SnackBar.MessageType.MESSAGE,
                SnackBar.SnackBarType.SINGLELINE_ACTION);
    }

    public void addAll(View v){
        mSnackBar.showSingleLineInfo("Call Customer Support at:", "18001234567", SnackBar.MessageType.PHONE, SnackBar.SnackBarType.SINGLELINE_INFO);
        mSnackBar.showSingleLineInfo("Email Customer Support at:", "support@hdfcbank.com", SnackBar.MessageType.EMAIL, SnackBar.SnackBarType.SINGLELINE_INFO);
        mSnackBar.showSingleLineInfo("Nearest Bank Branch at:", "goo.gl/sample", SnackBar.MessageType.MAP, SnackBar.SnackBarType.SINGLELINE_INFO);
        mSnackBar.showSingleLineInfo("More info at:", "support.hdfcbank.com", SnackBar.MessageType.WEB, SnackBar.SnackBarType.SINGLELINE_INFO);
    }

    @Override
    public void onBackPressed() {
        mSnackBar.onBackPressedHandler();
    }

    @Override
    public void positiveButtonClicked() {

        mSnackBar.showSingleLineOption("Check Customer Id at", "Cheque Book", SnackBar.MessageType.CHEQUE, SnackBar.SnackBarType.SINGLELINE_OPTION);
        mSnackBar.showSingleLineOption("Check Customer Id at", "Account Statement", SnackBar.MessageType.ACCOUNT_STATEMENT, SnackBar.SnackBarType.SINGLELINE_OPTION);

    }

    @Override
    public void negativeButtonClicked() {
        mSnackBar.removeSnacks();
    }

    @Override
    public void radioButtonClicked(SnackBar.MessageType messageType) {
        Toast.makeText(this, messageType.toString(), Toast.LENGTH_SHORT).show();

        mSnackBar.showSingleLineInfo("Call Customer Support at:", "18001234567", SnackBar.MessageType.PHONE, SnackBar.SnackBarType.SINGLELINE_INFO);
        mSnackBar.showSingleLineInfo("Email Customer Support at:", "support@hdfcbank.com", SnackBar.MessageType.EMAIL, SnackBar.SnackBarType.SINGLELINE_INFO);
        mSnackBar.showSingleLineInfo("More info at:", "support.hdfcbank.com", SnackBar.MessageType.WEB, SnackBar.SnackBarType.SINGLELINE_INFO);
    }
}
