package com.seunghyun.randomseats;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.RuntimeExecutionException;
import com.google.android.play.core.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class DataViewModel extends ViewModel {
    private MutableLiveData<Integer> currentPage;
    private MutableLiveData<Integer> currentStage;
    private MutableLiveData<Boolean> isChangingStage;
    private MutableLiveData<ArrayList<Integer>> randomNumbers;
    private MutableLiveData<ArrayList<String>> notUseSeatTags;
    private MutableLiveData<ArrayList<String>> shownSeatsList;
    private MutableLiveData<HashMap<String, String>> fixedSeatsMap;

    public DataViewModel() {
    }

    static void updateIfRequire(final Activity context, final boolean isNullTextShow) {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);
        Task<AppUpdateInfo> appUpdateInfo = appUpdateManager.getAppUpdateInfo();
        appUpdateInfo.addOnCompleteListener(task -> {
            if (task.isComplete()) {
                AppUpdateInfo updateInfo;
                try {
                    updateInfo = task.getResult();
                } catch (RuntimeExecutionException e) { //구글 플레이 연결이 되지 않으면
                    if(isNullTextShow) Toast.makeText(context, R.string.connect_failed, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    return;
                }
                if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    final Snackbar snackbar = Snackbar.make(context.findViewById(R.id.activity_main_container), R.string.update_available, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.show, v -> {
                        try {
                            appUpdateManager.startUpdateFlowForResult(updateInfo, AppUpdateType.IMMEDIATE, context, 1);
                        } catch (IntentSender.SendIntentException e) {
                            if (isNullTextShow)
                                Toast.makeText(context, R.string.connect_failed, Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    });
                    snackbar.show();
                } else if (isNullTextShow) {
                    Toast.makeText(context, R.string.update_not_available, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    static void makeReview(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Log.d("testing", context.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.store_not_found), Toast.LENGTH_LONG).show();
        }
    }

    MutableLiveData<Integer> getCurrentPage() {
        if (currentPage == null) currentPage = new MutableLiveData<>();
        return currentPage;
    }

    MutableLiveData<Integer> getCurrentStage() {
        if (currentStage == null) {
            currentStage = new MutableLiveData<>();
        }
        return currentStage;
    }

    MutableLiveData<Boolean> getIsChangingStage() {
        if (isChangingStage == null) isChangingStage = new MutableLiveData<>();
        return isChangingStage;
    }

    MutableLiveData<ArrayList<Integer>> getRandomNumbers() {
        if (randomNumbers == null) randomNumbers = new MutableLiveData<>();
        return randomNumbers;
    }

    MutableLiveData<ArrayList<String>> getNotUseSeatTags() {
        if (notUseSeatTags == null) notUseSeatTags = new MutableLiveData<>();
        return notUseSeatTags;
    }

    MutableLiveData<ArrayList<String>> getShownSeatsList() {
        if (shownSeatsList == null) shownSeatsList = new MutableLiveData<>();
        return shownSeatsList;
    }

    MutableLiveData<HashMap<String, String>> getFixedSeatsMap() {
        if (fixedSeatsMap == null) fixedSeatsMap = new MutableLiveData<>();
        return fixedSeatsMap;
    }
}
