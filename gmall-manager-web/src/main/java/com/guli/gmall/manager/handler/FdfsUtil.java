package com.guli.gmall.manager.handler;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FdfsUtil {

    public static  String upLoadImage(MultipartFile multipartFile){
        String imgUrl = "http://192.168.109.128";
        TrackerServer ts = null;
        StorageServer ss = null;
        try {
            ClientGlobal.init("fdfs.conf");
            TrackerClient tc = new TrackerClient();
            ts = tc.getConnection();
            ss = tc.getStoreStorage(ts);
            StorageClient sc  = new StorageClient(ts,ss);
            byte[] bytes = multipartFile.getBytes();
            String fileName = multipartFile.getOriginalFilename();
            int index = fileName.lastIndexOf(".");
            String name  = fileName.substring(index+1);
            String[] uploadArray = sc.upload_appender_file(bytes,name,null);
            for (String s : uploadArray) {
                imgUrl += "/" + s;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }finally {
            if(ss!=null){
                try {
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(ts!=null){
                try {
                    ts.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return imgUrl;
    }

}
