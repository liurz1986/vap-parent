package com.vrv.vap.admin.common.util;

import com.github.tobato.fastdfs.proto.storage.DownloadCallback;

import java.io.InputStream;

public class DownloadInputStream implements DownloadCallback<InputStream> {

        @Override
        public InputStream recv(InputStream ins){
           return ins;
        }


}
