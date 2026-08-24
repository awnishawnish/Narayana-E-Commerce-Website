package NarayanGroup.example.E_Commerce.DTO.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
