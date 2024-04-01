package com.creavispace.project.global.util;

import com.creavispace.project.global.exception.CreaviCodeException;
import com.creavispace.project.global.exception.GlobalErrorCode;

public class CustomValueOf {
    
    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name, GlobalErrorCode errorCode){
        try {
            return Enum.valueOf(enumType, name);
        } catch (Exception e) {
            throw new CreaviCodeException(errorCode);
        }
    }
}
