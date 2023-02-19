package hello.exception.exhandler.advice;

import hello.exception.exception.UserException;
import hello.exception.exhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
// 여러 컨트롤러에서 발생하는 예외를 처리할 수 있음
// @ControllerAdvice와 동일함, @ResponseBody 포함했다는 것만 다름
@RestControllerAdvice(basePackages = "hello.exception.api") // @ControllerAdvice, @ResponseBody 포함
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 얘 없으면 정상적으로 에러 처리하고 새로운 객체를 리턴하기 때문에 200으로 리턴함
    @ExceptionHandler(IllegalArgumentException.class)
    // 해당 컨트롤러(ApiExceptionV2Controller)에서 특정 예외가 발생하면
    // ExceptionHandlerExceptionResolver가 해당 컨트롤러에 @ExceptionHandler가 있을경우
    // 해당 로직을 실행하여 정상흐름(처리)으로 반환함 !
    public ErrorResult illegalExHandler(IllegalArgumentException e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandler(UserException e) {
        log.error("[exceptionHandler] ex", e);
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult illegalExHandler(Exception e) {
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("EX", e.getMessage());
    }
}
