    package com.bilibili.dao.repository;

    import com.bilibili.domain.Video;
    import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

    public interface VideoRepository extends ElasticsearchRepository<Video, Long> {
        Video findByTitleLike(String keyword);
    }
