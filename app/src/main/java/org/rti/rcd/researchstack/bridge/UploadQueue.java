package org.rti.rcd.researchstack.bridge;

import java.util.List;


public interface UploadQueue
{
    List<UploadRequest> loadUploadRequests();

    void saveUploadRequest(UploadRequest request);

    void deleteUploadRequest(UploadRequest request);
}
