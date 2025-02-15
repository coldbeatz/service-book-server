package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import servicebook.entity.news.News;

import servicebook.localization.LocalizedString;

import servicebook.requests.NewsRequest;

import servicebook.services.NewsService;

import servicebook.utils.responce.ResponseUtil;

import java.util.List;

@RestController
@RequestMapping("admin/news")
@RequiredArgsConstructor
public class NewsController extends BaseController {

    private final NewsService newsService;

    @GetMapping("/{id}")
    public ResponseEntity<News> getById(@PathVariable Long id) {
        News news = newsService.getById(id);

        return ResponseUtil.success(news);
    }

    @GetMapping
    public ResponseEntity<List<News>> all() {
        List<News> newsList = newsService.getAll();

        return ResponseEntity.ok(newsList);
    }

    @PostMapping
    public ResponseEntity<News> save(@RequestBody NewsRequest request) {
        News news = new News();

        news.setTitle(new LocalizedString());
        news.setContent(new LocalizedString());

        buildNews(request, news);

        news.setCreatedBy(getAuthenticatedUser());
        newsService.saveOrUpdate(news);

        return ResponseEntity.ok(news);
    }

    @PutMapping("/{id}")
    public ResponseEntity<News> update(@PathVariable Long id, @RequestBody NewsRequest request) {
        News news = newsService.getById(id);

        buildNews(request, news);

        news.setUpdatedBy(getAuthenticatedUser());
        newsService.saveOrUpdate(news);

        return ResponseEntity.ok(news);
    }

    private void buildNews(NewsRequest request, News news) {
        request.getTitle().updateLocalizedString(news.getTitle());
        request.getContent().updateLocalizedString(news.getContent());
        news.setDelayedPostingDate(request.getDelayedPostingDate());
        news.setPostingOptions(request.getPostingOptions());
    }
}
