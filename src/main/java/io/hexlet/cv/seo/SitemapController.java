package io.hexlet.cv.seo;

import io.hexlet.cv.repository.ResumeRepository;
import io.hexlet.cv.service.ResumeApiService;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@code sitemap.xml} с каталогом, опубликованными резюме и профилями их авторов,
 * и {@code robots.txt}, который закрывает кабинет и API.
 */
@RestController
public class SitemapController {

    /** Лимит одного файла sitemap по протоколу. */
    private static final int MAX_URLS = 50_000;

    private final ResumeRepository resumeRepository;
    private final String publicUrl;

    public SitemapController(ResumeRepository resumeRepository,
                             @Value("${app.public-url:http://localhost:5173}") String publicUrl) {
        this.resumeRepository = resumeRepository;
        this.publicUrl = publicUrl.replaceAll("/+$", "");
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        var page = resumeRepository.search(ResumeApiService.PUBLISHED, "",
                PageRequest.of(0, MAX_URLS / 2 - 1, Sort.by(Sort.Direction.DESC, "updatedAt")));
        var xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        url(xml, publicUrl + "/resumes", null);
        var authors = new LinkedHashSet<Long>();
        for (var r : page.getContent()) {
            var lastmod = r.getUpdatedAt() == null ? null : r.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE);
            url(xml, publicUrl + "/resumes/" + r.getId(), lastmod);
            authors.add(r.getUser().getId());
        }
        authors.forEach(id -> url(xml, publicUrl + "/profiles/" + id, null));
        return xml.append("</urlset>\n").toString();
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String robots() {
        return """
                User-agent: *
                Disallow: /app/
                Disallow: /admin/
                Disallow: /api/
                Disallow: /password/
                Sitemap: %s/sitemap.xml
                """.formatted(publicUrl);
    }

    private static void url(StringBuilder xml, String loc, String lastmod) {
        xml.append("  <url><loc>").append(SpaShell.escape(loc)).append("</loc>");
        if (lastmod != null) {
            xml.append("<lastmod>").append(lastmod).append("</lastmod>");
        }
        xml.append("</url>\n");
    }
}
