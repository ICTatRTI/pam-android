package org.rti.rcd.researchstack;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.backbone.storage.database.sqlite.SqlCipherDatabaseHelper;
import org.researchstack.backbone.storage.database.sqlite.UpdatablePassphraseProvider;
import org.researchstack.backbone.storage.file.EncryptionProvider;
import org.researchstack.backbone.storage.file.FileAccess;
import org.researchstack.backbone.storage.file.PinCodeConfig;
import org.researchstack.backbone.storage.file.SimpleFileAccess;
import org.researchstack.backbone.storage.file.aes.AesProvider;
import org.rti.rcd.researchstack.bridge.BridgeEncryptedDatabase;
import org.researchstack.skin.AppPrefs;
import org.researchstack.skin.PermissionRequestManager;
import org.researchstack.skin.notification.NotificationConfig;
import org.researchstack.skin.notification.SimpleNotificationConfig;

public class ResearchStack extends org.researchstack.skin.ResearchStack
{

    @Override
    protected AppDatabase createAppDatabaseImplementation(Context context)
    {
        SQLiteDatabase.loadLibs(context);
        return new BridgeEncryptedDatabase(context,
                SqlCipherDatabaseHelper.DEFAULT_NAME,
                null,
                SqlCipherDatabaseHelper.DEFAULT_VERSION,
                new UpdatablePassphraseProvider());
    }

    @Override
    protected FileAccess createFileAccessImplementation(Context context)
    {
        return new SimpleFileAccess();
    }

    @Override
    protected PinCodeConfig getPinCodeConfig(Context context)
    {
        long autoLockTime = AppPrefs.getInstance(context).getAutoLockTime();
        return new PinCodeConfig(autoLockTime);
    }

    @Override
    protected EncryptionProvider getEncryptionProvider(Context context)
    {
        return new AesProvider();
    }

    @Override
    protected org.researchstack.skin.ResourceManager createResourceManagerImplementation(Context context)
    {
        return new ResourceManager();
    }

    @Override
    protected org.researchstack.skin.UiManager createUiManagerImplementation(Context context)
    {
        return new ApplicationUiManager();
    }

    @Override
    protected org.researchstack.skin.DataProvider createDataProviderImplementation(Context context)
    {
        return new ApplicationDataProvider();
    }

    @Override
    protected org.researchstack.skin.TaskProvider createTaskProviderImplementation(Context context)
    {
        return new ApplicationTaskProvider(context);
    }

    @Override
    protected NotificationConfig createNotificationConfigImplementation(Context context)
    {
        return new SimpleNotificationConfig();
    }

    @Override
    protected PermissionRequestManager createPermissionRequestManagerImplementation(Context context)
    {
        return new PermissionResultManager();
    }
}
