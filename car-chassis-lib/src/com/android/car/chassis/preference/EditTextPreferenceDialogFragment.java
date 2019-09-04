/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.car.chassis.preference;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;

/**
 * Presents a dialog with an {@link EditText} associated with an {@link EditTextPreference}.
 *
 * <p>Note: this is borrowed as-is from androidx.preference.EditTextPreferenceDialogFragmentCompat
 * with updates to formatting to match the project style. Automotive applications should use this
 * implementations in order to launch the system themed platform {@link AlertDialog} instead of the
 * one in the support library.
 */
public class EditTextPreferenceDialogFragment extends PreferenceDialogFragment {

    private static final String SAVE_STATE_TEXT = "EditTextPreferenceDialogFragment.text";

    private EditText mEditText;

    private CharSequence mText;

    /**
     * Returns a new instance of {@link EditTextPreferenceDialogFragment} for the {@link
     * EditTextPreference} with the given {@code key}.
     */
    public static EditTextPreferenceDialogFragment newInstance(String key) {
        EditTextPreferenceDialogFragment fragment =
                new EditTextPreferenceDialogFragment();
        Bundle b = new Bundle(/* capacity= */ 1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mText = getEditTextPreference().getText();
        } else {
            mText = savedInstanceState.getCharSequence(SAVE_STATE_TEXT);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(SAVE_STATE_TEXT, mText);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mEditText = view.findViewById(android.R.id.edit);

        if (mEditText == null) {
            throw new IllegalStateException(
                    "Dialog view must contain an EditText with id @android:id/edit");
        }

        mEditText.requestFocus();
        mEditText.setText(mText);
        // Place cursor at the end
        mEditText.setSelection(mEditText.getText().length());
    }

    private EditTextPreference getEditTextPreference() {
        return (EditTextPreference) getPreference();
    }

    @Override
    protected boolean needInputMethod() {
        return true;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            String value = mEditText.getText().toString();
            if (getEditTextPreference().callChangeListener(value)) {
                getEditTextPreference().setText(value);
            }
        }
    }

}
