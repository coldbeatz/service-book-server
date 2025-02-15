package servicebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import servicebook.entity.news.News;

public interface NewsRepository extends JpaRepository<News, Long> {

}
