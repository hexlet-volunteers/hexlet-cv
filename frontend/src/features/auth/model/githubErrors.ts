/** Тексты для `/login?error=...` после неудачного входа через GitHub. */
export const githubErrors: Record<string, string> = {
  github_failed: 'Не удалось войти через GitHub. Попробуйте ещё раз.',
  github_no_email:
    'На GitHub нет подтверждённого основного email. Подтвердите адрес в настройках GitHub или войдите по email и паролю.',
  github_conflict:
    'Этот email уже привязан к другому аккаунту. Войдите по email и паролю или напишите в поддержку.',
}
