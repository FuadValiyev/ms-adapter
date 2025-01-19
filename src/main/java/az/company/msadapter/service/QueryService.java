package az.company.msadapter.service;

import az.company.msadapter.dto.SqlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueryService {

    private final JdbcTemplate jdbcTemplate;

    public Object executeQuery(SqlRequest request) {

        LocalDateTime now = LocalDateTime.now();
        log.info("[requestNumber={}, time={}] Incoming query: {}",
                request.getRequestNumber(), now, request.getQuery());

        String sql = request.getQuery();
        String upperSql = sql.trim().toUpperCase();

        if (upperSql.startsWith("DROP")) {
            log.warn("[requestNumber={}] Attempted DROP query -> Rejected!",
                    request.getRequestNumber());
            return Map.of("message", "Cannot use DROP query");
        }

        if (upperSql.startsWith("SELECT")) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            log.info("[requestNumber={}] SELECT returned {} rows",
                    request.getRequestNumber(), rows.size());
            log.info(rows.toString());

            return Map.of("data", rows);
        } else {
            int affected = jdbcTemplate.update(sql);

            log.info("[requestNumber={}] Non-SELECT query affectedRows={}",
                    request.getRequestNumber(), affected);

            if (affected == 1) {
                return Map.of("message", "Successes");
            }
            return Map.of("message", "Fails");
        }
    }
}