package org.researchstack.sampleapp.org.rti.rcd;

import android.content.Context;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.Task;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.DataResponse;
import org.researchstack.skin.model.SchedulesAndTasksModel;
import org.researchstack.skin.model.User;

import rx.Observable;

/**
 * Created by apreston on 4/4/17.
 */

public class ResearchnetDataProvider extends DataProvider {
    public static final String TEMP_CONSENT_JSON_FILE_NAME = "/consent_sig";
    public static final String USER_SESSION_PATH           = "/user_session";
    public static final String USER_PATH                   = "/user";


    @Override
    public Observable<DataResponse> initialize(Context context) {
        return null;
    }

    @Override
    public Observable<DataResponse> signUp(Context context, String email, String username, String password) {
        return null;
    }

    @Override
    public Observable<DataResponse> signIn(Context context, String username, String password) {
        return null;
    }

    @Override
    public Observable<DataResponse> signOut(Context context) {
        return null;
    }

    @Override
    public Observable<DataResponse> resendEmailVerification(Context context, String email) {
        return null;
    }

    @Override
    public boolean isSignedUp(Context context) {
        return false;
    }

    @Override
    public boolean isSignedIn(Context context) {
        return false;
    }

    @Override
    public boolean isConsented(Context context) {
        return false;
    }

    @Override
    public Observable<DataResponse> withdrawConsent(Context context, String reason) {
        return null;
    }

    @Override
    public void uploadConsent(Context context, TaskResult consentResult) {

    }

    @Override
    public void saveConsent(Context context, TaskResult consentResult) {

    }

    @Override
    public User getUser(Context context) {
        return null;
    }

    @Override
    public String getUserSharingScope(Context context) {
        return null;
    }

    @Override
    public void setUserSharingScope(Context context, String scope) {

    }

    @Override
    public String getUserEmail(Context context) {
        return null;
    }

    @Override
    public void uploadTaskResult(Context context, TaskResult taskResult) {

    }

    @Override
    public SchedulesAndTasksModel loadTasksAndSchedules(Context context) {
        return null;
    }

    @Override
    public Task loadTask(Context context, SchedulesAndTasksModel.TaskScheduleModel task) {
        return null;
    }

    @Override
    public void processInitialTaskResult(Context context, TaskResult taskResult) {

    }

    @Override
    public Observable<DataResponse> forgotPassword(Context context, String email) {
        return null;
    }
}
