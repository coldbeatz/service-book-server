package servicebook.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import servicebook.entity.news.News;

import servicebook.localization.LocalizedString;

import servicebook.requests.NewsRequest;

import servicebook.services.HtmlContentService;
import servicebook.services.NewsService;
import servicebook.user.User;

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

    private final HtmlContentService htmlContentService;

    /**
     * Отримати новину за її ID
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<News> getById(@PathVariable Long id) {
        News news = newsService.getById(id);

        return ResponseEntity.ok(news);
    }

    @GetMapping("/available")
    public ResponseEntity<List<News>> getAvailableWebsiteNews() {
        List<News> newsList = newsService.getAvailableWebsiteNews();

        return ResponseEntity.ok(newsList);
    }

    /**
     * Отримати список усіх новин
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<News>> all() {
        List<News> newsList = newsService.getAll();

        return ResponseEntity.ok(newsList);
    }

    /**
     * Видалити новину за ID
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        News news = newsService.getById(id);
        newsService.delete(news);

        return ResponseEntity.noContent().build();
    }

    /**
     * Створити нову новину
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

        User user = getAuthenticatedUser();

        String prepareContentEn = htmlContentService.processContent(request.getContent().getEn(), user);
        String prepareContentUa = htmlContentService.processContent(request.getContent().getUa(), user);

        news.getContent().setEn(prepareContentEn);
        news.getContent().setUa(prepareContentUa);

        news.setDelayedPostingDate(request.getDelayedPostingDate());
        news.setPostingOptions(request.getPostingOptions());

        newsService.saveOrUpdate(news);
    }
}