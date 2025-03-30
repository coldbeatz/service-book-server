package servicebook.services;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.InitializingBean;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import servicebook.entity.news.News;
import servicebook.entity.news.NewsPostingOption;

import servicebook.localization.Localization;

import servicebook.repository.NewsRepository;

import servicebook.services.mail.EmailService;
import servicebook.user.User;
import servicebook.user.UserService;

import java.time.LocalDateTime;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService implements InitializingBean {

    private final NewsRepository newsRepository;

    private final EmailService emailService;
    private final UserService userService;

    private final HtmlContentService htmlContentService;

    /**
     * Кешований список новин
     */
    private List<News> cachedNews;

    /**
     * Відкладені новини для відправки новини на пошту чи публікація на сайті
     */
    private final List<News> delayedNews = new CopyOnWriteArrayList<>();

    @Override
    public void afterPropertiesSet() {
        cachedNews = newsRepository.findAll();
        initDelayedNews();
    }

    private void initDelayedNews() {
        delayedNews.clear();

        for (News news : cachedNews) {
            if (!news.isPosted()) {
                delayedNews.add(news);
            }
        }
    }

    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void runWithFixedDelay() {
        LocalDateTime now = LocalDateTime.now();

        for (News news : delayedNews) {
            if (news.isPosted()) {
                delayedNews.remove(news);
                continue;
            }

            LocalDateTime delay = news.getDelayedPostingDate();

            if (delay == null || delay.isBefore(now)) {
                sendNewsToUsers(news);
                delayedNews.remove(news);
            }
        }
    }

    private void sendNewsToUsers(News news) {
        if (!news.hasPostingOption(NewsPostingOption.EMAIL)) {
            news.setPosted(true);
            newsRepository.save(news);

            log.info("Sent news ID={} to website", news.getId());
        } else {
            List<String> subscribedEmails = userService.getSubscribedEmails();

            for (String email : subscribedEmails) {
                String subject = news.getTitle().getEn();
                String htmlBody = news.getContent().getEn();

                emailService.sendHtmlMessage(email, subject, htmlBody);
            }

            news.setPosted(true);
            newsRepository.save(news);

            log.info("Sent news ID={} to {} subscribers", news.getId(), subscribedEmails.size());
        }
    }

    /**
     * Повертає список новин, доступних для видимості на сайті
     * Враховується лише:
     * - дата публікації, якщо вона в минулому або відсутня
     * - наявність опції WEBSITE у способах публікації
     *
     * @return список новин, доступних на сайті
     */
    public List<News> getAvailableWebsiteNews() {
        LocalDateTime now = LocalDateTime.now();

        return cachedNews.stream().filter(news -> {
            LocalDateTime delay = news.getDelayedPostingDate();
            boolean showOnWebsite = news.hasPostingOption(NewsPostingOption.WEBSITE);

            return showOnWebsite && (delay == null || delay.isBefore(now));
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<News> getAll() {
        return cachedNews;
    }

    @Transactional
    public void saveOrUpdate(News news) {
        removeNewsFromCache(news);

        LocalDateTime delay = news.getDelayedPostingDate();

        if (delay == null || delay.isBefore(LocalDateTime.now())) {
            sendNewsToUsers(news);
        } else {
            news.setPosted(false);
            delayedNews.add(news);
        }

        User user = news.getUpdatedBy() != null ? news.getUpdatedBy() : news.getCreatedBy();

        for (Localization lang : Localization.values()) {
            String content = lang.getValue(news.getContent());
            String prepareContent = htmlContentService.processContent(content, user);

            lang.setValue(news.getContent(), prepareContent);
        }

        newsRepository.save(news);
        cachedNews.add(news);
    }

    @Transactional(readOnly = true)
    public News getById(Long id) {
        return cachedNews.stream()
                .filter(news -> news.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("News not found"));
    }

    @Transactional
    public void delete(News news) {
        newsRepository.delete(news);

        removeNewsFromCache(news);
    }

    private void removeNewsFromCache(News news) {
        Predicate<News> predicate = n -> n.getId().equals(news.getId());

        delayedNews.removeIf(predicate);
        cachedNews.removeIf(predicate);
    }
}