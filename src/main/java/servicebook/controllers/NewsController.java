package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import servicebook.entity.news.News;

import servicebook.localization.LocalizedString;

import servicebook.requests.NewsRequest;

import servicebook.services.NewsService;

import servicebook.utils.responce.ResponseUtil;

import java.time.LocalDateTime;

import java.util.List;

/**
 * Контролер для керування новинами системи.
 * Дозволяє створювати, оновлювати, видаляти та переглядати новини.
 */
@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController extends BaseController {

    private final NewsService newsService;

    /**
     * Отримати новину за її ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<News> getById(@PathVariable Long id) {
        News news = newsService.getById(id);

        return ResponseUtil.success(news);
    }

    /**
     * Отримати список усіх новин
     */
    @GetMapping
    public ResponseEntity<List<News>> all() {
        List<News> newsList = newsService.getAll();

        return ResponseEntity.ok(newsList);
    }

    /**
     * Видалити новину за ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        News news = newsService.getById(id);
        newsService.delete(news);

        return ResponseEntity.noContent().build();
    }

    /**
     * Створити нову новину
     */
    @PostMapping
    public ResponseEntity<News> save(@RequestBody NewsRequest request) {
        News news = new News();

        news.setTitle(new LocalizedString());
        news.setContent(new LocalizedString());

        news.setCreatedBy(getAuthenticatedUser());

        saveOrUpdate(request, news);
        return ResponseEntity.status(HttpStatus.CREATED).body(news);
    }

    /**
     * Оновити наявну новину
     */
    @PutMapping("/{id}")
    public ResponseEntity<News> update(@PathVariable Long id, @RequestBody NewsRequest request) {
        News news = newsService.getById(id);

        news.setUpdatedBy(getAuthenticatedUser());
        news.setUpdatedAt(LocalDateTime.now());

        saveOrUpdate(request, news);
        return ResponseEntity.ok(news);
    }

    private void saveOrUpdate(NewsRequest request, News news) {
        request.getTitle().updateLocalizedString(news.getTitle());
        request.getContent().updateLocalizedString(news.getContent());

        news.setDelayedPostingDate(request.getDelayedPostingDate());
        news.setPostingOptions(request.getPostingOptions());

        newsService.saveOrUpdate(news);
    }
}