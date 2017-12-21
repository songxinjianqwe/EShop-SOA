package cn.sinjinsong.eshop.common.base.exception;

import cn.sinjinsong.eshop.common.base.exception.annotation.RestExceptionAnnotationUtil;
import cn.sinjinsong.eshop.common.base.exception.domain.RestFieldError;
import cn.sinjinsong.eshop.common.util.InternationalizeUtil;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseRestException extends RuntimeException {
    private HttpStatus status;
    private int code;
    private List<RestFieldError> error;
    private String moreInfoURL = "";

    public BaseRestException() {
    }

    public BaseRestException(Object rejectedValue) {
        RestExceptionAnnotationUtil.setAttr(this);
        this.error = Arrays.asList(new RestFieldError(RestExceptionAnnotationUtil.getFieldName(this), rejectedValue, InternationalizeUtil
                .getMessage("i18n." + RestExceptionAnnotationUtil.getMsgKey(this), LocaleContextHolder.getLocale())));
    }

    public BaseRestException(List<FieldError> error) {
        RestExceptionAnnotationUtil.setAttr(this);
        this.error = toRestFieldErrorList(error);
    }

    public static List<RestFieldError> toRestFieldErrorList(List<FieldError> errors) {
        List<RestFieldError> fieldErrors = new ArrayList<>(errors.size());
        for (FieldError error : errors) {
            fieldErrors.add(new RestFieldError(error));
        }
        return fieldErrors;
    }

    public List<RestFieldError> getErrors() {
        return error;
    }

    public void setErrors(List<RestFieldError> error) {
        this.error = error;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }


    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMoreInfoURL() {
        return moreInfoURL;
    }

    public void setMoreInfoURL(String moreInfoURL) {
        this.moreInfoURL = moreInfoURL;
    }
    
    
}
