import { withMarketingLayout } from '../components/withMarketingLayout'
import { MarketingTeam, type MarketingTeamDTO } from '@widgets/admin-marketing'

export interface PageProps {
  team: MarketingTeamDTO[]
}

const Team = (props: PageProps) => {
  const { team } = props
  return <MarketingTeam team={team} />
}

const TeamWithLayout = withMarketingLayout(Team)

export default TeamWithLayout
