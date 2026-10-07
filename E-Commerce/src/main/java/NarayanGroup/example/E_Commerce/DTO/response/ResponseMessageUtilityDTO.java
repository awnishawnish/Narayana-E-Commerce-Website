package NarayanGroup.example.E_Commerce.DTO.response;


import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseMessageUtilityDTO {
    private String status;
    private int httpStatus;
    private String message;
    private String msId;
    private Object data;
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Boolean first;
    private Boolean last;

    public ResponseMessageUtilityDTO(String failureStatus, int value, String validationFailed, String defaultMsId, Map<String, Object> stringObjectMap) {
        this.status = failureStatus;
        this.httpStatus = value;
        this.message = validationFailed;
        this.msId = defaultMsId;
        this.data = stringObjectMap;
    }

    public ResponseMessageUtilityDTO(String failureStatus, int value, String validationFailed, String defaultMsId, Object object) {
        this.status = failureStatus;
        this.httpStatus = value;
        this.message = validationFailed;
        this.msId = defaultMsId;
        this.data = object;
    }

}
