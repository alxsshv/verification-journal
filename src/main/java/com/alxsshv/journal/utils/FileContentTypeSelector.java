package com.alxsshv.journal.utils;

import org.springframework.http.MediaType;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.MediaType.APPLICATION_PDF;

public class FileContentTypeSelector {
    public static MediaType getContentType (String extension){
        switch(extension.toLowerCase()){
            case "pdf" : return APPLICATION_PDF;
            case "doc", "dot": return MediaType.valueOf("application/msword");
            case "docx" : return MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            case "dotx" : return MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.template");
            case "docm", "dotm" : return MediaType.valueOf( "application/vnd.ms-word.document.macroEnabled.12");
            case "xls", "xlt", "xla" : return MediaType.valueOf("application/vnd.ms-excel");
            case "xlsx" : return MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "xltx" : return MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.template");
            case "xlsm" : return MediaType.valueOf("application/vnd.ms-excel.sheet.macroEnabled.12");
            case "xltm" : return MediaType.valueOf("application/vnd.ms-excel.template.macroEnabled.12");
            case "xlam" : return MediaType.valueOf("application/vnd.ms-excel.addin.macroEnabled.12");
            case "xlsb" : return MediaType.valueOf("application/vnd.ms-excel.sheet.binary.macroEnabled.12");
            case "ppt", "pot", "pps", "ppa" : return MediaType.valueOf("application/vnd.ms-powerpoint");
            case "pptx" : return MediaType.valueOf("application/vnd.openxmlformats-officedocument.presentationml.presentation");
            case "potx" : return MediaType.valueOf("application/vnd.openxmlformats-officedocument.presentationml.template");
            case "ppsx" : return MediaType.valueOf("application/vnd.openxmlformats-officedocument.presentationml.slideshow");
            case "ppam" : return MediaType.valueOf("application/vnd.ms-powerpoint.addin.macroEnabled.12");
            case "pptm" : return MediaType.valueOf("application/vnd.ms-powerpoint.presentation.macroEnabled.12");
            case "potm" : return MediaType.valueOf("application/vnd.ms-powerpoint.template.macroEnabled.12");
            case "ppsm" : return MediaType.valueOf(" application/vnd.ms-powerpoint.slideshow.macroEnabled.12");
            case "vsd" : return MediaType.valueOf("application/vnd.visio");
            case "csv" : return MediaType.valueOf("text/csv");
            case "ods":  return MediaType.valueOf("application/vnd.oasis.opendocument.spreadsheet");
            case "odt": return MediaType.valueOf("application/vnd.oasis.opendocument.text");
            case "odg" : return MediaType.valueOf("application/vnd.oasis.opendocument.graphics");
            case "json" : return MediaType.valueOf("application/json");
            case "xml" : return MediaType.valueOf("application/xml");
            case "zip" : return  MediaType.valueOf("application/zip");
            case "png" : return  MediaType.valueOf("image/png");
            case "jpeg", "jpg" : return MediaType.valueOf("image/jpeg");
            case "ico" : return MediaType.valueOf("image/x-icon");
            case "gif" : return MediaType.valueOf("image/gif");
            default: return APPLICATION_OCTET_STREAM;
        }
    }
}
