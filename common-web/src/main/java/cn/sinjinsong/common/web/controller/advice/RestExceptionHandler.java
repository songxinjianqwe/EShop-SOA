package cn.sinjinsong.common.web.controller.advice;


import cn.sinjinsong.eshop.common.base.exception.BaseRestException;
import cn.sinjinsong.eshop.common.base.exception.domain.RestError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {
	
	@ExceptionHandler(BaseRestException.class)
	public ResponseEntity<RestError> handleBaseRestException(BaseRestException e) {
		return new ResponseEntity<>(new RestError(e.getStatus(), e.getCode(), e.getErrors(), e.getMoreInfoURL()), e.getStatus());
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<RestError> handle(RuntimeException e) {
	    e.printStackTrace();
	    log.info("cause: {}",e.getCause());
	    if(e.getCause() instanceof  BaseRestException){
	        return handleBaseRestException((BaseRestException) e.getCause());
        }
        return new ResponseEntity<>(new RestError(HttpStatus.INTERNAL_SERVER_ERROR,500,null,null),HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
}
