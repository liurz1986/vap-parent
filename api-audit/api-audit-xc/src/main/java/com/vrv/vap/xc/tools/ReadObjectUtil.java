package com.vrv.vap.xc.tools;

import java.io.*;
import java.util.List;

public class ReadObjectUtil {
    public static Object myReadObject(Class<?> targetClass, List<Class<?>> safeClasses, long maxObjects,
                                      long maxBytes, InputStream in) throws ClassNotFoundException, IOException {
        InputStream lis = null;
        ObjectInputStream ois = null;
        Object returnObject = null;
        try {
            // create an input stream limited to a certain number of bytes
            lis = new FilterInputStream(in) {
                private long len = 0;

                public int read() throws IOException {
                    int val = super.read();
                    if (val != -1) {
                        len++;
                        checkLength();
                    }
                    return val;
                }

                public int read(byte[] b, int off, int len) throws IOException {
                    int val = super.read(b, off, len);
                    if (val > 0) {
                        len += val;
                        checkLength();
                    }
                    return val;
                }
                private void checkLength() throws IOException {
                    if (len > maxBytes) {
                        throw new SecurityException(
                                "Security violation: attempt to deserialize too many bytes from stream. Limit is "
                                        + maxBytes);
                    }
                }
            };
            ois = new ObjectInputStream(lis) {
                private int objCount = 0;
                boolean b = enableResolveObject(true);

                protected Object resolveObject(Object obj) throws IOException {
                    if (objCount++ > maxObjects)
                        throw new SecurityException(
                                "Security violation: attempt to deserialize too many objects from stream. Limit is "
                                        + maxObjects);
                    Object object = super.resolveObject(obj);
                    return object;
                }

                protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
                    Class<?> clazz = super.resolveClass(osc);
                    if (clazz.equals(targetClass) || safeClasses.contains(clazz))
                        return clazz;
                    throw new SecurityException("Security violation: attempt to deserialize unauthorized " + clazz);
                }
            };
            if (null != ois) {
                returnObject = ois.readObject();
            }
        } catch (IOException e) {
            //异常处理
        } finally {
            safeClose(lis);
            safeClose(ois);
        }
        return returnObject;
    }

    public static void safeClose(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                //异常处理
            }
        }
    }
}
