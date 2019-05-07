/*
 * Copyright 2019 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.odk.collect.android.formfilling;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.odk.collect.android.R;
import org.odk.collect.android.activities.FormEntryActivity;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.test.FormLoadingUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.odk.collect.android.test.CustomMatchers.withIndex;

public class FieldListUpdateTest {
    private static final String FIELD_LIST_TEST_FORM = "fieldlist-updates.xml";

    @Rule
    public IntentsTestRule<FormEntryActivity> activityTestRule = FormLoadingUtils.getFormActivityTestRuleFor(FIELD_LIST_TEST_FORM);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @BeforeClass
    public static void copyFormToSdCard() throws IOException {
        FormLoadingUtils.copyFormToSdCard(FIELD_LIST_TEST_FORM);
    }

    @Test
    public void relevanceChangeAtEnd_ShouldToggleLastWidgetVisibility() {
        onView(withId(R.id.menu_goto)).perform(click());
        onView(withId(R.id.menu_go_up)).perform(click());
        // TODO: what is with those hidden views with the same text?
        onView(allOf(withText("Single relevance at end"), isDisplayed())).perform(click());
        onView(withText("Source1")).perform(click());

        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText(""));
        onView(withText("Target1")).check(doesNotExist());
        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText("A"));
        onView(withText("Target1")).check(matches(isDisplayed()));
        onView(withText("Target1")).check(isCompletelyBelow(withText("Source1")));

        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText(""));
        onView(withText("Target1")).check(doesNotExist());
    }

    @Test
    public void relevanceChangeAtBeginning_ShouldToggleFirstWidgetVisibility() {
        onView(withId(R.id.menu_goto)).perform(click());
        onView(withId(R.id.menu_go_up)).perform(click());
        onView(allOf(withText("Single relevance at beginning"), isDisplayed())).perform(click());
        onView(withText("Source2")).perform(click());

        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText(""));
        onView(withText("Target2")).check(doesNotExist());
        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText("A"));
        onView(withText("Target2")).check(matches(isDisplayed()));
        onView(withText("Target2")).check(isCompletelyAbove(withText("Source2")));

        onView(withIndex(withClassName(endsWith("EditText")), 1)).perform(replaceText(""));
        onView(withText("Target2")).check(doesNotExist());
    }

    @Test
    public void relevanceChangeInMiddle_ShouldToggleMiddleWidgetVisibility() {
        onView(withId(R.id.menu_goto)).perform(click());
        onView(withId(R.id.menu_go_up)).perform(click());
        onView(allOf(withText("Single relevance in middle"), isDisplayed())).perform(click());
        onView(withText("Source3")).perform(click());

        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText(""));
        onView(withText("Target3")).check(doesNotExist());
        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText("A"));
        onView(withText("Target3")).check(matches(isDisplayed()));
        onView(withText("Target3")).check(isCompletelyBelow(withText("Source3")));
        onView(withText("Target3")).check(isCompletelyAbove(withText("Filler3")));

        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText(""));
        onView(withText("Target3")).check(doesNotExist());
    }

    @Test
    public void longPress_ShouldClearAndUpdate() {
        onView(withId(R.id.menu_goto)).perform(click());
        onView(withId(R.id.menu_go_up)).perform(click());
        onView(allOf(withText("Single relevance in middle"), isDisplayed())).perform(click());
        onView(withText("Source3")).perform(click());

        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText(""));
        onView(withText("Target3")).check(doesNotExist());
        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText("A"));
        onView(withText("Target3")).check(matches(isDisplayed()));

        onView(withText("Source3")).perform(longClick());
        onView(withText(R.string.clear_answer)).perform(click());
        onView(withText(R.string.discard_answer)).perform(click());
        onView(withIndex(withClassName(endsWith("EditText")), 0)).check(matches(withText("")));
        onView(withText("Target3")).check(doesNotExist());
    }

    @Test
    public void changeInValueUsedInLabel_ShouldChangeLabelText() {
        onView(withId(R.id.menu_goto)).perform(click());
        onView(withId(R.id.menu_go_up)).perform(click());
        onView(allOf(withText("Label change"), isDisplayed())).perform(click());
        onView(withText(startsWith("Hello"))).perform(click());

        String name = UUID.randomUUID().toString();

        onView(withIndex(withClassName(endsWith("EditText")), 1)).perform(replaceText(""));
        onView(withText("Hello, , how are you today?")).check(matches(isDisplayed()));
        onView(withIndex(withClassName(endsWith("EditText")), 1)).perform(replaceText(name));
        onView(withText("Hello, " + name + ", how are you today?")).check(matches(isDisplayed()));
        onView(withIndex(withClassName(endsWith("EditText")), 1)).perform(replaceText(""));
        onView(withText("Hello, , how are you today?")).check(matches(isDisplayed()));
    }

    @Test
    public void changeInValueUsedInHint_ShouldChangeHintText() {
        onView(withId(R.id.menu_goto)).perform(click());
        onView(withId(R.id.menu_go_up)).perform(click());
        onView(allOf(withText("Hint change"), isDisplayed())).perform(click());
        onView(withText(startsWith("What is your"))).perform(click());

        String name = UUID.randomUUID().toString();

        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText(""));
        onView(withText("Please don't use your calculator, !")).check(matches(isDisplayed()));
        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText(name));
        onView(withText("Please don't use your calculator, " + name + "!")).check(matches(isDisplayed()));
        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText(""));
        onView(withText("Please don't use your calculator, !")).check(matches(isDisplayed()));
    }

    /** TODO: calculation doesn't seem to be updated whether or not there's a fieldlist.
    @Test
    public void changeInValueUsedInOtherField_ShouldChangeValue() {
        onView(withId(R.id.menu_goto)).perform(click());
        onView(withId(R.id.menu_go_up)).perform(click());
        onView(allOf(withText("Value change"), isDisplayed())).perform(click());
        onView(withText(startsWith("What is your"))).perform(click());

        String name = UUID.randomUUID().toString();

        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText(""));
        onView(withIndex(withClassName(endsWith("EditText")), 1)).check(matches(withText("0")));
        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText(name));
        onView(withIndex(withClassName(endsWith("EditText")), 1)).check(matches(withText(name.length())));
        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText(""));
        onView(withIndex(withClassName(endsWith("EditText")), 1)).check(matches(withText("0")));
    }
    **/

    @Test
    public void selectionChangeAtFirstCascadeLevel_ShouldUpdateNextLevels() {
        onView(withId(R.id.menu_goto)).perform(click());
        onView(withId(R.id.menu_go_up)).perform(click());
        onView(allOf(withText("Cascading select"), isDisplayed())).perform(click());
        onView(withText(startsWith("Level1"))).perform(click());

        // No choices should be shown for levels 2 and 3 when no selection is made for level 1
        onView(withText("A1")).check(doesNotExist());
        onView(withText("B1")).check(doesNotExist());
        onView(withText("C1")).check(doesNotExist());
        onView(withText("A1A")).check(doesNotExist());

        // Selecting C for level 1 should only reveal options for C at level 2
        onView(withText("C")).perform(click());
        onView(withText("A1")).check(doesNotExist());
        onView(withText("B1")).check(doesNotExist());
        onView(withText("C1")).check(matches(isDisplayed()));
        onView(withText("A1A")).check(doesNotExist());

        // Selecting A for level 1 should reveal options for A at level 2
        onView(withText("A")).perform(click());
        onView(withText("A1")).check(matches(isDisplayed()));
        onView(withText("A1A")).check(doesNotExist());
        onView(withText("B1")).check(doesNotExist());
        onView(withText("C1")).check(doesNotExist());

        // Selecting A1 for level 2 should reveal options for A1 at level 3
        onView(withText("A1")).perform(click());
        onView(withText("A1A")).check(matches(isDisplayed()));
        onView(withText("B1")).check(doesNotExist());
        onView(withText("C1")).check(doesNotExist());
    }

    // TODO: figure out why the third level isn't cleared. Wondering whether there might be an issue
    // with populateDynamicChoices
    //    @Test
    //    public void clearingParentSelect_ShouldUpdateAllDependentLevels() {
    //        onView(withId(R.id.menu_goto)).perform(click());
    //        onView(withId(R.id.menu_go_up)).perform(click());
    //        onView(allOf(withText("Cascading select"), isDisplayed())).perform(click());
    //        onView(withText(startsWith("Level1"))).perform(click());
    //
    //        onView(withText("A")).perform(click());
    //        onView(withText("A1")).perform(click());
    //        onView(withText("A1B")).perform(click());
    //
    //        onView(withText("A")).perform(longClick());
    //        onView(withText(R.string.clear_answer)).perform(click());
    //        onView(withText(R.string.discard_answer)).perform(click());
    //
    //        onView(withIndex(withClassName(endsWith("RadioButton")), 0)).check(matches(isNotChecked()));
    //        onView(withText("A1")).check(doesNotExist());
    //        onView(withText("A1B")).check(doesNotExist());
    //    }

    @Test
    public void selectionChangeAtOneCascadeLevelWithMinimalAppearance_ShouldUpdateNextLevels() {
        onView(withId(R.id.menu_goto)).perform(click());
        onView(withId(R.id.menu_go_up)).perform(click());
        onView(allOf(withText("Cascading select minimal"), isDisplayed())).perform(click());
        onView(withText(startsWith("Level1"))).perform(click());

        // No choices should be shown for levels 2 and 3 when no selection is made for level 1
        onView(withText("A1")).check(doesNotExist());
        onView(withText("B1")).check(doesNotExist());
        onView(withText("C1")).check(doesNotExist());
        onView(withText("A1A")).check(doesNotExist());

        // Selecting C for level 1 should only reveal options for C at level 2
        onView(withIndex(withText(R.string.select_one), 0)).perform(click());
        onView(withText("C")).perform(click());
        onView(withText("A1")).check(doesNotExist());
        onView(withText("B1")).check(doesNotExist());
        onView(withIndex(withText(R.string.select_one), 0)).perform(click());
        onView(withText("C1")).perform(click());
        onView(withText("A1A")).check(doesNotExist());

        // Selecting A for level 1 should reveal options for A at level 2
        onView(withText("C")).perform(click());
        onView(withText("A")).perform(click());
        onView(withIndex(withText(R.string.select_one), 0)).perform(click());
        onView(withText("A1")).check(matches(isDisplayed()));
        onView(withText("A1A")).check(doesNotExist());
        onView(withText("B1")).check(doesNotExist());
        onView(withText("C1")).check(doesNotExist());

        // Selecting A1 for level 2 should reveal options for A1 at level 3
        onView(withText("A1")).perform(click());
        onView(withIndex(withText(R.string.select_one), 0)).perform(click());
        onView(withText("A1A")).check(matches(isDisplayed()));
        onView(withText("B1A")).check(doesNotExist());
        onView(withText("B1")).check(doesNotExist());
        onView(withText("C1")).check(doesNotExist());
    }

    @Test
    public void questionsAppearingBeforeCurrentTextQuestion_ShouldNotChangeFocus() {
        onView(withId(R.id.menu_goto)).perform(click());
        onView(withId(R.id.menu_go_up)).perform(click());
        onView(allOf(withText("Push off screen"), isDisplayed())).perform(click());
        onView(withText(startsWith("Source9"))).perform(click());

        onView(withText("Target9-15")).check(doesNotExist());
        onView(withIndex(withClassName(endsWith("EditText")), 0)).perform(replaceText("A"));

        onView(withText("Target9-15")).check(matches(isDisplayed()));

        onView(withIndex(withClassName(endsWith("EditText")), 15)).check(matches(isCompletelyDisplayed()));
        onView(withIndex(withClassName(endsWith("EditText")), 15)).check(matches(hasFocus()));
    }

    @Test
    public void questionsAppearingBeforeCurrentBinaryQuestion_ShouldNotChangeFocus() throws IOException {
        onView(withId(R.id.menu_goto)).perform(click());
        onView(withId(R.id.menu_go_up)).perform(click());
        onView(allOf(withText("Push off screen binary"), isDisplayed())).perform(click());
        onView(withText(startsWith("Source10"))).perform(click());

        onView(withText("Target10-15")).check(doesNotExist());

        // FormEntryActivity expects an image at a fixed path so copy the app logo there
        Bitmap icon = BitmapFactory.decodeResource(ApplicationProvider.getApplicationContext().getResources(), R.drawable.notes);
        File tmpJpg = new File(Collect.TMPFILE_PATH);
        tmpJpg.createNewFile();
        FileOutputStream fos = new FileOutputStream(tmpJpg);
        icon.compress(Bitmap.CompressFormat.JPEG, 90, fos);

        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

        onView(withId(R.id.capture_image)).perform(click());

        onView(withText("Target10-15")).check(matches(isDisplayed()));
        onView(withId(R.id.capture_image)).check(matches(isCompletelyDisplayed()));
    }
}
