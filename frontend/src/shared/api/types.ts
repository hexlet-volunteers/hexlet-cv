/** Типы ответов REST API. Совпадают с DTO в `io.hexlet.cv.dto.api` и `io.hexlet.cv.dto.resume`. */

export interface Page<T> {
  items: T[]
  page: number
  totalPages: number
  totalItems: number
}

export interface CurrentUser {
  id: number
  email: string
  firstName: string | null
  lastName: string | null
  role: string | null
}

export interface Author {
  id: number
  firstName: string | null
  lastName: string | null
}

export interface ResumeSummary {
  id: number
  name: string
  summary: string | null
  city: string | null
  relocation: string | null
  englishFluency: string | null
  updatedAt: string | null
  author: Author
}

export interface ResumeWork {
  company: string | null
  position: string | null
  beginDate: string | null
  endDate: string | null
  current: boolean | null
  description: string | null
}

export interface ResumeEducation {
  institution: string | null
  faculty: string | null
  beginDate: string | null
  endDate: string | null
  current: boolean | null
  description: string | null
}

export interface ResumeDetails {
  id: number
  name: string
  summary: string | null
  skillsDescription: string | null
  projectsDescription: string | null
  awardsDescription: string | null
  aboutMyself: string | null
  githubUrl: string | null
  hexletUrl: string | null
  contactTelegram: string | null
  city: string | null
  relocation: string | null
  englishFluency: string | null
  updatedAt: string | null
  author: Author
  works: ResumeWork[]
  educations: ResumeEducation[]
}

export interface PublicProfile {
  id: number
  firstName: string | null
  lastName: string | null
  about: string | null
  resumes: ResumeSummary[]
}

/** Место работы или учёбы в форме: даты в формате `YYYY-MM-DD`, пустая строка — не указано. */
export interface WorkForm {
  company: string
  position: string
  beginDate: string
  endDate: string
  current: boolean
  description: string
}

export interface EducationForm {
  institution: string
  faculty: string
  beginDate: string
  endDate: string
  current: boolean
  description: string
}

/** Поля формы резюме в кабинете. */
export interface ResumeForm {
  name: string
  summary: string
  skillsDescription: string
  projectsDescription: string
  awardsDescription: string
  aboutMyself: string
  githubUrl: string
  city: string
  relocation: string
  englishFluency: string
  contactEmail: string
  contactTelegram: string
  contactPhone: string
  published: boolean
  works: WorkForm[]
  educations: EducationForm[]
}

export interface OwnResume extends Partial<
  Omit<ResumeForm, 'name' | 'published'>
> {
  id: number
  name: string
  published: boolean
  updatedAt: string | null
}

/** Трекер откликов (#1155). Даты — `YYYY-MM-DD`, время — `YYYY-MM-DD HH:mm`. */
export type ApplicationStage =
  | 'SAVED'
  | 'APPLIED'
  | 'SCREENING'
  | 'INTERVIEW'
  | 'OFFER'
  | 'REJECTED'

export type ApplicationEventType =
  | 'CREATED'
  | 'STAGE_CHANGED'
  | 'REPLY'
  | 'INTERVIEW'
  | 'LETTER'
  | 'NOTE'

export interface ApplicationAlert {
  type: 'SILENCE' | 'DEADLINE'
  days: number
}

export interface ApplicationSummary {
  id: number
  company: string
  position: string
  salary?: string
  source?: string
  stage: ApplicationStage
  rejectedAtStage?: ApplicationStage
  resume?: { id: number; name: string }
  deadline?: string
  nextStepTitle?: string
  nextStepAt?: string
  lastEventAt: string
  createdAt?: string
  alerts: ApplicationAlert[]
}

export interface ApplicationDetails extends ApplicationSummary {
  vacancyUrl?: string
  events: {
    id: number
    type: ApplicationEventType
    text: string
    occurredAt: string
  }[]
}

export interface ApplicationList {
  items: ApplicationSummary[]
  counts: Record<'ALL' | ApplicationStage, number>
  rejections: {
    total: number
    byStage: Partial<Record<ApplicationStage, number>>
  }
}

export interface ApplicationForm {
  company: string
  position: string
  salary?: string
  vacancyUrl?: string
  source?: string
  stage?: ApplicationStage
  resumeId?: number | null
  deadline?: string | null
  nextStepTitle?: string
  nextStepAt?: string | null
}
