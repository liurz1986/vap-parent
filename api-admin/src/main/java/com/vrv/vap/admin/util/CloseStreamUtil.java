package com.vrv.vap.admin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CloseStreamUtil {

    private static final Logger log = LoggerFactory.getLogger(CloseStreamUtil.class);

    public static void closeStream(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    public static void closeStream(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
