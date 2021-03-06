package org.rti.rcd.researchstack;
import android.content.Context;

import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.result.TaskResult;
import org.rti.rcd.researchstack.researchnet.ResearchNetDataProvider;


public class ApplicationDataProvider extends ResearchNetDataProvider
{
    public ApplicationDataProvider()
    {
        super();
    }

    @Override
    public void processInitialTaskResult(Context context, TaskResult taskResult)
    {
        // treat the initial task like other tasks
        uploadTaskResult(context,taskResult);

    }

    @Override
    protected ResourcePathManager.Resource getTasksAndSchedules()
    {
        return org.researchstack.skin.ResourceManager.getInstance().getTasksAndSchedules();
    }

    @Override
    protected String getBaseUrl()
    {
        return BuildConfig.STUDY_BASE_URL;
    }

    @Override
    protected String getStudyId()
    {
        return BuildConfig.STUDY_ID;
    }

    @Override
    protected String getStudyName() {
        return BuildConfig.STUDY_NAME;
    }

    @Override
    protected int getAppVersion() {
        return BuildConfig.VERSION_CODE;
    }

    @Override
    protected String getResearchnNetAppKey() {
        return BuildConfig.RESEARCHNET_APP_KEY;
    }



}
