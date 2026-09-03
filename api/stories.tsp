import "@typespec/http";
import "@typespec/openapi";

using TypeSpec.Http;
using TypeSpec.OpenAPI;

namespace HexletCareer.Api.Stories;

@doc("Карточка истории успеха выпускника Хекслета")
model StoryDto {
  @doc("Уникальный идентификатор истории")
  id: int64;

  @doc("Имя выпускника Хекслета")
  @maxLength(255)
  authorName: string;

  @doc("Ссылка на аватарку/фотографию")
  @maxLength(2048)
  avatarUrl: string;

  @doc("Компания, куда устроился работать выпускник")
  @maxLength(255)
  companyName: string;

  @doc("Полученный оффер (занимаемая должность)")
  @maxLength(255)
  offerPosition: string;

  @doc("Текст истории выпускника")
  @maxLength(50000)
  text: string;

  @doc("Порядок ручной сортировки для отображения на лендинге")
  @minValue(0)
  displayOrder: int32;
}

@doc("Универсальная обертка для пагинации ответов")
model PageDto<T> {
  @doc("Элементы текущей страницы")
  content: T[];

  @doc("Общее количество элементов")
  totalElements: int64;

  @doc("Общее количество страниц")
  totalPages: int32;

  @doc("Номер текущей страницы")
  number: int32;
}

@doc("Страница со списком историй успеха")
model StoryPageResponse extends PageDto<StoryDto> {}

@route("/v1/stories")
@tag("Stories")
interface StoriesOperations {
  @doc("Получить список опубликованных историй с пагинацией и сортировкой")
  @get
  @operationId("getStories")
  op getStories(
    @doc("Номер запрашиваемой страницы (0-based)")
    @query page?: int32 = 0,

    @doc("Количество элементов на одной странице")
    @minValue(1)
    @maxValue(100)
    @query size?: int32 = 10,

    @doc("Сортировка в формате property,asc|desc")
    @query sort?: string[] = #["displayOrder,asc"]
  ): StoryPageResponse;
}
