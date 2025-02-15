package servicebook.services;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import servicebook.entity.news.News;

import servicebook.repository.NewsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    @Transactional(readOnly = true)
    public List<News> getAll() {
        return newsRepository.findAll();
    }

    @Transactional
    public void saveOrUpdate(News news) {
        newsRepository.save(news);
    }

    @Transactional(readOnly = true)
    public News getById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void delete(News news) {
        newsRepository.delete(news);
    }
}
