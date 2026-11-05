import { Link } from 'react-router'
import { plans } from '../../model/content'
import s from './landing.module.css'

const cx = (...names: (string | false | undefined)[]) =>
  names.filter(Boolean).join(' ')

/** Путь от резюме до оффера на первом экране: что уже сделано и что дальше. */
const stages = [
  { state: 'pass', job: 'готово', title: 'Резюме', meta: <>Исправлено <b>12 из 14</b> замечаний</> },
  { state: 'pass', job: 'готово', title: 'Вакансии', meta: <><b>38</b> подходят под ваш стек</> },
  { state: 'pass', job: 'готово', title: 'Отклики', meta: <>Отправлено <b>11</b>, 3 ответили</> },
  { state: 'running', job: 'сейчас', title: 'Собеседование', meta: <>Тренировка перед интервью в Т-Банке</> },
  { state: 'pending', job: 'дальше', title: 'Оффер', meta: <>Подскажем, как обсудить зарплату</> },
] as const

function Hero() {
  return (
    <section className={s.hero}>
      <div className={cx(s.wrap, s.heroInner)}>
        <div className={s.prompt}>
          <b>&gt;_</b> Хекслет Карьера
          <span className={s.caret} />
        </div>
        <h1 className={s.heroTitle}>
          Работа в IT — <em>по шагам</em>
        </h1>
        <p className={s.heroLead}>
          Соберите резюме, найдите подходящие вакансии, откликайтесь и
          тренируйтесь перед собеседованиями в одном сервисе. На каждом шаге
          видно, что уже получилось и что поправить.
        </p>
        <div className={s.heroActions}>
          <Link to="/register" className={cx(s.btn, s.btnPrimary)}>
            Начать бесплатно
          </Link>
          <Link to="/app" className={cx(s.btn, s.btnGhost)}>
            Посмотреть кабинет
          </Link>
          <span className={s.heroNote}>Студентам Хекслета — бесплатно</span>
        </div>

        <div className={s.pipeline} aria-label="Пример: путь к офферу в кабинете">
          <div className={s.pipelineBar}>
            <div className={s.dots}>
              <i />
              <i />
              <i />
            </div>
            <span>Ваш путь к офферу · Junior Frontend</span>
            <span>3 из 5 шагов</span>
          </div>
          <div className={s.stages}>
            {stages.map((st) => (
              <div className={s.stage} key={st.title}>
                <div className={s.stageHead}>
                  <span
                    className={cx(
                      s.status,
                      st.state === 'pass' && s.pass,
                      st.state === 'running' && s.running,
                      st.state === 'pending' && s.pending,
                    )}
                  >
                    {st.state === 'pass' ? '✓' : ''}
                  </span>
                  {st.job}
                </div>
                <div className={s.stageTitle}>{st.title}</div>
                <div className={s.stageMeta}>{st.meta}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  )
}

function Features() {
  return (
    <section id="features" className={s.section}>
      <div className={s.wrap}>
        <div className={s.kicker}>{'Что внутри'}</div>
        <h2 className={s.h2}>Четыре инструмента, которые знают друг о друге</h2>
        <p className={s.lead}>
          Вакансия помнит, какое резюме вы отправили. Письмо собрано под вакансию.
          Тренировка готовит к завтрашнему собеседованию, а не к абстрактному.
        </p>

        <div className={s.bento}>
          <article className={cx(s.card, s.span7)}>
            <div>
              <h3 className={s.cardTitle}>Вакансии с процентом совпадения</h3>
              <p className={s.cardText}>
                hh.ru, Хабр Карьера, Getmatch и Telegram-каналы в одной ленте.
                «Джуны с опытом 3 года» отсеиваются сами.
              </p>
            </div>
            <div className={s.demo}>
              <div className={s.vacancy}>
                <div className={s.vacancyBody}>
                  <div className={s.vacancyName}>Junior Frontend Developer</div>
                  <div className={s.vacancyMeta}>React · TypeScript · удалённо · hh.ru</div>
                </div>
                <span className={cx(s.match, s.matchHigh)}>92%</span>
              </div>
              <div className={s.vacancy}>
                <div className={s.vacancyBody}>
                  <div className={s.vacancyName}>Frontend-разработчик (стажёр+)</div>
                  <div className={s.vacancyMeta}>React · Redux · офис · Хабр Карьера</div>
                </div>
                <span className={cx(s.match, s.matchMid)}>71%</span>
              </div>
              <div className={cx(s.vacancy, s.dim)}>
                <div className={s.vacancyBody}>
                  <div className={s.vacancyName}>Junior Frontend</div>
                  <div className={s.vacancyMeta}>опыт от 3 лет · скрыто фильтром</div>
                </div>
                <span className={cx(s.match, s.matchLow)}>18%</span>
              </div>
            </div>
          </article>

          <article className={cx(s.card, s.span5)}>
            <div>
              <h3 className={s.cardTitle}>Резюме проходит ATS</h3>
              <p className={s.cardText}>
                AI-ревью по чек-листу рекрутёров и ключевые слова под конкретную
                вакансию.
              </p>
            </div>
            <div className={s.demo}>
              <ul className={s.checks}>
                <li><span className={s.ok}>✓</span> Стек в первом экране</li>
                <li><span className={s.ok}>✓</span> Проекты со ссылками на GitHub</li>
                <li><span className={s.ok}>✓</span> Ключевые слова вакансии: 9/10</li>
                <li><span className={s.warn}>!</span> Нет результата в цифрах у проекта</li>
              </ul>
            </div>
          </article>

          <article className={cx(s.card, s.span5)}>
            <div>
              <h3 className={s.cardTitle}>Письма под вакансию</h3>
              <p className={s.cardText}>
                Сопроводительное, фоллоу-ап, ответ на отказ и торг по офферу.
              </p>
            </div>
            <div className={s.demo}>
              <div className={s.letterTabs}>
                <span className={s.tabOn}>сопроводительное</span>
                <span>фоллоу-ап</span>
                <span>торг</span>
              </div>
              <div className={s.letter}>
                Здравствуйте! Откликаюсь на <mark>Junior Frontend</mark>. Делал
                SPA на <mark>React + TypeScript</mark> с тестами на Jest — ссылка
                на код ниже…
              </div>
            </div>
          </article>

          <article className={cx(s.card, s.span7)}>
            <div>
              <h3 className={s.cardTitle}>Трекер откликов</h3>
              <p className={s.cardText}>
                Какое резюме куда ушло, кому пора напомнить о себе и на каком этапе
                воронка проседает.
              </p>
            </div>
            <div className={s.demo}>
              <div className={s.kanban}>
                <div className={s.col}>
                  <span>отклик · 4</span>
                  <div className={s.tile}>Озон Тех<small>2 дня назад</small></div>
                  <div className={cx(s.tile, s.tileAlert)}>Авито<small>7 дней тишины</small></div>
                </div>
                <div className={s.col}>
                  <span>скрининг · 2</span>
                  <div className={s.tile}>Контур<small>звонок в пт</small></div>
                </div>
                <div className={s.col}>
                  <span>собес · 1</span>
                  <div className={s.tile}>Т-Банк<small>завтра 11:00</small></div>
                </div>
                <div className={s.col}>
                  <span>оффер · 1</span>
                  <div className={cx(s.tile, s.tileOffer)}>Эвотор<small>ждёт ответа</small></div>
                </div>
              </div>
            </div>
          </article>
        </div>
      </div>
    </section>
  )
}

function Interview() {
  return (
    <section className={cx(s.section, s.dark)}>
      <div className={cx(s.wrap, s.split)}>
        <div>
          <div className={s.kicker}>{'Главное отличие'}</div>
          <h2 className={s.h2}>Тренажёр собеседований, который честно скажет, где ошибка</h2>
          <p className={s.lead}>
            AI задаёт вопросы и даёт задачу, как на настоящем техническом
            интервью. Потом оценивает две вещи: как вы объяснили решение и
            работает ли ваш код на самом деле.
          </p>
          <div className={s.compare}>
            <div className={cx(s.compareCard, s.compareThem)}>
              <h4>Обычный AI-тренажёр</h4>
              <p>«Отличный ответ, уверенно!» — даже если код с ошибкой.</p>
            </div>
            <div className={cx(s.compareCard, s.compareUs)}>
              <h4>Хекслет Карьера</h4>
              <p>Запускает ваш код и показывает, где он ломается.</p>
            </div>
          </div>
        </div>

        <div className={s.terminal}>
          <div className={s.pipelineBar}>
            <div className={s.dots}>
              <i />
              <i />
              <i />
            </div>
            <span>Итог тренировки · задача 3 из 5</span>
            <span />
          </div>
          <div className={s.result}>
            <div className={s.resultRow}>
              <span className={cx(s.resultIcon, s.pass)}>✓</span>
              <div>
                <div className={s.resultTitle}>Объяснение решения</div>
                <div className={s.resultText}>Понятно и по порядку, без лишней воды</div>
              </div>
            </div>
            <div className={s.resultRow}>
              <span className={cx(s.resultIcon, s.failIcon)}>!</span>
              <div>
                <div className={s.resultTitle}>Код работает в 4 случаях из 6</div>
                <div className={s.resultText}>
                  На маленьких данных всё верно. На большом списке решение не
                  успевает за отведённое время.
                </div>
              </div>
            </div>
            <div className={s.resultFix}>
              <div className={s.resultLabel}>Что поправить</div>
              Два вложенных цикла перебирают все пары. Храните уже встреченные
              числа в Set — проверка станет мгновенной.
              <div className={s.resultLesson}>→ Урок «Хеш-таблицы» в курсе по алгоритмам</div>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

const ecosystem = [
  {
    title: <>Проекты → <span>портфолио</span></>,
    text: 'Портфолио собирается из проектов, которые прошли код-ревью на Хекслете. GitHub подключается в один клик.',
  },
  {
    title: <>Слабое место → <span>урок</span></>,
    text: 'Провалили алгоритмы на тренировке — получаете конкретный урок из каталога Хекслета, а не совет «старайтесь лучше».',
  },
  {
    title: <>Командный проект → <span>опыт</span></>,
    text: 'Работа в команде со спринтами, тимлидом и код-ревью попадает в резюме строкой опыта и в профиль кейсом.',
  },
  {
    title: <>Вопросы выпускников → <span>подготовка</span></>,
    text: 'База вопросов и тестовых заданий, которые выпускники приносят с реальных собеседований.',
  },
]

function Ecosystem() {
  return (
    <section id="how" className={s.section}>
      <div className={s.wrap}>
        <div className={s.kicker}>{'Продолжение учёбы'}</div>
        <h2 className={s.h2}>Навыки в резюме подтверждены кодом, а не словами</h2>
        <p className={s.lead}>
          Карьера встроена в Хекслет. Всё, что вы сделали на обучении, работает
          на поиск работы.
        </p>
        <div className={s.rows}>
          {ecosystem.map((r, i) => (
            <div className={s.row} key={i}>
              <div className={s.rowNum}>0{i + 1}</div>
              <h3 className={s.rowTitle}>{r.title}</h3>
              <p className={s.rowText}>{r.text}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

function Pricing() {
  return (
    <section id="pricing" className={s.section} style={{ paddingTop: 0 }}>
      <div className={s.wrap}>
        <div className={s.kicker}>{'Тарифы'}</div>
        <h2 className={s.h2}>Начните бесплатно</h2>
        <p className={s.lead}>«Про» нужен, когда поиск становится активным.</p>
        <div className={s.plans}>
          {plans.map((p) => (
            <div
              key={p.id}
              className={cx(
                s.plan,
                p.accent === 'brand' && s.planFeatured,
                p.accent === 'dark' && s.planStudent,
              )}
            >
              {p.badge && <span className={s.planBadge}>{p.badge}</span>}
              <div className={s.planName}>{p.name}</div>
              <div className={s.planPrice}>{p.price}</div>
              <div className={s.planNote}>{p.priceNote.replace(/^·\s*/, '')}</div>
              <ul className={s.planItems}>
                {p.items.map((it) => (
                  <li key={it}>{it}</li>
                ))}
              </ul>
              {p.id !== 'students' && (
                <Link
                  to={p.to}
                  className={cx(
                    s.btn,
                    s.planAction,
                    p.accent === 'brand' && s.btnPrimary,
                    p.accent === 'outline' && s.btnOutline,
                  )}
                >
                  {p.action}
                </Link>
              )}
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

function Cta() {
  return (
    <section className={s.cta}>
      <div className={s.wrap} style={{ position: 'relative', zIndex: 1 }}>
        <h2 className={s.ctaTitle}>Начните с резюме — это 5 минут</h2>
        <p className={s.ctaText}>
          Импортируйте профиль с hh.ru или из Хекслета. AI-ревью сразу покажет,
          что поправить.
        </p>
        <Link to="/register" className={cx(s.btn, s.btnWhite)}>
          Собрать резюме
        </Link>
      </div>
    </section>
  )
}

/** Новая главная: один файл секций + CSS-модуль, данные тарифов из content.ts. */
export function NewLanding() {
  return (
    <div className={s.page}>
      <Hero />
      <Features />
      <Interview />
      <Ecosystem />
      <Pricing />
      <Cta />
    </div>
  )
}
