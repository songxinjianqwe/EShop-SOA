package cn.sinjinsong.eshop.common.base.exception.domain;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

/**
 * 所有异常都将返回该错误对象
 */
public class RestError implements Serializable{
	private HttpStatus status;
	private int code;
	private List<RestFieldError> fieldErrors;
	private String moreInfoURL;
	
	public RestError() {
	}

	public RestError(HttpStatus status, int code, List<RestFieldError> fieldErrors, String moreInfoURL
			) {
		this.status = status;
		this.code = code;
		this.fieldErrors = fieldErrors;
		this.moreInfoURL = moreInfoURL;
	}

	
	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public List<RestFieldError> getFieldErrors() {
		return fieldErrors;
	}

	public void setFieldErrors(List<RestFieldError> fieldErrors) {
		this.fieldErrors = fieldErrors;
	}

	public String getMoreInfoURL() {
		return moreInfoURL;
	}

	public void setMoreInfoURL(String moreInfoURL) {
		this.moreInfoURL = moreInfoURL;
	}

	@Override
	public String toString() {
		return "RestError [status=" + status + ", code=" + code + ", fieldErrors=" + fieldErrors + ", moreInfoURL="
				+ moreInfoURL + "]";
	}
	
	
	
}
