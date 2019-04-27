//package com.likuncheng.controller;
//
//import org.apache.shiro.ShiroException;
//import org.apache.shiro.authz.UnauthorizedException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//import com.likuncheng.utils.BaseController;
//import com.likuncheng.utils.Constants;
//import com.likuncheng.utils.ResponseBase;
//
//import javax.servlet.http.HttpServletRequest;
//
//@SuppressWarnings(value= {"all"})
//@RestControllerAdvice
//public class ExceptionController {
//
//	@Autowired
//	private BaseController baseController;
//	
//    // 捕捉shiro的异常
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ExceptionHandler(ShiroException.class)
//    public ResponseBase handle401(ShiroException e) {
//        return baseController.setResult(Constants.HTTP_RES_CODE_401,e.getMessage(), null);
//    }
//
//    // 捕捉UnauthorizedException
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ExceptionHandler(UnauthorizedException.class)
//    public ResponseBase handle401() {
//        return baseController.setResult(Constants.HTTP_RES_CODE_401,"您的权限不足", null);
//    }
//
//    // 捕捉其他所有异常
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseBase globalException(HttpServletRequest request, Throwable ex) {
//        return baseController.setResult(Constants.HTTP_RES_CODE_401,ex.getMessage(), null);
//    }
//
//    private HttpStatus getStatus(HttpServletRequest request) {
//        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
//        if (statusCode == null) {
//            return HttpStatus.INTERNAL_SERVER_ERROR;
//        }
//        return HttpStatus.valueOf(statusCode);
//    }
//}
//
