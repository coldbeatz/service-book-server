package servicebook.utils.responce;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import servicebook.exceptions.ClientException;

public class ResponseUtil {

    public static ResponseEntity<SuccessResponse> success() {
        return ResponseEntity.ok(new SuccessResponse());
    }

    public static <T> ResponseEntity<T> success(T data) {
        return ResponseEntity.ok(data);
    }

    public static ResponseEntity<ErrorResponse> error(HttpStatus status, String code) {
        return ResponseEntity.status(status).body(new ErrorResponse(code));
    }

    public static ResponseEntity<ErrorResponse> badRequest() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("bad_request"));
    }

    public static ResponseEntity<ErrorResponse> error(String code) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(code));
    }

    public static ResponseEntity<ErrorResponse> error(ClientException exception) {
        return error(exception.getCode());
    }

    public static ResponseEntity<ErrorResponse> error() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse());
    }
}
