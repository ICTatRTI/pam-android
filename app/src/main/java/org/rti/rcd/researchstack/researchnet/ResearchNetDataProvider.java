package org.rti.rcd.researchstack.researchnet;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.storage.NotificationHelper;
import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.backbone.storage.database.TaskNotification;
import org.researchstack.backbone.storage.file.StorageAccessException;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.skin.AppPrefs;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.DataResponse;
import org.researchstack.skin.ResourceManager;
import org.researchstack.skin.model.SchedulesAndTasksModel;
import org.researchstack.skin.model.TaskModel;
import org.researchstack.skin.model.User;
import org.researchstack.skin.notification.TaskAlertReceiver;
import org.researchstack.skin.schedule.ScheduleHelper;
import org.researchstack.skin.task.ConsentTask;
import org.researchstack.skin.task.SmartSurveyTask;
import org.rti.rcd.researchstack.BuildConfig;
import org.rti.rcd.researchstack.bridge.BridgeDataInput;
import org.rti.rcd.researchstack.bridge.BridgeMessageResponse;
import org.rti.rcd.researchstack.bridge.Info;
import org.rti.rcd.researchstack.bridge.body.ConsentSignatureBody;
import org.rti.rcd.researchstack.bridge.body.SurveyAnswer;
import org.rti.rcd.researchstack.researchnet.body.SignInBody;
import org.rti.rcd.researchstack.researchnet.body.SignUpBody;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/*
* This is a very simple implementation that hits only part of the ResearchNet REST API
*/
public abstract class ResearchNetDataProvider extends DataProvider
{
    public static final String TEMP_CONSENT_JSON_FILE_NAME = "/consent_sig";
    public static final String USER_SESSION_PATH           = "/user_session";
    public static final String USER_PATH                   = "/user";

    private ResearchNetDataProvider.ResearchnetService service;
    protected UserSessionInfo userSessionInfo;
    protected Gson gson     = new Gson();
    protected boolean signedIn = false;

    // these are used to get task/step guids without rereading the json files and iterating through
    private Map<String, String> loadedTaskGuids = new HashMap<>();
    private Map<String, String> loadedTaskDates = new HashMap<>();
    private Map<String, String> loadedTaskCrons = new HashMap<>();

    protected abstract ResourcePathManager.Resource getTasksAndSchedules();

    protected abstract String getBaseUrl();

    protected abstract String getStudyId();

    protected final String getUserAgent() {
        return getAppVersion() + " (" + getDeviceName() + "; Android " + Build.VERSION.RELEASE + ") ResearchNetSDK/0";
    }

    protected abstract String getStudyName();

    protected abstract int getAppVersion();

    protected abstract String getReearchnetAppKey();

    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        if (TextUtils.isEmpty(manufacturer)){
            manufacturer = "Unknown";
        }

        String model = Build.MODEL;
        if(TextUtils.isEmpty(model)){
            model = "Android";
        }

        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public ResearchNetDataProvider()
    {
        buildRetrofitService(null);
    }

    private void buildRetrofitService(UserSessionInfo userSessionInfo)
    {
        final String sessionToken;
        if(userSessionInfo != null)
        {
            sessionToken = userSessionInfo.getSessionToken();
        }
        else
        {
            sessionToken = "";
        }

        Interceptor headerInterceptor = chain -> {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("User-Agent", getUserAgent())
                    .header("Authorization", "Token "+getReearchnetAppKey())
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        };

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().addInterceptor(headerInterceptor);

        if (BuildConfig.DEBUG)
        {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> LogExt.i(
                    getClass(),
                    message));
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(interceptor);
        }

        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getBaseUrl())
                .client(client)
                .build();
        service = retrofit.create(ResearchNetDataProvider.ResearchnetService.class);
    }

    @Override
    public Observable<DataResponse> initialize(Context context)
    {
        return Observable.defer(() -> {
            userSessionInfo = loadUserSession(context);
            signedIn = userSessionInfo != null;

            buildRetrofitService(userSessionInfo);
            return Observable.just(new DataResponse(true, null));

        }).doOnNext(response -> {
            // will crash if the user hasn't created a pincode yet, need to fix needsAuth()
            if(StorageAccess.getInstance().hasPinCode(context))
            {
                LogExt.e(getClass(), "do on next");
                // do nothing
            }
        });
    }


    /**
     * @param context
     * @return true if we are consented
     */
    @Override
    public boolean isConsented(Context context)
    {
        return userSessionInfo.isConsented() || StorageAccess.getInstance()
                .getFileAccess()
                .dataExists(context, TEMP_CONSENT_JSON_FILE_NAME);
    }

    @Override
    public Observable<DataResponse> withdrawConsent(Context context, String reason)
    {
       //TODO implement later
        return null;
    }

    @Override
    public Observable<DataResponse> signUp(Context context, String email, String username, String password)
    {
        // we should pass in data groups, remove roles
        SignUpBody body = new SignUpBody(getStudyId(), email, username, password, null, null);

        // saving email to user object should exist elsewhere.
        // Save email to user object.
        ResearchNetUser user = loadUser(context);
        if(user == null)
        {
            user = new ResearchNetUser();
        }

        body.setFirstNameFromName(user.getName());
        body.setLastNameFromName(user.getName());
        body.setGender("male"); //TODO need to update layout to include this variable
        body.setUsername(email);
        body.setDob(user.getBirthDate());


        user.setEmail(email);
        saveUser(context, user);

        return service.signUp(body).map(message -> {
            DataResponse response = new DataResponse();
            response.setSuccess(true);
            return response;
        });
    }


    @Override
    public Observable<DataResponse> signIn(Context context, String username, String password)
    {
        SignInBody body = new SignInBody(username, password);

        // response 412 still has a response body, so catch all http errors here
        return service.signIn(body).doOnNext(response -> {

            if(response.code() == 200)
            {
                userSessionInfo = response.body();
            }
            else if(response.code() == 412)
            {
                try
                {
                    String errorBody = response.errorBody().string();
                    userSessionInfo = gson.fromJson(errorBody, UserSessionInfo.class);
                }
                catch(IOException e)
                {
                    throw new RuntimeException("Error deserializing server sign in response");
                }

            }

            if(userSessionInfo != null)
            {
                // if we are direct from signing in, we need to load the user profile object
                // from the server. that wouldn't work right now
                signedIn = true;
                saveUserSession(context, userSessionInfo);
                buildRetrofitService(userSessionInfo);

            }
        }).map(response -> {
            boolean success = response.isSuccess() || response.code() == 412;
            return new DataResponse(success, response.message());
        });
    }

    @Override
    public Observable<DataResponse> signOut(Context context)
    {
        return null;
    }

    @Override
    public Observable<DataResponse> resendEmailVerification(Context context, String email)
    {
        return null;
    }

    @Override
    public boolean isSignedUp(Context context)
    {
        User user = loadUser(context);
        return user != null && user.getEmail() != null;
    }

    @Override
    public boolean isSignedIn(Context context)
    {
        return signedIn;
    }


    @Override
    public void saveConsent(Context context, TaskResult consentResult)
    {
        ConsentSignatureBody signature = createConsentSignatureBody(consentResult);
        writeJsonString(context, gson.toJson(signature), TEMP_CONSENT_JSON_FILE_NAME);

        ResearchNetUser user = loadUser(context);
        if(user == null)
        {
            user = new ResearchNetUser();
        }
        user.setName(signature.name);
        user.setBirthDate(signature.birthdate);
        saveUser(context, user);
    }

    @NonNull
    protected ConsentSignatureBody createConsentSignatureBody(TaskResult consentResult)
    {
        StepResult<StepResult> formResult = (StepResult<StepResult>) consentResult.getStepResult(
                ConsentTask.ID_FORM);

        String sharingScope = (String) consentResult.getStepResult(ConsentTask.ID_SHARING)
                .getResult();

        String fullName = (String) formResult.getResultForIdentifier(ConsentTask.ID_FORM_NAME)
                .getResult();

        Long birthdateInMillis = (Long) formResult.getResultForIdentifier(ConsentTask.ID_FORM_DOB)
                .getResult();

        String base64Image = (String) consentResult.getStepResult(ConsentTask.ID_SIGNATURE)
                .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE);

        String signatureDate = (String) consentResult.getStepResult(ConsentTask.ID_SIGNATURE)
                .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE_DATE);

        // Save Consent Information
        // User is not signed in yet, so we need to save consent info to disk for later upload
        return new ConsentSignatureBody(getStudyId(),
                fullName,
                new Date(birthdateInMillis),
                base64Image,
                "image/png",
                sharingScope);
    }

    @Override
    public User getUser(Context context)
    {
        return loadUser(context);
    }

    @Override
    public String getUserSharingScope(Context context)
    {
        return userSessionInfo.getSharingScope();
    }

    @Override
    public void setUserSharingScope(Context context, String scope)
    {

        // This is stubbed out. //TODO Hook this up to the /consent api call
        userSessionInfo.setSharingScope(scope);
        saveUserSession(context, userSessionInfo);

    }

    private ConsentSignatureBody loadConsentSignatureBody(Context context)
    {
        String consentJson = loadJsonString(context, TEMP_CONSENT_JSON_FILE_NAME);
        return gson.fromJson(consentJson, ConsentSignatureBody.class);
    }

    @Override
    public void uploadConsent(Context context, TaskResult consentResult)
    {
        //TODO Doing nothing just like my old roommate.
    }


    @Override
    public String getUserEmail(Context context)
    {
        User user = loadUser(context);
        return user == null ? null : user.getEmail();
    }

    @Override
    public Observable<DataResponse> forgotPassword(Context context, String email)
    {
        // TODO forgot password isn't implemented yet
        return null;
    }

    private void saveUserSession(Context context, UserSessionInfo userInfo)
    {
        String userSessionJson = gson.toJson(userInfo);
        writeJsonString(context, userSessionJson, USER_SESSION_PATH);
    }

    private ResearchNetUser loadUser(Context context)
    {
        try
        {
            String user = loadJsonString(context, USER_PATH);
            return gson.fromJson(user, ResearchNetUser.class);
        }
        catch(StorageAccessException e)
        {
            return null;
        }
    }

    private void saveUser(Context context, ResearchNetUser profile)
    {
        writeJsonString(context, gson.toJson(profile), USER_PATH);
    }

    private void writeJsonString(Context context, String userSessionJson, String userSessionPath)
    {
        StorageAccess.getInstance()
                .getFileAccess()
                .writeData(context, userSessionPath, userSessionJson.getBytes());
    }

    private UserSessionInfo loadUserSession(Context context)
    {
        try
        {
            String userSessionJson = loadJsonString(context, USER_SESSION_PATH);
            return gson.fromJson(userSessionJson, UserSessionInfo.class);
        }
        catch(StorageAccessException e)
        {
            return null;
        }
    }

    private String loadJsonString(Context context, String path)
    {
        return new String(StorageAccess.getInstance().getFileAccess().readData(context, path));
    }

    @Override
    public SchedulesAndTasksModel loadTasksAndSchedules(Context context)
    {
        SchedulesAndTasksModel schedulesAndTasksModel = getTasksAndSchedules().create(context);

        AppDatabase db = StorageAccess.getInstance().getAppDatabase();

        List<SchedulesAndTasksModel.ScheduleModel> schedules = new ArrayList<>();
        for(SchedulesAndTasksModel.ScheduleModel schedule : schedulesAndTasksModel.schedules)
        {
            if(schedule.tasks.size() == 0)
            {
                LogExt.e(getClass(), "No tasks in schedule");
                continue;
            }

            // only supporting one task per schedule for now
            SchedulesAndTasksModel.TaskScheduleModel task = schedule.tasks.get(0);

            if(task.taskFileName == null)
            {
                LogExt.e(getClass(), "No filename found for task with id: " + task.taskID);
                continue;
            }

            // loading the task json here is bad, but the taskID is in the schedule
            // json but the readable id is in the task json
            TaskModel taskModel = loadTaskModel(context, task);
            TaskResult result = db.loadLatestTaskResult(taskModel.identifier);

            // cache cron string for later lookup
            loadedTaskCrons.put(taskModel.identifier, schedule.scheduleString);

            if(result == null)
            {
                schedules.add(schedule);
            }
            else if(StringUtils.isNotEmpty(schedule.scheduleString))
            {
                Date date = ScheduleHelper.nextSchedule(schedule.scheduleString,
                        result.getEndDate());
                if(date.before(new Date()))
                {
                    schedules.add(schedule);
                }
            }
        }

        schedulesAndTasksModel.schedules = schedules;
        return schedulesAndTasksModel;
    }

    private TaskModel loadTaskModel(Context context, SchedulesAndTasksModel.TaskScheduleModel task)
    {
        TaskModel taskModel = ResourceManager.getInstance()
                .getTask(task.taskFileName)
                .create(context);

        // cache guid and createdOnDate
        loadedTaskGuids.put(taskModel.identifier, taskModel.guid);
        loadedTaskDates.put(taskModel.identifier, taskModel.createdOn);

        return taskModel;
    }

    @Override
    public Task loadTask(Context context, SchedulesAndTasksModel.TaskScheduleModel task)
    {
        // currently we only support task json files, override this method to taskClassName
        if(StringUtils.isEmpty(task.taskFileName))
        {
            return null;
        }

        TaskModel taskModel = loadTaskModel(context, task);
        SmartSurveyTask smartSurveyTask = new SmartSurveyTask(context, taskModel);
        return smartSurveyTask;
    }

    @Override
    public void uploadTaskResult(Context context, TaskResult taskResult)
    {
        // Update/Create TaskNotificationService
        if(AppPrefs.getInstance(context).isTaskReminderEnabled())
        {
            Log.i("ApplicationDataProvider", "uploadTaskResult() _ isTaskReminderEnabled() = true");

            String chronTime = findChronTime(taskResult.getIdentifier());

            // If chronTime is null then either the task is not repeating OR its not found within
            // the task_and_schedules.xml
            if(chronTime != null)
            {
                scheduleReminderNotification(context, taskResult.getEndDate(), chronTime);
            }
        }

        List<BridgeDataInput> files = new ArrayList<>();

        for(StepResult stepResult : taskResult.getResults().values())
        {
            SurveyAnswer surveyAnswer = SurveyAnswer.create(stepResult);
            files.add(new BridgeDataInput(surveyAnswer,
                    SurveyAnswer.class,
                    stepResult.getIdentifier() + ".json",
                    FormatHelper.DEFAULT_FORMAT.format(stepResult.getEndDate())));
        }

        uploadBridgeData(context,
                new Info(context,
                        getGuid(taskResult.getIdentifier()),
                        getCreatedOnDate(taskResult.getIdentifier())),
                files);
    }

    public void uploadBridgeData(Context context, Info info, BridgeDataInput... dataFiles)
    {
        uploadBridgeData(context, info, Arrays.asList(dataFiles));
    }

    public void uploadBridgeData(Context context, Info info, List<BridgeDataInput> dataFiles)
    {
        //TODO to implement using a researchnet API
        return;
    }

    // these stink, I should be able to query the DB and find these
    private String getCreatedOnDate(String identifier)
    {
        return loadedTaskDates.get(identifier);
    }

    private String getGuid(String identifier)
    {
        return loadedTaskGuids.get(identifier);
    }

    private String findChronTime(String identifier)
    {
        return loadedTaskCrons.get(identifier);
    }

    private void scheduleReminderNotification(Context context, Date endDate, String chronTime)
    {
        Log.i("ApplicationDataProvider", "scheduleReminderNotification()");

        // Save TaskNotification to DB
        TaskNotification notification = new TaskNotification();
        notification.endDate = endDate;
        notification.chronTime = chronTime;
        NotificationHelper.getInstance(context).saveTaskNotification(notification);

        // Add notification to Alarm Manager
        Intent intent = new Intent(TaskAlertReceiver.ALERT_CREATE);
        intent.putExtra(TaskAlertReceiver.KEY_NOTIFICATION, notification);
        context.sendBroadcast(intent);
    }

    @Override
    public abstract void processInitialTaskResult(Context context, TaskResult taskResult);


    /**
     * 400	BadRequestException	            variable
     * 400	PublishedSurveyException	    A published survey cannot be updated or deleted (only closed).
     * 400	InvalidEntityException	        variable based on fields that are invalid
     * 401	✓ NotAuthenticatedException	    Not signed in.
     * 403	UnauthorizedException	        Caller does not have permission to access this service.
     * 404	EntityNotFoundException	        <entityTypeName> not found.
     * 409	EntityAlreadyExistsException	<entityTypeName> already exists.
     * 409	ConcurrentModificationException	<entityTypeName> has the wrong version number; it may have been saved in the background.
     * 410	UnsupportedVersionException	    "This app version is not supported. Please update." The app has sent a valid User-Agent header and the server has determined that the app's version is out-of-date and no longer supported by the configuration of the study on the server. The user should be prompted to update the application before using it further. Data will not be accepted by the server and schedule, activities, surveys, etc. will not be returned to this app until it sends a later version number.
     * 412	✓ ConsentRequiredException	    Consent is required before signing in. This exception is returned with a JSON payload that includes the user's session. The user is considered signed in at this point, but unable to use any service endpoint that requires consent to participate in the study.
     * 423	BridgeServerException           "Account disabled, please contact user support" Contact BridgeIT@sagebase.org to resolve this issue.
     * 473	StudyLimitExceededException	    The study '<studyName>' has reached the limit of allowed participants.
     * 500	BridgeServerException	        variable
     * 503	ServiceUnavailableException	    variable
     **/
    private void handleError(Context context, int responseCode)
    {
        String intentAction = null;

        switch(responseCode)
        {
            // Not signed in.
            case 401:
                intentAction = DataProvider.ERROR_NOT_AUTHENTICATED;
                break;

            // Not Consented
            case 412:
                intentAction = DataProvider.ERROR_CONSENT_REQUIRED;
                break;
        }

        if(intentAction != null)
        {
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(intentAction));
        }
    }

    // figure out what directory to save files in and where to put this method
    public static File getFilesDir(Context context)
    {
        return new File(context.getFilesDir() + "/upload_request/");
    }

    public interface ResearchnetService
    {

        /**
         * @return One of the following responses
         * <ul>
         * <li><b>201</b> returns message that user has been signed up</li>
         * <li><b>473</b> error - returns message that study is full</li>
         * </ul>
         */
        @Headers("Content-Type: application/json")
        @POST("participant/")
        Observable<BridgeMessageResponse> signUp(@Body SignUpBody body);

        /**
         * @return One of the following responses
         * <ul>
         * <li><b>200</b> returns UserSessionInfo Object</li>
         * <li><b>404</b> error - "Credentials incorrect or missing"</li>
         * <li><b>412</b> error - "User has not consented to research"</li>
         * </ul>
         */
        @Headers("Content-Type: application/json")
        @POST("api-token-auth/")
        Observable<Response<UserSessionInfo>> signIn(@Body SignInBody body);





    }

}
