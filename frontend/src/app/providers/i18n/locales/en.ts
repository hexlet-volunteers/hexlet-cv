export default {
  translation: {
    articles: {
      description: 'Articles, news, and tips about job searching',
      readNextActionLabel: 'Next',
      readingTime_one: '{{count}} minute',
      readingTime_other: '{{count}} minutes',
      title: 'News',
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
    auth: {
      social: {
        providers: {
          google: 'Google',
          github: 'Github',
        },
      },
      signUp: {
        title: 'Sign up',
        subtitle: 'Use your preferred sign-in method',
        divider: 'or',
        fields: {
          lastName: {
            label: 'Last name',
            placeholder: 'Last name',
          },
          firstName: {
            label: 'First name',
            placeholder: 'First name',
          },
          name: {
            errors: {
              required: 'Enter your name',
              too_short: 'Name is too short',
              too_long: 'Name is too long',
              invalid_format: 'Invalid name format',
            },
          },
          email: {
            label: 'Email',
            placeholder: 'E-mail',
            errors: {
              required: 'Enter your email',
              too_long: 'Email is too long',
              invalid_format: 'Invalid email format',
            },
          },
          password: {
            label: 'Password',
            placeholder: 'Your password',
            errors: {
              required: 'Enter your password',
              too_long: 'Password is too long',
              too_short: 'Password must be at least 8 characters',
              invalid_format: 'Invalid password format',
            },
          },
          terms: {
            errors: {
              required: 'You must accept the terms',
            },
            label: 'I accept the terms',
          },
        },
        signInLink: 'Already have an account? Sign in',
        submit: 'Sign up',
      },
      signIn: {
        title: 'Sign in',
        subtitle: 'Use your preferred sign-in method',
        divider: 'or',
        fields: {
          email: {
            label: 'Email',
            placeholder: 'E-mail',
            errors: {
              invalid: 'Invalid email or password',
              required: 'Enter your email',
              too_long: 'Email is too long',
              invalid_format: 'Invalid email format',
            },
          },
          password: {
            label: 'Password',
            placeholder: 'Password',
            errors: {
              required: 'Enter your password',
              too_long: 'Password is too long',
              too_short: 'Password must be at least 8 characters',
              invalid_format: 'Invalid password format',
            },
          },
        },
        submit: 'Sign in',
      },
    },
    homePage: {
      greetings: 'Welcome',
      aboutUs: {
        title: 'Finding a job in IT is easier than you think',
        description:
          'With Hexlet Career, you get offers faster - in weeks, not months.',
        buttons: {
          tryFree: 'Try for free',
          startWithProjects: 'Start with projects →',
        },
      },
      whoWeAre: {
        title: 'Hello, we are Hexlet',
        subtitle: 'ecosystem for starting and developing in IT:',
        features: {
          resume: 'Create a resume',
          apply: 'Apply for jobs',
          searchJobs: 'Search for vacancies and internships',
          chatRecruiters: 'Chat with recruiters',
          coverLetters: 'Write cover letters',
          prepareInterviews: 'Prepare for interviews',
          getExperience: 'Get commercial experience',
        },
      },
      commercialProjects: {
        badge: 'Commercial Projects',
        title: 'Commercial Projects You Will Participate In',
        description:
          'Practice in real Hexlet products, commits to GitHub, team development experience and portfolio results',
        benefits: {
          title: 'What It Gives You',
          description:
            'Project roles, code review, tasks from real backlog, releases - everything like in a real company',
        },
        projectsList: {
          title: 'Projects List',
          items: [
            'Hexlet SICP - course progress tracker',
            'Hexlet Correction - service for marking errors and typos',
            'Run IT - environment for running and checking code',
            'Hexlet Comparator - comparison of programming online schools',
          ],
        },
      },
      marketAnalytics: {
        badge: 'Analytics',
        title: 'Market and Job Analytics',
        description:
          'We aggregate job vacancies for free in a single window. Track salary trends, profession demand, and new opportunities',
        cards: {
          liveData: {
            title: 'Live Data',
            description:
              'Daily updates on vacancies, salaries, and employer requirements',
          },
          unifiedWindow: {
            title: 'Unified Window',
            description:
              'All vacancies in one place: filter, compare, and find suitable offers',
          },
        },
      },
      trainingPrograms: {
        title: 'Our training programs',
        button: 'Learn more',
      },
      performanceReview: {
        title: 'Performance review and grade determination',
        button: 'Sign up',
      },
      knowledgeBaseAndInterview: {
        title: 'Knowledge base and interviews',
        knowledgeBaseTitle: 'Knowledge Base',
        knowledgeBaseDescription:
          'Step-by-step guides, checklists and templates for job search.',
        interviewQuestionsTitle: 'Interview questions',
        interviewQuestionsDescription:
          'Collection of real questions and answers about roles and stack.',
        realInterviewVideosTitle: 'Real interviews videos',
        realInterviewVideosDescription:
          'Interviews analysis with expert comments and checklists.',
        button: 'Get access',
      },
      webinars: {
        title: 'Webinars with HR and Experts',
        viewSchedule: 'View Schedule',
      },
      ourTeam: {
        sectionTitle: 'Our Team',
        aboutBadge: 'About Us',
      },
      totaAi: {
        aiAssistant: 'AI Assistant',
        title: 'Tota AI — Career Accelerator Agent',
        description:
          'Helps with resumes, applications, correspondence with recruiters, and interview preparation.',
      },
    },
    accountPage: {
      purchases: {
        title: 'My purchases and subscriptions',
        dashboard_cards: {
          courses_in_process: 'Courses in progress',
          week_progress: 'Weekly progress',
          done_lessons: 'Lessons completed',
          last_result: 'Last result',
          nearest_event: 'Nearest event',
        },
        table: {
          order_number: 'Order',
          product_name: 'Product',
          purchase_date: 'Date',
          price: 'Price',
          status: 'Status',
          receipt: 'Receipt',
        },
      },
      progress: {
        title: 'My training',
        dates: {
          today: 'updated today',
          yesterday: 'updated yesterday',
          days_ago: 'updated {{count}} d. ago',
        },
        status: {
          done: 'Done',
          inProgress: 'In progress',
        },
        courseBadge: {
          newProgram: 'New',
          completedProgram: 'Сertificate',
        },
      },
      notifications: {
        title: 'Notifications',
        today: 'today',
        yesterday: 'yesterday',
        daysAgo: 'long ago',
        noNotifications: 'No notifications',
      },
      webinars: {
        title: 'Webinars',
        registration: 'Register',
        addToCalendar: 'Add to Calendar',
        shedule: 'Schedule',
        noWebinars: 'No webinars scheduled yet',
        location: {
          online: 'Online',
          offline: 'Offline',
        },
      },
      knowledgeBase: {
        title: 'Knowledge base',
        openJournalButton: 'Open journal',
        readButton: 'Read',
        baseIsEmpty: 'The knowledge base is empty',
        modalTitle: 'Journal',
      },
    },
    activityCards: {
      courses_in_process: 'Courses in Progress',
      week_progress: 'Weekly Progress',
      done_lessons: 'Lessons completed',
      last_result: 'Latest Result',
      nearest_event: 'Upcoming Event',
    },
    emptyPlaceholders: {
      noPurchasesTitle: 'You have no subscriptions or orders yet',
    },

    buttonsLabels: {
      goToCatalog: 'Go to Catalog',
      open: 'Open',
      continue: 'Continue',
    },
    adminPage: {
      header: {
        title: 'Admin Panel',
        logoutButton: 'Logout',
      },
      interviews: {
        title: 'Interviews',
        input: 'Search: title/speaker',
        button: 'Create',
        interviewTitle: 'Title',
        interviewSpeaker: 'Speaker',
        interviewVideo: 'Video',
        interviewPublished: 'Published',
      },
      knowledgeBase: {
        title: 'Knowledge base',
        input: 'Search: title/category',
        button: 'Create',
        articleTitle: 'Title',
        articlecategory: 'Category',
        articlePublished: 'Published',
        baseIsEmpty: 'The knowledge base is empty',
        nothingFound: 'Nothing is found by request',
      },
      studyPrograms: {
        title: 'Study programs',
        button: 'Add program',
        programName: 'Name',
        programDuration: 'Duration',
        programLessons: 'Lessons',
        programPublished: 'Published',
      },
      webinars: {
        title: 'Webinars',
        input: 'Search: name/date/link',
        button: 'Create',
        webinarName: 'Name',
        webinarDate: 'Date',
        webinarRegistration: 'Registration',
        webinarVideo: 'Video',
        webinarFeature: 'Feature',
        webinarPublished: 'Published',
        noWebinars: 'There are no webinars yet',
        nothingFound: 'Nothing is found by request',
      },
    },
  },
}
