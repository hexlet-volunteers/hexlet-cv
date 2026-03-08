import { AdminUsers, type TProps } from '@widgets/admin-users/'
import { AdminLayout } from '../components/AdminLayout'
import type React from 'react'

const Users = ({ users }: TProps) => {
  return <AdminUsers users={users} />
}

Users.layout = (page: React.ReactNode) => <AdminLayout>{page}</AdminLayout>

export default Users
