import { AdminUsers } from '@widgets/admin-users/'
import { AdminLayout } from '../components/AdminLayout'
import type { UserDTO } from '@entities/user'
import type React from 'react'
import type { InertiaPage } from '@shared/types/inertia'

interface UsersProps {
  users: UserDTO[]
}

const Users: InertiaPage<UsersProps> = ({ users }) => {
  return <AdminUsers users={users} />
}

Users.layout = (page: React.ReactNode) => <AdminLayout>{page}</AdminLayout>

export default Users
