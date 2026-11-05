package io.hexlet.cv.dto.resume;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Поля резюме, которые автор заполняет в кабинете.
 */
@Getter
@Setter
public class ResumeFormDTO {

    @NotBlank(message = "Укажите название резюме")
    @Size(max = 255)
    private String name;

    @Size(max = 10000)
    private String summary;

    @Size(max = 10000)
    private String skillsDescription;

    @Size(max = 10000)
    private String projectsDescription;

    @Size(max = 10000)
    private String awardsDescription;

    @Size(max = 10000)
    private String aboutMyself;

    @Size(max = 255)
    @Pattern(regexp = "^$|^https?://.+", message = "Ссылка должна начинаться с http:// или https://")
    private String githubUrl;

    @Size(max = 255)
    private String city;

    @Size(max = 255)
    private String relocation;

    @Size(max = 255)
    private String englishFluency;

    @Size(max = 255)
    @Email(message = "Некорректный email")
    private String contactEmail;

    @Size(max = 255)
    private String contactTelegram;

    @Size(max = 255)
    private String contactPhone;

    @Valid
    @Size(max = 30)
    private List<ResumeWorkFormDTO> works = new ArrayList<>();

    @Valid
    @Size(max = 30)
    private List<ResumeEducationFormDTO> educations = new ArrayList<>();

    /** Опубликовать резюме в каталоге. */
    private boolean published;
}
