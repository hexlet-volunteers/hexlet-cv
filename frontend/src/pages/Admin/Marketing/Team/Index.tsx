import { withMarketingLayout } from '../components/withMarketingLayout'
import { MarketingTeam, type MarketingTeamDTO } from '@widgets/admin-marketing'

/**
 * Пропсы для страницы управления командой.
 */
export interface PageProps {
  /** Массив участников команды для отображения на странице. */
  team: MarketingTeamDTO[]
}

/**
 * Компонент страницы управления командой в админ-панели.
 * Отображает список участников команды с использованием компонента MarketingTeam.
 *
 * @param props - Пропсы компонента.
 * @param props.team - Массив участников команды для отображения.
 * @returns React-компонент страницы команды.
 */
const Team = (props: PageProps) => {
  const { team } = props
  return <MarketingTeam team={team} />
}

/**
 * Компонент страницы команды, обернутый в макет маркетинга.
 * Использует HOC withMarketingLayout для применения макетов.
 */
const TeamWithLayout = withMarketingLayout<PageProps>(Team)

export default TeamWithLayout
