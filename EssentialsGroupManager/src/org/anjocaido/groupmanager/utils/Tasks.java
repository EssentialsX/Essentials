/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anjocaido.groupmanager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import org.anjocaido.groupmanager.data.Group;

/**
 *
 * @author gabrielcouto
 */
public abstract class Tasks {

    public static void copy(InputStream src, File dst) throws IOException {
        InputStream in = src;
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        try {
            in.close();
        } catch (Exception e) {
        }
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        copy(in, dst);
    }

    public static void removeOldFiles(File folder) {
        if (folder.isDirectory()) {
            long oldTime = System.currentTimeMillis() - 86400000L;
            for (File olds : folder.listFiles()) {
                if (olds.isFile()) {
                    if (olds.lastModified() < oldTime) {
                        try {
                            olds.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    public static String getDateString() {
        GregorianCalendar now = new GregorianCalendar();
        String date = "";
        date += now.get(GregorianCalendar.DAY_OF_MONTH);
        date += "-";
        date += now.get(GregorianCalendar.HOUR);
        date += "-";
        date += now.get(GregorianCalendar.MINUTE);
        return date;
    }
    public static String getStringListInString(List<String> list){
        if(list==null){
            return "";
        }
        String result="";
        for(int i=0;i<list.size();i++){
            result+=list.get(i);
            if(i<list.size()-1){
                result+=", ";
            }
        }
        return result;
    }
    public static String getStringArrayInString(String[] list){
        if(list==null){
            return "";
        }
        String result="";
        for(int i=0;i<list.length;i++){
            result+=list[i];
            if(i<((list.length)-1)){
                result+=", ";
            }
        }
        return result;
    }
    public static String getGroupListInString(List<Group> list){
        if(list==null){
            return "";
        }
        String result="";
        for(int i=0;i<list.size();i++){
            result+=list.get(i).getName();
            if(i<list.size()-1){
                result+=", ";
            }
        }
        return result;
    }
}
