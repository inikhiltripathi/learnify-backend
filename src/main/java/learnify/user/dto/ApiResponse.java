package learnify.user.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {

    private String status;
    private String message;
    private T data = null;
    private Object error = null;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();


    private ApiResponse(String status, String message, T data, Object error) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.error = error;
        // timestamp apne aap set ho jayega
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("SUCCESS", message, data, null);
    }


    public static <T> ApiResponse<T> failure(String message, Object error) {
        return new ApiResponse<>("FAILURE", message, null, error);
    }

    public static <T> ApiResponse<T> message(String status, String message) {
        return new ApiResponse<>(status, message, null, null);
    }

}
