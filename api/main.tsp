import "@typespec/http";
import "@typespec/rest";

using TypeSpec.Http;
using TypeSpec.Rest;

@service(#{ title: "Hexlet Career API" })
@route("/api")
namespace HexletCareer.Api;

/**
 * Общий ответ API при ошибке.
 */
@error
model ApiError {
  code: string;
  message: string;
}