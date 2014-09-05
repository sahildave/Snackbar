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


public class SnackBarActivity extends ActionBarActivity {

    private SnackBar mSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snack_bar);
        mSnackBar = new SnackBar(this);
    }

    public void addCall(View v){
        mSnackBar.show("Call Customer Support at:", "18001234567", SnackBar.MessageType.PHONE, SnackBar.SnackBarType.SINGLELINE);
    }

    public void addEmail(View v){
        mSnackBar.show("Email Customer Support at:", "support@hdfcbank.com", SnackBar.MessageType.EMAIL, SnackBar.SnackBarType.SINGLELINE);
    }

    public void addMap(View v){
        mSnackBar.show("Nearest Bank Branch at:", "goo.gl/sample", SnackBar.MessageType.MAP, SnackBar.SnackBarType.SINGLELINE);
    }

    public void addInfo(View v){
        mSnackBar.show("More info at:", "support.hdfcbank.com", SnackBar.MessageType.WEB, SnackBar.SnackBarType.SINGLELINE);
    }

    public void addAll(View v){
        mSnackBar.show("Call Customer Support at:", "18001234567", SnackBar.MessageType.PHONE, SnackBar.SnackBarType.SINGLELINE);
        mSnackBar.show("Email Customer Support at:", "support@hdfcbank.com", SnackBar.MessageType.EMAIL, SnackBar.SnackBarType.SINGLELINE);
        mSnackBar.show("Nearest Bank Branch at:", "goo.gl/sample", SnackBar.MessageType.MAP, SnackBar.SnackBarType.SINGLELINE);
        mSnackBar.show("More info at:", "support.hdfcbank.com", SnackBar.MessageType.WEB, SnackBar.SnackBarType.SINGLELINE);
    }

    @Override
    public void onBackPressed() {
        mSnackBar.onBackPressedHandler();
    }
}
