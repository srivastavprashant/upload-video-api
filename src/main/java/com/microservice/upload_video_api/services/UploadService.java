package com.microservice.upload_video_api.services;

import com.microservice.upload_video_api.models.dto.CompleteVideoUpload.req.CompleteVideoUploadRequest;
import com.microservice.upload_video_api.models.dto.InitiateUpload.req.InitiateUploadRequest;
import com.microservice.upload_video_api.models.dto.PreSign.req.PreSignRequest;
import com.microservice.upload_video_api.models.dto.Video;
import com.microservice.upload_video_api.models.dto.ETagList;
import com.microservice.upload_video_api.models.dto.InitiateUpload.res.UploadInitiateResponse;

import java.util.List;
import java.util.Map;

public interface UploadService {
    //    for saving video, basic method
    UploadInitiateResponse initiateMultipartUpload(InitiateUploadRequest initiateUploadRequest);

    Map<String, String> completeMultipartUpload(CompleteVideoUploadRequest completeVideoUploadRequest);

    Map<String, String> initiateSingleUpload(Video videoData);

    Map<String, String> generatePreSignedUrlMultiPartMethod(PreSignRequest preSignRequest);
}
