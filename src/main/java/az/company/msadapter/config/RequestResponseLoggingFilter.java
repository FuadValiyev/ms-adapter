package az.company.msadapter.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper requestWrapper =
                new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper((HttpServletResponse) response);

        chain.doFilter(requestWrapper, responseWrapper);

        long duration = System.currentTimeMillis() - startTime;

        String method = requestWrapper.getMethod();
        String uri = requestWrapper.getRequestURI();
        String requestBody = getStringValue(
                requestWrapper.getContentAsByteArray(),
                requestWrapper.getCharacterEncoding());

        int status = responseWrapper.getStatus();
        String responseBody = getStringValue(
                responseWrapper.getContentAsByteArray(),
                responseWrapper.getCharacterEncoding());

        String requestNumber = parseRequestNumber(requestBody);

        log.info(">>> Incoming Request => method={}, uri={}, requestNumber={}, body={}",
                method, uri, requestNumber, requestBody);

        log.info("<<< Outgoing Response => status={}, duration={}ms, body={}",
                status, duration, responseBody);

        responseWrapper.copyBodyToResponse();
    }

    private String getStringValue(byte[] content, String encoding) {
        if (content == null || content.length == 0) {
            return "";
        }
        try {
            if (encoding != null) {
                return new String(content, encoding);
            } else {
                return new String(content, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            return new String(content, StandardCharsets.UTF_8);
        }
    }


    private String parseRequestNumber(String requestBody) {
        if (requestBody == null || requestBody.isBlank()) {
            return null;
        }
        try {
            JsonNode json = objectMapper.readTree(requestBody);
            JsonNode rn = json.get("requestNumber");
            return (rn != null) ? rn.asText() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
