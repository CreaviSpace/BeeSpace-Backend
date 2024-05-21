package com.creavispace.project.common.utils;

import com.creavispace.project.common.exception.CreaviCodeException;
import com.creavispace.project.common.exception.GlobalErrorCode;

public class CustomValueOf {
    
    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name, GlobalErrorCode errorCode){
        if(name == null) return null;
        try {
            return Enum.valueOf(enumType, name);
        } catch (Exception e) {
            throw new CreaviCodeException(errorCode);
        }
    }
}
