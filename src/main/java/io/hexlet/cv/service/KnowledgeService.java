package io.hexlet.cv.service;

import io.hexlet.cv.dto.knowledge.KnowledgeArticleDto;
import io.hexlet.cv.dto.knowledge.KnowledgeInterviewDto;
import io.hexlet.cv.handler.exception.ResourceNotFoundException;
import io.hexlet.cv.mapper.KnowledgeMapper;
import io.hexlet.cv.repository.KnowledgeArticleRepository;
import io.hexlet.cv.repository.KnowledgeInterviewRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.hexlet.cv.util.ControllerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeService {
    private final KnowledgeInterviewRepository knowledgeInterviewRepository;
    private final KnowledgeArticleRepository knowledgeArticleRepository;
    private final KnowledgeMapper knowledgeMapper;
    private final ControllerUtils utils;

    @Transactional
    public Page<KnowledgeArticleDto> getArticles(String category, Pageable pageable) {

        var page = StringUtils.hasText(category)
                ? knowledgeArticleRepository.findByIsPublishedTrueAndCategoryOrderByPublishedAtDesc(
                category.trim(), pageable)
                : knowledgeArticleRepository.findByIsPublishedTrueOrderByPublishedAtDesc(pageable);

        log.debug("Getting articles, category: {}, page: {}",
                StringUtils.hasText(category) ? category : "all",
                pageable.getPageNumber());

        return page.map(knowledgeMapper::toArticleDTO);
    }

    @Transactional
    public KnowledgeArticleDto getArticleById(Long id) {
        log.debug("Getting article by id: {}", id);
        return knowledgeArticleRepository.findByIdAndIsPublishedTrue(id)
                .map(knowledgeMapper::toArticleDTO)
                .orElseThrow(() -> new ResourceNotFoundException("article.not.found.or.not.public"));
    }

    @Transactional
    public List<KnowledgeArticleDto> getRecentArticles(int limit) {
        log.debug("Getting {} recent articles", limit);
        return knowledgeArticleRepository.findByIsPublishedTrueOrderByPublishedAtDesc()
                .stream()
                .limit(limit)
                .map(knowledgeMapper::toArticleDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<KnowledgeInterviewDto> getInterviews(String category, Pageable pageable) {

        var page = StringUtils.hasText(category)
                ? knowledgeInterviewRepository.findByIsPublishedTrueAndCategoryOrderByPublishedAtDesc(
                category.trim(), pageable)
                : knowledgeInterviewRepository.findByIsPublishedTrueOrderByPublishedAtDesc(pageable);

        log.debug("Getting interview, category: {}, page: {}",
                StringUtils.hasText(category) ? category : "all", pageable.getPageNumber());

        return page.map(knowledgeMapper::toInterviewDTO);
    }

    @Transactional
    public KnowledgeInterviewDto getInterviewById(Long id) {
        log.debug("Getting interview by id: {}", id);
        return knowledgeInterviewRepository.findByIdAndIsPublishedTrue(id)
                .map(knowledgeMapper::toInterviewDTO)
                .orElseThrow(() -> new ResourceNotFoundException("interview.not.found.or.not.public"));
    }

    @Transactional
    public List<KnowledgeInterviewDto> getRecentInterviews(int limit) {
        log.debug("Getting {} recent interviews", limit);
        return knowledgeInterviewRepository.findByIsPublishedTrueOrderByPublishedAtDesc()
                .stream()
                .limit(limit)
                .map(knowledgeMapper::toInterviewDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> buildArticlesPageProps(String category, Pageable pageable) {
        var articlesPage = getArticles(category, pageable);
        Map<String, Object> pageProps = new HashMap<>();
        pageProps.put("articles", articlesPage.getContent());
        pageProps.put("pagination", utils.createPaginationMap(articlesPage, pageable));
        if (category != null && !category.isBlank()) {
            pageProps.put("selectedCategory", category);
        }
        return pageProps;
    }

    @Transactional
    public Map<String, Object> buildInterviewsPageProps(String category, Pageable pageable) {
        Map<String, Object> pageProps = new HashMap<>();
        var interviewsPage = getInterviews(category, pageable);
        pageProps.put("interviews", interviewsPage.getContent());
        pageProps.put("pagination", utils.createPaginationMap(interviewsPage, pageable));
        if (category != null && !category.isBlank()) {
            pageProps.put("selectedCategory", category);
        }
        return pageProps;
    }
}
