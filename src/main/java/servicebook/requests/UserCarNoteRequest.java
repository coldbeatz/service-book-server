package servicebook.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserCarNoteRequest {

    private Long id;

    private String shortDescription;
    private String content;
}
