package cn.sinjinsong.eshop.common.base.exception.annotation;


import cn.sinjinsong.eshop.common.base.exception.BaseRestException;
import cn.sinjinsong.eshop.common.base.exception.meta.RestFieldAnnotationNotFoundException;
import cn.sinjinsong.eshop.common.base.exception.meta.RestResponseStatusAnnotationNotFoundException;

/**
 * Created by SinjinSong on 2017/3/27.
 */
public class RestExceptionAnnotationUtil {
    public static void setAttr(BaseRestException e) {
        RestResponseStatus status = e.getClass().getAnnotation(RestResponseStatus.class);
        if (status == null) {
            throw new RestResponseStatusAnnotationNotFoundException();
        }
        e.setStatus(status.value());
        e.setCode(Integer.parseInt(status.value().value() + "" + String.format("%02d", status.code())));
        e.setMoreInfoURL(status.url());

    }

    public static String getMsgKey(BaseRestException e) {
        RestResponseStatus status = e.getClass().getAnnotation(RestResponseStatus.class);
        if (status == null) {
            throw new RestResponseStatusAnnotationNotFoundException();
        }
        if (status.msgKey().equals("")) {
            String simpleName = e.getClass().getSimpleName();
            return simpleName.substring(0, simpleName.lastIndexOf("Exception"));
        } else {
            return status.msgKey();
        }
    }

    public static String getFieldName(BaseRestException e) {
        RestField field = e.getClass().getAnnotation(RestField.class);
        if (field == null) {
            throw new RestFieldAnnotationNotFoundException();
        }
        return field.value();
    }
}
