package servicebook.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import servicebook.entity.news.NewsPostingOption;

import java.time.LocalDateTime;

import java.util.List;

@Getter
@Setter
@ToString
public class NewsRequest {

    private LocalizedRequest title;
    private LocalizedRequest content;

    private LocalDateTime delayedPostingDate;

    private List<NewsPostingOption> postingOptions;
}
