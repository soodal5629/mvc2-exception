package hello.exception.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 해당 리졸버는 WAS 까지 전달되지만 BasicErrorController 호출안하고 깔끔하게 끝나버림
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try{
            if(ex instanceof UserException) {
                log.info("UserException resolver to 400");
                // 똑똑하게 처리하려면 HTTP 헤더가 JSON인 경우와 아닌 경우(html 등)를 나눠서 처리해줘야 함
                String acceptHeader = request.getHeader("accept");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                // json 일 경우
                if("application/json".equals(acceptHeader)) {
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("ex", ex.getClass()); // 어떤 exception인지
                    errorResult.put("message", ex.getMessage());

                    String result = objectMapper.writeValueAsString(errorResult);

                    // HTTP 응답 바디에 json 데이터 넣기
                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(result);

                    return new ModelAndView();
                } else{
                    // TEXT or HTML
                    return new ModelAndView("error/500"); // 템플릿에 error 폴더 안에 있는 500.html
                }



            }
        } catch(IOException e){
            log.error("resolver ex", e);
        }
        return null;
    }
}
