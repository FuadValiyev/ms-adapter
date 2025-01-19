package az.company.msadapter.controller;

import az.company.msadapter.dto.SqlRequest;
import az.company.msadapter.service.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/query")
public class QueryController {

    private final QueryService queryService;

    @PostMapping("/execute")
    public ResponseEntity<?> executeQuery(@RequestBody SqlRequest request) {
        try {
            Object response = queryService.executeQuery(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}