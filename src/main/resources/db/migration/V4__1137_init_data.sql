-- issue #1137
-- stories_stories
INSERT INTO stories_stories
    (author_name, avatar_url, company_name, offer_position, text, display_order, is_published)
VALUES
    ( 'Алексей П.',
     'https://images.unsplash.com/vector-1754112354550-df4f71cc0914?q=80&w=812&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
     'Авито', 'QA Automation Engineer',
     '"Было принято решение сменить профессиональный стек и перейти в автоматизацию тестирования.
Благодаря качественному код-ревью менторов удалось быстро освоить навыки написания промышленного кода и успешно пройти отбор."',
     1, true),
    ( 'Мария К.',
     'https://images.unsplash.com/vector-1756708634275-b02986eddc72?q=80&w=812&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
     'Авито', 'QA Automation Engineer',
     '"Было принято решение сменить профессиональный стек и перейти в автоматизацию тестирования.
Благодаря качественному код-ревью менторов удалось быстро освоить навыки написания промышленного кода и успешно пройти отбор."',
     2, true),
    ( 'Дмитрий С.',
     'https://images.unsplash.com/vector-1740296579293-3b641f44a570?q=80&w=580&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
     'Авито', 'QA Automation Engineer',
     '"Было принято решение сменить профессиональный стек и перейти в автоматизацию тестирования.
Благодаря качественному код-ревью менторов удалось быстро освоить навыки написания промышленного кода и успешно пройти отбор."',
     3, true);

-- Синхронизация identity-колонок после вставки фиксированных ID.
ALTER TABLE stories_stories ALTER COLUMN id RESTART WITH 4;