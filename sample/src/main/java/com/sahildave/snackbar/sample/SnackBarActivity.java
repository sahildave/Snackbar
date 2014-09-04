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

    public static final String SAVED_SNACKBAR = "SAVED_SNACKBAR";
    public static final String SAVED_COUNT = "SAVED_COUNT";


    private int mSnackIndex = 0;
    private SnackBar mSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snack_bar);


    }

    public void onSnackClicked(View v){
        mSnackBar = new SnackBar(this, SnackBar.SnackBarType.SINGLELINE);
        mSnackBar.show("Call Customer Support at:", "18001234567", SnackBar.MessageType.PHONE, SnackBar.SnackBarType.SINGLELINE);
    }

    public void addSnack(View v){
        mSnackBar = new SnackBar(this, SnackBar.SnackBarType.SINGLELINE);
        mSnackBar.show("Email Customer Support at:", "support@hdfcbank.com", SnackBar.MessageType.EMAIL, SnackBar.SnackBarType.SINGLELINE);
    }

    @Override
    public void onBackPressed() {
        mSnackBar.onBackPressedHandler();
    }

    @Override
    protected void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
        // use this to save your snacks for later
        saveState.putBundle(SAVED_SNACKBAR, mSnackBar.onSaveInstanceState());
        // just for saving the number of times the button has been pressed
        saveState.putInt(SAVED_COUNT, mSnackIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle loadState) {
        super.onRestoreInstanceState(loadState);
        // use this to load your snacks for later
        mSnackBar.onRestoreInstanceState(loadState.getBundle(SAVED_SNACKBAR));
        // might as well load the counter too
        mSnackIndex = loadState.getInt(SAVED_COUNT);
    }
}
