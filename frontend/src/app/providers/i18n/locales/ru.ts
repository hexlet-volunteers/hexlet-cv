export default {
  translation: {
    articles: {
      description: 'Статьи, новости и советы о поиске работы',
      readNextActionLabel: 'Читать далее',
      readingTime_one: '{{count}} минута',
      readingTime_few: '{{count}} минуты',
      readingTime_many: '{{count}} минут',
      readingTime_other: '{{count}} минут',
      title: 'Новости',
    },
    communities: {
      action_connect_to: 'Присоединиться к сообществу',
      anchor: 'Сообщество',
      description:
        'Мы объединяем выпускников и участников: общение, обмен опытом, поддержка и совместные проекты.',
      link_bot_title: 'Бот Карьеры',
      link_bot_description:
        'Автотесты, подсказки по откликам, сопровождение поиска.',
      link_channel_hexlet_title: 'Телеграм канал Хекслета',
      link_channel_hexlet_description:
        'Анонсы, инсайты, советы по карьере и обучению.',
      link_channel_vacancies_title: 'Канал вакансии для новичков',
      link_channel_vacancies_description:
        'Свежие джуниор-вакансии и стажировки каждый день.',
      title: 'Наше сообщество',
    },
    header: {
      auth: {
        account: 'Личный кабинет',
        admin: 'Панель управления',
        signOut: 'Выйти',
        tryFreeLine1: 'Попробовать',
        tryFreeLine2: 'бесплатно',
        signIn: 'Войти',
        signUp: 'Регистрация',
      },
    },
    auth: {
      social: {
        providers: {
          google: 'Google',
          github: 'Github',
        },
      },
      signUp: {
        title: 'Регистрация',
        subtitle: 'Используйте привычный способ входа',
        divider: 'или',
        fields: {
          lastName: {
            label: 'Фамилия',
            placeholder: 'Фамилия',
          },
          firstName: {
            label: 'Имя',
            placeholder: 'Имя',
          },
          name: {
            errors: {
              required: 'Введите имя',
              too_short: 'Имя слишком короткое',
              too_long: 'Имя слишком длинное',
              invalid_format: 'Некорректный формат имени',
            },
          },
          email: {
            label: 'Email',
            placeholder: 'E-mail',
            errors: {
              required: 'Введите email',
              too_long: 'Email слишком длинный',
              invalid_format: 'Некорректный формат email',
            },
          },
          password: {
            label: 'Пароль',
            placeholder: 'Пароль',
            errors: {
              required: 'Введите пароль',
              too_long: 'Пароль слишком длинный',
              too_short: 'Пароль должен быть не короче 8 символов',
              invalid_format: 'Некорректный формат пароля',
            },
          },
          terms: {
            errors: {
              required: 'Вы должны принять условия',
            },
            label: 'Я принимаю условия',
          },
        },
        signInLink: 'Уже есть аккаунт? Войти',
        submit: 'Зарегистрироваться',
      },
      signIn: {
        title: 'Войти',
        subtitle: 'Используйте привычный способ входа',
        divider: 'или',
        fields: {
          email: {
            label: 'Email',
            placeholder: 'E-mail',
            errors: {
              invalid: 'Неверный email или пароль',
              required: 'Введите email',
              too_long: 'Email слишком длинный',
              invalid_format: 'Некорректный формат email',
            },
          },
          password: {
            label: 'Пароль',
            placeholder: 'Your password',
            errors: {
              required: 'Введите пароль',
              too_long: 'Пароль слишком длинный',
              too_short: 'Пароль должен быть не короче 8 символов',
              invalid_format: 'Некорректный формат пароля',
            },
          },
        },
        submit: 'Войти',
      },
    },
    homePage: {
      greetings: 'Добро пожаловать!',
      aboutUs: {
        title: 'Найти работу в IT легче, чем ты думаешь',
        description:
          'С Hexlet Карьерой ты получаешь офферы быстрее - за недели, а не месяцы.',
        buttons: {
          tryFree: 'Попробовать бесплатно',
          startWithProjects: 'Начать с проектов →',
        },
      },
      whoWeAre: {
        title: 'Привет, мы Хекслет',
        subtitle: 'экосистема для старта и развития в IT:',
        features: {
          resume: 'Составлять резюме',
          apply: 'Откликаться',
          searchJobs: 'Искать вакансии и стажировки',
          chatRecruiters: 'Переписываться с рекрутерами',
          coverLetters: 'Писать сопроводительные',
          prepareInterviews: 'Готовить интервью',
          getExperience: 'Получать коммерческий опыт',
        },
      },
      commercialProjects: {
        badge: 'Коммерческие проекты',
        title: 'Коммерческие проекты, в которых вы примете участие',
        description:
          'Практика в реальных продуктах Хекслета коммит в GitHub, опыт командной разработки и результаты в портфолио',
        benefits: {
          title: 'Что это дает',
          description:
            'Проектные роли, code review, задачи из реального бэклога, релизы - все как в компании',
        },
        projectsList: {
          title: 'Список проектов',
          items: [
            'Hexlet SICP - трекер прохождения курса',
            'Hexlet Correction - сервис для отметок ошибок и опечаток',
            'Run IT - среда для запуска и проверки кода',
            'Hexlet Comparator - сравнение онлайн-школ программирования',
          ],
        },
      },
      marketAnalytics: {
        badge: 'Аналитика',
        title: 'Аналитика рынка и вакансий',
        description:
          'Мы агрегируем вакансии бесплатно в едином окне. Следи за трендами зарплат, востребованностью профессий и новыми возможностями',
        cards: {
          liveData: {
            title: 'Живые данные',
            description:
              'Ежедневные обновления по вакансиям, зарплатам и требованиям работодателей',
          },
          unifiedWindow: {
            title: 'Единое окно',
            description:
              'Все вакансии в одном месте: фильтруй, сравнивай и находи подходящие предложения',
          },
        },
      },
      trainingPrograms: {
        title: 'Наши программы обучения',
        button: 'Узнать подробнее',
      },
      performanceReview: {
        title: 'Performance review и определение грейда',
        button: 'Записаться',
      },
      knowledgeBaseAndInterview: {
        title: 'База знаний и интервью',
        knowledgeBaseTitle: 'База знаний',
        knowledgeBaseDescription:
          'Пошаговые гайды, чек-листы и шаблоны для поиска работы.',
        interviewQuestionsTitle: 'Вопросы с собеседований',
        interviewQuestionsDescription:
          'Коллекция реальных вопросов и ответов по ролям и стеку.',
        realInterviewVideosTitle: 'Записи реальных собесов',
        realInterviewVideosDescription:
          'Разбор интервью с комментариями экспертов и чек-листами.',
        button: 'Получить доступ',
      },
      webinars: {
        title: 'Вебинары с HR и экспертами',
        viewSchedule: 'Смотреть расписание',
      },
      ourTeam: {
        sectionTitle: 'Наша команда',
        aboutBadge: 'О нас',
      },
      totaAi: {
        aiAssistant: 'ИИ-помощник',
        title: 'Тота ИИ — агент-ускоритель карьеры',
        description:
          'Помогает с резюме, откликами, перепиской с рекрутерами и подготовкой к собеседованиям.',
      },
    },
    accountPage: {
      purchases: {
        title: 'Мои покупки и подписки',
        dashboard_cards: {
          courses_in_process: 'Курсов в работе',
          week_progress: 'Прогресс недели',
          done_lessons: 'Уроков завершено',
          last_result: 'Последний результат',
          nearest_event: 'Ближайшее событие',
        },
        table: {
          order_number: 'Заказ',
          product_name: 'Товар',
          purchase_date: 'Дата',
          price: 'Цена',
          status: 'Статус',
          receipt: 'Чек',
        },
      },
      progress: {
        title: 'Моё обучение',
        dates: {
          today: 'обновлено сегодня',
          yesterday: 'обновлено вчера',
          days_ago: 'обновлено {{count}} дн. назад',
        },
        status: {
          done: 'Завершено',
          inProgress: 'В процессе',
        },
        programBadge: {
          newProgram: 'Новый',
          completedProgram: 'Сертификат',
        },
      },
      studyPrograms: {
        title: 'Программы обучения',
        noPrograms: 'Пока нет активных программ',
        incorrectData: 'Произошла ошибка при загрузке программ',
      },
      notifications: {
        title: 'Уведомления',
        today: 'сегодня',
        yesterday: 'вчера',
        daysAgo: 'давно',
        noNotifications: 'Уведомлений нет',
      },
    },
    emptyPlaceholders: {
      noPurchasesTitle: 'У вас еще нет подписок и заказов',
      noLearningProgress: 'Начните обучение сейчас',
    },
    activityCards: {
      courses_in_process: 'Курсов в работе',
      week_progress: 'Прогресс недели',
      done_lessons: 'Уроков завершено',
      last_result: 'Последний результат',
      nearest_event: 'Ближайшее событие',
    },
    buttonsLabels: {
      goToCatalog: 'В каталог',
      open: 'Открыть',
      continue: 'Продолжить',
    },
    adminPage: {
      header: {
        title: 'Admin Panel',
        logoutButton: 'Выход',
      },
      interviews: {
        title: 'Интервью',
        input: 'Поиск: заголовок/спикер',
        button: 'Создать',
        interviewTitle: 'Заголовок',
        interviewSpeaker: 'Спикер',
        interviewVideo: 'Видео',
        interviewPublished: 'Публикация',
      },
      knowledgeBase: {
        title: 'База знаний',
        input: 'Поиск: заголовок/категория',
        button: 'Создать',
        articleTitle: 'Заголовок',
        articlecategory: 'Категория',
        articlePublished: 'Публикация',
        baseIsEmpty: 'База знаний пока пуста',
        nothingFound: 'По запросу ничего не найдено',
      },
      studyPrograms: {
        title: 'Программы обучения',
        button: 'Добавить программу',
        programName: 'Название',
        programDuration: 'Длительность',
        programLessons: 'Уроков',
        programPublished: 'Публикация',
      },
      webinars: {
        title: 'Вебинары',
        input: 'Поиск: название/дата/ссылка',
        button: 'Создать',
        webinarName: 'Название',
        webinarDate: 'Дата',
        webinarRegistration: 'Регистрация',
        webinarVideo: 'Запись',
        webinarFeature: 'Фичер',
        webinarPublished: 'Публикация',
        noWebinars: 'Пока нет доступных вебинаров',
        nothingFound: 'По запросу ничего не найдено',
      },
    },
  },
}
