/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.android.car.ui.utils;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import static com.google.common.truth.Truth.assertThat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.rule.ActivityTestRule;

import com.android.car.ui.FocusArea;
import com.android.car.ui.FocusParkingView;
import com.android.car.ui.recyclerview.CarUiRecyclerView;
import com.android.car.ui.test.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/** Unit tests for {@link ViewUtils}. */
public class ViewUtilsTest {

    @Rule
    public ActivityTestRule<ViewUtilsTestActivity> mActivityRule =
            new ActivityTestRule<>(ViewUtilsTestActivity.class);

    private ViewUtilsTestActivity mActivity;
    private FocusArea mFocusArea1;
    private FocusArea mFocusArea2;
    private FocusArea mFocusArea3;
    private FocusArea mFocusArea4;
    private FocusArea mFocusArea5;
    private FocusParkingView mFpv;
    private View mView2;
    private View mFocusedByDefault3;
    private View mView4;
    private View mDefaultFocus4;
    private CarUiRecyclerView mList5;
    private View mRoot;

    @Before
    public void setUp() {
        mActivity = mActivityRule.getActivity();
        mFocusArea1 = mActivity.findViewById(R.id.focus_area1);
        mFocusArea2 = mActivity.findViewById(R.id.focus_area2);
        mFocusArea3 = mActivity.findViewById(R.id.focus_area3);
        mFocusArea4 = mActivity.findViewById(R.id.focus_area4);
        mFocusArea5 = mActivity.findViewById(R.id.focus_area5);
        mFpv = mActivity.findViewById(R.id.fpv);
        mView2 = mActivity.findViewById(R.id.view2);
        mFocusedByDefault3 = mActivity.findViewById(R.id.focused_by_default3);
        mView4 = mActivity.findViewById(R.id.view4);
        mDefaultFocus4 = mActivity.findViewById(R.id.default_focus4);
        mList5 = mActivity.findViewById(R.id.list5);
        mRoot = mFocusArea1.getRootView();

        mRoot.post(() -> {
            setUpRecyclerView(mList5);
        });
    }

    @Test
    public void testRootVisible() {
        mRoot.post(() -> assertThat(mRoot.getVisibility()).isEqualTo(VISIBLE));
    }

    @Test
    public void testFindFocusedByDefaultView() {
        mRoot.post(() -> {
            View focusedByDefault = ViewUtils.findFocusedByDefaultView(mRoot);
            assertThat(focusedByDefault).isEqualTo(mFocusedByDefault3);
        });
    }

    @Test
    public void testFindFocusedByDefaultView_skipNotFocusable() {
        mRoot.post(() -> {
            mFocusedByDefault3.setFocusable(false);
            View focusedByDefault = ViewUtils.findFocusedByDefaultView(mRoot);
            assertThat(focusedByDefault).isNull();
        });
    }

    @Test
    public void testFindFocusedByDefaultView_skipInvisibleView() {
        mRoot.post(() -> {
            mFocusArea3.setVisibility(INVISIBLE);
            assertThat(mFocusArea3.getVisibility()).isEqualTo(INVISIBLE);
            View focusedByDefault = ViewUtils.findFocusedByDefaultView(mRoot);
            assertThat(focusedByDefault).isNull();
        });
    }

    @Test
    public void testFindFocusedByDefaultView_skipInvisibleAncestor() {
        mRoot.post(() -> {
            mRoot.setVisibility(INVISIBLE);
            View focusedByDefault = ViewUtils.findFocusedByDefaultView(mFocusArea3);
            assertThat(focusedByDefault).isNull();
        });
    }

    @Test
    public void testFindImplicitDefaultFocusView_inRoot() {
        mRoot.post(() -> {
            View firstItem = mList5.getLayoutManager().findViewByPosition(0);
            View implicitDefaultFocus = ViewUtils.findImplicitDefaultFocusView(mRoot);
            assertThat(implicitDefaultFocus).isEqualTo(firstItem);
        });
    }

    @Test
    public void testFindImplicitDefaultFocusView_inFocusArea() {
        mRoot.post(() -> {
            View firstItem = mList5.getLayoutManager().findViewByPosition(0);
            View implicitDefaultFocus = ViewUtils.findImplicitDefaultFocusView(mFocusArea5);
            assertThat(implicitDefaultFocus).isEqualTo(firstItem);
        });
    }

    @Test
    public void testFindImplicitDefaultFocusView_skipInvisibleAncestor() {
        mRoot.post(() -> {
            mRoot.setVisibility(INVISIBLE);
            View implicitDefaultFocus = ViewUtils.findImplicitDefaultFocusView(mFocusArea5);
            assertThat(implicitDefaultFocus).isNull();
        });
    }

    @Test
    public void testFindFirstFocusableDescendant() {
        mRoot.post(() -> {
            mFocusArea2.setFocusable(true);
            View firstFocusable = ViewUtils.findFirstFocusableDescendant(mRoot);
            assertThat(firstFocusable).isEqualTo(mFocusArea2);
        });
    }

    @Test
    public void testFindFirstFocusableDescendant_skipItself() {
        mRoot.post(() -> {
            mFocusArea2.setFocusable(true);
            View firstFocusable = ViewUtils.findFirstFocusableDescendant(mFocusArea2);
            assertThat(firstFocusable).isEqualTo(mView2);
        });
    }

    @Test
    public void testFindFirstFocusableDescendant_skipInvisibleAndGoneView() {
        mRoot.post(() -> {
            mFocusArea2.setVisibility(INVISIBLE);
            mFocusArea3.setVisibility(GONE);
            View firstFocusable = ViewUtils.findFirstFocusableDescendant(mRoot);
            assertThat(firstFocusable).isEqualTo(mView4);
        });
    }

    @Test
    public void testFindFirstFocusableDescendant_skipInvisibleAncestor() {
        mRoot.post(() -> {
            mRoot.setVisibility(INVISIBLE);
            View firstFocusable = ViewUtils.findFirstFocusableDescendant(mFocusArea2);
            assertThat(firstFocusable).isNull();
        });
    }

    @Test
    public void testIsImplicitDefaultFocusView_firstItem() {
        mRoot.post(() -> {
            View firstItem = mList5.getLayoutManager().findViewByPosition(0);
            assertThat(ViewUtils.isImplicitDefaultFocusView(firstItem)).isTrue();
        });
    }

    @Test
    public void testIsImplicitDefaultFocusView_secondItem() {
        mRoot.post(() -> {
            View secondItem = mList5.getLayoutManager().findViewByPosition(1);
            assertThat(ViewUtils.isImplicitDefaultFocusView(secondItem)).isFalse();
        });
    }

    @Test
    public void testIsImplicitDefaultFocusView_normalView() {
        mRoot.post(() -> {
            assertThat(ViewUtils.isImplicitDefaultFocusView(mView2)).isFalse();
        });
    }

    @Test
    public void testIsImplicitDefaultFocusView_skipInvisibleAncestor() {
        mRoot.post(() -> {
            mFocusArea5.setVisibility(INVISIBLE);
            View firstItem = mList5.getLayoutManager().findViewByPosition(0);
            assertThat(ViewUtils.isImplicitDefaultFocusView(firstItem)).isFalse();
        });
    }

    @Test
    public void testRequestFocus() {
        mRoot.post(() -> {
            assertRequestFocus(mView2, true);
        });
    }

    @Test
    public void testRequestFocus_nullView() {
        mRoot.post(() -> {
            assertRequestFocus(null, false);
        });
    }

    @Test
    public void testRequestFocus_alreadyFocused() {
        mRoot.post(() -> {
            assertRequestFocus(mView2, true);
            // mView2 is already focused before requesting focus.
            assertRequestFocus(mView2, true);
        });
    }

    @Test
    public void testRequestFocus_notFocusable() {
        mRoot.post(() -> {
            mView2.setFocusable(false);
            assertRequestFocus(mView2, false);
        });
    }

    @Test
    public void testRequestFocus_disabled() {
        mRoot.post(() -> {
            mView2.setEnabled(false);
            assertRequestFocus(mView2, false);
        });
    }

    @Test
    public void testRequestFocus_notVisible() {
        mRoot.post(() -> {
            mView2.setVisibility(View.INVISIBLE);
            assertRequestFocus(mView2, false);
        });
    }

    @Test
    public void testRequestFocus_skipInvisibleAncestor() {
        mRoot.post(() -> {
            mFocusArea2.setVisibility(View.INVISIBLE);
            assertRequestFocus(mView2, false);
        });
    }

    @Test
    public void testRequestFocus_zeroWidth() {
        mRoot.post(() -> {
            mView2.setRight(mView2.getLeft());
            assertThat(mView2.getWidth()).isEqualTo(0);
            assertRequestFocus(mView2, false);
        });
    }

    @Test
    public void testRequestFocus_detachedFromWindow() {
        mRoot.post(() -> {
            mFocusArea2.removeView(mView2);
            assertRequestFocus(mView2, false);
        });
    }

    @Test
    public void testRequestFocus_FocusParkingView() {
        mRoot.post(() -> {
            assertRequestFocus(mView2, true);
            assertRequestFocus(mFpv, false);
        });
    }

    @Test
    public void testRequestFocus_scrollableContainer() {
        mRoot.post(() -> {
            assertRequestFocus(mList5, false);
        });
    }

    @Test
    public void testAdjustFocus_inRoot() {
        mRoot.post(() -> {
            assertRequestFocus(mView2, true);
            ViewUtils.adjustFocus(mRoot, null);
            assertThat(mFocusedByDefault3.isFocused()).isTrue();
        });
    }

    @Test
    public void testAdjustFocus_inFocusAreaWithDefaultFocus() {
        mRoot.post(() -> {
            assertRequestFocus(mView2, true);
            ViewUtils.adjustFocus(mFocusArea3, null);
            assertThat(mFocusedByDefault3.isFocused()).isTrue();
        });
    }

    @Test
    public void testAdjustFocus_inFocusAreaWithoutDefaultFocus() {
        mRoot.post(() -> {
            assertRequestFocus(mView4, true);
            ViewUtils.adjustFocus(mFocusArea2, null);
            assertThat(mView2.isFocused()).isTrue();
        });
    }

    @Test
    public void testAdjustFocus_inFocusAreaWithoutFocusableDescendant() {
        mRoot.post(() -> {
            assertRequestFocus(mView2, true);
            boolean success = ViewUtils.adjustFocus(mFocusArea1, null);
            assertThat(mFocusArea1.hasFocus()).isFalse();
            assertThat(success).isFalse();
        });
    }

    private static void assertRequestFocus(@Nullable View view, boolean focused) {
        boolean result = ViewUtils.requestFocus(view);
        assertThat(result).isEqualTo(focused);
        if (view != null) {
            assertThat(view.isFocused()).isEqualTo(focused);
        }
    }

    private void setUpRecyclerView(@NonNull CarUiRecyclerView list) {
        list.setLayoutManager(new LinearLayoutManager(mActivity));
        list.setAdapter(new TestAdapter());
    }

    private static class TestAdapter extends RecyclerView.Adapter<TestViewHolder> {

        private static final List<String> DATA = Arrays.asList("first", "second");

        @NonNull
        @Override
        public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.test_list_item, parent, false);
            return new TestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
            holder.bind(DATA.get(position));
        }

        @Override
        public int getItemCount() {
            return DATA.size();
        }
    }

    private static class TestViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTextView;

        TestViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.text);
        }

        void bind(String text) {
            mTextView.setText(text);
        }
    }
}