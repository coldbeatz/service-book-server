package servicebook.services.upload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileUploadResponse {

    private FileUploadStatus status;

    private String message;

    private String fileName;
}
