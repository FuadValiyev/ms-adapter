package az.company.msadapter.dto;

import lombok.Data;

@Data
public class SqlRequest {

    private String requestNumber;
    private String query;
}