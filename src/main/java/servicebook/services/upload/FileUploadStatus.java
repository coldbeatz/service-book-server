package servicebook.services.upload;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum FileUploadStatus {

    @JsonProperty("success")
    SUCCESS,

    @JsonProperty("error")
    ERROR,

    @JsonEnumDefaultValue
    UNKNOWN
}
